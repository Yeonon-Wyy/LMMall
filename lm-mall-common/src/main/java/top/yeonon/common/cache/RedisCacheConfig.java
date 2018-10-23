package top.yeonon.common.cache;

import org.springframework.cache.annotation.*;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yeonon
 * @date 2018/4/13 0013 12:47
 **/
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

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
                if (param == null) {
                    continue;
                }
                sb.append(":" + param.toString());
            }
            return sb.toString();
        };
    }
}
