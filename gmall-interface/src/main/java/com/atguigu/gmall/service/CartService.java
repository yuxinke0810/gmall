package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @param skuId 商品Id
     * @param userId 用户Id
     * @param skuNum 商品数量
     */
    void addToCart(String skuId, String userId, Integer skuNum);

    /**
     * 根据userId查询购物车数据
     * @param userId userId
     * @return List<CartInfo>
     */
    List<CartInfo> getCartList(String userId);

    /**
     * 合并购物车
     * @param cartListCK cartListCK
     * @param userId userId
     * @return List<CartInfo>
     */
    List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId);

    /**
     * 修改商品状态
     * @param skuId skuId
     * @param isChecked isChecked
     * @param userId userId
     */
    void checkCart(String skuId, String isChecked, String userId);
}
