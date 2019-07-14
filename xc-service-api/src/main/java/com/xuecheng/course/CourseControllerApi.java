package com.xuecheng.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;

import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @Author zhangl
 * @Description  课程管理api
 * @Date 23:31 2019/4/18
 * @Param
 * @return
 **/

public interface CourseControllerApi {
    //根据课程id查询整个课程
    TeachplanNode findTeachplanList(String courseId);
    //添加课程计划
    ResponseResult addTeachplan(Teachplan teachplan);
    //查询我的课程
    QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);
}
