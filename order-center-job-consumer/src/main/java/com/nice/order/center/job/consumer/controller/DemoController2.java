package com.nice.order.center.job.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DemoController2 {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("nacos/consumer")
    public String nacosConsumer() {
        // 使用 LoadBalanceClient 和 RestTemplate 结合的方式来访问
        ServiceInstance serviceInstance = loadBalancerClient.choose("order-center-provider");
        String url = String.format("http://%s:%s/getServerPort", serviceInstance.getHost(), serviceInstance.getPort());
        System.out.println("request url:" + url);
        return restTemplate.getForObject(url, String.class);
    }

}