package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.CmsSysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author:zhangl
 * @date:2019/4/21
 * @description:
 */
@Service
public class CmsSysDictionaryService {
    @Autowired
    private CmsSysDictionaryRepository cmsSysDictionaryRepository;
    public SysDictionary findByType(String type){
        return cmsSysDictionaryRepository.findByDType(type);
    }
}
