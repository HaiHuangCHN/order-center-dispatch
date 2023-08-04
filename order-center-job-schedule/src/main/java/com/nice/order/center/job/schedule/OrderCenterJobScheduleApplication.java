package com.nice.order.center.job.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderCenterJobScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderCenterJobScheduleApplication.class, args);
    }

}
