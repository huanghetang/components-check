package com.tdh.common.component.custom.impl;

import com.rabbitmq.client.*;
import com.tdh.common.component.config.CheckConfig;
import com.tdh.common.component.custom.ComponentCheck;
import com.tdh.common.component.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @ClassName: RabbitmqCheck
 * @Description:
 * @Author zm
 * @Date 2018/11/14 17:14
 **/
@Component
public class RabbitmqCheck implements ComponentCheck {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitmqCheck.class);

    @Autowired
    private CheckConfig checkConfig;

    public static volatile boolean startRabbitMQThread = false; //开启RabbitMQ线程

    private LinkedList<String> rabbitLinkedList = new LinkedList<String>();//消息队列

    public Map<String, String> checkStatus() {

        Map<String, String> statusMap = new LinkedHashMap<String, String>();

        CachingConnectionFactory connectionFactory = null;

        try {
            String contextPath = checkConfig.getProjectName();

            InetAddress address = InetAddress.getLocalHost();

            String hostName = address.getHostName().toString(); //获取本机计算机名称

            ApplicationContext applicationContext = SpringUtil.getApplicationContext();//获取容器

            connectionFactory = SpringUtil.getBean("connectionFactory", CachingConnectionFactory.class); //从容器中获取RabbitMQ配置
            final CachingConnectionFactory connectionFactory1 = connectionFactory;
            if (!startRabbitMQThread) {
                startRabbitMQThread = true;//保证MQ生产者定时器和MQ消费者阻塞线程开启一次

                rabbitSchedule(connectionFactory1);//MQ生产者定时器

                new Thread(new Runnable() {
                    public void run() {
                        rabbitReceive(connectionFactory1);//MQ消费者阻塞线程
                    }
                }).start();
            }
            if (rabbitLinkedList.size() < 3) {
                statusMap.put(contextPath + "_" + hostName + "_RabbitMQ", "1");
            } else {
                statusMap.put(contextPath + "_" + hostName + "_RabbitMQ", "0");
            }
        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("spring容器中没有配置ConnectionFactory");
        } catch (UnknownHostException e) {
            LOG.warn("获取主机名错误", e);
        }

        return statusMap;
    }

    public boolean isCheck() {
        return checkConfig.getRabbitmq();
    }

    public boolean confirmCheck(String beanName) {
        return false;
    }


    //rabbit定时器发消息
    public void rabbitSchedule(final CachingConnectionFactory connectionFactory) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    org.springframework.amqp.rabbit.connection.Connection connection = connectionFactory.createConnection();

                    Channel channel = connection.createChannel(false);

                    channel.queueDeclare(checkConfig.getRabbitQueueName(), false, false, false, null);

                    String message = "checkRabbitMQ";

                    channel.basicPublish("", checkConfig.getRabbitQueueName(), null, message.getBytes("utf-8"));

                    System.out.println("rabbit schedule Sent>>>>>>>>>>>>>>>>>>>>>>>>'" + message + "'");
                    if (rabbitLinkedList.size() > 10) {
                        rabbitLinkedList.clear();
                    }
                    rabbitLinkedList.addLast(message);

                    channel.close();

                    connection.close();

                } catch (Exception e) {
                    LOG.error("测试RabbitMQ组件时，发送消息错误。", e);
                }
            }
        }, 0, checkConfig.getRabbitAwaitTime());
    }

    //rabbit定时任务消费者
    public void rabbitReceive(CachingConnectionFactory connectionFactory) {
        try {
            org.springframework.amqp.rabbit.connection.Connection connection = connectionFactory.createConnection();

            Channel channel = connection.createChannel(false);

            channel.queueDeclare(checkConfig.getRabbitQueueName(), false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");

                    if ("checkRabbitMQ".equals(message) && rabbitLinkedList.size() > 0) {

                        rabbitLinkedList.remove();
                        System.out.println("rabbit Received>>>>>>>>>>>>>>>>>>>>>>>> '" + message + "'");
                    }


                }
            };

            channel.basicConsume(checkConfig.getRabbitQueueName(), true, consumer);//注册消费者

            System.in.read();

        } catch (Exception e) {
            LOG.error("测试RabbitMQ组件时，接受消息错误。", e);
        }
    }

}
