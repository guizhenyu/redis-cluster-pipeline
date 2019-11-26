package com.redis.cluster.common.util;


import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class RedisUtils {

    /**
     * 根据key查询数据
     *
     * @param jedisCluster
     * @param key
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T get(JedisCluster jedisCluster, String key, Class<T> t) {
        String value = jedisCluster.get(key);
        return StringUtils.isEmpty(value) ? null : parseValue(value, t);
    }

    /**
     * 设置单个key,value
     *
     * @param jedisCluster
     * @param key
     * @param value
     * @return
     */
    public static <T> String set(JedisCluster jedisCluster, String key, T value) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        return jedisCluster.set(keySerializer.serialize(key), genericJackson2JsonRedisSerializer.serialize(value));
    }

    /**
     * 设置单个key,value, 并且设置key存活时间
     *
     * @param jedisCluster
     * @param key
     * @param value
     * @return
     */
    public static <T> String set(JedisCluster jedisCluster, String key, T value, Long timeOut, TimeUnit timeUnit) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        return jedisCluster.setex(keySerializer.serialize(key), (int) TimeoutUtils.toSeconds(timeOut, timeUnit),
                genericJackson2JsonRedisSerializer.serialize(value));
    }

    /**
     * 根据keys批量查询
     *
     * @param jc
     * @param keys
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> batchGet(JedisCluster jc, List<String> keys, Class<T> t) {
        Map<String, T> resMap = new HashMap<>();
        if (keys == null || keys.size() == 0) {
            return resMap;
        }
        //如果只有一条，直接使用get即可
        if (keys.size() == 1) {
            String value = jc.get(keys.get(0));
            resMap.put(keys.get(0), StringUtils.isEmpty(value) ? null : parseValue(value, t));
            return resMap;
        }

        List<String> keyList = null;
        JedisPool currentJedisPool = null;
        Map<JedisPool, List<String>> jedisPoolMap = getJedisPoolByKeys(jc, keys);

        //保存结果
        List<Object> res = new ArrayList<>();
        //执行
        for (Map.Entry<JedisPool, List<String>> entry : jedisPoolMap.entrySet()) {
            currentJedisPool = entry.getKey();
            try (
                    Jedis jedis = currentJedisPool.getResource();
                    Pipeline currentPipeline = jedis.pipelined();
            ) {
                keyList = entry.getValue();
                for (String key : keyList) {
                    currentPipeline.get(key);
                }
                //从pipeline中获取结果
                res = currentPipeline.syncAndReturnAll();
                for (int i = 0; i < keyList.size(); i++) {
                    resMap.put(keyList.get(i), res.get(i) == null ? null : parseValue(res.get(i), t));
                }
            } catch (Exception e) {
                log.error("RedisBatchUtil getByKeys error, e:{}", e);
            }
        }
        return resMap;
    }

    /**
     * 批量set, 并且设置key的存活时间
     *
     * @param jc
     * @param keyDeviceLocationMap
     * @param timeOut
     * @param timeUnit
     */
    public static <T> void batchSet(JedisCluster jc, Map<String, T> keyDeviceLocationMap, Long timeOut, TimeUnit timeUnit) {

        JedisPool currentJedisPool = null;
        List<String> keyList = null;
        Map<JedisPool, List<String>> jedisPoolMap = getJedisPoolByKeys(jc, new ArrayList<>(keyDeviceLocationMap.keySet()));

        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        //执行
        for (Map.Entry<JedisPool, List<String>> entry : jedisPoolMap.entrySet()) {
            currentJedisPool = entry.getKey();
            try (
                    Jedis jedis = currentJedisPool.getResource();
                    Pipeline currentPipeline = jedis.pipelined();
            ) {
                keyList = entry.getValue();

                for (String key : keyList) {
                    currentPipeline.setex(keySerializer.serialize(key), (int) TimeoutUtils.toSeconds(timeOut, timeUnit),
                            genericJackson2JsonRedisSerializer.serialize(keyDeviceLocationMap.get(key)));
                }
                currentPipeline.syncAndReturnAll();
            } catch (Exception e) {
                log.error("RedisBatchUtil setByKeys error, e:{}", e);
            }
        }
    }


    /**
     * 批量set, 不设置key的存活时间
     *
     * @param jc
     * @param keyDeviceLocationMap
     */
    public static <T> void batchSet(JedisCluster jc, Map<String, T> keyDeviceLocationMap) {
        JedisPool currentJedisPool = null;
        List<String> keyList = null;
        Map<JedisPool, List<String>> jedisPoolMap = getJedisPoolByKeys(jc, new ArrayList<>(keyDeviceLocationMap.keySet()));
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //执行
        for (Map.Entry<JedisPool, List<String>> entry : jedisPoolMap.entrySet()) {
            currentJedisPool = entry.getKey();
            try (
                    Jedis jedis = currentJedisPool.getResource();
                    Pipeline currentPipeline = jedis.pipelined();
            ) {
                keyList = entry.getValue();
                for (String key : keyList) {
                    currentPipeline.set(keySerializer.serialize(key), genericJackson2JsonRedisSerializer.serialize(keyDeviceLocationMap.get(key)));
                }
                //一次执行
                currentPipeline.syncAndReturnAll();
            } catch (Exception e) {
                log.error("RedisBatchUtil batchSet error, e:{}", e);
            }
        }
    }

    /**
     * 根据keys 获取对应的节点
     *
     * @param jc
     * @param keys
     * @return
     */
    public static Map<JedisPool, List<String>> getJedisPoolByKeys(JedisCluster jc, List<String> keys) {
        //获取redisCluster的对象描述
        MetaObject metaObject = SystemMetaObject.forObject(jc);
        //获取redisCluster的缓存信息(slot与jedisPool的对应关系等等)
        JedisClusterInfoCache cache = (JedisClusterInfoCache) metaObject.getValue("connectionHandler.cache");

        //保存地址+端口和命令的映射
        Map<JedisPool, List<String>> jedisPoolMap = new HashMap<>();
        JedisPool currentJedisPool = null;
        for (String key : keys) {
            //计算哈希槽
            int crc = JedisClusterCRC16.getSlot(key);
            //通过哈希槽获取节点的连接信息
            currentJedisPool = cache.getSlotPool(crc);
            if (jedisPoolMap.containsKey(currentJedisPool)) {
                jedisPoolMap.get(currentJedisPool).add(key);
            } else {
                List<String> keyList = new ArrayList<>();
                keyList.add(key);
                jedisPoolMap.put(currentJedisPool, keyList);
            }
        }

        return jedisPoolMap;
    }


    /**
     * 根据pattern获取keys
     *
     * @param jc
     * @param pattern
     * @return
     */
    public static Set<String> getKeysByPattern(JedisCluster jc, String pattern) {
        Map<String, JedisPool> nodes = jc.getClusterNodes();
        Set<String> keys = new HashSet<>();
        //遍历所有连接池，逐个进行模糊查询
        for (String key : nodes.keySet()) {
            JedisPool pool = nodes.get(key);
            //获取Jedis对象，Jedis对象支持keys模糊查询
            try (
                    Jedis connection = pool.getResource();
            ) {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                log.error("RedisBatchUtil find keys by pattern error, e: {}", e);
            }
        }
        return keys;
    }

    /**
     * 根据key,删除value
     *
     * @param jedisCluster
     * @param key
     * @return
     */
    public static Long delete(JedisCluster jedisCluster, String key) {
        return jedisCluster.del(key);
    }

    /**
     * 根据key,删除value
     *
     * @param jedisCluster
     * @param keys
     * @return
     */
    public static void batchDelete(JedisCluster jedisCluster, List<String> keys) {
        JedisPool currentJedisPool = null;
        List<String> keyList = null;
        Map<JedisPool, List<String>> jedisPoolMap = getJedisPoolByKeys(jedisCluster, keys);
        for (Map.Entry<JedisPool, List<String>> entry : jedisPoolMap.entrySet()) {
            currentJedisPool = entry.getKey();
            try (
                    Jedis jedis = currentJedisPool.getResource();
                    Pipeline currentPipeline = jedis.pipelined();
            ) {
                keyList = entry.getValue();

                for (String key : keyList) {
                    currentPipeline.del(key);
                }
                //一次执行
                currentPipeline.syncAndReturnAll();
            } catch (Exception e) {
                log.error("RedisBatchUtil batchDelete error, e:{}", e);
            }
        }
    }

    /**
     * 解析redis的value为具体的对象
     *
     * @param value
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T parseValue(Object value, Class<T> t) {
        try {
            return JSON.parseObject(value.toString(), t);
        } catch (Exception e) {
            log.error("parse redis value error, e:{}", e);
            return null;
        }
    }
}
