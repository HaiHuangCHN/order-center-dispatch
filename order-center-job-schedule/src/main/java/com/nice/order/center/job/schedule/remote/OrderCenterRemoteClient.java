package com.nice.order.center.job.schedule.remote;

import com.nice.order.center.job.schedule.dto.req.OrderDetailQueryRemoteReqDTO;
import com.nice.order.center.job.schedule.dto.res.OrderDetailQueryRemoteResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author haihuang95@zto.com
 * @date 2023/7/28 20:30TestController
 */
@FeignClient(name = "order-center-provider", fallbackFactory = OrderCenterRemoteClientFallbackFactory.class)
public interface OrderCenterRemoteClient {

    @GetMapping(value = "/getServerPort")
    String getServerPort();

    @GetMapping(value = "/queryByOrderNo2")
    ResponseEntity<OrderDetailQueryRemoteResDTO> getOrderByOrderNo2(@RequestParam("orderNo") String orderNo,
                                                                    @RequestBody OrderDetailQueryRemoteReqDTO orderDetailQueryReqVo);
}
