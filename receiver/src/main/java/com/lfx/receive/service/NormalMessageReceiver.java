package com.lfx.receive.service;

import com.lfx.receive.util.MsgConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class NormalMessageReceiver implements MsgConsumer {
    private static final Logger log = LoggerFactory.getLogger(NormalMessageReceiver.class);

    @Override
    public void onMessage(Object message) {
        log.info("收到消息:" + message);
        //处理接收到的数据
        List zpOidList = (List) message;
        for (int i = 0; i < zpOidList.size(); i++) {
            //具体的业务逻辑
        }
    }

    @Override
    public void onError(Object msg, Exception e) {
        log.error("发生错误,消息:{}", msg, e);
    }
}
