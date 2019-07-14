package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author:zhangl
 * @date:2019/7/11
 * @description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJWt {
    //客户端负载均衡
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;
    //申请令牌测试
    @Test
    public void testjwt(){
      //采用客户端负载均衡从eureka获取认证的服务ip和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        //认证请求的路径
        String authoPath=uri+"/auth/oauth/token";
       // String authoPath="http://127.0.0.1:40400/auth/oauth/token";


        //请求的内容分为两部分，head和body
        MultiValueMap<String,String> heards=new LinkedMultiValueMap<>();
        String httpBasic=httpbasic("XcWebApp","XcWebApp");
        heards.add("Authorization",httpBasic);
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String,String> body=new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity=
                new HttpEntity<>(body,heards);

        //reatTemple处理400和401异常
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode()!=400&&response.getRawStatusCode()!=401) {
                    super.handleError(response);
                }

            }
        });
        ResponseEntity<Map> response = restTemplate.exchange(authoPath, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
        Map body1 = response.getBody();
        System.out.println(body1);

    }

    private String httpbasic(String clientId,String clientSecret){
         //将客户端id和客户端密码拼接，按“客户端id:客户端密码”,按base64编码
         String  string=clientId+":"+clientSecret;

        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Base " +new String(encode);
    }

}
