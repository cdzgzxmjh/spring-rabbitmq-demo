package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author maijiaheng
 * @date 2019/7/2 17:05
 */
public class CustomCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String ackId = correlationData.getId();
        if (ack) {
            System.out.println("异步confirm : ack - " + ackId);
        } else {
            System.out.println("异步confirm : nack - " + ackId);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // TODO
    }
}
