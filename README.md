# rabbitMq与springBoot的整合demo

基本的发送与接收

####git_brach : tx_amqp
基于外部事务(@Transactional)的事务控制整合<br>
这里有一个要(keng)点，SpringBoot的事务发起问题，即@Transactional的生效与处理问题<br>
要使@Transactional的外部事务生效，根据Spring的事务原理，需要满足两点:<br>
1.事务所在类的动态代理<br>
2.定义依赖注入的事务管理器(tx-manager)<br>
事实上，只需要定义一个tx-manager，spring在启动装配时就会对事务所在类进行代理化，通过BeanPostProcessor机制注册对应的动态代理类(具体基于AbstractAutoProxyCreator的扩展实现，更深入的源码逻辑有待进一步理清)，因此本demo定义了一个RabbitTransactionManager<br>
spring-jdbc的依赖中包含了基于JDBC的事务管理实现，因此整合了spring-jdbc的项目无需主动定义tx-manager即可实现@Transactional外部事务，网上大量的文章与git上不少demo都犯了这个错误，又主要表现为两点:<br>
1.使用了数据库，所以引入了依赖spring-jdbc的外部引用，并且将rabbitmq的操作与db操作混合在同一事务，以此判断直接使用@Transactional可以使外部事务的生效，而忽视了事务管理器的必要性<br>
2.部分文章将对rabbitmq的操作放在异常抛出之前，因而判定异常可以使mq操作回滚，实属大缪！！事实上由于缺少事务管理器定义，事务所在类根本没有被代理，更勿论事务生效与回滚。这种情况下的mq操作不生效仅仅是因为异常中断而已<br>
另外，使用@EnableTransactionManagement注解显式开启tx-manager的确可以使事务所在类发生代理注入，但是由于缺少实际注入的tx-manager对象，发生事务处理时必然报错，亦不可行