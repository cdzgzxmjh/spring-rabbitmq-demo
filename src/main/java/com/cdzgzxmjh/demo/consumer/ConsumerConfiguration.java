package com.cdzgzxmjh.demo.consumer;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
