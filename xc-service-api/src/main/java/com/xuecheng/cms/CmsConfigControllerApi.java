package com.xuecheng.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;

public interface CmsConfigControllerApi {
    //根据配置id查询
    CmsConfig getModel(String id);
}
