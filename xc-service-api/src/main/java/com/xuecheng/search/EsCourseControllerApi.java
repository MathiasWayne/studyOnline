package com.xuecheng.search;


import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;

public interface EsCourseControllerApi {
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam);
}
