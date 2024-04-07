package com.nice.order.center.job.schedule.controller;

import com.nice.order.center.job.schedule.remote.OrderCenterRemoteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/8/1 16:09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final OrderCenterRemoteClient orderCenterRemoteClient;

    @GetMapping(value = "/testFeignClient")
    public ResponseEntity<String> testFeignClient() {
        return ResponseEntity.status(HttpStatus.OK).body(orderCenterRemoteClient.getServerPort());
    }

}
