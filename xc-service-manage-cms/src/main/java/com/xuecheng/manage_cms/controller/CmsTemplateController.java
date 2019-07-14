package com.xuecheng.manage_cms.controller;

import com.xuecheng.cmsTemplate.CmsTemplateControllerApi;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:zhangl
 * @date:2019/4/8
 * @description:
 */
@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {
    @Autowired
    private CmsTemplateService cmsTemplateService;
    @Override
    @GetMapping("/list")
    public QueryResponseResult findAll() {
        return cmsTemplateService.templateList();
    }
}
