package com.lfx.receive.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author: lifuxing
 * @createDate: 2021/12/24
 * @description: 消息监听类.
 */

public class QueueListener implements Runnable {
    public static final Logger log = LoggerFactory.getLogger(QueueListener.class);
    private RedisTemplate<String, Object> redisTemplate;
    private String queue;
    private MsgConsumer consumer;

    public QueueListener(RedisTemplate<String, Object> redisTemplate, String queue, MsgConsumer consumer) {
        this.redisTemplate = redisTemplate;
        this.queue = queue;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        log.info("QueueListener start...queue:{}", queue);
        while (RedisMqConsumerContainer.run) {
            try {
                Object msg = redisTemplate.opsForList().rightPop(queue, 30, TimeUnit.SECONDS);
                if (msg != null) {
                    try {
                        consumer.onMessage(msg);
                    } catch (Exception e) {
                        consumer.onError(msg, e);
                    }
                }
            } catch (QueryTimeoutException ignored) {
            } catch (Exception e) {
                if (RedisMqConsumerContainer.run) {
                    log.error("Queue:{}", queue, e);
                } else {
                    log.info("QueueListener exits...queue:{}", queue);
                }
            }
        }
    }
}