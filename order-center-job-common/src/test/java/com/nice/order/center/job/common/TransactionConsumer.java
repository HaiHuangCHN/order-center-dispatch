package com.nice.order.center.job.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 事务消息消费者
 *
 * @author haihuang95@zto.com
 * @date 2023/8/11 17:14
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "transaction-group", topic = "transaction-str", consumeMode =
        ConsumeMode.ORDERLY)
public class TransactionConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String str) {
        log.info("===>" + str);
    }

}
