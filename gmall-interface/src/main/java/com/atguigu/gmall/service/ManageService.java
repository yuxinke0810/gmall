package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {

    /**
     * 获取所有一级分类数据
     * @return
     */
    List<BaseCatalog1> getBaseCatalog1();

    /**
     * 根据一级分类Id查询所有二级分类
     * @param catalog1Id 一级分类Id
     * @return
     */
    List<BaseCatalog2> getBaseCatalog2(String catalog1Id);

    /**
     * 根据二级分类Id查询所有三级分类
     * @param catalog2Id 二级分类Id
     * @return
     */
    List<BaseCatalog3> getBaseCatalog3(String catalog2Id);

    /**
     * 根据三级分类Id查询平台属性集合
     * @param catalog3Id 三级分类Id
     * @return
     */
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     * 保存或修改平台属性数据
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性id查询平台属性值集合
     * @param attrId 平台属性id
     * @return
     */
//    List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     * 根据平台属性id查询平台属性对象
     * @param attrId 平台属性id
     * @return
     */
    BaseAttrInfo getAttrInfo(String attrId);

    /**
     * 根据三级分类id获取spuInfo集合
     * @param catalog3Id
     * @return
     */
    List<SpuInfo> spuList(String catalog3Id);

    /**
     * 根据spuInfo对象属性获取spuInfo集合
     * @param spuInfo 对象属性
     * @return
     */
    List<SpuInfo> spuList(SpuInfo spuInfo);

    /**
     * 获取所有销售属性数据
     * @return
     */
    List<BaseSaleAttr> baseSaleAttrList();

    /**
     * 保存spuInfo
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);
}
