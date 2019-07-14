package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author:zhangl
 * @date:2019/5/9
 * @description:
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {
    String previewUrl;
    public CoursePublishResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);
        this.previewUrl = previewUrl;
    }
}
