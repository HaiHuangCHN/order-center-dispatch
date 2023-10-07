package com.nice.order.center.job.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQTransactionListener
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 执行本地事务
        String tag = String.valueOf(msg.getHeaders().get("rocketmq_TAGS"));
        if (StringUtils.equals("TAGA", tag)) {
            // 这里只讲TAGA消息提交，状态为可执行
            return RocketMQLocalTransactionState.COMMIT;
        } else if (StringUtils.equals("TAGB", tag)) {
            return RocketMQLocalTransactionState.ROLLBACK;
        } else if (StringUtils.equals("TAGC", tag)) {
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        return RocketMQLocalTransactionState.UNKNOWN;
    }

    // MQ回调检查本地事务执行情况
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("checkLocalTransaction===>{}", msg);
        return RocketMQLocalTransactionState.COMMIT;
    }
}

