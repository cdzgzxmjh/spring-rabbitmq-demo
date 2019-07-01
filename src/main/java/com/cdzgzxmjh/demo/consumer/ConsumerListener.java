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
    @RabbitListener(queues = {"my.topic.one", "my.topic.two", "one.topic.one", "one.topic.zero",
            "q1.direct.0", "q1.direct.1", "q1.direct.2", "q1.fanout.0", "q1.fanout.1", "q1.fanout.2"})
    public void listen(Message msg) {
        System.out.println("Receive : " + new String(msg.getBody()));
    }
}
