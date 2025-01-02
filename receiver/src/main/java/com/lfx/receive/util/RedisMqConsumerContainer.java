package com.lfx.receive.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lifuxing
 * @createDate: 2021/12/24
 * @description: 消息--消费者容器.
 */

public class RedisMqConsumerContainer {
    private static final Logger log = LoggerFactory.getLogger(RedisMqConsumerContainer.class);
    private Map<String, QueueConfiguration> consumerMap = new HashMap<>();
    private RedisTemplate<String, Object> redisTemplate;
    static boolean run;
    private ExecutorService exec;
    @Value("${thread.num}")
    private int thread;

    public RedisMqConsumerContainer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addConsumer(QueueConfiguration configuration) {
        if (consumerMap.containsKey(configuration.getQueue())) {
            log.warn("Key:{} this key already exists, and it will be replaced", configuration.getQueue());
        }
        if (configuration.getConsumer() == null) {
            log.warn("Key:{} consumer cannot be null, this configuration will be skipped", configuration.getQueue());
        }
        consumerMap.put(configuration.getQueue(), configuration);
    }

    public void destroy() {
        run = false;
        this.exec.shutdown();
        log.info("QueueListener exiting.");
        while (!this.exec.isTerminated()) {

        }
        log.info("QueueListener exited.");
    }

    public void init() {
        run = true;
        this.exec = Executors.newCachedThreadPool(r -> {
            final AtomicInteger threadNumber = new AtomicInteger(1);
            return new Thread(r, "RedisMQListener-" + threadNumber.getAndIncrement());
        });
        consumerMap = Collections.unmodifiableMap(consumerMap);
        for (int i = 0; i < thread; i++) {
            log.info("启动线程"+i);
            consumerMap.forEach((k, v) -> exec.submit(new QueueListener(redisTemplate, v.getQueue(), v.getConsumer())));
        }
    }
}