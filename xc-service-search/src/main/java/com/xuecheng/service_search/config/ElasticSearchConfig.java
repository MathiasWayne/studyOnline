package com.xuecheng.service_search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:zhangl
 * @date:2019/5/14
 * @description:
 */
@Configuration
public class ElasticSearchConfig {
    @Value("${xuecheng.elasticsearch.hostlist}")
    private String hostlist;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        //解析host连接地址
        String[] host = hostlist.split(",");
        //创建httphost数组，存放host地址
        HttpHost[] httpHosts=new HttpHost[host.length];
        for (int i = 0; i < host.length; i++) {
            String item=host[i];
            httpHosts[i]=new HttpHost(item.split(":")[0],Integer.parseInt(item.split(":")[1]),"http");
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    @Bean
    public RestClient restClient(){
        String[] host = hostlist.split(",");
        //创建httphost数组，存放host地址
        HttpHost[] httpHosts=new HttpHost[host.length];
        for (int i = 0; i < host.length; i++) {
            String item=host[i];
            httpHosts[i]=new HttpHost(item.split(":")[0],Integer.parseInt(item.split(":")[1]),"http");
        }
        return RestClient.builder(httpHosts).build();
    }
}
