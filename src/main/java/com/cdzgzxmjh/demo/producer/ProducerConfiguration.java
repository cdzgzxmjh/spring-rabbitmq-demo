package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * @author maijiaheng
 * @date 2019/6/29 11:23
 */
@Configuration
public class ProducerConfiguration {
    private String demoQueueName = "demo-1-queue";
    private String demoExchange = "demo-exchange-direct-m";

    @Bean
    public ConnectionFactory initConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");
        factory.setPublisherConfirms(false);
        return factory;
    }

    /**
     * 定义一个管理器，用于动态管理broken状态
     * @param factory
     * @return
     */
    @Bean
    @Autowired
    public AmqpAdmin initAmqpAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    /**
     * 定义一个exchange，初次执行交互时实际创建
     * @return
     */
    @Bean
    public Exchange initExchange() {
        return new DirectExchange(demoExchange);
    }

    /**
     * 定义一个queue，初次执行交互时实际创建
     * @return
     */
    @Bean
    public Queue initQueue() {
        return new Queue(demoQueueName);
    }

    /**
     * 定义一个binding
     * @param admin
     * @return
     */
    @Autowired
    public Binding initBinding(AmqpAdmin admin) {
        /*
         * param 0 : 绑定关系中的被路由目标，如 EXCHANGE_A -> QUEUE_B，此处为QUEUE_B名称
         * param 1 : 绑定关系中的被路由目标类型，见Binding.DestinationType
         * param 2 : exchange
         * param 3 : routing key
         * param 4 : 参数Map
         */
        Binding binding = new Binding(demoQueueName, Binding.DestinationType.QUEUE, demoExchange, "amqp", null);
        admin.declareBinding(binding);
        return binding;
    }

    @Bean
    @Autowired
    public RabbitTemplate initRabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(demoExchange);
        template.setRoutingKey("amqp");
        template.setChannelTransacted(true);
        template.setDefaultReceiveQueue(demoQueueName);

        return template;
    }

    /**
     * 在没有其他tx-manager的情况下，必须至少创建一个，否则无法进行@Transaction
     * 的动态代理，从而也无法形成外部事务
     * @param factory
     * @return
     */
    @Bean
    @Autowired
    @ConditionalOnMissingBean(value = AbstractPlatformTransactionManager.class)
    public RabbitTransactionManager initRabbitTransactionManager(ConnectionFactory factory) {
        RabbitTransactionManager txManager = new RabbitTransactionManager(factory);
        return txManager;
    }
}
