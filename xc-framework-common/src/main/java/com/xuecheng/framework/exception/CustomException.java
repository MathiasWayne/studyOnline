package com.xuecheng.framework.exception;


import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author:zhangl
 * @date:2019/4/10
 * @description: 自定义异常信息类
 */
public class CustomException extends RuntimeException {

    ResultCode resultCode;
    public CustomException(ResultCode resultCode){
        this.resultCode=resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
