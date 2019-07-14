package com.xuecheng.manage_course.controller;

import com.xuecheng.course.CourseBaseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.CourseView;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseBaseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author:zhangl
 * @date:2019/4/21
 * @description:
 */
@RestController
@RequestMapping("/course")
public class CourseBaseController implements CourseBaseControllerApi {
    @Autowired
    private CourseBaseServer courseBaseServer;

    @Override
    @PostMapping("/coursebase/add")
    public ResponseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return   courseBaseServer.addCourseBase(courseBase);
    }

    @Override
    @GetMapping("/getCourseBase/{courseId}")
    public CourseBase findCourseBaseById(@PathVariable("courseId") String courseId) {
        return courseBaseServer.findOneById(courseId);
    }

    @Override
    @PostMapping("/updateCoursebase/{courseId}")
    public ResponseResult updateCoursebase(@PathVariable("courseId") String courseId,@RequestBody CourseBase courseBase) {

        return courseBaseServer.updateCoursebase(courseId,courseBase);
    }
    /**
     * @Author zhangl
     * @Description 添加课程图片
     * @Date 21:15 2019/4/26
     * @Param
     * @return
     **/

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseBaseServer.addCoursePic(courseId,pic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findByCourseId(@PathVariable("courseId") String courseId) {
        return courseBaseServer.findByCourseId(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteByCourseId(@RequestParam("courseId") String courseId) {

        return courseBaseServer.deleteByCourseid(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseView(@PathVariable("id") String id) {
        return  courseBaseServer.courseView(id);
    }

}
