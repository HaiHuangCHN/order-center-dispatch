package com.nice.order.center.dispatch.schedule.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author hai.huang.a@outlook.com
 * @date 2023/7/28 20:30TestController
 */
@FeignClient(name = "order-center", fallbackFactory = OrderCenterRemoteClientFallbackFactory.class)
public interface OrderCenterRemoteClient {

    @GetMapping(value = "/getServerPort")
    String getServerPort();

}
