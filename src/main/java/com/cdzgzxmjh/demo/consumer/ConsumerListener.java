package com.cdzgzxmjh.demo.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author maijiaheng
 * @date 2019/6/29 14:26
 */
@Component
@RabbitListener(queues = ConsumerConfiguration.DEMO_QUEUE_NAME)
public class ConsumerListener {
    /**
     * 此处有两种写法
     * 1.在方法上注解@RabbitListener，做单一类型的指定监听
     * 2.在类上做@RabbitListener，并在方法上注解@RabbitHandler，这种写法可以定
     * 义多个handler方法（分别注解@RabbitHandler即可），通过不同的类型做自动适配
     *
     * 此处使用第二种写法，需要对参数进行说明：
     * 1.必须含有与Payload对应类型的形参，否则找不到可用的适配器会报错
     * 2.默认注入两个参数，是否定义形参可选，分别是com.rabbitmq.client.Channel
     * 与org.springframework.amqp.core.Message (注意是org.springframework.amqp包下)
     * 3.Payload可以自定义转换，见ConsumerConfiguration.initConverter()
     * 与ConsumerConfiguration.rabbitListenerContainerFactory()
     * 4.相对应地，需要定义SimpleRabbitListenerContainerFactory
     *
     * 执行的时序链：
     * org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
     * -> org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer (worker模式监听)
     * -> org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter (适配)
     * -> Listener (invoke)
     *
     */
    @RabbitHandler
    public void process(String adaptedMessage, Channel channel, Message message) {
        System.out.println(adaptedMessage);
        // 模拟ImmediateAcknowledgeAmqpException抛错
//        throw new ImmediateAcknowledgeAmqpException("test");
        // 模拟普通运行时异常
//        int i = 1 / 0;
        try {
            /*
             * 记录确认(ack)
             * multiple : true 批次确认 false 单记录确认
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            /*
             * 记录否认(nack)
             * multiple : true 批次确认 false 单记录确认
             * requeue : 是否重新进入队列 false 则丢弃
             */
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            /*
             * 记录拒绝(reject)
             * requeue : 是否重新进入队列 false 则丢弃
             */
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            /*
             * 直接中断Channel通信
             */
//            channel.basicCancel(message.getMessageProperties().getConsumerTag());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
