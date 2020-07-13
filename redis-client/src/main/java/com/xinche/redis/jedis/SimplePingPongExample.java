package com.xinche.redis.jedis;

import redis.clients.jedis.Jedis;

/**
 *
 * default ping
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/7/13 23:20
 */
public class SimplePingPongExample {
    public static void main(String[] args) {
        // default localhost:6379
        final Jedis jedis = new Jedis();
        // get pong
        System.out.println(jedis.ping());
    }
}
