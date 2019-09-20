package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取所有一级分类数据
     *
     * @return List<BaseCatalog1>
     */
    @Override
    public List<BaseCatalog1> getBaseCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    /**
     * 根据一级分类Id查询所有二级分类
     *
     * @param catalog1Id 一级分类Id
     * @return List<BaseCatalog2>
     */
    @Override
    public List<BaseCatalog2> getBaseCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
//        Example example = new Example(BaseCatalog2.class);
//        example.createCriteria().andAllEqualTo(catalog1Id);
//        return baseCatalog2Mapper.selectByExample(example);
    }

    /**
     * 根据二级分类Id查询所有三级分类
     *
     * @param catalog2Id 二级分类Id
     * @return List<BaseCatalog3>
     */
    @Override
    public List<BaseCatalog3> getBaseCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    /**
     * 根据三级分类Id查询平台属性集合
     *
     * @param catalog3Id 三级分类Id
     * @return List<BaseAttrInfo>
     */
    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
//        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
//        baseAttrInfo.setCatalog3Id(catalog3Id);
//        return baseAttrInfoMapper.select(baseAttrInfo);
        return baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id(catalog3Id);
    }

    /**
     * 保存或修改平台属性数据
     *
     * @param baseAttrInfo baseAttrInfo
     */
    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo != null || baseAttrInfo.getId().length() > 0) {
            //修改BaseAttrInfo
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        } else {
            //保存BaseAttrInfo
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        //先情况数据在插入 根据attrId清空数据
        BaseAttrValue attrValue = new BaseAttrValue();
        attrValue.setAttrId(baseAttrInfo.getId());
        int delete = baseAttrValueMapper.delete(attrValue);
        if (delete == 0) {
            throw new RuntimeException("删除失败");
        }
        //保存BaseAttrValue
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList != null && attrValueList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }
    }

    /**
     * 根据平台属性id查询平台属性值集合
     *
     * @param attrId 平台属性id
     * @return BaseAttrInfo
     */
    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        //获取baseAttrInfo对象
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //根据平台属性id查询平台属性值集合
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        //将平台属性值集合放入baseAttrInfo对象
        baseAttrInfo.setAttrValueList(baseAttrValues);
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> spuList(String catalog3Id) {
        return null;
    }

    /**
     * 根据spuInfo对象属性获取spuInfo集合
     *
     * @param spuInfo 对象属性
     * @return List<SpuInfo>
     */
    @Override
    public List<SpuInfo> spuList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    /**
     * 获取所有销售属性数据
     *
     * @return List<BaseSaleAttr>
     */
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    /**
     * 保存spuInfo
     *
     * @param spuInfo spuInfo
     */
    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存数据 spuInfo supImage spuSaleAttr spuSaleAttrValue

        //spuInfo
        spuInfoMapper.insertSelective(spuInfo);

        //supImage
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                //设置spuId
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }

        //spuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                //设置spuId
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                //spuSaleAttrValue
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        //设置spuId
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    /**
     * 根据sup图片对象获取spu图片集合
     *
     * @param spuImage sup图片对象
     * @return List<SpuImage>
     */
    @Override
    public List<SpuImage> spuImageList(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    /**
     * 根据spuId获取销售属性集合
     *
     * @param spuId spuId
     * @return List<SpuSaleAttr>
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    /**
     * 保存skuInfo数据
     *
     * @param skuInfo skuInfo
     */
    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        //skuInfo skuImage skuSaleAttrValue skuAttrValue

        //skuInfo
        skuInfoMapper.insertSelective(skuInfo);

        //skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }

        //skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }

        //skuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }
    }

    /**
     * 根据skuId从redis中查询skuInfo
     *
     * @param skuId skuId
     * @return SkuInfo
     */
    @Override
    public SkuInfo getSkuInfo(String skuId) {
        return getSkuInfoRedisson(skuId);
        //return getSkuInfoJedis(skuId);
    }

    /**
     * 根据skuId从redis中查询skuInfo Redisson锁
     *
     * @param skuId skuId
     * @return SkuInfo
     */
    private SkuInfo getSkuInfoRedisson(String skuId) {
        Jedis jedis = null;
        SkuInfo skuInfo = null;
        RLock lock = null;
        try {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://192.168.187.130:6379");
            RedissonClient redissonClient = Redisson.create(config);
            //使用Redisson调用getLock
            lock = redissonClient.getLock("myLock");
            //加锁
            lock.lock(10, TimeUnit.SECONDS);
            //获取jedis
            jedis = redisUtil.getJedis();
            //定义key
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            //判断redis中是否有key
            if (jedis.exists(skuKey)) {
                //如果有从缓存中获取
                String skuJson = jedis.get(skuKey);
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            } else {
                //如果没有从db获取并将数据放入缓存 并设置过期时间
                skuInfo = getSkuInfoDB(skuId);
                jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (lock != null) {
                //解锁
                lock.unlock();
            }
        }
        return getSkuInfoDB(skuId);
    }

    /**
     * 根据skuId从redis中查询skuInfo Jedis锁
     *
     * @param skuId skuId
     * @return SkuInfo
     */
    private SkuInfo getSkuInfoJedis(String skuId) {
        Jedis jedis = null;
        SkuInfo skuInfo = null;
        try {
            //获取jedis
            jedis = redisUtil.getJedis();
            //定义key
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            //获取数据
            String skuJson = jedis.get(skuKey);
            if (skuJson == null || skuJson.length() == 0) {
                //枷锁
                System.out.println("缓存中没有数据");
                //定义上锁的key
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                //执行set命令
                String lockKey = jedis.set(skuLockKey, "Ok", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals(lockKey)) {
                    //此时枷锁成功
                    skuInfo = getSkuInfoDB(skuId);
                    String skuRedisStr = JSON.toJSONString(skuInfo);
                    jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, skuRedisStr);
                    //删除锁
                    jedis.del(skuLockKey);
                    return skuInfo;
                } else {
                    //等待
                    Thread.sleep(1000);
                    //再次调用
                    return getSkuInfo(skuId);
                }
            } else {
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    /**
     * 根据skuId从数据库中查询skuInfo
     *
     * @param skuId skuId
     * @return SkuInfo
     */
    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        skuInfo.setSkuImageList(getSkuImageBySkuId(skuId));
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        return skuInfo;
    }

    /**
     * 根据skuId获取SkuImage集合
     *
     * @param skuId skuId
     * @return List<SkuImage>
     */
    @Override
    public List<SkuImage> getSkuImageBySkuId(String skuId) {
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        return skuImageMapper.select(skuImage);
    }

    /**
     * 根据skuId，spuId查询销售属性集合
     *
     * @param skuInfo skuInfo
     * @return List<SpuSaleAttr>
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
    }

    /**
     * 根据spuId查询数据
     *
     * @param spuId spuId
     * @return List<SkuSaleAttrValue>
     */
    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    /**
     * 根据spuId查询数据
     *
     * @param spuId spuId
     * @return Map
     */
    @Override
    public Map getSkuValueIdsMap(String spuId) {
        List<Map> mapList = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
        Map<String, String> skuValueIds = new HashMap<>();
        for (Map map : mapList) {
            String skuId = map.get("sku_id") + "";
            String valueIds = (String) map.get("value_ids");
            skuValueIds.put(valueIds, skuId);
        }
        return skuValueIds;
    }

//    /**
//     * 根据平台属性id查询平台属性对象
//     * @param attrId 平台属性id
//     * @return
//     */
//    @Override
//    public List<BaseAttrValue> getAttrValueList(String attrId) {
//        BaseAttrValue baseAttrValue = new BaseAttrValue();
//        baseAttrValue.setAttrId(attrId);
//        return baseAttrValueMapper.select(baseAttrValue);
//    }

//    public SkuInfo getSkuInfoOld(String skuId) {
//        /*
//            redis:五种数据类型使用场景
//            string：短信验证码，存储一个变量
//            hash：json字符串{对象转换的字符串}
//            list：lpush，pop队列使用
//            set：去重，交集，并集，补集，差集 ... 不重复
//            zSet：评分，排序
//         */
//        Jedis jedis = null;
//        SkuInfo skuInfo = null;
//        try {
//            //获取jedis
//            jedis = redisUtil.getJedis();
//            //定义key
//            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
//            //判断redis中是否有key
//            if (jedis.exists(skuKey)) {
//                //如果有从缓存中获取
//                String skuJson = jedis.get(skuKey);
//                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
//                return skuInfo;
//            } else {
//                //如果没有从db获取并将数据放入缓存 并设置过期时间
//                skuInfo = getSkuInfoDB(skuId);
//                jedis.setex(skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
//                return skuInfo;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
//        return getSkuInfoDB(skuId);
//    }

}