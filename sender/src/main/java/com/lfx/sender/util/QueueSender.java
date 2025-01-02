package com.lfx.sender.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: lifuxing
 * @createDate: 2021/12/24
 * @description: 消息-生产者
 */
@Component
public class QueueSender {
    private static final String queueName = "zpQueue";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public QueueSender(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendMsg(String queue, Object msg) {
        //判断消息队列长度，满足条件再添加数据到队列
        long length = redisTemplate.opsForList().size(queueName);
        if (length < 1){
            redisTemplate.opsForList().rightPush(queue, msg);
        }
    }
}