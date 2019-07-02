package com.cdzgzxmjh.demo.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:21
 */
@RestController
public class ProducerController {
    @Autowired
    private Producer producer;

    @GetMapping("/rabbitmq/publish")
    public String publish() {
        producer.publish();
        return "Ok";
    }

    @GetMapping("/rabbitmq/batch")
    public String batchPublish() {
        producer.batchPublish();
        return "Ok";
    }

    @GetMapping("/rabbitmq/asyn")
    public String asyn() {
        producer.asynPublish();
        return "Ok";
    }

    @GetMapping("/rabbitmq/template")
    public String send() {
        producer.send();
        return "Ok";
    }
}
