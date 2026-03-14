package com.liepin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //开启注册中心的服务注册与发现功能
public class CompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompanyApplication.class, args);
    }

}
