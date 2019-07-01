package com.cdzgzxmjh.demo.producer;

import com.cdzgzxmjh.demo.common.BaseUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author maijiaheng
 * @date 2019/6/29 11:23
 */
@Configuration
public class ProducerConfiguration {
    private String[] exchangeType = {ExchangeTypes.DIRECT, ExchangeTypes.FANOUT, ExchangeTypes.TOPIC};
    private int queueNum = 3;

    private Map<String, List<Queue>> queueStorage;

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
     * 定义exchange
     * @return
     */
    @Bean("exchangeRegistry")
    @Autowired
    public Map<String, Exchange> initExchanges(AmqpAdmin admin) {
        Map<String, Exchange> exchangeMap = new HashMap<>();
        for (String type : exchangeType) {
            Exchange exchange = BaseUtils.newExchange(getExchangeName(type), type);
            admin.declareExchange(exchange);
            exchangeMap.put(type, exchange);
        }
        return exchangeMap;
    }

    /**
     * 定义queue
     * @return
     */
    @Bean("queueStorage")
    @Autowired
    public Map<String, List<Queue>> initQueues(AmqpAdmin admin) {
        return getQueueStorage(admin);
    }

    private Map<String, List<Queue>> getQueueStorage(AmqpAdmin admin) {
        if (Objects.isNull(queueStorage)) {
            Map<String, List<Queue>> queueMap = new HashMap<>();
            for (String type : exchangeType) {
                List<Queue> queues;
                if (ExchangeTypes.TOPIC.equals(type)) {
                    queues = new ArrayList<>();
                    queues.add(new Queue("one.topic.zero"));
                    queues.add(new Queue("one.topic.one"));
                    queues.add(new Queue("my.topic.one"));
                    queues.add(new Queue("my.topic.two"));
                } else {
                    queues = BaseUtils.newQueues(getQueuePrefix(type), queueNum);
                }
                for (Queue queue : queues) {
                    admin.declareQueue(queue);
                }
                queueMap.put(type, queues);
            }
            queueStorage = queueMap;
        }
        return queueStorage;
    }

    @Bean
    @Autowired
    public List<Binding> initBindings(AmqpAdmin admin) {
        List<Binding> bindingList = new ArrayList<>();
        Map<String, List<Queue>> queueMap = getQueueStorage(admin);
        for (String type : exchangeType) {
            List<Binding> bindings;
            if (ExchangeTypes.TOPIC.equals(type)) {
                /*
                 * 专门对topic类型的绑定做特别的定义，绑定名称以英文符号'.'分割
                 * ，#匹配0或多个单词，*匹配1个单词
                 */
                bindings = new ArrayList<>();
                bindings.add(new Binding("one.topic.zero", Binding.DestinationType.QUEUE, getExchangeName(type), "#", null));
                bindings.add(new Binding("one.topic.one", Binding.DestinationType.QUEUE, getExchangeName(type), "#", null));
                bindings.add(new Binding("my.topic.one", Binding.DestinationType.QUEUE, getExchangeName(type), "*.*.one", null));
                bindings.add(new Binding("my.topic.two", Binding.DestinationType.QUEUE, getExchangeName(type), "#.two", null));
            } else {
                bindings = BaseUtils.newBinding(getExchangeName(type), queueMap.get(type), getRoutingKeyPrefix(type));
            }
            bindingList.addAll(bindings);
            for (Binding binding : bindings) {
                admin.declareBinding(binding);
            }
        }
        return bindingList;
    }

    @Bean("templateRegistry")
    @Autowired
    public Map<String, RabbitTemplate> initRabbitTemplates(ConnectionFactory factory) {
        Map<String, RabbitTemplate> templates = new HashMap<>();

        for (String type : exchangeType) {
            RabbitTemplate template = new RabbitTemplate(factory);
            template.setExchange(getExchangeName(type));
            template.setRoutingKey(getRoutingKeyPrefix(type) + "1");
            templates.put(type, template);
        }

        return templates;
    }

    private String getExchangeName(String type) {
        return "t1." + type;
    }

    private String getQueuePrefix(String type) {
        return "q1." + type + ".";
    }

    private String getRoutingKeyPrefix(String type) {
        return "r1." + type + ".";
    }
}
