package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum CourseCode implements ResultCode {
    Teachplan_ISEmpty(false,100000,"课程计划为空");
   boolean success;
   int code;
   String message;

    CourseCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int code() {
        return 0;
    }

    @Override
    public String message() {
        return null;
    }
}
