package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Lenovo
 * 分页查询MongoDB，cms
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String>{
     /**
     *
      * @Author zhangl
      * @Description //TODO 根据唯一索引建立查询
      * @Date 20:15 2019/4/8
      * @Param [pageWebPath, siteID, pageNAme]
      * @return com.xuecheng.framework.domain.cms.CmsPage
      **/
     
    CmsPage findByPageWebPathAndSiteIdAndPageName(String pageWebPath,String siteID,String pageNAme);
}
