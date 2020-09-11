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
    @RabbitListener(queues = ConsumerConfiguration.DEMO_QUEUE_NAME_1)
    public void listen1(Message msg) {
        System.out.println("Receive from queue -- " + ConsumerConfiguration.DEMO_QUEUE_NAME_1 + ": " + new String(msg.getBody()));
    }

    @RabbitListener(queues = ConsumerConfiguration.DEMO_QUEUE_NAME_2)
    public void listen2(Message msg) {
        System.out.println("Receive from queue -- " + ConsumerConfiguration.DEMO_QUEUE_NAME_2 + ": " + new String(msg.getBody()));
    }
}
