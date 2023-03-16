package io.irakoze.apiratelimiter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisRateLimiterTest {
    private RedisRateLimiter redisRateLimiter;

    @Mock
    JedisPool jedisPool = RedisConfig.getJedisPool();;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.redisRateLimiter = new RedisRateLimiter(jedisPool);
    }

    @Test
    public void testIsRateLimited_withLowUsage_shouldReturnFalse() {
        ApiClient client = new ApiClient("client1");

        Jedis jedisMock = mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedisMock);
        when(jedisMock.get("system")).thenReturn("0");
        when(jedisMock.get("month:client1")).thenReturn("0");
        when(jedisMock.get("second:client1")).thenReturn("0");

        assertFalse(redisRateLimiter.isRateLimited(client));
    }

    @Test
    public void testIsRateLimited_withSystemLimitExceeded_shouldReturnTrue() {
        ApiClient client = new ApiClient("client1");

        Jedis jedisMock = mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedisMock);
        when(jedisMock.get("system")).thenReturn("1001");
        when(jedisMock.get("month:client1")).thenReturn("0");
        when(jedisMock.get("second:client1")).thenReturn("0");

        assertTrue(redisRateLimiter.isRateLimited(client));
    }

    @Test
    public void testIsRateLimited_withMonthLimitExceeded_shouldReturnTrue() {
        ApiClient client = new ApiClient("client1");

        Jedis jedisMock = mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedisMock);
        when(jedisMock.get("system")).thenReturn("0");
        when(jedisMock.get("month:client1")).thenReturn("40961");
        when(jedisMock.get("second:client1")).thenReturn("0");

        assertTrue(redisRateLimiter.isRateLimited(client));
    }

    @Test
    public void testIsRateLimited_withSecondLimitExceeded_shouldReturnTrue() {
        ApiClient client = new ApiClient("client1");

        Jedis jedisMock = mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedisMock);
        when(jedisMock.get("system")).thenReturn("0");
        when(jedisMock.get("month:client1")).thenReturn("0");
        when(jedisMock.get("second:client1")).thenReturn("26");

        assertTrue(redisRateLimiter.isRateLimited(client));
    }
}
