package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeachplanMapper {
    //跟课程id查询课程
    TeachplanNode findTeachplanNode(String courseId);
}
