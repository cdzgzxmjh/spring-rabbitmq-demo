package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:21
 */
@RestController
public class ProducerController {
    @Autowired
    private Producer producer;

    @GetMapping("/rabbitmq/direct")
    public String sendDirect() {
        return producer.send(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME), ExchangeTypes.DIRECT) ? "OK" : "FAIL";
    }

    @GetMapping("/rabbitmq/fanout")
    public String sendFanout() {
        return producer.send(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME), ExchangeTypes.FANOUT) ? "OK" : "FAIL";
    }

    @GetMapping("/rabbitmq/topic")
    public String sendTopic() {
        producer.sendTopic(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME), "1233");
        producer.sendTopic(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME), "gg.one.one");
        producer.sendTopic(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME), "my.quick.two");
        producer.sendTopic(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME), "b.one");
        return "OK";
    }
}
