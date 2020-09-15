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
        for (int i = 0; i < 5; i++) {
            producer.send(i + ":" + LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
        }
        return "Ok";
    }
}
