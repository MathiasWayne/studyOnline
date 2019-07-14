package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;

/**
 * @author:zhangl
 * @date:2019/7/13
 * @description:
 */
@Api(value = "用户中心",description = "用户中心管理")
public interface UcenterControllerApi {
     XcUserExt getUserext(String username);
}
