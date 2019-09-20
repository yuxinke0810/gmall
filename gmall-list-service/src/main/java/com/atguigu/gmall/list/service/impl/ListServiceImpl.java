package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private JestClient jestClient;

    public static final String BS_INDEX = "gmall";

    public static final String BS_TYPE = "SkuInfo";

    /**
     * 保存数据到es中
     * @param skuLsInfo skuLsInfo
     */
    @Override
    public void saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        //定义动作
        Index index = new Index.Builder(skuLsInfo).index(BS_INDEX).type(BS_TYPE).id(skuLsInfo.getId()).build();
        try {
            //执行动作
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检索数据
     * @param skuLsParams skuLsParams
     * @return SkuLsResult
     */
    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        //定义dsl语句
        String query = makeQueryStringForSearch(skuLsParams);
        //定义动作
        Search search = new Search.Builder(query).addIndex(BS_INDEX).addType(BS_TYPE).build();
        //执行动作
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取结果集
        SkuLsResult skuLsResult = makeResultForSearch(searchResult, skuLsParams);
        return skuLsResult;
    }

    /**
     * 设置返回结果
     * @param searchResult 通过dsl语句查询的结果
     * @param skuLsParams 用户输入的参数
     * @return SkuLsResult
     */
    private SkuLsResult makeResultForSearch(SearchResult searchResult, SkuLsParams skuLsParams) {
        SkuLsResult skuLsResult = new SkuLsResult();
        //给List<SkuLsInfo> skuLsInfoList赋值
        //声明集合存储SkuLsInfo数据
        List<SkuLsInfo> skuLsInfoList = new ArrayList<>();
        //给skuLsInfoList集合赋值
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        //遍历hits
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo skuLsInfo = hit.source;
            //获取skuName的高亮
            if (hit.highlight != null && hit.highlight.size() > 0) {
                Map<String, List<String>> highlight = hit.highlight;
                List<String> list = highlight.get("skuName");
                String skuNameHI = list.get(0);
                skuLsInfo.setSkuName(skuNameHI);
            }
            skuLsInfoList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        //给long total赋值
        Long total = searchResult.getTotal();
        skuLsResult.setTotal(total);
        //给long totalPages赋值
//        long totalPages = 0L;
//        if (total % skuLsParams.getPageSize() == 0) {
//            totalPages = total / skuLsParams.getPageSize();
//        } else {
//            totalPages = (total / skuLsParams.getPageSize()) + 1;
//        }
        long totalPages = (total + skuLsParams.getPageSize() - 1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPages);
        //给List<String> attrValueIdList赋值 获取平台属性值Id
        List<String> attrValueIdList = new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
        for (TermsAggregation.Entry bucket : buckets) {
            String valueId = bucket.getKey();
            attrValueIdList.add(valueId);
        }
        skuLsResult.setAttrValueIdList(attrValueIdList);
        return skuLsResult;
    }

    /**
     * 动态生成dsl语句
     * @param skuLsParams skuLsParams
     * @return String
     */
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //定义查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //创建bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //判断 三级分类Id
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0) {
            //创建term
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            //创建filter，并添加term
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //判断 平台属性值Id
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            //循坏valueId数组
            for (String valueId : skuLsParams.getValueId()) {
                //创建term
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                //创建filter，并添加term
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //判断keyword
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0) {
            //创建match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            //创建must
            boolQueryBuilder.must(matchQueryBuilder);
            //设置高亮
            HighlightBuilder highlighter = searchSourceBuilder.highlighter();
            //设置高亮规则
            highlighter.field("skuName");
            highlighter.preTags("<span style=color:red>");
            highlighter.postTags("</span>");
            //将设置好的高亮规则放入查询器
            searchSourceBuilder.highlight(highlighter);
        }
        //创建query
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页
        //from 从第几条开始查询
        int from = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        //size 每页显示的条数
        searchSourceBuilder.size(skuLsParams.getPageSize());
        //设置排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        //设置聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr");
        groupby_attr.field("skuAttrValueList.valueId");
        //aggs 放入查询器
        SearchSourceBuilder aggregation = searchSourceBuilder.aggregation(groupby_attr);
        String query = searchSourceBuilder.toString();
        System.out.println("query = " + query);
        return query;
    }

}
