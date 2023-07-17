package com.nice.order.center.job.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * Tag 过滤
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class TagProducer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消息生产者 Producer，并指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("test_producer_group");
        // 2. 指定 Nameserver 地址
        producer.setNamesrvAddr("47.115.219.13:9876");
        // 3. 启动 Producer
        producer.start();

        for (int i = 0; i < 3; i++) {
            // 4. 创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            Message msg = new Message("TopicTestFilterTag", "Tag2", ("Hello World" + i).getBytes());
            // 5. 发送消息
            SendResult result = producer.send(msg);
            // 发送状态
            SendStatus status = result.getSendStatus();
            log.info("发送结果：{}", result);
            log.info("发送状态：{}", status);
            // 线程睡1秒
            TimeUnit.SECONDS.sleep(1);
        }
        // 6. 关闭生产者 Producer
        producer.shutdown();
    }
}

