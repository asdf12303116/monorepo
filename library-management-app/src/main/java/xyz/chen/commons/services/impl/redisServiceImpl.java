package xyz.chen.commons.services.impl;


import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.chen.commons.services.redisCacheService;

import java.time.Duration;

@Service
public class redisServiceImpl implements redisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${default-prefix:cache}")
    private String DEFAULT_PREFIX;
    @Value("${default-expire:7200s}")
    private Duration DEFAULT_EXPIRE;

    public redisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void set(String prefix, String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(getFullKey(null, key), value, duration);
    }

    @Override
    public void set(String key, Object value, Duration duration) {
        set(null, key, value, duration);
    }

    @Override
    public void set(String key, Object value) {
        set(null, key, value, DEFAULT_EXPIRE);
    }

    @Override
    public Object get(String prefix, String key) {
        return redisTemplate.opsForValue().get(getFullKey(prefix, key));
    }

    @Override
    public Object get(String key) {
        return get(null, key);
    }

    @Override
    public Boolean del(String prefix, String key) {
        return redisTemplate.delete(getFullKey(prefix, key));
    }

    @Override
    public Boolean del(String key) {
        return del(null, key);
    }

    @Override
    public Boolean setExpire(String prefix, String key, Duration duration) {
        return redisTemplate.expire(getFullKey(prefix, key), duration);
    }

    @Override
    public Boolean setExpire(String key, Duration duration) {
        return setExpire(null, key, duration);
    }

    @Override
    public Long getExpire(String prefix, String key) {
        return redisTemplate.getExpire(getFullKey(prefix, key));
    }

    @Override
    public Long getExpire(String key) {
        return getExpire(null, key);
    }

    @Override
    public Boolean hasKey(String prefix, String key) {
        return redisTemplate.hasKey(getFullKey(prefix, key));
    }

    @Override
    public Boolean hasKey(String key) {
        return hasKey(null, key);
    }

    String getFullKey(String prefix, String key) {
        if (StrUtil.isEmpty(prefix)) {
            return DEFAULT_PREFIX + ':' + key;
        } else {
            return prefix + ':' + key;
        }
    }
}
