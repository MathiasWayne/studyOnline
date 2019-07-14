package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.ZuulService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:zhangl
 * @date:2019/7/14
 * @description:
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    private ZuulService zuulService;
    @Override
    public String filterType() {
        //网关在下发过滤之前
        return "pre";
    }

    @Override
    public int filterOrder() {
        //过滤顺序，数字越小，越先过滤
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //开启过滤
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //过滤器执行的主要过程
        //获取请求
        RequestContext currentContext = RequestContext.getCurrentContext();
        //请求信息
        HttpServletRequest request = currentContext.getRequest();
        //查询cookie中令牌
        String tokenFromCookie = zuulService.getTokenFromCookie(request);
        if(StringUtils.isEmpty(tokenFromCookie)){
            //拒绝访问
            refuse_access();
            return null;
        }
        //查询请求头中的信息
        String jwtFromHeader = zuulService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwtFromHeader)) {
            //拒绝访问
            refuse_access();
            return null;
        }
        //查询redis中的用户信息
        long expire = zuulService.getExpire(tokenFromCookie);
        if(expire<0){
            //拒绝访问
            refuse_access();
            return null;
        }
        return null;
    }




    //拒绝访问
    private void refuse_access(){
        //获取RequestContext
        RequestContext requestContext = RequestContext.getCurrentContext();
        //设置拒绝访问
        requestContext.setSendZuulResponse(false);
        //设置响应内容
        ResponseResult responseResult=new ResponseResult(CommonCode.UNAUTHENTICATED);
        String context = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(context);
        requestContext.setResponseStatusCode(200);
        //设置响应头信息
        HttpServletResponse response = requestContext.getResponse();
        response.setContentType("application/json;charset=UTF-8");

    }
}
