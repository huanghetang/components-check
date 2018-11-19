package com.tdh.common.component.custom.impl;

import com.tdh.common.component.config.CheckConfig;
import com.tdh.common.component.custom.ComponentCheck;
import com.tdh.common.component.listener.ReceiveListener;
import com.tdh.common.component.utils.SpringUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.jms.*;
import javax.jms.Queue;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @ClassName: ActivemqCheck
 * @Description:
 * @Author zm
 * @Date 2018/11/14 17:23
 **/
@Component
public class ActivemqCheck implements ComponentCheck {

    private static final Logger LOG = LoggerFactory.getLogger(ActivemqCheck.class);

    @Autowired
    private CheckConfig checkConfig;

    public static boolean startActiveMQThread = false; //开启ActiveMQ线程

    public static volatile LinkedList<String> activeLinkedList = new LinkedList<String>();//消息队列

    public Map<String, String> checkStatus() {

        Map<String, String> statusMap = new LinkedHashMap<String, String>();
        try {
            String contextPath = checkConfig.getProjectName();

            InetAddress address = InetAddress.getLocalHost();

            String hostName = address.getHostName().toString(); //获取本机计算机名称

            ApplicationContext applicationContext = SpringUtil.getApplicationContext();//获取容器

            ActiveMQConnectionFactory activeMQConnectionFactory = SpringUtil.getBean("activeMqConnectionFactory", ActiveMQConnectionFactory.class);// //从容器中获取ActiveMQ配置

            final ActiveMQConnectionFactory activeMQConnectionFactory1 = activeMQConnectionFactory;

            if (!startActiveMQThread) {

                startActiveMQThread = true;//保证MQ生产者定时器和MQ消费者阻塞线程开启一次

                activeSchedule(activeMQConnectionFactory1);//MQ生产者定时器

                new Thread(new Runnable() {
                    public void run() {
                        activeReceive(activeMQConnectionFactory1);//MQ消费者阻塞线程
                    }
                }).start();
            }
            if (activeLinkedList.size() < 3) {
                statusMap.put(contextPath + "_" + hostName + "_ActiveMQ", "1");
            } else {
                statusMap.put(contextPath + "_" + hostName + "_ActiveMQ", "0");

            }
        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("spring容器中没有配置ActiveMQConnectionFactory");
        } catch (UnknownHostException e) {
            LOG.warn("获取主机名错误", e);
        }

        return statusMap;
    }

    public boolean isCheck() {
        return checkConfig.getActivemq();
    }

    public boolean confirmCheck(String beanName) {
        return false;
    }


    //active定时器发消息
    public void activeSchedule(final ActiveMQConnectionFactory activeMQConnectionFactory) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Connection connection = null;
                Session session = null;
                MessageProducer producer = null;
                try {

                    connection = activeMQConnectionFactory.createConnection();
                    //发送消息
                    session = connection.createSession(true, Session.SESSION_TRANSACTED);
                    //获取消息发送的目的地，指消息发往那个地方
                    Queue queue = session.createQueue(checkConfig.getActiveQueueName());
                    //获取消息发送的生产者
                    producer = session.createProducer(queue);

                    TextMessage msg = session.createTextMessage("checkActiveMqMSG");

                    System.out.println("active schedule send>>>>>>>>>>>>>>>>>>>>>>>>>>checkActiveMqMSG");

                    producer.send(msg);//发送消息
                    if (activeLinkedList.size() > 10) {
                        activeLinkedList.clear();
                    }
                    activeLinkedList.addLast(msg.getText());
                    session.commit();

                } catch (Exception e) {
                    LOG.error("测试ActiveMQ组件发送消息错误", e);

                } finally {
                    if (producer != null) {
                        try {
                            producer.close();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                    if (session != null) {
                        try {
                            session.close();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 0, checkConfig.getActiveAwaitTime());
    }

    private void activeReceive(ActiveMQConnectionFactory activeMQConnectionFactory) {
        try {
            //从工厂中创建一个链接
            Connection connection = activeMQConnectionFactory.createConnection();
            //启动链接
            connection.start();
            //创建一个事物session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue queue = session.createQueue(checkConfig.getActiveQueueName());

            MessageConsumer consumer = session.createConsumer(queue);
            //设置消息监听器
            consumer.setMessageListener(new ReceiveListener());

            System.in.read();
        } catch (Exception e) {
            LOG.error("测试ActiveMQ组件接受消息错误", e);
        }

    }

}




