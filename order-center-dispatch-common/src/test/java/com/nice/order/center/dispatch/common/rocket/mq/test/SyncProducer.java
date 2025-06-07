package com.nice.order.center.dispatch.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * 同步消息
 * <p>
 * 这种可靠性同步地发送方式使用的比较广泛，比如：重要的消息通知，短信通知
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class SyncProducer {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, MQBrokerException,
            RemotingException, InterruptedException {
        // 1. 创建消息生产者 producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer_group_test");
        // 2. 指定 Nameserver 地址
        producer.setNamesrvAddr("47.115.219.13:9876");
        // producer.setVipChannelEnabled(false);
        // 3. 启动producer
        producer.start();
        for (int i = 0; i < 10; i++) {
            // 4. 创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            Message msg = new Message("TopicTest", "TagA", ("Hello World " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // Call send message to deliver message to one of brokers
            producer.setSendMsgTimeout(1000000);
            // 5. 发送消息
            SendResult result = producer.send(msg);
            // 发送状态
            SendStatus status = result.getSendStatus();
            log.info("发送结果：{}", result);
            log.info("发送状态：{}", status);
        }
        // Shut down once the producer instance is no longer in use
        producer.shutdown();
    }
}
