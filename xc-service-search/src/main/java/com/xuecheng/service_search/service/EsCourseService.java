package com.xuecheng.service_search.service;

import com.google.common.collect.Lists;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;

import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author:zhangl
 * @date:2019/5/23
 * @description:
 */
@Service
public class EsCourseService {

    private static final Logger LOGGER =LoggerFactory.getLogger(EsCourseService.class);

    @Value("${xuecheng.elasticsearch.course.index}")
    private String es_index;
    @Value("${xuecheng.elasticsearch.course.type}")
    private String es_type;
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_field;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam){
        //设置索引
        SearchRequest searchRequest=new SearchRequest(es_index);
        //设置类型
        searchRequest.types(es_type);
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //创建boolean查询对象
        BoolQueryBuilder booleanQueryBuilder= QueryBuilders.boolQuery();
        //source源字段过滤
        String[] source_fields = source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields,new String[]{});
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            //匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description");
            //设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //提升关键字的boot值
            multiMatchQueryBuilder.field("name",10);
            booleanQueryBuilder.must(multiMatchQueryBuilder);
        }
        //过滤查询
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            booleanQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            booleanQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            booleanQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
       //分页
       if(page<0){
           page=1;
       }
       if(size<0){
           size=20;
       }
      int start=(page-1)*size;
       searchSourceBuilder.from(start);
       searchSourceBuilder.size(size);
       //高亮设置
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
       //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
      searchSourceBuilder.highlighter(highlightBuilder);

        //布尔查询
        searchSourceBuilder.query(booleanQueryBuilder);
        //请求搜索
        searchRequest.source(searchSourceBuilder);
       SearchResponse searchResponse=null;
        try {
            searchResponse=restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            LOGGER.error("xuecheng search error..{}",e.getMessage());
            return new QueryResponseResult(CommonCode.SUCCESS,new QueryResult<CoursePub>());
        }
        //处理结果集
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
       //记录总数
        long totalHits = hits.getTotalHits();
       //数据列表
        List<CoursePub> list= Lists.newArrayList();
        for (SearchHit hit : searchHits) {
            CoursePub coursePub=new CoursePub();
            //取出source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //取出名称
            String name = (String) sourceAsMap.get("name");
            coursePub.setName(name);
            //图片
            String pic = (String) sourceAsMap.get("pic");
            coursePub.setPic(pic);
            //价格
            Float price = null;
            try {
                if(sourceAsMap.get("price")!=null ){
                    price = Float.parseFloat((String) sourceAsMap.get("price"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice(price);
            Float price_old = null;
            try {
                if(sourceAsMap.get("price_old")!=null ){
                    price_old = Float.parseFloat((String) sourceAsMap.get("price_old"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice_old(price_old);
            list.add(coursePub);
        }
        QueryResult<CoursePub> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(totalHits);
        QueryResponseResult<CoursePub> coursePubQueryResponseResult = new
                QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return coursePubQueryResponseResult;
    }
}
