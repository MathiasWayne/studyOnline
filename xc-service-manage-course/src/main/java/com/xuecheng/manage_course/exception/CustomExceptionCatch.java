package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author:zhangl
 * @date:2019/7/14
 * @description:
 */
@ControllerAdvice
public class CustomExceptionCatch extends ExceptionCatch {
    static{
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
