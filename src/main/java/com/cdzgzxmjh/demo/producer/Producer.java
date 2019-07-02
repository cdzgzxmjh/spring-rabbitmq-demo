package com.cdzgzxmjh.demo.producer;

/**
 * @author maijiaheng
 * @date 2019/7/2 10:58
 */
public interface Producer {
    void publish();

    void batchPublish();

    void asynPublish();
}
