package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.Response;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseMarketInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author:zhangl
 * @date:2019/4/22
 * @description:
 */
@Service
public class CourseMarketInfoService {
    @Autowired
    private CourseMarketInfoRepository courseMarketInfoRepository;

    public CourseMarket findByCourseId(String courseId){
        Optional<CourseMarket> one = courseMarketInfoRepository.findById(courseId);
        if (!one.isPresent()) {
            return null;
        }
        return one.get();
    }
    //更新或者保存
    public ResponseResult updateCourseMarket(String courseId,CourseMarket courseMarket){
        CourseMarket marketInfo = this.findByCourseId(courseId);
        if (marketInfo==null) {
            //没有当前课程营销信息
            courseMarketInfoRepository.save(courseMarket);
        }else {
            courseMarket.setId(courseId);
            courseMarketInfoRepository.save(courseMarket);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
