package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CmsPageRepositoryTest {
@Autowired
private CmsPageRepository cmsPageRepository;
    @Test
    public void testPage() {
        Pageable pa= PageRequest.of(1,2);
        Page<CmsPage> all = cmsPageRepository.findAll(pa);
        System.out.println(all);
    }
    @Test
    public void testExPage(){
        Pageable pageable=PageRequest.of(1,2);

        CmsPage cms=new CmsPage();
        cms.setPageAliase("课程详情");
        //条件匹配器
        ExampleMatcher exampleMatcher=ExampleMatcher.matching();
        //条件匹配规则
        exampleMatcher=exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //条件筛选器
        Example<CmsPage> page=Example.of(cms,exampleMatcher);
        //分页查询，参数一条件筛选器，
        Page<CmsPage> all = cmsPageRepository.findAll(page, pageable);

        System.out.println(all.getTotalPages());
        List<CmsPage> content = all.getContent();
        System.out.println(content);





    }
}