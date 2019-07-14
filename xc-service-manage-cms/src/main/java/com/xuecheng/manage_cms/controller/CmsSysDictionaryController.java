package com.xuecheng.manage_cms.controller;

import com.xuecheng.cms.CmsSysDictionaryControllerApi;
import com.xuecheng.cmsSite.CmsSiteControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsSysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:zhangl
 * @date:2019/4/21
 * @description:
 */
@RestController
@RequestMapping("/sys")
public class CmsSysDictionaryController implements CmsSysDictionaryControllerApi {
@Autowired
private CmsSysDictionaryService cmsSysDictionaryService;

    @GetMapping("/dictionary/get/{dType}")
    @Override
    public SysDictionary findSysDictionaryByType(@PathVariable("dType") String type) {
        return cmsSysDictionaryService.findByType(type);
    }
}
