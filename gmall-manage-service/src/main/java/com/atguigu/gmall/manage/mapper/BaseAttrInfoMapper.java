package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {

    /**
     * 根据三级分类id查询平台属性集合
     * @param catalog3Id 三级分类id
     * @return List<BaseAttrInfo>
     */
    List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id);

    /**
     * 平台属性值id查询数据
     * @param valueIds valueIds
     * @return List<BaseAttrInfo>
     */
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String valueIds);
}
