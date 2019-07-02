package com.cdzgzxmjh.demo.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

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
        Channel channel = checkChannel();
        // 待返回响应的有序Set
        SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<>());
        try {
            channel.addConfirmListener(new ConfirmListener() {
                private Long currentSeq = 0L;
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    synchronized (currentSeq) {
                        System.out.println("成功处理数量：" + (deliveryTag - currentSeq));
                        currentSeq = deliveryTag;
                    }
                    if (multiple) {
                        // 全批数据完成
                        System.out.println("批次发送成功");
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        System.out.println(deliveryTag + ":发送成功");
                        confirmSet.remove(deliveryTag);
                    }
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    synchronized (currentSeq) {
                        System.out.println("失败处理数量：" + (deliveryTag - currentSeq));
                        currentSeq = deliveryTag;
                    }
                    if (multiple) {
                        // 全批数据失败
                        System.out.println("批次发送失败");
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        System.out.println(deliveryTag + ":发送失败");
                        confirmSet.remove(deliveryTag);
                    }
                }
            });
            channel.confirmSelect();
            for (int i=0; i<5; i++) {
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
