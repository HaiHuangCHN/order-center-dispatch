package com.nice.order.center.job.schedule.remote;

import com.nice.order.center.job.schedule.dto.req.OrderDetailQueryRemoteReqDTO;
import com.nice.order.center.job.schedule.dto.res.OrderDetailQueryRemoteResDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
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

            @Override
            public ResponseEntity<OrderDetailQueryRemoteResDTO> getOrderByOrderNo2(String orderNo, OrderDetailQueryRemoteReqDTO orderDetailQueryReqVo) {
                return null;
            }
        };
    }

}
