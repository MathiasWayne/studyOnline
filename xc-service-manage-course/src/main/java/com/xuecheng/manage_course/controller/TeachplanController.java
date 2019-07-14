package com.xuecheng.manage_course.controller;

import com.xuecheng.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseBaseServer;
import com.xuecheng.manage_course.service.TeachplanServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author:zhangl
 * @date:2019/4/19
 * @description:
 */
@RestController
@RequestMapping("/course")
public class TeachplanController implements CourseControllerApi {
    @Autowired
    private TeachplanServer teachplanServer;
    @Autowired
    private CourseBaseServer courseBaseServer;
    @GetMapping("/teachplan/list/{courseId}")
    @Override
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId){
        return  teachplanServer.findTeachplanList(courseId);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody  Teachplan teachplan) {

        return teachplanServer.addTeachplan(teachplan);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return courseBaseServer.findCoursePageList(page, size, courseListRequest);
    }
}
