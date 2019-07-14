package com.xuecheng.auth.controller;

import com.xuecheng.auth.service.AuthService;
import com.xuecheng.autho.AuthControllerApi;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author:zhangl
 * @date:2019/7/12
 * @description:
 */
@RestController
public class AuthController implements AuthControllerApi {
    @Autowired
    private AuthService authService;
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;
    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
      //检验账是否输入
        if(loginRequest==null|| StringUtils.isEmpty(loginRequest.getUsername())){
          //TODO 抛出异常用户名为空
            throw new CustomException(AuthCode.AUTH_USERNAME_NONE);
        }
      //校验密码是否输入
        if (StringUtils.isEmpty( loginRequest.getPassword())) {
            //TODO 抛出异常密码未输入
            throw new CustomException(AuthCode.AUTH_PASSWORD_NONE);
        }
        AuthToken login = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), clientId, clientSecret);
        //将令牌写入cookie
        String access_token = login.getAccess_token();
        saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        String cookie = getTokenFormCookie();
        //清除redis缓存
        authService.delToken(cookie);
        //退出登录，清除cookie
        clearCookie(cookie);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {

        //从cookie中获取到token
        String token = getTokenFormCookie();
        if(StringUtils.isBlank(token)){
            return new JwtResult(CommonCode.FAIL,null);
        }
        //根据cookie中的token获取redis中的用户信息
        AuthToken userToken = authService.getUserToken(token);
        //返回用户信息至前端
        if(userToken==null){
            return new JwtResult(CommonCode.FAIL,null);
        }
        return new JwtResult(CommonCode.SUCCESS,userToken.getJwt_token());
    }

    private String getTokenFormCookie(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> cookie = CookieUtil.readCookie(request, "uid");
        String token = cookie.get("uid");
        return token;
    }
    private void saveCookie(String token){
     //获取httpResponse
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }
    private void clearCookie(String cookie){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", cookie, 0, false);
    }
}
