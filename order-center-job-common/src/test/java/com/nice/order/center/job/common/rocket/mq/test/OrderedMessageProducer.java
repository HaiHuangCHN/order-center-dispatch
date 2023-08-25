package com.nice.order.center.job.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * 顺序消息
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/8/11 12:19
 */
@Slf4j
public class OrderedMessageProducer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消息生产者 Producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer_group_test");

        // 2. 指定 Nameserver 地址
        producer.setNamesrvAddr("47.115.219.13:9876");

        // 3. 启动 Producer
        producer.start();

        // 构建消息集合
        List<OrderStep> orderSteps = OrderStep.buildOrders();

        for (int i = 0; i < orderSteps.size(); i++) {
            // 4. 创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            String body = orderSteps.get(i) + "";
            Message message = new Message("TopicTestOrderedMessage", "Order", "i" + i, body.getBytes());
            SendResult result = producer.send(message, new MessageQueueSelector() {

                /**
                 *
                 * @param mqs：队列集合
                 * @param msg：消息对象
                 * @param arg：业务标识的参数
                 * @return
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Long orderId = (Long)arg;
                    long index = orderId % mqs.size();
                    log.info(msg + "=" + index);
                    return mqs.get((int)index);
                }
            }, orderSteps.get(i).getOrderId());
            // 发送状态
            SendStatus status = result.getSendStatus();
            log.info("发送结果：{}", result);
            log.info("发送状态：{}", status);
        }
        producer.shutdown();
    }

}

