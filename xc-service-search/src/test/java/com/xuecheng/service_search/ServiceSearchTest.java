package com.xuecheng.service_search;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author:zhangl
 * @date:2019/5/14
 * @description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceSearchTest {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RestClient restClient;



    @Test
    public void testDeleteIndex() throws Exception{
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
          //删除索引
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
        //删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    //创建索引库
    @Test
    public void testCreateIndex() throws IOException {
//创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
//设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards",1)
                .put("number_of_replicas",0));
//设置映射
        createIndexRequest.mapping("doc"," {\n" +
                " \t\"properties\": {\n" +
                " \"name\": {\n" +
                " \"type\": \"text\",\n" +
                " \"analyzer\":\"ik_max_word\",\n" +
                " \"search_analyzer\":\"ik_smart\"\n" +
                " },\n" +
                " \"description\": {\n" +
                " \"type\": \"text\",\n" +
                " \"analyzer\":\"ik_max_word\",\n" +
                " \"search_analyzer\":\"ik_smart\"\n" +
                " },\n" +
                " \"studymodel\": {\n" +
                " \"type\": \"keyword\"\n" +
                " },\n" +
                " \"price\": {\n" +
                " \"type\": \"float\"\n" +
                " }\n" +
                " }\n" +
                "}", XContentType.JSON);
//创建索引操作客户端
        IndicesClient indices = client.indices();
//创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
//得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //查询文档
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest(
                "xc_course",
                "doc",
                "4028e581617f945f01617f9dabc40000");
        GetResponse getResponse = client.get(getRequest);
        boolean exists = getResponse.isExists();
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    //查询所有文档
    @Test
    public void testSearchAll() throws IOException{
        SearchRequest searchRequest=new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        /*//全部查询
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());*/
        /*//term精确匹配
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));*/
        /*//匹配关键字
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring 开发").operator(Operator.OR));*/
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架",
                "name", "description").minimumShouldMatch("50%");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //source源过滤,第一个参数表示结果集存在哪些字段，第二个参数表示结果集表示不存在哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse=client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String type = searchHit.getType();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String)sourceAsMap.get("name");
            String studymodel = (String)sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);

        }

    }






}
