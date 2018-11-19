package com.tdh.common.component.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: CheckConfig
 * @Description:
 * @Author zm
 * @Date 2018/11/14 9:53
 **/
@Component
public class CheckConfig {

    @Value("${tdh.component.check.projectName:noProjectName}")
    private String projectName;

    @Value("${tdh.component.check.db:true}")//默认为true表示检查组件
    private Boolean db;

    @Value("${tdh.component.check.redis:true}")
    private Boolean redis;

    @Value("${tdh.component.check.rabbitmq:true}")
    private Boolean rabbitmq;

    @Value("${tdh.component.check.activemq:true}")
    private Boolean activemq;


    @Value("${tdh.component.check.rabbitQueueName:check_rabbit_queue}")
    private String rabbitQueueName;

    @Value("${tdh.component.check.rabbitAwaitTime:4000}")
    private Long rabbitAwaitTime;

    @Value("${tdh.component.check.activeQueueName:check_active_queue}")
    private String activeQueueName;

    @Value("${tdh.component.check.activeAwaitTime:5000}")
    private Long activeAwaitTime;

    @Value("${tdh.component.check.excludeBeanName:null}")
    private String excludeBeanName;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean getDb() {

        return db;
    }

    public void setDb(boolean db) {
        this.db = db;
    }

    public boolean getRedis() {
        return redis;
    }

    public void setRedis(boolean redis) {
        this.redis = redis;
    }

    public boolean getRabbitmq() {
        return rabbitmq;
    }

    public void setRabbitmq(boolean rabbitmq) {
        this.rabbitmq = rabbitmq;
    }

    public boolean getActivemq() {
        return activemq ;
    }

    public void setActivemq(boolean activemq) {
        this.activemq = activemq;
    }

    public String getRabbitQueueName() {
        return rabbitQueueName;
    }

    public void setRabbitQueueName(String rabbitQueueName) {
        this.rabbitQueueName = rabbitQueueName;
    }

    public Long getRabbitAwaitTime() {
        return rabbitAwaitTime;
    }

    public void setRabbitAwaitTime(Long rabbitAwaitTime) {
        this.rabbitAwaitTime = rabbitAwaitTime;
    }

    public String getActiveQueueName() {
        return activeQueueName;
    }

    public void setActiveQueueName(String activeQueueName) {
        this.activeQueueName = activeQueueName;
    }

    public Long getActiveAwaitTime() {
        return activeAwaitTime;
    }

    public void setActiveAwaitTime(Long activeAwaitTime) {
        this.activeAwaitTime = activeAwaitTime;
    }

    /**
     * 解析要排除的beanId
     * @return
     */
    public List<String> getExcludeBeanId() {
        if ("null".equals(excludeBeanName)){
            return new ArrayList<String>();
        }
        String[] beanIds = excludeBeanName.split(",");
        return  Arrays.asList(beanIds);
    }

    public void setExcludeBeanId(String excludeBeanId) {
        this.excludeBeanName = excludeBeanId;
    }
}
