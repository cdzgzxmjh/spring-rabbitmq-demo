package com.cdzgzxmjh.demo.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:17
 */
@Service("producerTarget")
public class ProducerImpl implements Producer {
    private ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

    @Override
    public void publish() {
        Channel channel = checkChannel();
        try {
            channel.confirmSelect();
            for (int i=0; i<5; i++) {
                channel.basicPublish(ProducerConfiguration.DEMO_EXCHANGE
                        , ProducerConfiguration.BASIC_DIRECT_ROUTING_KEY
                        , MessageProperties.PERSISTENT_BASIC
                        , (i + " : " +  LocalDateTime.now().format(DateTimeFormatter.ISO_TIME))
                                .getBytes(Charset.defaultCharset()));
                if (channel.waitForConfirms()) {
                    System.out.println(i + " : 发送成功");
                } else {
                    System.out.println(i + " : 发送失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchPublish() {
        Channel channel = checkChannel();
        try {
            channel.confirmSelect();
            for (int i=0; i<5; i++) {
                channel.basicPublish(ProducerConfiguration.DEMO_EXCHANGE
                        , ProducerConfiguration.BASIC_DIRECT_ROUTING_KEY
                        , MessageProperties.PERSISTENT_BASIC
                        , (i + " : " +  LocalDateTime.now().format(DateTimeFormatter.ISO_TIME))
                                .getBytes(Charset.defaultCharset()));
            }
            if (channel.waitForConfirms()) {
                System.out.println("批次发送成功");
            } else {
                System.out.println("批次发送失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void asynPublish() {

    }

    public void setChannel(Channel channel) {
        channelThreadLocal.set(channel);
    }

    public void clearChannel() {
        channelThreadLocal.remove();
    }

    private Channel checkChannel() {
        Channel channel;
        if (Objects.isNull(channel = channelThreadLocal.get())
                || !channel.isOpen()) {
            throw new RuntimeException("未找到启用的channel");
        }
        return channel;
    }
}
