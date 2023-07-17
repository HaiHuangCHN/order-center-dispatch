package com.nice.order.center.job.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * 同步消息
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class SyncProducer {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, MQBrokerException,
            RemotingException, InterruptedException {
        // Instantiate with a producer group name
        DefaultMQProducer producer = new DefaultMQProducer("test_producer_group");
        // Specify name server addresses
        producer.setNamesrvAddr("47.115.219.13:9876");
        // producer.setVipChannelEnabled(false);
        // Launch the instance
        producer.start();
        for (int i = 0; i < 10; i++) {
            // Create a message instance, specifying topic, tag and message body
            Message msg = new Message("TopicTest" /* Topic */, "TagA" /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */);
            // Call send message to deliver message to one of brokers
            producer.setSendMsgTimeout(1000000);
            SendResult sendResult = producer.send(msg);
            log.info("Send Result = {}", sendResult);
        }
        // Shut down once the producer instance is no longer in use
        producer.shutdown();
    }
}
