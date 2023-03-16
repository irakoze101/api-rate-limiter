package io.irakoze.apiratelimiter;

import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RedisRateLimiter implements RateLimiterMiddleware {
    private JedisPool jedisPool;
    private final int maxRequestsPerSecond = 25;
    private final int maxRequestsPerMonth = 40960;
    private final int maxRequestsPerSystem = 1000;
    private static final String REDIS_KEY_PREFIX = "api-rate-limiter";
    private static final Logger Log = LoggerFactory.getLogger(RedisRateLimiter.class);
    private final Map<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    public RedisRateLimiter(JedisPool pool) {
        this.jedisPool = pool;
    }

    @Override
    public boolean isRateLimited(ApiClient client) {
        var clientId = client.getClientId();
        var systemKey = "system";
        var monthKey = "month:" + clientId;
        var secondKey = "second:" + clientId;

        var systemCount = Integer.parseInt(jedisPool.getResource().get(systemKey));
        var monthCount = Integer.parseInt(jedisPool.getResource().get(monthKey));
        var secondCount = Integer.parseInt(jedisPool.getResource().get(secondKey));

        if (systemCount > maxRequestsPerSystem ||
            monthCount > maxRequestsPerMonth ||
            secondCount > maxRequestsPerSecond) {
            Log.info("System rate limit exceeded");
            return true;
        }

        // The algorithm should be tested for performance
        var bucket = tokenBuckets.computeIfAbsent(clientId, k -> TokenBuckets.builder()
                .withCapacity(maxRequestsPerSecond)
                .withFixedIntervalRefillStrategy(maxRequestsPerSecond, 1, TimeUnit.SECONDS)
            // .withInitialTokens(maxRequestsPerSecond)
                .build());
        if (!bucket.tryConsume()) {
            Log.info("Rate limit exceeded for client: " + clientId);
            return true;
        }

        jedisPool.getResource().incr(systemKey);
        jedisPool.getResource().incr(monthKey);
        jedisPool.getResource().incr(secondKey);
        jedisPool.getResource().expire(systemKey, 1);

        return false;
    }
}
