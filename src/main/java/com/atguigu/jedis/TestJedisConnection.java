package com.atguigu.jedis;

import redis.clients.jedis.Jedis;

public class TestJedisConnection {

    public static void main(String[] args) {

        Jedis jedis = new Jedis("hadoop102",6379);
        String ping = jedis.ping();
        System.out.println("ping = " + ping);
    }
}
