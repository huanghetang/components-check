package com.tdh.common.component.listener;

import com.tdh.common.component.custom.impl.ActivemqCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * active消费者监听
 */
public class ReceiveListener implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiveListener.class);

    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;
            if (msg != null && "checkActiveMqMSG".equals(msg.getText())) {
                System.out.println("active receive >>>>>>>>>>>>>>>>>>>>>>>>>>" + msg.getText());
                if (ActivemqCheck.activeLinkedList.size() > 0) {
                    ActivemqCheck.activeLinkedList.remove();
                }
            }
        } catch (JMSException e) {
            LOG.error("测试ActiveMQ组件接受消息错误", e);
        }
    }

}