package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    private ManageService manageService;

    @GetMapping("/spuImageList")
    public List<SpuImage> spuImageList(SpuImage spuImage){
        return manageService.spuImageList(spuImage);
    }

    @GetMapping("/spuSaleAttrList")
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){
        return manageService.spuSaleAttrList(spuId);
    }

    @PostMapping("/saveSkuInfo")
    public String saveSkuInfo(@RequestBody SkuInfo skuInfo){
        if (skuInfo != null) {
            manageService.saveSkuInfo(skuInfo);
        }
        return "success";
    }

}
