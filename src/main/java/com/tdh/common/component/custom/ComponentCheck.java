package com.tdh.common.component.custom;

import java.util.Map;

/**
 * @ClassName: CheckComponent
 * @Description: 检查组件的接口
 * @Author zm
 * @Date 2018/11/14 10:57
 **/
public interface ComponentCheck {
    /**
     * 该方法中实现检测组件的逻辑
     *
     * @return key为组件名，value为组件状态，组件状态分为1 正常，0 不正常
     */
    Map<String, String> checkStatus();

    /**
     * 该方法表示这个类型的组件要不要被检测
     *
     * @return true:要被检测，false:不检测
     */
    boolean isCheck();


    /**
     * 改方法表示某个具体的类型且配置在容器中名字为beanName的组件要不要被检测
     * @param beanName 配置在容器中的名字
     * @return true:要检测，false:不检测
     */
    boolean confirmCheck(String beanName);
}
