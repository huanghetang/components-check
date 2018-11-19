package com.tdh.common.component.custom;

import com.tdh.common.component.config.CheckConfig;
import com.tdh.common.component.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: BaseComponentCheck
 * @Description: 组件检查实现类模板
 * @Author zm
 * @Date 2018/11/15 9:33
 **/
public class BaseComponentCheck<T> implements ComponentCheck {

    //得到泛型类T
    public Class getMyClass() {

        //返回表示此 Class 所表示的实体类的 直接父类 的 Type。注意，是直接父类
        Type type = getClass().getGenericSuperclass();

        // 判断 是否泛型
        if (type instanceof ParameterizedType) {
            // 返回表示此类型实际类型参数的Type对象的数组.
            // 当有多个泛型类时，数组的长度就不是1了
            Type[] ptype = ((ParameterizedType) type).getActualTypeArguments();
            return (Class) ptype[0];  //将第一个泛型T对应的类返回（这里只有一个）
        } else {
            return Object.class;//若没有给定泛型，则返回Object类
        }

    }


    protected final Logger LOG = LoggerFactory.getLogger(BaseComponentCheck.class);


    @Autowired
    protected CheckConfig checkConfig; //从文件中读取的属性

    public Map<String, String> checkStatus() {
        Map<String, String> statusMap = new LinkedHashMap<String, String>();

        try {
            String contextPath = checkConfig.getProjectName();

            InetAddress address = InetAddress.getLocalHost();

            String hostName = address.getHostName().toString(); //获取本机计算机名称

            ApplicationContext applicationContext = SpringUtil.getApplicationContext();//获取容器

            String[] beanNamesForType = applicationContext.getBeanNamesForType(getMyClass());//获取所有数据源的beanName

            for (String beanName : beanNamesForType) {

                Object obj = SpringUtil.getBean(beanName, getMyClass());//获取数据源

                if (obj != null) {

                    if (confirmCheck(beanName)) {//该beanName要检测

                        if (isAvailable(obj)) {  //检查数据库是否可用
                            statusMap.put(contextPath + "_" + hostName + "_" + beanName, "1");
                        } else {
                            statusMap.put(contextPath + "_" + hostName + "_" + beanName, "0");
                        }
                    }

                }
            }
        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("spring容器中没有配置：{}" + getMyClass());
        } catch (UnknownHostException e) {
            LOG.warn("获取主机名错误", e);
        }

        return statusMap;
    }

    /**
     * 功能开关，表示这种类型的组件要不要检测
     * @return true表示检测，false表示不检测
     */
    public boolean isCheck() {
        return false;
    }

    /**
     * 根据配置文件排除需要检测的bean
     *
     * @param beanName 配置在容器中的名字
     * @return
     */
    public boolean confirmCheck(String beanName) {
        List<String> excludeBeanId = checkConfig.getExcludeBeanId();
        boolean result = true;
        for (String beanIdPattern : excludeBeanId) {

            Pattern pattern = Pattern.compile(beanIdPattern);  // 编译正则表达式

            Matcher matcher = pattern.matcher(beanName);

            if (matcher.matches()) {   // 字符串是否与正则表达式相匹配
                result = false;
                return result;
            }
        }
        return result;
    }


    /**
     * 检查具体的bean是否可用
     *
     * @return
     */
    public boolean isAvailable(Object obj) {
        return true;
    }
}
