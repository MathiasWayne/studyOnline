package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
*
 * @Author zhangl
 * @Description //TODO
 * @Date 22:29 2019/4/27
 * @Param
 * @return
 **/

@FeignClient(value= XcServiceList.XC_SERVICE_MANAGE_CMS)
public interface CmsPageClient {
    @GetMapping("/cms/page/get/{id}")
     CmsPage findById(@PathVariable("id") String id);

    @PostMapping("/cms/page/save")
    CmsPageResult save(@RequestBody CmsPage cmsPage);
}
