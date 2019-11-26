package com.redis.cluster.common;

import com.redis.cluster.common.config.RedisProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisConfiguration {

    @Autowired
    private RedisProperties redisClusterProperties;


    @Bean
    public JedisCluster getJedisCluster() {
        String[] serverArray = redisClusterProperties.getCluster().getNodes().split(",");
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            HostAndPort hostAndPort = new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim()));
            nodes.add(hostAndPort);
        }

        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();

        jedisPoolConfig.setMaxIdle(redisClusterProperties.getPool().getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(redisClusterProperties.getPool().getMaxWait());
        jedisPoolConfig.setMinIdle(redisClusterProperties.getPool().getMinIdle());
        jedisPoolConfig.setTestOnBorrow(redisClusterProperties.getPool().getTestOnBorrow());
        jedisPoolConfig.setTestWhileIdle(redisClusterProperties.getPool().getTestWhileIdle());
        jedisPoolConfig.setMaxTotal(redisClusterProperties.getPool().getMaxActive());
        Integer timeout = redisClusterProperties.getTimeout();

        //创建集群对象
        return new JedisCluster(nodes, timeout, timeout, redisClusterProperties.getMaxAttempts(),
                redisClusterProperties.getPassword(), jedisPoolConfig);
    }
}
