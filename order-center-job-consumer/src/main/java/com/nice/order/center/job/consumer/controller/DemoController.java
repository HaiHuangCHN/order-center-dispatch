//package com.nice.order.center.job.consumer.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.Resource;
//
//@RestController
//public class DemoController {
//
//    @Resource
//    private RestTemplate restTemplate;
//
//    /**
//     * 消费者去访问具体服务，这种写法可以实现配置文件和代码的分离
//     */
//    @Value("${providers.order-center-provider}")
//    private String serverURL;
//
//    @GetMapping(value = "nacos/consumer")
//    public String nacosConsumer() {
//        return restTemplate.getForObject(serverURL + "/getServerPort", String.class);
//    }
//}
