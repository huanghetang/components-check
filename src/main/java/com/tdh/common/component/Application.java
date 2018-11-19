//package com.tdh.common.component;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
//import org.springframework.context.annotation.ImportResource;
//
///**
// * @ClassName: Application
// * @Description:
// * @Author zm
// * @Date 2018/11/2 10:21
// **/
//@SpringBootApplication
////@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
//        DataSourceTransactionManagerAutoConfiguration.class, RabbitAutoConfiguration.class})
//@ImportResource({"spring/redis-context.xml","spring/producer.xml","spring/activeMQ.xml"})
////@ImportResource({"spring/redis-context.xml"})
//public class Application {
//
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
//}
