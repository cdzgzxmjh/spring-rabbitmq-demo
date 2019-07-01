package com.cdzgzxmjh.demo.common;

import org.springframework.amqp.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maijiaheng
 * @date 2019/7/1 9:43
 */
public class BaseUtils {
    private BaseUtils() {
    }

    public static Queue newQueue(String name) {
        Queue queue = new Queue(name);
        return queue;
    }

    public static List<Queue> newQueues(String prefix, int size) {
        List<Queue> queueList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            queueList.add(newQueue(prefix + i));
        }
        return queueList;
    }

    public static List<Binding> newBinding(String exchange, List<Queue> queueList, String routingKeyPrefix) {
        int i = 0;
        List<Binding> bindingList = new ArrayList<>();
        for (Queue queue : queueList) {
            Binding binding = new Binding(queue.getName(), Binding.DestinationType.QUEUE, exchange, routingKeyPrefix + i++, null);
            bindingList.add(binding);
        }
        return bindingList;
    }

    public static Exchange newExchange(String name, String type) {
        ExchangeBuilder builder = new ExchangeBuilder(name, type);
        return builder.build();
    }
}
