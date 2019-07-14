package com.xuecheng.course;

import com.xuecheng.framework.domain.course.Category;
import com.xuecheng.framework.domain.course.ext.CategoryNode;

import java.util.List;

/**
 * @Author zhangl
 * @Description  课程分类
 * @Date 17:07 2019/4/21
 * @Param
 * @return
 **/

public interface CourseCategoryControllerApi {
    //查询所有课程分类
    CategoryNode findCategoryList();
}
