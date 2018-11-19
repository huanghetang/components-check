package com.tdh.common.component.custom.impl;

import com.tdh.common.component.custom.BaseComponentCheck;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @ClassName: RedisCheck
 * @Description:
 * @Author zm
 * @Date 2018/11/14 17:00
 **/
@Component
public class RedisCheck extends BaseComponentCheck<ShardedJedisPool> {


    /**
     * 功能开关
     *
     * @return
     */
    public boolean isCheck() {
        return checkConfig.getRedis();
    }


    /**
     * 检查Redis是否可用
     *
     * @return
     */
    public boolean isAvailable(Object obj) {
        ShardedJedis jedis = null;
        ShardedJedisPool ShardedJedisPool = (redis.clients.jedis.ShardedJedisPool) obj;
        try {
            jedis = ShardedJedisPool.getResource();

            jedis.set("checkRedis", "1");

            String checkRedis = jedis.get("checkRedis");

            jedis.del("checkRedis");

            jedis.close();

            if (!"1".equals(checkRedis)) {
                return false;
            }
        } catch (Exception e) {
            LOG.error("MQ组件不可用", e);
            jedis.close();
            return false;
        }

        return true;
    }
}
