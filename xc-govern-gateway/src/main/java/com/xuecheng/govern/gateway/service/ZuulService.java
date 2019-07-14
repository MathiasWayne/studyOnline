package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author:zhangl
 * @date:2019/7/14
 * @description:
 */
@Service
public class ZuulService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**1、从cookie查询用户身份令牌是否存在，不存在则拒绝访问
    2、从http header查询jwt令牌是否存在，不存在则拒绝访问
    3、从Redis查询user_token令牌是否过期，过期则拒绝访问*/
    //从cookie中获取token信息
    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> cookieToken = CookieUtil.readCookie(request, "uid");
        String access_token = cookieToken.get("uid");
        if(StringUtils.isEmpty(access_token)){
            return null;
        }
        return access_token;
    }
    //从请求头中获取token信息
    public String getJwtFromHeader(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            //拒绝访问
            return null;
        }
        if(!authorization.startsWith("Bearer ")){
            //拒绝访问
            return null;
        }
        return authorization;
    }
    //从redis中获取token信息
   public long getExpire(String access_token){
        String key="user_token:"+access_token;
       Long expire = redisTemplate.getExpire(key);
       return expire;
   }
}
