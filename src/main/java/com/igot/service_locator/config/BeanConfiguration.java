package com.igot.service_locator.config;


import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.*;

@Configuration
public class BeanConfiguration {

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    RedisTemplate redisTemplate() {
//        return new RedisTemplate<>();
//    }
}
