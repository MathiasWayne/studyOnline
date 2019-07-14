package com.xuecheng.manage_course.controller;

import com.xuecheng.course.CourseCategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CourseBaseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:zhangl
 * @date:2019/4/21
 * @description:
 */
@RestController
@RequestMapping("/category")
public class CourseCategoryController implements CourseCategoryControllerApi {
    @Autowired
    private CourseBaseServer courseBaseServer;
    @GetMapping("/list")
    @Override
    public CategoryNode findCategoryList(){
      return   courseBaseServer.findCategoryList();
    }
}
