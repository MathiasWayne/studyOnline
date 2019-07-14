package com.xuecheng.manage_cms.controller;

import com.xuecheng.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:zhangl
 * @date:2019/4/13
 * @description:
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    private CmsConfigService service;

    @Override
    @GetMapping("/getModel/{id}")
    public CmsConfig getModel(@PathVariable("id") String id) {
        CmsConfig model = service.getModel(id);
        return model;
    }



}
