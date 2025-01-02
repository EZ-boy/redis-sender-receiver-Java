package com.lfx.receive.util;

/**
 * @author: lifuxing
 * @createDate: 2021/12/24
 * @description: 消费接口类.
 */

public interface MsgConsumer {
    void onMessage(Object message);

    void onError(Object msg, Exception e);
}