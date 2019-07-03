package com.cdzgzxmjh.demo.consumer;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

/**
 * @author maijiaheng
 * @date 2019/6/29 11:23
 */
@Configuration
public class ConsumerConfiguration {
    public static final String DEMO_QUEUE_NAME = "demo-1-queue";

    /**
     * 以API形式定义ConnectionFactory，如果不做定义，spring会以默认配置创建
     * 缺省的ConnectionFactory，详见
     * org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration$RabbitConnectionFactoryCreator.rabbitConnectionFactory
     * 参数取自 org.springframework.boot.autoconfigure.amqp.RabbitProperties 默认值
     * 可以通过spring.rabbit 进行properties形式的自定义设置
     * @return
     */
    @Bean
    public ConnectionFactory initConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");
        return factory;
    }

    /**
     * 生成ListenerContainer的工厂类，Container作为消息监听Consumer
     * @param connectionFactory
     * @param converter
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        /*
         * 设置ack应答模式
         * AcknowledgeMode.MANUAL 手动应答，数据获取后会根据手动应答进行ack反馈，
         * 如果出现异常，除非出现连接中断，否则channel依然会持续等待应答
         * AcknowledgeMode.NONE 相当于RabbitMq的autoAck=true，即自动ack应答，只要
         * 获取到消息，马上给出ack有效应答，不等待任何后续处理
         * AcknowledgeMode.AUTO 默认值，根据具体的执行或异常进行应答，具体如下：
         * 1.正常完成执行，返回ack应答
         * 2.抛出AmqpRejectAndDontRequeueException，消息拒绝，且不返回队列
         * 3.抛出ImmediateAcknowledgeAmqpException，返回ack应答
         * 4.其他异常，消息被拒绝，且默认返回队列，因此会造成内存运行中的死循环，
         * 为避免此问题，应用AcknowledgeMode.AUTO应该设置默认拒绝重入队操作为false
         */
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        /*
         * 设置默认拒绝情况下不做重入队，避免死循环
         */
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    /**
     * 定义转换类型，fromMessage从spring-amqp的Message对象转为spring-message包
     * 的Message对象，返回type为spring-message的Message对象对应的泛型Type，与
     * Listener中的适配对象对应
     * @return
     */
    @Bean
    public MessageConverter initConverter() {
        return new AbstractMessageConverter() {
            @Override
            protected Message createMessage(Object object, MessageProperties messageProperties) {
                return new Message((object.toString()).getBytes(), messageProperties);
            }

            @Override
            public String fromMessage(Message message) throws MessageConversionException {
                return new String(message.getBody(), Charset.defaultCharset());
            }
        };
    }
}
