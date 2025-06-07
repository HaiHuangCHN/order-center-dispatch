package com.nice.order.center.dispatch.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送大于4MB的消息
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class BigMsgProducer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消息生产者 Producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("producer_group_test");
        // 2. 指定 Nameserver 地址
        producer.setNamesrvAddr("47.115.219.13:9876");
        // 3. 启动 Producer
        producer.start();

        List<Message> messages = new ArrayList<>();

        int i = 0;
        while (i < 10000) {
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            String context = "你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳," +
                    "你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳" + "你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳," +
                    "你好北京，你好上海，你好深圳,你好北京，你好上海，你好深圳," + "你好北京，你好上海，你好深圳";
            Message msg = new Message("TopicTestBigMsg", "Tag1", (context + 1).getBytes());
            messages.add(msg);
            i++;
        }
        log.info("此次发送批量消息条数==>" + messages.size());
        int count = 0;
        // 把大的消息分裂成若干个小的消息
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            ++count;
            log.info("第" + count + "次");
            try {
                List<Message> listItem = splitter.next();
                // 5. 发送消息
                SendResult result = producer.send(listItem);
                // 发送状态
                SendStatus status = result.getSendStatus();
                log.info("发送结果：{}", result);
                log.info("发送状态：{}", status);
            } catch (Exception e) {
                e.printStackTrace();
                // 处理error
            }
        }
        // 6. 关闭生产者producer
        producer.shutdown();
    }
}
