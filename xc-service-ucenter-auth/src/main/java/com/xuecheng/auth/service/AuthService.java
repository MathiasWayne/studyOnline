package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author:zhangl
 * @date:2019/7/12
 * @description: 申请令牌 并存入到redis中
 */
@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;

    //保存令牌到redis中
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //申请令牌
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            //TODO 抛出异常
            throw new CustomException(AuthCode.AUTH_CODE_ERROR);
        }
        String access_token = authToken.getAccess_token();
        String cont = JSON.toJSONString(authToken);
        boolean b = saveTORedis(access_token, cont, tokenValiditySeconds);
        if (!b) {
            //TODO 保存数据库 抛出异常
            throw new CustomException(AuthCode.AUTH_SAVE_REDIS);
        }
        return authToken;
    }

    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        if (serviceInstance == null) {
            LOGGER.error("choose an auth instance fail");
            //TODO 抛出异常
            throw new CustomException(AuthCode.AUTH_SERVER_ERROR);
        }
        String authoUril = serviceInstance.getUri() + "/auth/oauth/token";
        //定义body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //授权方式
        formData.add("grant_type", "password");
        //账号
        formData.add("username", username);
        //密码
        formData.add("password", password);
        //定义头
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", httpBasic(clientId, clientSecret));
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        Map map = null;
        try {
            //http请求spring security的申请令牌接口
            ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(authoUril, HttpMethod.POST,
                    new HttpEntity<MultiValueMap<String, String>>(formData, header), Map.class);
            map = mapResponseEntity.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            LOGGER.error("request oauth_token_password error: {}", e.getMessage());
            e.printStackTrace();
            //TODO 抛出异常
            throw new CustomException(AuthCode.AUTH_HTTP_ERROR);
        }
        if (map == null ||
                map.get("access_token") == null ||
                map.get("refresh_token") == null ||
                //jti是jwt令牌的唯一标识作为用户身份令牌
                map.get("jti") == null) {
            //处理用户名和密码不正确异常
            String error_description = (String) map.get("error_description");
            if (StringUtils.isNotEmpty(error_description)) {
                if (error_description.equals("坏的凭证")) {
                    //密码或用户名不正确
                    throw new CustomException(AuthCode.AUTH_CREDENTIAL_ERROR);
                } else if (error_description.indexOf("UserDetailsService returned null") >= 0) {
                    //用户名不存在
                    throw new CustomException(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }
            }
            //TODO 抛出异常
            throw new CustomException(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
        }
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) map.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) map.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) map.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    //获取httpbasic认证串
    private String httpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    //保存数据到redis中
    private boolean saveTORedis(String access_token, String conent, long ttl) {
        //令牌名称
        String name = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(name).set(conent, ttl, TimeUnit.SECONDS);
        //获取过期时间
        Long expire = stringRedisTemplate.getExpire(name);
        return expire > 0;
    }

    //根据cookie中保存的tooken获取redis中用户信息
    public AuthToken getUserToken(String token) {
        String autToken = "user_token:" + token;
        String s = stringRedisTemplate.opsForValue().get(autToken);
        if (StringUtils.isNotBlank(s)) {
            AuthToken authTokenJson;
            try {
                 authTokenJson = JSON.parseObject(s, AuthToken.class);
            }catch (Exception e){
                LOGGER.error("autToken令牌json解析异常:",e);
                return null;
            }
            return authTokenJson;
        }
        return null;
    }
    //从redis中删除令牌
    public boolean delToken(String access_token){
        String name = "user_token:" + access_token;
        stringRedisTemplate.delete(name);
        return true;
    }
}
