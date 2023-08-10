package com.nice.order.center.job.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import java.util.concurrent.TimeUnit;

/**
 * 异步消息
 * <p>
 * 异步消息通常用在对响应时间敏感的业务场景，即发送端不能容忍长时间地等待 Broker 的响应
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class AsyncProducer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消息生产者 Producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer_group_test");
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
            Message msg = new Message("TopicTest", null, ("Hello World" + i).getBytes());
            // 5. 发送异步消息
            producer.send(msg, new SendCallback() {
                /**
                 * 发送成功回调函数
                 *
                 * @param sendResult
                 */
                public void onSuccess(SendResult sendResult) {
                    log.info("发送结果 = {}", sendResult);
                }

                /**
                 * 发送失败回调函数
                 *
                 * @param e
                 */
                public void onException(Throwable e) {
                    log.error("发送异常", e);
                }
            });

            // 线程睡1秒
            TimeUnit.SECONDS.sleep(1);
        }
        // 6. 关闭生产者producer
        producer.shutdown();
    }
}

