package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

    /**
     * 获取所有一级分类数据
     *
     * @return List<BaseCatalog1>
     */
    @PostMapping("/getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getBaseCatalog1();
    }

    /**
     * 根据一级分类Id查询所有二级分类
     *
     * @param catalog1Id 一级分类Id
     * @return List<BaseCatalog2>
     */
    @PostMapping("/getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        return manageService.getBaseCatalog2(catalog1Id);
    }

    /**
     * 根据二级分类Id查询所有三级分类
     *
     * @param catalog2Id 二级分类Id
     * @return List<BaseCatalog3>
     */
    @PostMapping("/getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        return manageService.getBaseCatalog3(catalog2Id);
    }

    /**
     * 根据三级分类Id查询平台属性集合
     *
     * @param catalog3Id 三级分类Id
     * @return List<BaseAttrInfo>
     */
    @GetMapping("/attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id) {
        return manageService.getAttrList(catalog3Id);
    }

    /**
     * 保存或修改平台属性数据
     *
     * @param baseAttrInfo 平台属性对象
     * @return String
     */
    @PostMapping("/saveAttrInfo")
    public String saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
        return "success";
    }

//    /**
//     * 根据平台属性id查询平台属性值集合
//     * @param attrId 平台属性id
//     * @return
//     */
//    @PostMapping("/getAttrValueList")
//    public List<BaseAttrValue> getAttrValueList(String attrId){
//        return manageService.getAttrValueList(attrId);
//    }

    /**
     * 根据平台属性id查询平台属性对象
     *
     * @param attrId 平台属性id
     * @return List<BaseAttrValue>
     */
    @PostMapping("/getAttrValueList")
    private List<BaseAttrValue> getAttrValueList(String attrId) {
        //先通过attrId查询平台属性
        BaseAttrInfo baseAttrInfo = manageService.getAttrInfo(attrId);
        //返回平台属性中的属性集合
        return baseAttrInfo.getAttrValueList();
    }

    /**
     * 获取所有销售属性数据
     * @return List<BaseSaleAttr>
     */
    @PostMapping("/baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList() {
        return manageService.baseSaleAttrList();
    }

}