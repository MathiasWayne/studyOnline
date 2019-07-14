package com.xuecheng.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.CourseView;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseBaseControllerApi {
    ResponseResult addCourseBase(CourseBase courseBase);
    CourseBase findCourseBaseById(String id);
    ResponseResult updateCoursebase(String courseId,CourseBase courseBase);
    //添加课程图片
     ResponseResult addCoursePic(String courseId,String picId);
     //根据课程id查询课程
    CoursePic findByCourseId(String courseId);
    //根据课程id删除课程
    ResponseResult deleteByCourseId(String courseId);
    //课程详情
    CourseView courseView(String courseId);
}
