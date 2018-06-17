package top.yeonon.lmmall.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/4/13 0013 12:47
 **/
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {


    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    //缓存管理器

//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;
//
//    @Bean
//    @Override
//    public CacheManager cacheManager() {
//
//        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory).build();
//        redisCacheManager.
//        return cacheManager;
//    }


    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            String[] value = new String[1];
            Cacheable cacheable = method.getAnnotation(Cacheable.class);
            if (cacheable != null) {
                value = cacheable.value();
            }
            CachePut cachePut = method.getAnnotation(CachePut.class);
            if (cachePut != null) {
                value = cachePut.value();
            }
            CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
            if (cacheEvict != null) {
                value = cacheEvict.value();
            }

            sb.append(value[0]);
            for (Object param : params) {
                if (param == null) continue;
                sb.append(":" + param.toString());
            }
            return sb.toString();
        };
    }
}
