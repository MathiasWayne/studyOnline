package com.xuecheng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author:zhangl
 * @date:2019/4/16
 * @description:
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")
@ComponentScan(basePackages = {"com.xuecheng.framework"})
@ComponentScan(basePackages = {"com.xuecheng.manage_cms_client"})
public class ManageCmsClient {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClient.class);
    }
}
