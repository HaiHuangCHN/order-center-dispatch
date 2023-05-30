package com.nice.order.center.job.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Twitter 的 SnowFlake算法，使用SnowFlake算法生成一个整数
 *
 * @author hai.huang.a@outlook.com
 * @date 2022/6/23 23:56
 */
@Slf4j
public final class SnowFlakeIdGeneratorUtil {

    /* 起始的时间戳 */
    private static final long START_TIMESTAMP = 1624698370256L;
    /* 起始的时间戳 */

    /* 每一部分占用的位数 */
    // 序列号占用的位数
    private static final long SEQUENCE_BIT = 12;
    // 机器标识占用的位数
    private static final long MACHINE_BIT = 5;
    // 数据中心占用的位数
    private static final long DATA_CENTER_BIT = 5;
    /* 每一部分占用的位数 */

    /* 每一部分的最大值 */
    //  (0)1 0000 0000 0000 = -4096 = -1L << 12
    // ^(0)1                = -1
    //  (1)0 1111 1111 1111 = 4095
    // 这里为4095 (0b111111111111=0xfff=4095)
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    // 11111 =31
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    // 11111 =31
    private static final long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    /* 每一部分的最大值 */

    /* 每一部分向左的位移 */
    // 机器标识向左移12位
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    // 数据中心向左移17位（12+5）
    private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    // 时间截向左移22位（5+5+12）
    private static final long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;
    /* 每一部分向左的位移 */

    // dataCenterId + machineId = 10bit工作机器ID
    // 数据中心（0~31）
    private final long dataCenterId;
    // 机器标识（0~31）
    private final long machineId;
    // 毫秒内序列号
    private volatile long sequence = 0L;
    // 上一次生成ID的时间戳
    private volatile long lastTimetamsp = -1L;

    /**
     * 根据指定的数据中心ID和机器标志ID生成指定的序列号
     *
     * @param dataCenterId（0~31）
     * @param machineId（0~31）
     */
    public SnowFlakeIdGeneratorUtil(long dataCenterId, long machineId) {
        Assert.isTrue(dataCenterId >= 0 && dataCenterId <= MAX_DATA_CENTER_NUM, "dataCenterId is illegal!");
        Assert.isTrue(machineId >= 0 || machineId <= MAX_MACHINE_NUM, "machineId is illegal!");
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    private static final SnowFlakeIdGeneratorUtil DEFAULT_INSTANCE = new SnowFlakeIdGeneratorUtil(1, 1);

    /**
     * 静态工具类
     *
     * @return
     */
    public static synchronized Long nextIdDefault() {
        return DEFAULT_INSTANCE.nextId();
    }

    /**
     * 生成下一个ID（该方法是线程安全的）
     *
     * @return
     */
    public synchronized long nextId() {
        // 当前时间戳
        long currentTimestamp = currentMillis();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常
        if (currentTimestamp < lastTimetamsp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimetamsp - currentTimestamp));
        }

        if (currentTimestamp == lastTimetamsp) {
            // 相同毫秒内，则进行毫秒内序列递增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大（毫秒内序列溢出），阻塞到下一个毫秒，获取下一个毫秒
            if (sequence == 0L) {
                currentTimestamp = tillNextMill();
            }
        } else {
            //不同毫秒内，序列号重置为0
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimetamsp = currentTimestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
                | dataCenterId << DATA_CENTER_LEFT                    //数据中心部分
                | machineId << MACHINE_LEFT                           //机器标识部分
                | sequence;                                           //序列号部分
    }

    private long tillNextMill() {
        long currentMillis = currentMillis();
        while (currentMillis <= lastTimetamsp) {
            currentMillis = currentMillis();
        }
        return currentMillis;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间（毫秒）
     */
    private long currentMillis() {
        return System.currentTimeMillis();
    }

    private static Long getDataCenterId() {
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i : ints) {
            sums += i;
        }
        return (long)(sums % 32);
    }

    private static Long getWorkId() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }

}