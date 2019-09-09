package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseCatalog1;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;

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
     * 保存平台属性数据
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
