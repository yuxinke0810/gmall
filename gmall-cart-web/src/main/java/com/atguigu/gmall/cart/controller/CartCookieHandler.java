package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.config.CookieUtil;
import com.atguigu.gmall.service.ManageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieHandler {

    //定义购物车名称
    private String cookieCartName = "CART";

    //设置cookie过期时间
    private int COOKIE_CART_MAXAGE = 7*24*3600;

    @Reference
    private ManageService manageService;

    /**
     * 添加购物车
     * 1.查看购物车中是否有该商品
     * 2.true：数量相加
     * 3.false：直接添加
     * @param request request
     * @param response response
     * @param skuId skuId
     * @param userId userId
     * @param skuNum skuNum
     */
    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, int skuNum) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        //从cookie中获取购物车数据
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        //如果没有直接添加到集合 借助一个boolean变量来处理
        boolean ifExist = false;
        //判断cookieValue不能为空
        if (StringUtils.isNotEmpty(cookieValue)) {
            //将cookieValue转换成cartInfo集合
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            //判断是否有该商品
            for (CartInfo cartInfo : cartInfoList) {
                //比较添加商品的id
                if (cartInfo.getSkuId().equals(skuId)) {
                    //有该商品
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
                    //初始化实时价格
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    //将变量更改为true
                    ifExist = true;
                }
            }
        }
        //购物车中没有改商品
        if (!ifExist) {
            //拿到商品
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            //将该商品加入购物车
            CartInfo cartInfo = new CartInfo();
            //属性赋值
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            //放入集合中
            cartInfoList.add(cartInfo);
        }
        //将最终的集合放入cookie中
        CookieUtil.setCookie(request, response, cookieCartName, JSON.toJSONString(cartInfoList), COOKIE_CART_MAXAGE, true);
    }

    /**
     * 从cookie中获取购物车列表
     * @param request request
     * @return List<CartInfo>
     */
    public List<CartInfo> getCartList(HttpServletRequest request) {
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        if (StringUtils.isNotEmpty(cookieValue)) {
            List<CartInfo> cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            return cartInfoList;
        }
        return null;
    }

    /**
     * 删除未登录的购物车
     * @param request request
     * @param response response
     */
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, cookieCartName);
    }

    /**
     *
     * @param request request
     * @param response response
     * @param skuId skuId
     * @param isChecked isChecked
     */
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        //直接将isChecked赋值给购物车集合
        List<CartInfo> cartList = getCartList(request);
        if (cartList != null && cartList.size() >0) {
            for (CartInfo cartInfo : cartList) {
                if (cartInfo.getSkuId().equals(skuId)) {
                    cartInfo.setIsChecked(isChecked);
                }
            }
        }
        //购物车集合重新写入cookie
        CookieUtil.setCookie(request, response, cookieCartName, JSON.toJSONString(cartList), COOKIE_CART_MAXAGE, true);
    }
}
