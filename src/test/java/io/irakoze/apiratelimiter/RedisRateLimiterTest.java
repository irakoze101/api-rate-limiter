package io.irakoze.apiratelimiter;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.*;

public class RedisRateLimiterTest {
    private static final int MAX_REQUESTS_PER_SECOND = 10;
    private static final int MAX_REQUESTS_PER_MONTH = 10000;
    private static final int MAX_REQUESTS_PER_SYSTEM = 1000000;

    private JedisPool jedisPool;
    private RedisRateLimiter rateLimiter;

    @Before
    public void setUp() {
        jedisPool = new JedisPool("localhost");
        jedisPool.getResource().flushAll();

        rateLimiter = new RedisRateLimiter(jedisPool);
    }

    @After
    public void tearDown() {
        jedisPool.close();
    }

    @Test
    public void testRateLimiting() throws InterruptedException {
        ApiClient client1 = new ApiClient("client1");
        ApiClient client2 = new ApiClient("client2");

        // Make requests from client1 and client2 until they hit the rate limit
        for (int i = 0; i < MAX_REQUESTS_PER_SECOND; i++) {
            assertFalse(rateLimiter.isRateLimited(client1));
            assertFalse(rateLimiter.isRateLimited(client2));
        }

        assertTrue(rateLimiter.isRateLimited(client1));
        assertTrue(rateLimiter.isRateLimited(client2));

        // Wait for 1 second to reset the second count
        Thread.sleep(1000);

        // Make another request from client1, which should succeed
        assertFalse(rateLimiter.isRateLimited(client1));

        // Make more requests from client1 and client2 until they hit the monthly limit
        for (int i = 0; i < MAX_REQUESTS_PER_MONTH - MAX_REQUESTS_PER_SECOND; i++) {
            assertFalse(rateLimiter.isRateLimited(client1));
            assertFalse(rateLimiter.isRateLimited(client2));
        }

        assertTrue(rateLimiter.isRateLimited(client1));
        assertTrue(rateLimiter.isRateLimited(client2));

        // Wait for 1 month to reset the monthly count
        Thread.sleep(31 * 24 * 60 * 60 * 1000L);

        // Make another request from client1, which should succeed
        assertFalse(rateLimiter.isRateLimited(client1));
    }
}

