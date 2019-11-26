package com.redis.cluster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * description: RedisClusterApplication
 * date: 2019/11/20 6:12 下午
 * author: guizhenyu
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.redis.cluster"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class RedisClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisClusterApplication.class, args);
    }

}
