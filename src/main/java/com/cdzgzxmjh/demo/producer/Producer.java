package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:17
 */
@Service
public class Producer {
    @Autowired
    private RabbitTemplate template;

    @Transactional(rollbackFor = Exception.class)
    public void send(String msg) {
        template.convertAndSend(msg + " " + 1);
        template.convertAndSend(msg + " " + 2);
        // 人为的异常
//        System.out.println(1 / 0);
        template.convertAndSend(msg + " " + 3);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handle() {
        Object obj = template.receiveAndConvert();
        String str = "";
        if (Objects.nonNull(obj)) {
            str = obj.toString();
            System.out.println(str);
        } else {
            System.out.println("empty queue");
        }
        // 人为的异常
        System.out.println(1 / 0);
        template.convertAndSend(str + LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
    }
}
