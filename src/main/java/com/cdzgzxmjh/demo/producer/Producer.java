package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:17
 */
@Component
public class Producer {
    @Resource(name = "templateRegistry")
    private Map<String, RabbitTemplate> templates;

    public boolean send(String msg, String type) {
        RabbitTemplate template = templates.get(type);
        if (Objects.isNull(template)) {
            return false;
        }
        template.convertAndSend(msg);
        return true;
    }

    public boolean sendTopic(String msg, String routing) {
        RabbitTemplate template = templates.get(ExchangeTypes.TOPIC);
        if (Objects.isNull(template)) {
            return false;
        }
        template.convertAndSend(routing, msg);
        return true;
    }
}
