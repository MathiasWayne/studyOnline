package com.xuecheng.manage_cms.service;


import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;

import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author:zhangl
 * @date:2019/4/6
 * @description:  分页查询service层
 */
@Service
public class PageService {
    @Autowired
    private RabbitTemplate template;
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RestTemplate restTemplate;
    public QueryResponseResult findList( int page, int size, QueryPageRequest pageRequest) {

        if (pageRequest==null) {
            pageRequest=new QueryPageRequest();
        }
        CmsPage cmsPage=new CmsPage();
        //组装筛选条件
        if (StringUtils.isNotEmpty(pageRequest.getTemplateId())) {
            cmsPage.setTemplateId(pageRequest.getTemplateId());
        }
        if (StringUtils.isNotEmpty(pageRequest.getSiteId())) {
            cmsPage.setSiteId(pageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(pageRequest.getPageAliase())) {
            cmsPage.setPageAliase(pageRequest.getPageAliase());
        }
        //配置筛选条件,别名模糊查询，其他精确查询
        ExampleMatcher matcher=ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //条件筛选器
        Example<CmsPage> example=Example.of(cmsPage,matcher);
        if (pageRequest==null) {
            pageRequest=new QueryPageRequest();
        }
        if (page<=0) {

            page=1;
        }
        page=page-1;
        if(size<0){
            size=10;
        }
        //分页查询
        Pageable pages=new PageRequest(page,size);
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(example,pages);
        //分装结果集对象
        QueryResult result=new QueryResult();
        result.setList(cmsPages.getContent());
        result.setTotal(cmsPages.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS,result);
    }
    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        //1.判断当前数据是否唯一
        CmsPage cmsPageUn = cmsPageRepository.findByPageWebPathAndSiteIdAndPageName(cmsPage.getPageWebPath(), cmsPage.getSiteId(), cmsPage.getPageName());
        //2.当前数据不存在则添加，保证数据的唯一性
        if (cmsPageUn==null) {
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return  new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id查询页面
    public CmsPage findById(String id){
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        if (cmsPage.isPresent()) {
            CmsPage cmsPage1 = cmsPage.get();
            return cmsPage1;
        }
        return null;
    }
    //根据id修改页面
    public CmsPageResult edit(String id,CmsPage cmsPage){
        CmsPage one = this.findById(id);
        if (one!=null) {
            one.setTemplateId(cmsPage.getTemplateId());

            one.setSiteId(cmsPage.getSiteId());

            one.setPageAliase(cmsPage.getPageAliase());

            one.setPageName(cmsPage.getPageName());

            one.setPageWebPath(cmsPage.getPageWebPath());

            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

            one.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = cmsPageRepository.save(one);
            if (save!=null) {
                return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id删除
    public ResponseResult del(String id){
        //先根据id查询需要删除的id，存在则删除
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        if (cmsPage.isPresent()) {
            //存在删除
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //页面静态化
    /**
    *
     * @Author zhangl
     * @Description
     *  //获取页面模板
     *  //获取模板数据
     *  //拼装数据
     *  //生成页面
     * @Date 21:44 2019/4/14
     * @Param [pageId]
     * @return java.lang.String
     **/

    public String getHtml(String pageId){
        //获取页面模板
        String template = this.getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //获取数据对象
         Map model=this.getModelByPageId(pageId);
        if (model==null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        String html = this.getHtml(template, model);
        if (StringUtils.isEmpty(html)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    //获取页面模板
    private String getTemplateByPageId(String pageId){
        //查询页面信息
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage==null) {
            throw new CustomException(CmsCode.CMSPAGE_NOT_EXISTS);
        }
        //根据页面信息对象获取模板对象
        if (StringUtils.isEmpty(cmsPage.getTemplateId())) {
            //页面模板不存在
            throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> cmsTemplateOptional = cmsTemplateRepository.findById(cmsPage.getTemplateId());
        if (cmsTemplateOptional.isPresent()) {
            CmsTemplate cmsTemplate = cmsTemplateOptional.get();
            //获取模板文件
            String templateFileId = cmsTemplate.getTemplateFileId();
            if (StringUtils.isEmpty(templateFileId)) {
                throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            }
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource
            GridFsResource resource=new GridFsResource(gridFSFile,gridFSDownloadStream);
             //获取模板对象
            try {
                String cont = IOUtils.toString(resource.getInputStream(), "utf-8");
               return cont;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取数据
    private Map getModelByPageId(String pageId){
        CmsPage cmspage = this.findById(pageId);
        if (cmspage==null) {
            throw new CustomException(CmsCode.CMSPAGE_NOT_EXISTS);
        }
        String dataUrl = cmspage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        try {
            ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
            Map body = forEntity.getBody();
            return body;
        } catch (Exception e){
            //TODO 应该打印日志信息
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

    }

    //生成html页面
    private String getHtml(String template,Map model){
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);

            //获取模板
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




    //页面发布，作为生产者发布到rabbitMq中
    public ResponseResult postPage(String pageId){
        String html = this.getHtml(pageId);
        if (StringUtils.isEmpty(html)) {
           throw new CustomException(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //先保存页面信息
        CmsPage cmsPage = this.saveHtml(pageId, html);
        //如果保存成功才进行发送
        if (cmsPage==null) {
            return new ResponseResult(CommonCode.FAIL);
        }
        sendPostPage(pageId);
        return  new ResponseResult(CommonCode.SUCCESS);
    }
    //保存静态页面内容
    private  CmsPage saveHtml(String pageId,String content){
        //查询页面
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(pageId);
        if (cmsPage.isPresent()) {
            throw new CustomException(CmsCode.CMSPAGE_NOT_EXISTS);
        }
        CmsPage cmsPage1 = cmsPage.get();
        //存储之前先删除
        String htmlFileId = cmsPage1.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //保存页面到存储器中
        InputStream inputStream = IOUtils.toInputStream(content);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage1.getPageName());
        //保存后的文件id
        String s = objectId.toString();
        cmsPage1.setHtmlFileId(s);
        //保存cmspage到mongodb中
        cmsPageRepository.save(cmsPage1);
        return cmsPage1;
    }
    //发送信息到mq中
    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage==null) {
            throw new CustomException(CmsCode.CMSPAGE_NOT_EXISTS);
        }
        //封装发送到mq中的信息格式,json格式
        Map<String,String> param=new HashMap(1);
        param.put("pageId",pageId);
        //路由key
        String routingKey = cmsPage.getSiteId();
        String s = JSON.toJSONString(param);
         template.convertAndSend(RabbitConfig.EX_ROUTING_CMS_POSTPAGE,routingKey,s);
    }

    public CmsPageResult save(CmsPage cmsPage) {
        CmsPage cmsPage1 = cmsPageRepository.findByPageWebPathAndSiteIdAndPageName(cmsPage.getPageWebPath(), cmsPage.getSiteId(), cmsPage.getPageName());
        if (cmsPage1!=null) {
            //更新
           return  this.edit(cmsPage.getPageId(),cmsPage);
        }
          return   this.add(cmsPage);
    }


}
