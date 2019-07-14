package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.CourseView;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author:zhangl
 * @date:2019/4/21
 * @description:
 */
@Service
public class CourseBaseServer {
    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseCategoryMapper categoryMapper;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private CoursePicRepository coursePicRepository;
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseMarketInfoRepository courseMarketInfoRepository;
    @Autowired
    private CmsPageClient cmsPageClient;
    public QueryResponseResult<CourseInfo> findCoursePageList(int page, int size, CourseListRequest courseListRequest) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 10;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> coursePageList = courseMapper.findCoursePageList();
        long total = coursePageList.getTotal();
        List<CourseInfo> courseInfos = coursePageList.getResult();
        QueryResult result = new QueryResult();
        result.setList(courseInfos);
        result.setTotal(total);
        return new QueryResponseResult<>(CommonCode.SUCCESS, result);
    }


    //课程分类查询
    public CategoryNode findCategoryList() {
        return categoryMapper.findCategoryList();
    }

    //保存课程
    @Transactional
    public ResponseResult addCourseBase(CourseBase courseBase) {
        if (courseBase == null || StringUtils.isEmpty(courseBase.getName()) || StringUtils.isEmpty(courseBase.getGrade()) || StringUtils.isEmpty(courseBase.getStudymodel())) {
            throw new CustomException(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据课程id查询课程信息
    public CourseBase findOneById(String courseId) {

        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (!courseBase.isPresent()) {
            throw new CustomException(CourseCode.COURSE_ISNOTEXITENCE);
        }
        return courseBase.get();
    }

    //根据id跟新课程
    public ResponseResult updateCoursebase(String courseId, CourseBase courseBase) {
        if (StringUtils.isEmpty(courseId)) {
            throw new CustomException(CourseCode.COURSE_ISNOTEXITENCE);
        }
        Optional<CourseBase> base = courseBaseRepository.findById(courseId);
        if (!base.isPresent()) {
            throw new CustomException(CourseCode.COURSE_ISNOTEXITENCE);
        }
        courseBase.setId(courseId);
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @return
     * @Author zhangl
     * @Description 添加课程图片
     * @Date 21:17 2019/4/26
     * @Param
     **/
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        Optional<CoursePic> coursePic = coursePicRepository.findById(courseId);
        //如果存在则为修改
        CoursePic coursePic1 = null;
        if (coursePic.isPresent()) {
            coursePic1 = coursePic.get();
        }
        if (coursePic1 == null) {
            coursePic1 = new CoursePic();
        }
        coursePic1.setPic(pic);
        coursePic1.setCourseid(courseId);
        coursePicRepository.save(coursePic1);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @return
     * @Author zhangl
     * @Description 根据课程id查询课程图片信息
     * @Date 21:41 2019/4/26
     * @Param
     **/
    public CoursePic findByCourseId(String courseId) {
        Optional<CoursePic> coursePic = coursePicRepository.findById(courseId);
        return coursePic.get();
    }

    /**
     * @return
     * @Author zhangl
     * @Description //TODO 根据课程id删除课程信息
     * @Date 22:31 2019/4/26
     * @Param
     **/
    @Transactional
    public ResponseResult deleteByCourseid(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //课程详情
    public CourseView courseView(String id) {
        CourseView courseView = new CourseView();
        Optional<CourseBase> courseBase = courseBaseRepository.findById(id);
        //课程详细
        if (courseBase.isPresent()) {
            CourseBase courseBase1 = courseBase.get();
            courseView.setCourseBase(courseBase1);
        }
        //课程营销信息
        Optional<CourseMarket> market = courseMarketInfoRepository.findById(id);
        if (market.isPresent()) {
            CourseMarket courseMarket = market.get();
            courseView.setCourseMarket(courseMarket);
        }
        //课程图片
        Optional<CoursePic> coursePic = coursePicRepository.findById(id);
        if (coursePic.isPresent()) {
            CoursePic coursePic1 = coursePic.get();
            courseView.setCoursePic(coursePic1);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.findTeachplanNode(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (courseBase.isPresent()) {
            CourseBase base = courseBase.get();
            return base;
        }
        throw new CustomException(CourseCode.COURSE_ISNOTEXITENCE);

    }

    //课程预览 返回的是课程页面的url
    public CoursePublishResult preview(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);
//发布课程预览页面
        CmsPage cmsPage = new CmsPage();
//站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
//模板
        cmsPage.setTemplateId(publish_templateId);
//页面名称
        cmsPage.setPageName(courseId + ".html");
//页面别名
        cmsPage.setPageAliase(one.getName());
//页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
//页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
//数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
//远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
//页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
//页面url
        String pageUrl = previewUrl + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }


}



