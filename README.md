# components-check
一枚小白，工作中的需求，做完以后留个记录，也希望能帮到有缘人。
1.接口使用说明
      使用目的：为了检查该项目中的数据库，redis缓存库，Rabbitmq消息组件，ActiveMq消息组件能不能正常使用，也可以拓展自己自定义的组件进行检查。
      使用流程：
         1.首先需要引入依赖：
      <dependency>
          <groupId>tdh.platform.common</groupId>
          <artifactId>components-check</artifactId>
          <version>1.0-SNAPSHOT</version>
      </dependency>

      2.需要开启注解扫描并扫描到：com.tdh.common.component包及其子包。
      3.需要配置项目名（必须），可以配置其他开关属性。在springboot项目中可以在application.yml中配置

      tdh:
        component:
            check:
                projectName: utruck-app-webmobile         #项目名称，唯一标识，一定要配
              #  db: false                                #MySql数据库开关，不配置默认为ture表示检测
              #  redis: false                             #Redis缓存开关，不配置默认为ture表示检测
                rabbitmq: false                           #RabbitMQ开关，不配置默认为ture表示检测
                rabbitQueueName: test_check_rabbit_queue #RabbitMQ队列名称 不配置默认为check_rabbit_queue
                rabbitAwaitTime: 2000                    #RabbitMQ定时器间隔时间，单位ms.默认4000
              #  activemq: false                         #ActiveMQ开关，不配置默认为true表示检测
                activeQueueName: check_active_queue      #ActiveMQ队列名称 不配置默认为check_rabbit_queue
                excludeBeanName: dataSource\w*           #具体不检测的bean名称，支持java正则表达式，逗号分隔： dataSource1,dataSource2
               activeAwaitTime: 10000                   #ActiveMQ定时器间隔时间，单位ms.默认5000


    在spring web项目中需要写在.properties文件中，例如在ompcheck.properties文件中。
      tdh.component.check.projectName=utruck-app-web
      #  tdh.component.check.db= false
      # tdh.component.check.redis= false
      # tdh.component.check.rabbitmq= false
      # tdh.component.check.rabbitQueueName= test_check_rabbit_queue
      # tdh.component.check.rabbitAwaitTime= 2000
      tdh.component.check.activemq= false
      # tdh.component.check.activeQueueName= check_active_queue
      # tdh.component.check.excludeBeanName=dataSource\\w*
      tdh.component.check.excludeBeanName=dataSource.*
      tdh.component.check.activeAwaitTime= 10000
      在properties文件中配置\时需要转义。
      在springmvc.xml中引用该配置文件：
        <bean id="propertyConfigurer"
          class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer"
          lazy-init="false">
          <property name="order" value="1" />
          <property name="ignoreUnresolvablePlaceholders" value="true" />
          <property name="locations">
            <list>
              <value>classpath:compcheck.properties</value>
            </list>
          </property>
        </bean>


      4.检查数据库时需要在数据库中创建表，sql语句如下：
                 CREATE TABLE `check_component_dict` (
                    `ID` int(11) NOT NULL,
                    PRIMARY KEY (`ID`)
                  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
      5. 如果需要自定义组件，有两种方法：
      第一种方法继承com.tdh.common.component.custom.BaseComponentCheck父类，并重写3个方法：
        public boolean isCheck()表示该类型组件检查开关，关闭时改类型组件一律不检查
        public boolean confirmCheck(String beanName)表示配置的这个bean名称为beanName的这个组件要不要检查，父类已实现以逗号分隔的正在表达式匹配
                                                    可以不重写
        public boolean isAvailable(Object obj) 每一个bean组件检查的逻辑。
      第二种方法实现com.tdh.common.component.custom.ComponentCheck接口并实现3个方法

          Map<String, String> checkStatus();检查这种类型的所有组件并返回。Key组件名称，value=1（正常）或者0（不正常）
          boolean isCheck();
          boolean confirmCheck(String var1);

      6.需要暴露接口/component/check，不要被请求拦截，如果项目中使用shiro，需要在shiro的配置文件中配置白名单/component/check+访问修饰符（如果有）
        = anno



2.接口描述和工作原理
      接口描述：调用url为/component/check,请求方式：get,不需要入参，返回值为ResponseEntity。组件状态有两种1代表正常，0代表不正常。组件全部正常
      code为200，否则返回code为500。每一项结构为：项目名_主机名_bean名称：状态值
        返回值示例：
            {"code":"200",
            "utruck-app-webmobile_DESKTOP-6H4F0KJ_dataSource_local":"1", 
            "utruck-app-webmobile_DESKTOP-6H4F0KJ_dataSource_local_Utruck1_0":"1",
            "utruck-app-webmobile_DESKTOP-6H4F0KJ_dataSource_local_fin":"1",
            "utruck-app-webmobile_DESKTOP-6H4F0KJ_dataSource_local_permission":"1",
            "utruck-app-webmobile_DESKTOP-6H4F0KJ_Redis":"1",
            "utruck-app-webmobile_DESKTOP-6H4F0KJ_RabbitMQ":"1",
            "projectName":"utruck-app-webmobile"}

      工作原理：该接口会判断当前Spring容器中有没有com.tdh.common.component.custom.ComponentCheck类型的bean注入，如果有就用该bean对应的配置去
          检查该组件能不能正常使用。如果没有bean的实例则不检查。基于优卡项目，已实现检查的具体bean一共有四种，可以用java配置，也可以用xml配置。
          1.	数据库的数据源配置: javax.sql.DataSource类的实例。可以配置多个数据源，当配置多个时，检查多个。
          2.	Redis的连接池配置：redis.clients.jedis.shardedJedisPool类的实例。当配置多个时，检查多个。
          3.	Rabbitmq工厂配置：
          org.springframework.amqp.rabbit.connection.CachingConnectionFactoryj类的实例 id=connectionFactory。
          对应xml中的   <rabbit:connection-factory id="connectionFactory" …/>标签 
          4.	Activemq工厂配置 :
          org.apache.activemq.ActiveMQConnectionFactory类的实例且id=activeMqConnectionFactory.
          对应xml中的 
          <bean id="activeMqConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">标签







