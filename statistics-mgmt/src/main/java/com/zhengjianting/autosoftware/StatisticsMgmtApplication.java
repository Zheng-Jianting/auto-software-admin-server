package com.zhengjianting.autosoftware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class StatisticsMgmtApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatisticsMgmtApplication.class, args);
    }
}
