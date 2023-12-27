package xyz.chen.commons.services;


import java.time.Duration;


public interface redisCacheService {
    void set(String prefix, String key, Object value, Duration duration);

    void set(String key, Object value, Duration duration);

    void set(String key, Object value);

    Object get(String prefix, String key);

    Object get(String key);

    Boolean del(String prefix, String key);

    Boolean del(String key);

    Boolean setExpire(String prefix, String key, Duration duration);

    Boolean setExpire(String key, Duration duration);

    Long getExpire(String prefix, String key);

    Long getExpire(String key);

    Boolean hasKey(String prefix, String key);

    Boolean hasKey(String key);


}
