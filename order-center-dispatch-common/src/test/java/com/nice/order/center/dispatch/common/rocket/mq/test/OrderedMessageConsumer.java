package com.nice.order.center.dispatch.common.rocket.mq.test;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 顺序消息
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/8/11 17:14
 */
public class OrderedMessageConsumer {

    public static void main(String[] args) throws Exception {

        // 1. 创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group_test_ordered_message");
        // 2. 指定Nameserver地址
        consumer.setNamesrvAddr("47.115.219.13:9876");
        // 3. 订阅主题Topic和Tag
        consumer.subscribe("TopicTestOrderedMessage", "*");

        // 4. 注册消息监听器
        consumer.registerMessageListener((MessageListenerOrderly)(msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("线程名称：【" + Thread.currentThread().getName() + "】:" + new String(msg.getBody()));
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });

        // 5. 启动消费者
        consumer.start();

        System.out.println("消费者启动");
    }

}
