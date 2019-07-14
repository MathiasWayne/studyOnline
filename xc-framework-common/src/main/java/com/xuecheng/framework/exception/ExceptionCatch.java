package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author:zhangl
 * @date:2019/4/10
 * @description: 异常捕获类  @ControllerAdvice   controllerAdvice增强类,当存在异常时，此controller捕获异常
 */
@ControllerAdvice
public class ExceptionCatch {

    private static final Logger LOGGER= LoggerFactory.getLogger(ExceptionCatch.class);
    //ImmutableMap map集合只读，线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder=ImmutableMap.builder();

    static {
        //初始化集合
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
        builder.put(HttpRequestMethodNotSupportedException.class,CommonCode.REQUEST_ERROR);
    }
  /**
  *
   * @Author zhangl
   * @Description 自定义错误
   * @Date 0:07 2019/4/11
   * @Param [customException]
   * @return com.xuecheng.framework.model.response.ResponseResult
   **/

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        LOGGER.info("错误提示:"+customException.getMessage(),customException);
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }
    /**
    *
     * @Author zhangl
     * @Description 不可预计错误
     * @Date 0:06 2019/4/11
     * @Param []
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e){
      LOGGER.info("错误信息提示"+e.getMessage(),e);
        if (EXCEPTIONS==null) {
            EXCEPTIONS=builder.build();
        }
        //从ImmutableMap中取出对应的异常信息
        ResultCode resultCode = EXCEPTIONS.get(e.getClass());
        ResponseResult responseResult;
        if (resultCode!=null) {
            responseResult=new ResponseResult(resultCode);
        }else{
            responseResult=new ResponseResult(CommonCode.SERVER_ERROR);
        }
        return responseResult;
    }
}
