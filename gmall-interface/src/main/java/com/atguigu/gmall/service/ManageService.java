package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;
import java.util.Map;

public interface ManageService {

    /**
     * 获取所有一级分类数据
     * @return List<BaseCatalog1>
     */
    List<BaseCatalog1> getBaseCatalog1();

    /**
     * 根据一级分类Id查询所有二级分类
     * @param catalog1Id 一级分类Id
     * @return List<BaseCatalog2>
     */
    List<BaseCatalog2> getBaseCatalog2(String catalog1Id);

    /**
     * 根据二级分类Id查询所有三级分类
     * @param catalog2Id 二级分类Id
     * @return List<BaseCatalog3>
     */
    List<BaseCatalog3> getBaseCatalog3(String catalog2Id);

    /**
     * 根据三级分类Id查询平台属性集合
     * @param catalog3Id 三级分类Id
     * @return List<BaseAttrInfo>
     */
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     * 保存或修改平台属性数据
     * @param baseAttrInfo baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

//    /**
//     * 根据平台属性id查询平台属性值集合
//     * @param attrId 平台属性id
//     * @return
//     */
//    List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     * 根据平台属性id查询平台属性对象
     * @param attrId 平台属性id
     * @return BaseAttrInfo
     */
    BaseAttrInfo getAttrInfo(String attrId);

    /**
     * 根据三级分类id获取spuInfo集合
     * @param catalog3Id catalog3Id
     * @return List<SpuInfo>
     */
    List<SpuInfo> spuList(String catalog3Id);

    /**
     * 根据spuInfo对象属性获取spuInfo集合
     * @param spuInfo 对象属性
     * @return List<SpuInfo>
     */
    List<SpuInfo> spuList(SpuInfo spuInfo);

    /**
     * 获取所有销售属性数据
     * @return List<BaseSaleAttr>
     */
    List<BaseSaleAttr> baseSaleAttrList();

    /**
     * 保存spuInfo
     * @param spuInfo spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据sup图片对象获取spu图片集合
     * @param spuImage sup图片对象
     * @return List<SpuImage>
     */
    List<SpuImage> spuImageList(SpuImage spuImage);

    /**
     * 根据spuId获取销售属性集合
     * @param spuId spuId
     * @return List<SpuSaleAttr>
     */
    List<SpuSaleAttr> spuSaleAttrList(String spuId);

    /**
     * 保存skuInfo数据
     * @param skuInfo skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据skuId查询skuInfo
     * @param skuId skuId
     * @return SkuInfo
     */
    SkuInfo getSkuInfo(String skuId);

    /**
     * 根据skuId获取SkuImage集合
     * @param skuId skuId
     * @return List<SkuImage>
     */
    List<SkuImage> getSkuImageBySkuId(String skuId);

    /**
     * 根据skuId，spuId查询销售属性集合
     * @param skuInfo skuInfo
     * @return List<SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     * 根据spuId查询销售属性值集合
     * @param spuId spuId
     * @return List<SkuSaleAttrValue>
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 根据spuId查询销售属性值集合
     * @param spuId spuId
     * @return Map
     */
    Map getSkuValueIdsMap(String spuId);

}
