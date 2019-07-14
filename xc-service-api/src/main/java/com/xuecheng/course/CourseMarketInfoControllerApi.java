package com.xuecheng.course;

import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseMarketInfoControllerApi {
    CourseMarket getCourseMarketById(String courseId);

    //跟新营销
    ResponseResult updateCourseMarket(String courseId,CourseMarket courseMarket);
}
