package com.nice.order.center.job.common.rocket.mq.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;

/**
 * 事务消息
 *
 * @author hai.huang.a@outlook.com
 * @date 2023/7/9 23:56
 */
@Slf4j
public class TransactionProducer {

    public static void main(String[] args) throws Exception {

        // 1. 创建事务消息生产者 Producer，并制定生产者组名
        TransactionMQProducer producer = new TransactionMQProducer("test_producer_group");
        // 2. 指定 Nameserver 地址
        producer.setNamesrvAddr("47.115.219.13:9876");

        // 添加事务监听器
        producer.setTransactionListener(new TransactionListener() {

            /**
             * 在该方法中执行本地事务
             *
             * @param msg
             * @param arg
             * @return
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                if (StringUtils.equals("TAGA", msg.getTags())) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (StringUtils.equals("TAGB", msg.getTags())) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else if (StringUtils.equals("TAGC", msg.getTags())) {
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.UNKNOW;
            }

            /**
             * 该方法时MQ进行消息事务状态回查
             *
             * @param msg
             * @return
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                log.info("消息的Tag = {}", msg.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        // 3. 启动 Producer
        producer.start();

        String[] tags = {"TAGA", "TAGB", "TAGC"};

        for (int i = 0; i < 3; i++) {

            // 4.创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            Message msg = new Message("TopicTestTransaction", tags[i], ("Hello World" + i).getBytes());
            // 5.发送消息
            SendResult result = producer.sendMessageInTransaction(msg, null);
            // 发送状态
            SendStatus status = result.getSendStatus();
            log.info("发送结果：{}", result);
            log.info("发送状态：{}", status);
            // 线程睡1秒
            TimeUnit.SECONDS.sleep(2);
        }
        // 6.关闭生产者producer
        producer.shutdown();
    }
}
