package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:zhangl
 * @date:2019/4/8
 * @description:
 */
@Service
public class CmsSiteService {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    //查询所有站点
    public QueryResponseResult findAllList(){
        List<CmsSite> cmsSites = cmsSiteRepository.findAll();
        QueryResult<CmsSite> queryResult=new QueryResult<>();
        queryResult.setList(cmsSites);
        queryResult.setTotal(cmsSites.size());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

}
