package com.xuecheng.framework.domain.course;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author:zhangl
 * @date:2019/4/29
 * @description:
 */
@Data
@ToString
@NoArgsConstructor
public class CourseView implements Serializable {
   CourseBase courseBase;
   CourseMarket courseMarket;
   CoursePic coursePic;
   TeachplanNode teachplanNode;
}
