package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    public String userKey_prefix = "user:";
    public String userInfoKey_suffix = ":info";
    public int userKey_timeOut = 60*60*24;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(userId);
        return userAddressMapper.select(userAddress);
    }

    /**
     * 登录方法
     * @param userInfo userInfo
     * @return UserInfo
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        String passwd = userInfo.getPasswd();
        String newPwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(newPwd);
        UserInfo selectOne = userInfoMapper.selectOne(userInfo);
        if (selectOne != null) {
            Jedis jedis = null;
            try {
                jedis = redisUtil.getJedis();
                String userKey = userKey_prefix + selectOne.getId() + userInfoKey_suffix;
                jedis.setex(userKey, userKey_timeOut, JSON.toJSONString(selectOne));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
            return selectOne;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis = null;
        //获取jides
        try {
            jedis = redisUtil.getJedis();
            //定义key
            String userKey = userKey_prefix + userId + userInfoKey_suffix;
            String userJson = jedis.get(userKey);
            if (!StringUtils.isEmpty(userJson)) {
                return JSON.parseObject(userJson, UserInfo.class);
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

}
