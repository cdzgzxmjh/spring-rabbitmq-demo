package com.cdzgzxmjh.demo.producer;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author maijiaheng
 * @date 2019/6/29 11:23
 */
@Configuration
public class ProducerConfiguration {
    public static String DEMO_QUEUE_NAME = "demo-1-queue";
    public static String DEMO_EXCHANGE = "demo-exchange-direct-m";
    public static String DEMO_CHANNEL_EXCHANGE = "demo-exchange-channel-m";
    public static String BASIC_DIRECT_ROUTING_KEY = "amqp";

    @Bean
    public ConnectionFactory initConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");
        factory.setPublisherConfirms(true);
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
        return new DirectExchange(DEMO_EXCHANGE);
    }

    /**
     * 定义一个queue，初次执行交互时实际创建
     * @return
     */
    @Bean
    public Queue initQueue() {
        return new Queue(DEMO_QUEUE_NAME);
    }

    /**
     * 定义一个binding
     * @param admin
     * @return
     */
    @Autowired
    public void initBinding(AmqpAdmin admin) {
        admin.declareExchange(new FanoutExchange(DEMO_CHANNEL_EXCHANGE, false, false));
        /*
         * param 0 : 绑定关系中的被路由目标，如 EXCHANGE_A -> QUEUE_B，此处为QUEUE_B名称
         * param 1 : 绑定关系中的被路由目标类型，见Binding.DestinationType
         * param 2 : exchange
         * param 3 : routing key
         * param 4 : 参数Map
         */
        admin.declareBinding(new Binding(DEMO_QUEUE_NAME, Binding.DestinationType.QUEUE, DEMO_CHANNEL_EXCHANGE, "", null));
        admin.declareBinding(new Binding(DEMO_CHANNEL_EXCHANGE, Binding.DestinationType.EXCHANGE, DEMO_EXCHANGE, BASIC_DIRECT_ROUTING_KEY, null));
    }

    @Bean
    @Autowired
    public RabbitTemplate initRabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setExchange(DEMO_EXCHANGE);
        template.setRoutingKey(BASIC_DIRECT_ROUTING_KEY);

        // 用于confirm确认监听，基于spring的rabbitTemplate采用此方式进行异步confirm
        template.setConfirmCallback(new CustomCallback());

        return template;
    }
}
