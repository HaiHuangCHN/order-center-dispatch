package com.nice.order.center.job.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 通用消费者
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class Consumer {

    public static void main(String[] args) throws MQClientException {

        // Instantiate with specified consumer group name
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer_group");

        // Specify name server addresses
        consumer.setNamesrvAddr("47.115.219.13:9876");

        // Subscribe one more more topics to consume
        // 消费所有"*",消费Tag1和Tag2  Tag1 || Tag2
        consumer.subscribe("TopicTest", "*");

        // 设定消费模式：负载均衡|广播模式，默认为负载均衡
        // 负载均衡 10 条消息，每个消费者共计消费 10 条
        // 广播模式 10 条消息，每个消费者都消费 10 条
        // consumer.setMessageModel(MessageModel.BROADCASTING);

        // Register callback to execute on arrival of messages fetched from brokers
        consumer.registerMessageListener((MessageListenerConcurrently)(msg, context) -> {
            log.info("{} Receive New Message: {}", Thread.currentThread().getName(), msg);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // Launch the consumer instance
        consumer.start();

        log.info("Consumer Started");
    }
}

