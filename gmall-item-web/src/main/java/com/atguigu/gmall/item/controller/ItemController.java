package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    @RequestMapping("/{skuId}.html")
    //@LoginRequire
    public String item(@PathVariable String skuId, HttpServletRequest request){
        //根据skuId获取数据
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        //显示图片列表
//        List<SkuImage> skuImageList = manageService.getSkuImageBySkuId(skuId);
        //查询销售属性，销售属性值集合
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
//============================================================================================================
        //方法一
        //获取销售属性值Id集合
//        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //将数据放入Map中，然后将Map转换成json
//        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
//            //当本次循环的skuId与下次循环的skuId不一致时停止，拼接到最后是停止拼接
//        }
//        String key = "";
//        Map<String, Object> map = new HashMap<>();
//        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
//            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
//            //当本次循环的skuId与下次循环的skuId不一致时停止，拼接到最后是停止拼接
//            //第一次拼接 key=118
//            //第二次拼接 key=118|
//            //第三次拼接 key=118|120放入map中，并清空key
//            //第四次拼接 key=119
//            if(key.length() > 0){
//                key += "|";
//            }
//            key += skuSaleAttrValue.getSaleAttrValueId();
//            if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {
//                //放入map集合
//                map.put(key, skuSaleAttrValue.getSkuId());
//                //清空key
//                key = "";
//            }
//        }
//        //将map转换为json
//        String valuesSkuJson = JSON.toJSONString(map);
//        System.out.println("valuesSkuJson = " + valuesSkuJson);
//        request.setAttribute("valuesSkuJson", valuesSkuJson);
//============================================================================================================
        //方法二
        Map skuValueIdsMap = manageService.getSkuValueIdsMap(skuInfo.getSpuId());
        String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
        request.setAttribute("valuesSkuJson", valuesSkuJson);
//============================================================================================================
        //将销售属性，销售属性值集合放入作用域中
        request.setAttribute("spuSaleAttrList", spuSaleAttrList);
        //保存到作用域
        request.setAttribute("skuInfo", skuInfo);
        //将图片保存到作用域中
//        request.setAttribute("skuImageList", skuImageList);
        listService.incrHotScore(skuId);
        return "item";
    }

}
