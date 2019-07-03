# rabbitMq与springBoot的整合demo

基本的发送与接收

#### git_branch : consumer_ack_demo
消费端ack机制的整合与测试<br>
采用基于spring的rabbitListen的方式进行整合，异步监听机制，比较优雅<br>
关于整合的逻辑与API用法请参见com.cdzgzxmjh.demo.consumer.ConsumerListener注释<br>
关于Message转换与Payload转换/解码，请参见com.cdzgzxmjh.demo.consumer.ConsumerConfiguration.initConverter注释<br>
关于ack应答模式说明，请参见com.cdzgzxmjh.demo.consumer.ConsumerConfiguration.rabbitListenerContainerFactory代码注释<br>
