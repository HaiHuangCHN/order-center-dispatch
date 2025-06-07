package com.nice.order.center.dispatch.schedule.remote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCenterRemoteClientFallbackFactory implements FallbackFactory<OrderCenterRemoteClient> {

    @Override
    public OrderCenterRemoteClient create(Throwable cause) {
        log.error("OrderCenterRemoteClient.getServerPort 异常", cause);
        return new OrderCenterRemoteClient() {
            @Override
            public String getServerPort() {
                return "default port";
            }
        };
    }

}
