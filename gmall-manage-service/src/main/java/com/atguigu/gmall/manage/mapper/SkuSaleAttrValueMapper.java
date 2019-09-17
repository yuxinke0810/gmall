package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {

    /**
     * 根据spuId查询数据
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 根据spuId查询数据
     * @param spuId
     * @return
     */
    List<Map> getSaleAttrValuesBySpu(String spuId);

}
