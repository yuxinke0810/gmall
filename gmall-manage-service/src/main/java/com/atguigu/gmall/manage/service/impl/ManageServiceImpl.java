package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    /**
     * 获取所有一级分类数据
     * @return
     */
    @Override
    public List<BaseCatalog1> getBaseCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    /**
     * 根据一级分类Id查询所有二级分类
     * @param catalog1Id 一级分类Id
     * @return
     */
    @Override
    public List<BaseCatalog2> getBaseCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
//        Example example = new Example(BaseCatalog2.class);
//        example.createCriteria().andAllEqualTo(catalog1Id);
//        return baseCatalog2Mapper.selectByExample(example);
    }

    /**
     * 根据二级分类Id查询所有三级分类
     * @param catalog2Id 二级分类Id
     * @return
     */
    @Override
    public List<BaseCatalog3> getBaseCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    /**
     * 根据三级分类Id查询平台属性集合
     * @param catalog3Id 三级分类Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    /**
     * 保存或修改平台属性数据
     * @param baseAttrInfo
     */
    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo != null || baseAttrInfo.getId().length() > 0) {
            //修改BaseAttrInfo
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        } else {
            //保存BaseAttrInfo
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        //先情况数据在插入 根据attrId清空数据
        BaseAttrValue attrValue = new BaseAttrValue();
        attrValue.setAttrId(baseAttrInfo.getId());
        int delete = baseAttrValueMapper.delete(attrValue);
        if (delete == 0) {
            throw new RuntimeException("删除失败");
        }
        //保存BaseAttrValue
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList != null && attrValueList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }
    }

    /**
     * 根据平台属性id查询平台属性值集合
     * @param attrId 平台属性id
     * @return
     */
    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        //获取baseAttrInfo对象
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //根据平台属性id查询平台属性值集合
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        //将平台属性值集合放入baseAttrInfo对象
        baseAttrInfo.setAttrValueList(baseAttrValues);
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> spuList(String catalog3Id) {
        return null;
    }

    /**
     * 根据spuInfo对象属性获取spuInfo集合
     * @param spuInfo 对象属性
     * @return
     */
    @Override
    public List<SpuInfo> spuList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存数据 spuInfo supImage spuSaleAttr spuSaleAttrValue

        //spuInfo
        spuInfoMapper.insertSelective(spuInfo);

        //supImage
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                //设置spuId
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }

        //spuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                //设置spuId
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                //spuSaleAttrValue
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        //设置spuId
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    /**
     * 根据平台属性id查询平台属性对象
     * @param attrId 平台属性id
     * @return
     */
//    @Override
//    public List<BaseAttrValue> getAttrValueList(String attrId) {
//        BaseAttrValue baseAttrValue = new BaseAttrValue();
//        baseAttrValue.setAttrId(attrId);
//        return baseAttrValueMapper.select(baseAttrValue);
//    }

}
