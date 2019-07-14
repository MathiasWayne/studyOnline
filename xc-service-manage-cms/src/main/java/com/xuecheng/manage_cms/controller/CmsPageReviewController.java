package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletOutputStream;

/**
 * @author:zhangl
 * @date:2019/4/14
 * @description:
 */
@Controller
public class CmsPageReviewController extends BaseController {
    @Autowired
    private PageService service;

    @GetMapping("/cms/preview/{id}")
    public void review(@PathVariable("id") String id){
        String html = service.getHtml(id);
        if (StringUtils.isEmpty(html)) {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(html.getBytes("utf-8"));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
