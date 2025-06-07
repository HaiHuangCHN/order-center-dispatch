package com.nice.order.center.dispatch.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 延迟消息
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class DelayConsumer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消费者 Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group_test_delay");
        // 2. 指定 Nameserver 地址
        consumer.setNamesrvAddr("47.115.219.13:9876");
        // 3. 订阅主题Topic和Tag
        consumer.subscribe("TopicTestDelay", "*");
        // 4. 设置回调函数，处理消息
        // 接收消息内容
        consumer.registerMessageListener((MessageListenerConcurrently)(msgs, context) -> {
            for (MessageExt msg : msgs) {
                log.info("消息ID：【" + msg.getMsgId() + "】,延迟时间：" + (System.currentTimeMillis() - msg.getStoreTimestamp()));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // 5. 启动消费者consumer
        consumer.start();
        log.info("消费者启动");
    }
}
