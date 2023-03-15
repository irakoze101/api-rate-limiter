package io.irakoze.apiratelimiter;

public interface RateLimiterMiddleware {
    boolean isRateLimited(ApiClient apiClient);
}
