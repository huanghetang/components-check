package com.tdh.common.component.custom.impl;

import com.tdh.common.component.custom.BaseComponentCheck;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @ClassName: MysqlCheck
 * @Description: 检查mysql数据库
 * @Author zm
 * @Date 2018/11/14 16:47
 **/
@Component
public class MysqlCheck extends BaseComponentCheck<DataSource> {


    public boolean isCheck() {
        return checkConfig.getDb();
    }

    /**
     * 检查数据库是否可用
     *
     * @return
     */
    @Override
    public boolean isAvailable(Object obj) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        DataSource dataSource = (DataSource) obj;
        jdbcTemplate.setDataSource(dataSource);
        try {
            String insertSql = "insert into check_component_dict (ID) values (?)";
            jdbcTemplate.update(insertSql, 1);

//            String updateSql = "update check_component_dict set NUMBER = NUMBER+1 where ID = 1";
//            jdbcTemplate.update(updateSql);
//
//            String querySql = "select NUMBER from check_component_dict limit 1";
//            Integer count = jdbcTemplate.queryForObject(querySql, Integer.class);

            String deleteSql = "delete from check_component_dict";
            jdbcTemplate.update(deleteSql);
        } catch (Exception e) {
            LOG.error("数据库组件不可用", e);
            return false;
        }
        return true;
    }

}
