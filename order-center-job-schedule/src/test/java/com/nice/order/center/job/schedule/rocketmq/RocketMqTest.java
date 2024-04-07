package com.nice.order.center.job.schedule.rocketmq;

import com.nice.order.center.job.schedule.OrderCenterJobScheduleApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author hai.huang.a@outlook.com
 * @date 2023/8/25 22:37
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrderCenterJobScheduleApplication.class})
@Slf4j
public class RocketMqTest {

    /**
     * org.junit.runners.model.InvalidTestClassError: Invalid test class 'com.nice.order.center.job.schedule.rocketmq.RocketMqTest':
     * 1. Test class should have exactly one public zero-argument constructor
     * 2. No runnable methods
     * <p>
     * Due to the #1, field injection is the way I choose to resolve
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 同步消息-
     */
    @Test
    public void syncSendStr() {
        rocketMQTemplate.syncSend("TopicTest", "hello world syncSendStr1");
        // send 和 syncSend 是等价的
        // send 底层还是会调用 syncSend 的代码
        rocketMQTemplate.send("TopicTest", MessageBuilder.withPayload("hello world syncSendStr2").build());
        // TopicTest:tag1 底层会做处理，以「:」作为分割符，前者=Topic，后者=Tag
        SendResult res = rocketMQTemplate.syncSend("TopicTest:tag1", "hello world syncSendStr3 with tag");
        log.info("syncSend===>{}", res);
    }

    /**
     * 同步消息-
     */
    @Test
    public void syncSendPojo() {
        MsgTest msg = new MsgTest(1, "hello world syncSendPojo", new Date());
        SendResult res = rocketMQTemplate.syncSend("TopicTest", MessageBuilder.withPayload(msg).build());
        log.info("syncSend===>{}", res);
    }

    /**
     * 异步消息-String
     * 指发送方发出数据后，不等接收方发回响应，接着发送下个数据包
     * 关键实现异步发送回调接口（SendCallback）
     * 在执行消息的异步发送时应用不需要等待服务器响应即可直接返回，通过回调接口接收务器响应，并对服务器的响应结果进行处理
     * 这种方式任然需要返回发送消息任务的执行结果，异步不影响后续任务，不会造成阻塞
     */
    @Test
    public void asyncSendStr() {
        rocketMQTemplate.asyncSend("first-topic-str:tag1", "hello world test2 asyncSendStr", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步消息发送成功:{}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("异步消息发送失败:{}", throwable.getMessage());
            }
        });
    }

    /**
     * 单向消息
     * 特点为只负责发送消息，不等待服务器回应且没有回调函数触发，即只发送请求不等待应答
     * 此方式发送消息的过程耗时非常短，一般在微秒级别
     * 应用场景：适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集
     */
    @Test
    public void sendOneWayStr() {
        rocketMQTemplate.sendOneWay("first-topic-str:tag1", "hello world test2 sendOneWayStr");
        log.info("单向消息已发送");
    }

    /**
     * 批量消息
     */
    @Test
    public void asyncSendBatch() {
        Message<String> msg = MessageBuilder.withPayload("hello world test1").build();
        List<Message<String>> msgList = Arrays.asList(msg, msg, msg, msg, msg);
        SendResult res = rocketMQTemplate.syncSend("first-topic-str:tag1", msgList);
        log.info("asyncSendBatch===>{}", res);
        log.info("批量消息");
    }

    /**
     * 同步延迟消息
     * RocketMQ 的延迟消息发送其实是已发送就已经到 Broker 端了，然后消费端会延迟收到消息。
     * RocketMQ 目前只支持固定精度的定时消息。
     * 固定等级：1 到 18 分别对应1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * 延迟的底层方法是用定时任务实现的。
     */
    @Test
    public void syncSendDelayedStr() {
        Message<String> message = MessageBuilder.withPayload("syncSendDelayedStr" + new Date()).build();
        /**
         * @param destination formats: `topicName:tags`
         * @param message 消息体
         * @param timeout 发送超时时间
         * @param delayLevel 延迟级别  1到18
         * @return {@link SendResult}
         */
        SendResult res = rocketMQTemplate.syncSend("TopicTestDelay:tag1", message, 3000, 3);
        log.info("res==>{}", res);
    }

    /**
     * 异步延迟消息
     */
    @Test
    public void asyncSendDelayedStr() {
        // Callback
        SendCallback sc = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送异步延时消息成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("发送异步延时消息失败:{}", throwable.getMessage());
            }
        };

        Message<String> message = MessageBuilder.withPayload("asyncSendDelayedStr").build();
        rocketMQTemplate.asyncSend("first-topic-str:tag1", message, sc, 3000, 3);
    }

    /**
     * 顺序消息
     */
    @Test
    public void SendOrderStr() {
        List<MsgTest> msgList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            msgList.add(new MsgTest(100, "我是id为100的第" + (i + 1) + "条消息", new Date()));
        }
        //msgList.add(new MsgTest(1, "我是id为1的第1条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第1条消息", new Date()));
        //msgList.add(new MsgTest(1, "我是id为1的第2条消息", new Date()));
        //msgList.add(new MsgTest(1, "我是id为1的第3条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第2条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第3条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第4条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第5条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第6条消息", new Date()));
        //msgList.add(new MsgTest(2, "我是id为2的第7条消息", new Date()));
        //msgList.add(new MsgTest(1, "我是id为1的第4条消息", new Date()));
        //msgList.add(new MsgTest(1, "我是id为1的第5条消息", new Date()));
        //msgList.add(new MsgTest(1, "我是id为1的第6条消息", new Date()));
        //msgList.add(new MsgTest(1, "我是id为1的第7条消息", new Date()));
        msgList.forEach(msg -> {
            //rocketMQTemplate.sendOneWayOrderly("first-topic-str:tag1", msg,String.valueOf(msg.getId()));
            //rocketMQTemplate.syncSendOrderly("first-topic-str:tag1", msg, String.valueOf(msg.getId()));
            rocketMQTemplate.asyncSendOrderly("first-topic-str:tag1", msg, String.valueOf(msg.getId()),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("异步消息发送成功:{}", sendResult);
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.info("异步消息发送失败:{}", throwable.getMessage());
                        }
                    });

        });
    }

    /**
     * 事务消息  注意这里还有一个监听器 TransactionListenerImpl
     */
    @Test
    public void sendTransactionStr() {

        String[] tags = {"TAGA", "TAGB", "TAGC"};
        for (int i = 0; i < 3; i++) {
            Message<String> message = MessageBuilder.withPayload("事务消息===>" + i).build();
            TransactionSendResult res = rocketMQTemplate.sendMessageInTransaction("transaction-str:" + tags[i],
                    message, i + 1);
            if (LocalTransactionState.COMMIT_MESSAGE.equals(res.getLocalTransactionState()) && SendStatus.SEND_OK.equals(res.getSendStatus())) {
                log.info("事务消息发送成功");
            }

            log.info("事务消息发送结果:{}", res);
        }
    }

}
