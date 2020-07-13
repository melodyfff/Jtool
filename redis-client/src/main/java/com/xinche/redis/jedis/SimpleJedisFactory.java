package com.xinche.redis.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * SimpleJedisFactory
 *
 * {@link JedisPool} base on org.apache.commons.pool2
 *
 * @see GenericObjectPoolConfig
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/7/13 23:32
 */
public final class SimpleJedisFactory {
    static final JedisPoolConfig POOL_CONFIG = new JedisPoolConfig();
    static final JedisPool POOL;
    static {
        POOL_CONFIG.setMaxIdle(10);
        POOL_CONFIG.setMaxTotal(100);
        POOL_CONFIG.setMaxWaitMillis(100);
        POOL = new JedisPool(POOL_CONFIG);
    }

    static Jedis instance(){
        return POOL.getResource();
    }

    public static void main(String[] args) {
        System.out.println(POOL.getResource().ping());
    }
}
