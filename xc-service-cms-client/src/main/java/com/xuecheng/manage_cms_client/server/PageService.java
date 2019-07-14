package com.xuecheng.manage_cms_client.server;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author:zhangl
 * @date:2019/4/16
 * @description:
 */
@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    //获取页面
    public void savePageToServerPath(String pageId) {
        Optional<CmsPage> op = cmsPageRepository.findById(pageId);
        if (!op.isPresent()) {
            //页面不存在
            throw new CustomException(CmsCode.CMSPAGE_NOT_EXISTS);
        }
        //获取页面对象
        CmsPage cmsPage = op.get();
        //获取站点对象
        CmsSite cmsSite =this.getCmsSite(cmsPage.getSiteId());
        String pagePath=cmsSite.getSitePhysicalPath()+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
        //
        String htmlFileId = cmsPage.getHtmlFileId();
        InputStream inputStream = this.getFeildById(htmlFileId);
        if (inputStream==null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream=new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private CmsSite getCmsSite(String siteId){
        Optional<CmsSite> optionalCmsSite = cmsSiteRepository.findById(siteId);
        if (!optionalCmsSite.isPresent()) {
            throw new CustomException(CmsCode.CMS_COURSE_SITENULL);
        }
        CmsSite cmsSite = optionalCmsSite.get();
        return cmsSite;
    }
    //根据页面id获取页面内容
    private InputStream getFeildById(String feildId){
        GridFSFile fsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(feildId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());
        GridFsResource fsResource=new GridFsResource(fsFile,gridFSDownloadStream);
        try {
            return fsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
