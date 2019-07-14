package com.xuecheng.manage_course.controller;

import com.xuecheng.course.CourseMarketInfoControllerApi;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseMarketInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author:zhangl
 * @date:2019/4/22
 * @description:
 */
@RestController
@RequestMapping("/course")
public class CourseMarketinfoController implements CourseMarketInfoControllerApi {
   @Autowired
   private CourseMarketInfoService service;
    @Override
    @GetMapping("/marketinfo/oneById/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            throw new CustomException(CourseCode.COURSE_ISNOTEXITENCE);
        }
        return service.findByCourseId(courseId);
    }

    @Override
    @PostMapping("/marketinfo/updateOrSave/{courseId}")
    public ResponseResult updateCourseMarket(@PathVariable("courseId") String courseId, @RequestBody CourseMarket courseMarket) {
        return service.updateCourseMarket(courseId,courseMarket);
    }
}
