package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.cms.response.CourseCode;
import com.xuecheng.framework.domain.constant.Constant;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author:zhangl
 * @date:2019/4/19
 * @description:
 */
@Service
public class TeachplanServer {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanRepository teachplanRepository;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    public TeachplanNode findTeachplanList(String courseId){
        return  teachplanMapper.findTeachplanNode(courseId);
    }

    //添加课程
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan){
        //校验课程id和课程名称
        if (teachplan==null|| StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            throw new CustomException(CourseCode.Teachplan_ISEmpty);
        }

        //没有选目录层级
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            //如果父节点为空则获取上一级的id
            parentid = this.getTeachplanParentId(teachplan.getCourseid());

        }
        //挂载课程到父节点上
        Optional<Teachplan> parentTeachplan = teachplanRepository.findById(parentid);
        if (!parentTeachplan.isPresent()) {
            throw new CustomException(CourseCode.Teachplan_ISEmpty);
        }
        Teachplan parentplan = parentTeachplan.get();
        teachplan.setParentid(parentplan.getId());
        teachplan.setStatus(Constant.NUMBER_0);
        String grade = parentplan.getGrade();
        if (Constant.NUMBER_1.equals(grade)) {
            teachplan.setGrade(Constant.NUMBER_2);
        }else if(Constant.NUMBER_2.equals(grade)){
            teachplan.setGrade(Constant.NUMBER_3);
        }
        teachplan.setCourseid(parentplan.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    private String getTeachplanParentId(String courseId){
        //根据当前课程id判断当前课程是否存在
        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (!courseBase.isPresent()) {
            return  null;
        }
        CourseBase courseBase1 = courseBase.get();
        List<Teachplan> teachplans = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplans==null || teachplans.size()==0) {
            //课程存在，一级不存在，添加根目录
            Teachplan teachplan=new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setGrade(Constant.NUMBER_1);
            teachplan.setStatus(Constant.NUMBER_0);
            teachplan.setParentid(Constant.NUMBER_0);
            teachplan.setPname(courseBase1.getName());
            //保存计划
            Teachplan save = teachplanRepository.save(teachplan);
            return save.getId();
        }
        //根目录存在，返回根目录的parentid
        Teachplan teachplan = teachplans.get(0);
        return teachplan.getId();
    }
}
