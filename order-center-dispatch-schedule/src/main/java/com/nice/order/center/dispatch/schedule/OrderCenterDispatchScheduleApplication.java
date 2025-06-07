package com.nice.order.center.dispatch.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderCenterDispatchScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderCenterDispatchScheduleApplication.class, args);
    }

}
