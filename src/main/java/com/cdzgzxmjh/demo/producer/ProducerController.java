package com.cdzgzxmjh.demo.producer;

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

    @GetMapping("/rabbitmq/produce")
    public String send() {
        producer.send(LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
        return "Ok";
    }

    @GetMapping("/rabbitmq/handle")
    public String handle() {
        producer.handle();
        return "Ok";
    }
}
