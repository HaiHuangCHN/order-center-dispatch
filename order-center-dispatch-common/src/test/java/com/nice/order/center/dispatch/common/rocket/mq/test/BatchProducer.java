package com.nice.order.center.dispatch.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 批量消息
 * <p>
 * 如果您每次只发送不超过4MB的消息，则很容易使用批处理，样例如下：
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class BatchProducer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消息生产者 Producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer_group_test");
        // 2. 指定 Nameserver 地址
        producer.setNamesrvAddr("47.115.219.13:9876");
        // 3. 启动 Producer
        producer.start();
        List<Message> msgs = new ArrayList<Message>();
        // 4. 创建消息对象，指定主题Topic、Tag和消息体
        /**
         * 参数一：消息主题Topic
         * 参数二：消息Tag
         * 参数三：消息内容
         */
        Message msg1 = new Message("TopicTestBatch", "Tag1", ("Hello World" + 1).getBytes());
        Message msg2 = new Message("TopicTestBatch", "Tag1", ("Hello World" + 2).getBytes());
        Message msg3 = new Message("TopicTestBatch", "Tag1", ("Hello World" + 3).getBytes());

        msgs.add(msg1);
        msgs.add(msg2);
        msgs.add(msg3);
        // 5. 发送消息
        SendResult result = producer.send(msgs);
        // 发送状态
        SendStatus status = result.getSendStatus();
        log.info("发送结果：{}", result);
        log.info("发送状态：{}", status);
        // 线程睡 1 秒
        TimeUnit.SECONDS.sleep(1);
        // 6. 关闭生产者producer
        producer.shutdown();
    }
}
