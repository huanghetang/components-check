//package com.tdh.common.component.config;
//
//        import org.apache.commons.dbcp.BasicDataSource;
//        import org.springframework.context.annotation.Bean;
//        import org.springframework.context.annotation.Configuration;
//
//        import javax.sql.DataSource;
//
///**
// * @ClassName: DBConfig
// * @Description: 注册数据源模板
// * @Author zm
// * @Date 2018/11/6 13:38
// **/
//@Configuration
//public class DBConfig {
//
//    @Bean
//    public DataSource dataSource(){
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://192.168.1.207:60100/ofc");
//        dataSource.setUsername("wutaomgr");
//        dataSource.setPassword("wutaomgr");
//        return dataSource;
//    }
//
//    @Bean(name="dataSource02")
//    public DataSource dataSource02(){
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/demo");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//        return dataSource;
//
//    }
//}
