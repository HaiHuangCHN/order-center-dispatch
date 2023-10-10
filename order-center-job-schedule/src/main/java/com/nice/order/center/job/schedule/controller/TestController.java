package com.nice.order.center.job.schedule.controller;

import com.nice.order.center.job.schedule.dto.req.OrderDetailQueryRemoteReqDTO;
import com.nice.order.center.job.schedule.dto.res.OrderDetailQueryRemoteResDTO;
import com.nice.order.center.job.schedule.remote.OrderCenterRemoteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author haihuang95@zto.com
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

    @PostMapping(value = "/testFeignClient2")
    public ResponseEntity<OrderDetailQueryRemoteResDTO> testFeignClient2() {
        OrderDetailQueryRemoteReqDTO orderDetailQueryReqVo = new OrderDetailQueryRemoteReqDTO();
        orderDetailQueryReqVo.setOrderStatus("Test Status");
        return orderCenterRemoteClient.getOrderByOrderNo2(null, orderDetailQueryReqVo);
    }


}
