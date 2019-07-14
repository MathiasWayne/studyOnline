package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:zhangl
 * @date:2019/4/8
 * @description:
 */
@Service
public class CmsTemplateService {
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    public QueryResponseResult templateList(){
        List<CmsTemplate> templates = cmsTemplateRepository.findAll();
        QueryResult<CmsTemplate> queryResult=new QueryResult<>();
        queryResult.setTotal(templates.size());
        queryResult.setList(templates);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
