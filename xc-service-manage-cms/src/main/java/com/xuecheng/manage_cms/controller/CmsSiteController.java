package com.xuecheng.manage_cms.controller;

import com.xuecheng.cmsSite.CmsSiteControllerApi;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsSiteService;
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
@RequestMapping("/cms/site")
public class CmsSiteController implements CmsSiteControllerApi {
    @Autowired
    private CmsSiteService siteService;
    @GetMapping("/list")
    @Override
    public QueryResponseResult findAllList(){
      return   siteService.findAllList();
    }
}
