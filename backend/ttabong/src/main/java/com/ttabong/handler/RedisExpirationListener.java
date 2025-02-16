package com.ttabong.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class RedisExpirationListener extends KeyExpirationEventMessageListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public RedisExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info(message.toString() + "번 공고 활동 완료");
    }
}
