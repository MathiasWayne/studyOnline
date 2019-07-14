package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author:zhangl
 * @date:2019/4/27
 * @description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {

    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void feignTest(){
        CmsPage cmsPage = cmsPageClient.findById("5a754adf6abb500ad05688d9");
        System.out.println(cmsPage);
    }
}
