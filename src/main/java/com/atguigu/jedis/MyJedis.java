package com.atguigu.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ListPosition;

import java.util.Set;

public class MyJedis {
    private static String host = "hadoop102";
    private static Integer port = Integer.valueOf("6379");
    private static JedisPool jedisPool;


    public static Jedis getJedisFromPool() {
        if (jedisPool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(10); //最大可用连接数
            jedisPoolConfig.setMaxIdle(5); //最大闲置连接数
            jedisPoolConfig.setMinIdle(5); //最小闲置连接数
            jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
            jedisPoolConfig.setMaxWaitMillis(2000); //等待时间
            jedisPoolConfig.setTestOnBorrow(true); //取连接的时候进行一下测试 ping pong

            jedisPool = new JedisPool(jedisPoolConfig, host, port);

        }
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    //   todo: 1、测试String
    public static void testKey(Jedis jedis) throws InterruptedException {
        System.out.println("---------------------------Key Test---------------------------");
        //1.1 输出当前库中所有的key
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        //1.2 判断某个键值是否存在
        Boolean a1 = jedis.exists("a1");
        System.out.println(a1);
        //1.3 查看某个key对应的value的类型
        jedis.set("a1", "995");
        String a1Type = jedis.type("a1");
        System.out.println(a1Type);
        jedis.incrBy("a1", 1);
        String myA1 = jedis.get("a1");
        System.out.println("myA1 = " + myA1);
        //1.4 删除某个键
        jedis.del("a1");
        System.out.println(jedis.keys("*"));
        //1.5 设置过期时间
        jedis.set("a1", "995");
        jedis.expire("a1", 10);
        Thread.sleep(1000);
        //1.6查看过期时间，-1表示永不过期，-2表示已过期
        System.out.println(jedis.ttl("a1"));
        //1.7 查看当前库中key的数量
        System.out.println(jedis.dbSize());
        //1.8 清空当前库
        jedis.flushDB();
        System.out.println(jedis.dbSize());
        //1.9 清空所有库
        jedis.flushAll();
        System.out.println(jedis.dbSize());
    }


    //   todo:2、测试String
    public static void testString(Jedis jedis) {
        System.out.println("-----------------------------String Test--------------------------------");
        //2.1 添加键值对
        jedis.set("spark", "100");
        jedis.set("hadoop", "60");
        //2.2 获取键的值
        System.out.println(jedis.get("spark"));
        //2.3 将特定的值追加到 原值的末尾
        jedis.append("spark", "000");
        System.out.println(jedis.get("spark"));
        //2.4 获取值的长度
        System.out.println(jedis.strlen("spark"));
        //2.5 当key不存在的时候，设置key的值
        jedis.setnx("flink", "999");
        System.out.println(jedis.get("flink"));
        //2.6 将某个key中的value存储的数字值加1  减1
        jedis.incr("flink");
        System.out.println(jedis.get("flink"));
        jedis.decr("flink");
        System.out.println(jedis.get("flink"));
        //2.7  将某个key中的value存储的数字值加指定步长  减指定步长
        jedis.incrBy("flink", 100);
        System.out.println(jedis.get("flink"));
        jedis.decrBy("flink", 999);
        System.out.println(jedis.get("flink"));
        jedis.mset("gxb", "gxb", "zdm", "zdm", "gyh", "gyh", "gxw", "gxw");
        System.out.println(jedis.keys("*"));
        //2.8同时获取一个或者多个key的值
        System.out.println(jedis.mget("gxb", "zdm", "gyh", "gxw"));
        //2.9 同时增加1个或者多个key，当且仅当所有的key不存在时候，要么做，要么全部不执行
        jedis.msetnx("zxc", "zxc", "xcv", "xcv");
        System.out.println(jedis.keys("*"));
        //2.10 获取值 的字串
        System.out.println(jedis.getrange("gxb", 0, -1));
        //2.11 从指定的开始位置 覆盖旧值
        jedis.setrange("gxb", 0, "xxx");
        System.out.println(jedis.keys("gxb"));
        //2.12 同时设置值 和过期的时间
        jedis.setex("kekeba", 999999999, "huozhe");
        System.out.println(jedis.get("kekeba"));
        //2.13 设置新的值   并且获取旧的值
        jedis.getSet("flink", "888");
        System.out.println(jedis.get("flink"));
    }

    //    todo:测试list
    public static void testList(Jedis jedis) {
        System.out.println("-----------------------------------------List Test-------------------------------------");
        //1、 从左边插入一个或多个值
        jedis.lpush("facai", "gaoxubin", "gaoxuwen", "zhangdongmei", "gaoyuehe");
        System.out.println(jedis.lrange("facai", 0, -1));
        //2、 从右边插入一个或多个值
        jedis.rpush("facai", "gaoxingxing");
        System.out.println(jedis.lrange("facai", 0, -1));
        //3、 从key1  的右边删除一个值 插入到key2 列的左边
        jedis.rpoplpush("facai", "facai");
        System.out.println(jedis.lrange("facai", 0, -1));
        //4、获取列表的长度
        System.out.println(jedis.llen("facai"));
        //5、 在指定的value的  前面或者后面 插入新值
        jedis.linsert("facai", ListPosition.AFTER,"gaoxubin","haizi");
        //6、 从左边删除cout个指定的value
        jedis.lrem("facai", 5, "gaoxubin");
        System.out.println(jedis.lrange("facai", 0, -1));


    }

    //    todo：测试set
    public static void testSet(Jedis jedis) {
        //1、 将一个或者多个元素添加到集合中，已经存在的被忽略，并且出去该集合所有数据
        jedis.sadd("ff", "1", "2", "1", "4", "5");
        System.out.println(jedis.smembers("ff"));
        //2、返回集合中元素的个数
        System.out.println(jedis.scard("ff"));
        //3、从集合中删除指定的元素
        jedis.srem("ff","1");
        System.out.println(jedis.smembers("ff"));
        //4、随机从集合中取出n个值，不会从集合中删除
        System.out.println(jedis.srandmember("ff", 2)); //如果超过集合个数，取全部
        //5、多个集合的 交并补   sinter    sunion   sdiff
    }

    //    todo：测试Zset
    public static void testZset(Jedis jedis) {
        //1、往集合中添加指定的member  和score
        jedis.zadd("niu",75,"go");
        jedis.zadd("niu",55,"java");
        jedis.zadd("niu",65,"python");
        jedis.zadd("niu",85,"c");
        jedis.zadd("niu",95,"php");
        //2、从集合中取出指定的下标范围的数据   正序  倒序
        System.out.println(jedis.zrange("niu", 0, 2));
        System.out.println(jedis.zrevrange("niu", 0, 2));
        //3、从集合中取出指定score范围的数据   从小到大 和从大到小
        System.out.println(jedis.zrangeByScore("niu", 60, 100));
        System.out.println(jedis.zrevrangeByScore("niu", 100, 60));
        //4、给指定的member中的score加分
        jedis.zincrby("niu",999,"php");
        //5、返回集合中指定的member的排名,排名从0开始
        System.out.println(jedis.zrank("niu", "php"));
        System.out.println(jedis.zrange("niu", 0, -1));
    }

    //    todo：测试hash
    public static void testHash(Jedis jedis) {
        //1、给集合中添加指定的属性和值
            jedis.hset("stu","name","gxb");
            jedis.hset("stu","age","25");
            jedis.hset("stu","school","atguigu");
        //2、   给集合中添加指定的属性和值  当属性不存在
            jedis.hset("stu","parent","gyhzdm");
        //3、取出集合中指定的属性的value
        System.out.println(jedis.hget("stu", "name"));
        //4、判断集合中是否存在指定的 属性
        System.out.println(jedis.hexists("stu", "name"));
        //5、 列出集合的所有的属性   value
        System.out.println(jedis.hkeys("stu"));
        System.out.println(jedis.hvals("stu"));
        //6、 给集合中指定的属性  的value值增加 x
        jedis.hincrBy("stu","age",1);
        System.out.println(jedis.hvals("stu"));
    }

    public static void main(String[] args) throws InterruptedException {

        Jedis jedis = getJedisFromPool();
        String ping = jedis.ping();
        System.out.println("测试成功否？ = " + ping);
//      testKey(jedis);
//      testString(jedis);
//      testList(jedis);
//      testSet(jedis);
//        testZset(jedis);
        testHash(jedis);
        jedis.close();
    }
}
