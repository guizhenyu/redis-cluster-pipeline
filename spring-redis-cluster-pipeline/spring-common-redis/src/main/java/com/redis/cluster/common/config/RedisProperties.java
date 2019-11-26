package com.redis.cluster.common.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisProperties {
    private Cluster cluster;
    private String password;
    private Integer timeout;
    /**
     * 重试次数
     */
    private Integer maxAttempts;
    private Pool pool;
    @Data
    public static class Pool {

        /**
         * 连接池最大连接数，默认值为8，使用负值表示没有限制
         */
        private Integer maxActive = 300;
        /**
         * 连接池最大阻塞等待时间，单位毫秒，默认值为-1，表示永不超时
         */
        private Integer maxWait = 4000;
        /**
         * 连接池中的最大空闲连接，默认值为8
         */
        private Integer maxIdle = 100;
        /**
         * 连接池中的最小空闲连接
         */
        private Integer minIdle = 35;
        /**
         * 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
         */
        private Boolean testOnBorrow = true;
        /**
         * 如果为true，表示有一个idle object evitor线程对idle object进行扫描， 如果validate失败，此object会被从pool中drop掉；
         * 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
         */
        private Boolean testWhileIdle = true;
    }

    @Data
    public static class Cluster {
        /**
         * 集群节点
         */
        private String nodes;
    }

}
