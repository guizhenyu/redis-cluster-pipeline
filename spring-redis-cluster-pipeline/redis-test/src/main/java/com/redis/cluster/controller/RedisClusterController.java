package com.redis.cluster.controller;

import com.redis.cluster.common.Response;
import com.redis.cluster.common.util.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * description: RedisClusterController
 * date: 2019/11/20 10:24 上午
 * author: guizhenyu
 */

@RestController
@RequestMapping("/redis")//这边最好写上一个路径，空的话swagger会访问不了报406（主要是针对{PathVariable}）
@Api(tags = "RedisClusterController")
public class RedisClusterController {

    @Autowired
    private JedisCluster jedisCluster;


    /**
     * 根据key获取对应的value
     * @param key
     * @return
     */
    @GetMapping("/{key}")
    @ApiOperation("根据key查询value")
    public Response getKey(@PathVariable("key") String key){

        Object resultValue = RedisUtils.get(jedisCluster, key, Object.class);
        return Response.success(resultValue);
    }
    /**
     * 根据keys获取对应的value
     * @param keys
     * @return
     */
    @GetMapping("/keys")
    @ApiOperation("根据keys查询values")
    public Response getValuesByKeys(@RequestParam("keys") List<String> keys){

        Map<String, Object> resultValue = RedisUtils.batchGet(jedisCluster, keys, Object.class);
        return Response.success(resultValue);
    }

    /**
     * 根据pattern模糊查询所有的key
     * @param pattern
     * @return
     */
    @GetMapping("/keys/{pattern}")
    @ApiOperation("根据pattern查询keys")
    public Response getKeyByPattern(@PathVariable("pattern") String pattern){
        Object resultValue = RedisUtils.getKeysByPattern(jedisCluster, pattern);
        return Response.success(resultValue);
    }

    /**
     * 根据pattern模糊查询所有的value
     * @param pattern
     * @return
     */
    @GetMapping("/values/{pattern}")
    @ApiOperation("根据pattern查询values")
    public Response getValuesByPattern(@PathVariable("pattern") String pattern){
        Set<String> keysByPattern = RedisUtils.getKeysByPattern(jedisCluster, pattern);
        List<String> keys = new ArrayList<>();
        keys.addAll(keysByPattern);
        Map<String, Object> resultValue = RedisUtils.batchGet(jedisCluster, keys, Object.class);
        return Response.success(resultValue);
    }

    @ApiOperation("根据key删除value")
    @DeleteMapping("{key}")
    public Response deleteByKey(@PathVariable("key") String key){
        return Response.success(RedisUtils.delete(jedisCluster, key));
    }

    @ApiOperation("根据keys删除values")
    @DeleteMapping("keys")
    public Response deleteByKeys(@RequestParam("keys") List<String> keys){
        RedisUtils.batchDelete(jedisCluster, keys);
        return Response.success();
    }

    @ApiOperation("设置key和value")
    @PostMapping("")
    public Response set(@RequestBody Map<String, Object> keyValue){
        RedisUtils.batchSet(jedisCluster, keyValue);
        return Response.success();
    }

    /**
     * 根据pattern模糊删除所有的value
     * @param pattern
     * @return
     */
    @DeleteMapping("/values/{pattern}")
    @ApiOperation("根据pattern查询values")
    public Response delByPattern(@PathVariable("pattern") String pattern){
        RedisUtils.delKeysByPattern(jedisCluster, pattern);

        return Response.success();
    }

}
