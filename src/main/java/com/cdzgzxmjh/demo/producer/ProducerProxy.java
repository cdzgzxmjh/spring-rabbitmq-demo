package com.cdzgzxmjh.demo.producer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author maijiaheng
 * @date 2019/7/2 11:00
 */
@Service
@Primary
public class ProducerProxy implements Producer {
    @Resource(name = "producerTarget")
    private Producer target;

    @Autowired
    private ConnectionFactory factory;

    @Override
    public void publish() {
        invoke(() -> target.publish());
    }

    @Override
    public void batchPublish() {
        invoke(() -> target.batchPublish());
    }

    @Override
    public void asynPublish() {
        invoke(() -> target.asynPublish());
    }

    private void invoke(Runnable o) {
        checkTarget();
        Connection connection = factory.createConnection();
        Channel channel = connection.createChannel(false);
        ProducerImpl producer = (ProducerImpl) target;
        try {
            producer.setChannel(channel);
            o.run();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            producer.clearChannel();
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            connection.close();
        }
    }

    private void checkTarget() {
        if (Objects.isNull(target)) {
            throw new NullPointerException();
        }
        if (!(target instanceof ProducerImpl)) {
            throw new RuntimeException("非法的代理类型");
        }
    }
}
