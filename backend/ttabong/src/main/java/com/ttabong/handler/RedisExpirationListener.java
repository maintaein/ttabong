package com.ttabong.handler;

import com.ttabong.service.recruit.OrgRecruitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class RedisExpirationListener extends KeyExpirationEventMessageListener {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrgRecruitService orgRecruitService;

    public RedisExpirationListener(RedisMessageListenerContainer listenerContainer, OrgRecruitService orgRecruitService) {
        super(listenerContainer);
        this.orgRecruitService = orgRecruitService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String[] expireMessage = message.toString().split(" ");
        if(expireMessage[0].equals("EVENT_COMPLETE:")) {
            logger.info(message.toString() + "번 공고 활동 완료");
            orgRecruitService.updateScheduledStatus(Integer.parseInt(expireMessage[1]));
        }
    }
}
