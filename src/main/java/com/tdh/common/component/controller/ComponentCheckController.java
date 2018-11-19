package com.tdh.common.component.controller;


import com.tdh.common.component.config.CheckConfig;
import com.tdh.common.component.custom.ComponentCheck;
import com.tdh.common.component.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName: ComponentCheckController
 * @Description: 组件检查接口
 * @Author zm
 * @Date 2018/11/2 9:29
 **/
@Controller
@RequestMapping("component")
public class ComponentCheckController {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentCheckController.class);



    @Autowired
    private CheckConfig checkConfig;


    @RequestMapping(value = "check", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, String>> check() {

        Map<String, String> statusMap = new LinkedHashMap<String, String>();

        statusMap.put("code", "200");
        try {
            String contextPath = checkConfig.getProjectName();

            try {
                ApplicationContext applicationContext = SpringUtil.getApplicationContext();//获取容器

                String[] beanNamesForType = applicationContext.getBeanNamesForType(ComponentCheck.class);//获取所有组件

                for (String beanName : beanNamesForType) {

                    ComponentCheck thisComponentCheck = SpringUtil.getBean(beanName, ComponentCheck.class);//获取到当前这个组件检查类

                    if (thisComponentCheck.isCheck()) {//这个组件要检测
                        Map<String, String> status = thisComponentCheck.checkStatus();//获取检测结果
                        statusMap.putAll(status);//合并结果集
                    }
                }
            } catch (NoSuchBeanDefinitionException e) {
                LOG.warn("spring容器中没有配置自定义组件");
            }
            statusMap.put("projectName", contextPath);
        } catch (Exception e) {
            LOG.error("接口调用错误", e);
            statusMap.put("code", "500");
        }

        Collection<String> values = statusMap.values();
        if (values.contains("0")) {//包含不可用的组件状态
            statusMap.put("code", "500");
            return ResponseEntity.status(500).body(statusMap);
        }
        return ResponseEntity.status(200).body(statusMap);
    }


}
