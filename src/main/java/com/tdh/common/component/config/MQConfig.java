//package com.tdh.common.component.config;
//
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @ClassName: MQConfig
// * @Description: 注册mq连接工厂模板
// * @Author zm
// * @Date 2018/11/6 16:39
// **/
//@Configuration
//public class MQConfig {
//
//    @Bean
//    public CachingConnectionFactory connectionFactory() {
//        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
//        cachingConnectionFactory.setHost("192.168.1.132");
//        cachingConnectionFactory.setUsername("guest");
//        cachingConnectionFactory.setPassword("guest");
//        cachingConnectionFactory.setPort(5672);
//        return cachingConnectionFactory;
//
//    }
//}
