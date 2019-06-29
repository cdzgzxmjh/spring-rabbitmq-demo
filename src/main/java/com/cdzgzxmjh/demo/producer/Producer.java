package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.stereotype.Component;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:17
 */
@Component
public class Producer {
    @Autowired
    private RabbitTemplate template;

    public void send(String msg) {
        template.convertAndSend(msg);
    }
}
