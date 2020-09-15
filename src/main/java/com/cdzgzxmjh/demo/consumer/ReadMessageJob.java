package com.cdzgzxmjh.demo.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author maijiaheng
 * @date 2020/9/15 14:13
 */
@Component
public class ReadMessageJob {
    @Autowired
    private ConnectionFactory connectionFactory;

    @Scheduled(cron = "0/1 * * * * * ")
    public void readMessage() {
        try (Connection connection = connectionFactory.createConnection();
             Channel channel = connection.createChannel(false)) {
            int remainMessageCount;
            do {
                GetResponse response = channel.basicGet(ConsumerConfiguration.DEMO_QUEUE_NAME, false);
                if (Objects.nonNull(response)) {
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    remainMessageCount = response.getMessageCount();
                    System.out.println(new String(response.getBody()));
                    channel.basicAck(deliveryTag, response.getMessageCount() > 1);
                } else {
                    remainMessageCount = 0;
                }
            } while (remainMessageCount > 0);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
