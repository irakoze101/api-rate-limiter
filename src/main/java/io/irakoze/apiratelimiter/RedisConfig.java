package io.irakoze.apiratelimiter;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final int REDIS_TIMEOUT = 5000;
    private static final String REDIS_PASSWORD = "password"; // optional

    public static JedisPool getJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMinIdle(10);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT, REDIS_PASSWORD);

        return jedisPool;
    }
}

