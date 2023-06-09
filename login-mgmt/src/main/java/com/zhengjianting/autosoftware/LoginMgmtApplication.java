package com.zhengjianting.autosoftware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class LoginMgmtApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginMgmtApplication.class, args);
    }
}
