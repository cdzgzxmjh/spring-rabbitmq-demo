package com.cdzgzxmjh.demo.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author maijiaheng
 * @date 2019/6/29 14:26
 */
@Component
public class ConsumerListener {
    @RabbitListener(queues = ConsumerConfiguration.DEMO_QUEUE_NAME)
    public void listen(Message msg) {
        System.out.println("Receive : " + new String(msg.getBody()));
    }
}
