package com.xuecheng.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @author:zhangl
 * @date:2019/4/3
 * @description:
 */
public interface CmsPageControllerApi {
     QueryResponseResult findList(int page, int size, QueryPageRequest pageRequest);

     CmsPageResult add(CmsPage cmsPage);
      //根据id查询页面
     CmsPage findById(String id);
     //根据id修改页面
     CmsPageResult edit(String id ,CmsPage cmsPage);
     //根据id删除页面
     ResponseResult del(String id);
     //页面发布
     ResponseResult post(String pageId);
     //保存页面
     CmsPageResult save(CmsPage cmsPage);
     //课程预览
     CoursePublishResult preview(String id);
}
