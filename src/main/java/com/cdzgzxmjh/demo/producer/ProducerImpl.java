package com.cdzgzxmjh.demo.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author maijiaheng
 * @date 2019/6/29 12:17
 */
@Service("producerTarget")
public class ProducerImpl implements Producer {
    private ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<>();

    @Autowired
    private RabbitTemplate template;

    /**
     * 发送序号
     */
    private AtomicLong seq = new AtomicLong(0);

    /**
     * 待返回响应的有序Set，用于异步返回确认
     */
    private SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<>());

    private CustomConfirmListener confirmListener = new CustomConfirmListener(confirmSet);

    @Override
    public void send() {
        for (int i=0; i<5; i++) {
            CorrelationData correlationData = new CorrelationData(Long.toString(seq.incrementAndGet()));
            template.convertAndSend(template.getExchange(), template.getRoutingKey(), System.currentTimeMillis(), correlationData);
        }
    }

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
            channel.waitForConfirmsOrDie();
            System.out.println("批次发送成功");
        } catch (IOException | InterruptedException e) {
            System.out.println("批次发送失败");
            e.printStackTrace();
        }
    }

    @Override
    public void asynPublish() {
        Channel channel = checkChannel();
        try {
            channel.addConfirmListener(confirmListener);
            channel.confirmSelect();
            for (int i=0; i<5; i++) {
                // 如果通过单一Channel发送可以通过getNextPublishSeqNo获取序号
                Long seqNo = channel.getNextPublishSeqNo();
                channel.basicPublish(ProducerConfiguration.DEMO_EXCHANGE
                        , ProducerConfiguration.BASIC_DIRECT_ROUTING_KEY
                        , MessageProperties.PERSISTENT_BASIC
                        , (i + " : " +  LocalDateTime.now().format(DateTimeFormatter.ISO_TIME))
                                .getBytes(Charset.defaultCharset()));
                // 加入待响应数据集合
                confirmSet.add(seqNo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
