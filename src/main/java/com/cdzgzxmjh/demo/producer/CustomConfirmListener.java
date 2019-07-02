package com.cdzgzxmjh.demo.producer;

import com.rabbitmq.client.ConfirmListener;

import java.io.IOException;
import java.util.SortedSet;

/**
 * @author maijiaheng
 * @date 2019/7/2 17:29
 */
public class CustomConfirmListener implements ConfirmListener {
    private SortedSet<Long> confirmSet;

    private volatile Long currentSeq = 0L;

    public CustomConfirmListener(SortedSet<Long> confirmSet) {
        this.confirmSet = confirmSet;
    }

    @Override
    public void handleAck(long deliveryTag, boolean multiple) throws IOException {
        synchronized (currentSeq) {
            if (deliveryTag == currentSeq) {
                return ;
            }
            // 做差统计仅用于demo，并发条件下做差无效
            System.out.println("成功处理数量：" + (deliveryTag - currentSeq) + " | tag : " + deliveryTag + " | this : " + this);
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
            // 做差统计仅用于demo，并发条件下做差无效
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
}
