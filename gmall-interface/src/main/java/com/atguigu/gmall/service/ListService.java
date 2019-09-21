package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

public interface ListService {

    /**
     * 保存数据到es中
     * @param skuLsInfo skuLsInfo
     */
    void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    /**
     * 检索数据
     * @param skuLsParams skuLsParams
     * @return SkuLsResult
     */
    SkuLsResult search(SkuLsParams skuLsParams);

    /**
     * 记录每个商品被访问的次数
     * @param skuId
     */
    void incrHotScore(String skuId);

}
