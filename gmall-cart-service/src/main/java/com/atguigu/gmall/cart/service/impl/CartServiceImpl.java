package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.constant.CartConst;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Reference
    private ManageService manageService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 登录时添加商品到购物车
     * 1.先查询一下购物车中是否有相同的商品，如果有则数量相加
     * 2.如果没有直接添加到数控库
     * 3.更新缓存
     *
     * @param skuId  商品Id
     * @param userId 用户Id
     * @param skuNum 商品数量
     */
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        Jedis jedis = null;
        try {
            //获取jedis
            jedis = redisUtil.getJedis();
            //设置购物车的key
            String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
            //通过skuId和userId查询，是否由该商品
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
            if (cartInfoExist != null) {
                //有相同的商品 数量相加
                cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
                //给skuPrice初始化操作
                cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());
                //更新数据
                cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
                //同步缓存
                //jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfoExist));
            } else {
                //没有相同的商品 cartInfo数据来源于商品详情页面 相当于来自skuInfo 根据skuId查询skuInfo
                SkuInfo skuInfo = manageService.getSkuInfo(skuId);
                CartInfo info = new CartInfo();
                //属性赋值
                info.setSkuName(skuInfo.getSkuName());
                info.setSkuId(skuId);
                info.setCartPrice(skuInfo.getPrice());
                info.setSkuPrice(skuInfo.getPrice());
                info.setImgUrl(skuInfo.getSkuDefaultImg());
                info.setUserId(userId);
                info.setSkuNum(skuNum);
                //添加到数据库
                cartInfoMapper.insertSelective(info);
                cartInfoExist = info;
                //同步缓存
                //jedis.hset(cartKey, skuId, JSON.toJSONString(info));
            }
            //同步缓存
            jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfoExist));
            //设置过期时间
            String userKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USERINFOKEY_SUFFIX;
            //获取userKey的过期时间
            Long ttl = jedis.ttl(userKey);
            //给购物车设置过期时间
            jedis.expire(cartKey, ttl.intValue());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 根据userId查询购物车数据
     *
     * @param userId userId
     * @return List<CartInfo>
     */
    @Override
    public List<CartInfo> getCartList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        Jedis jedis = null;
        try {
            //获取jidis
            jedis = redisUtil.getJedis();
            //定义key
            String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
            //从key中获取数据
            List<String> stringList = jedis.hvals(cartKey);
            if (stringList != null && stringList.size() > 0) {
                //从缓存中获取数据
                for (String cartInfoStr : stringList) {
                    cartInfoList.add(JSON.parseObject(cartInfoStr, CartInfo.class));
                }
                //排序
                cartInfoList.sort(new Comparator<CartInfo>() {
                    @Override
                    public int compare(CartInfo o1, CartInfo o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });
                return cartInfoList;
            } else {
                //从数据库中获取数据
                cartInfoList = loadCartCache(userId);
                return cartInfoList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 合并购物车
     *
     * @param cartListCK cartListCK
     * @param userId userId
     * @return List<CartInfo>
     */
    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId) {
        //根据userId获取购物车数据
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        //合并 合并条件 skuId相同
        if (cartInfoListDB != null && cartInfoListDB.size() > 0) {
            for (CartInfo cartInfoCK : cartListCK) {
                //定义boolean变量
                boolean isMatch = false;
                for (CartInfo cartInfoDB : cartInfoListDB) {
                    if (cartInfoCK.getSkuId().equals(cartInfoDB.getSkuId())) {
                        //将数量进行相加
                        cartInfoDB.setSkuNum(cartInfoDB.getSkuNum() + cartInfoCK.getSkuNum());
                        //修改数据库
                        cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                        isMatch = true;
                    }
                }
                //没有要合并的数据
                if (!isMatch) {
                    //将未登录的购物车添加到数据库中 并将未登录对象赋值给登录对象
                    cartInfoCK.setUserId(userId);
                    cartInfoMapper.insertSelective(cartInfoCK);
                }
            }
        }
        //将合并之后的数据返回
        List<CartInfo> cartInfoList = loadCartCache(userId);
        //与未登录进行合并
        for (CartInfo cartInfoDB : cartInfoList) {
            for (CartInfo cartInfoCK : cartListCK) {
                if (cartInfoDB.getSkuId().equals(cartInfoCK.getSkuId())) {
                    if ("1".equals(cartInfoCK.getIsChecked())) {
                        //修改数据库的状态
                        cartInfoDB.setIsChecked(cartInfoCK.getIsChecked());
                        checkCart(cartInfoDB.getSkuId(), cartInfoCK.getIsChecked(), userId);
                    }
                }
            }
        }
        return cartInfoList;
    }

    /**
     * 修改商品状态
     * 1.获取jedis客户端
     * 2.获取购物车
     * 3.直接修改skuId商品的勾选状态 isChecked
     * 4.重新写入购物车
     * 5.新建一个购物车存储勾选的商品
     * @param skuId skuId
     * @param isChecked isChecked
     * @param userId userId
     */
    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        Jedis jedis = null;
        try {
            //获取jedis客户端
            jedis = redisUtil.getJedis();
            //定义key
            String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
            //获取购物车
            String cartInfoJson = jedis.hget(cartKey, skuId);
            CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
            //直接修改skuId商品的勾选状态 isChecked
            cartInfo.setIsChecked(isChecked);
            //重新写入购物车
            jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfo));
            //新建一个购物车的key
            String cartKeyChecked = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
            //新建一个购物车存储勾选的商品 isChecked = 1为勾选商品
            if ("1".equals(isChecked)) {
                jedis.hset(cartKeyChecked, skuId, JSON.toJSONString(cartInfo));
            } else {
                //删除被勾选的商品
                jedis.hdel(cartKeyChecked, skuId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 根据userId查询购物车
     *
     * @param userId userId
     * @return List<CartInfo>
     */
    private List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList == null && cartInfoList.size() == 0) {
            return null;
        }
        Jedis jedis = null;
        try {
            //获取jedis
            jedis = redisUtil.getJedis();
            //定义key
            String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
//            //查询得到的数据放入redis
//            for (CartInfo cartInfo : cartInfoList) {
//                jedis.hset(cartKey, cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
//            }
            Map<String, String> map = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
            }
            jedis.hmset(cartKey, map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return cartInfoList;
    }

}