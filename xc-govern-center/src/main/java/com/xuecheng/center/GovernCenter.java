package com.xuecheng.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author:zhangl
 * @date:2019/4/27
 * @description:
 */
@SpringBootApplication
@EnableEurekaServer
public class GovernCenter {
    public static void main(String[] args) {
        SpringApplication.run(GovernCenter.class,args);
    }
}
