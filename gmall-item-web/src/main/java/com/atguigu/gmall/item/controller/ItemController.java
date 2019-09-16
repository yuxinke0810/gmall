package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable String skuId, HttpServletRequest request){
        //根据skuId获取数据
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        //显示图片列表
//        List<SkuImage> skuImageList = manageService.getSkuImageBySkuId(skuId);
        //查询销售属性，销售属性值集合
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        //保存到作用域
        request.setAttribute("skuInfo", skuInfo);
        //将图片保存到作用域中
//        request.setAttribute("skuImageList", skuImageList);
        //将销售属性，销售属性值集合放入作用域中
        request.setAttribute("spuSaleAttrList", spuSaleAttrList);
        return "item";
    }

}
