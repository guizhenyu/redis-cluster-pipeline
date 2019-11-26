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
@RequestMapping("")
@Api(tags = "RedisClusterController")
public class RedisClusterController {

    @Autowired
    private JedisCluster jedisCluster;


    /**
     * 根据key获取对应的value
     * @param key
     * @return
     */
    @GetMapping("/key")
    @ApiOperation("根据key查询value")
    public Response getKey(@RequestParam("key") String key){

        Object resultValue = RedisUtils.get(jedisCluster, key, Object.class);
        return Response.success(resultValue);
    }

    /**
     * 根据pattern模糊查询所有的key
     * @param pattern
     * @return
     */
    @GetMapping("/keys/pattern")
    @ApiOperation("根据pattern查询keys")
    public Response getKeyByPattern(@RequestParam("pattern") String pattern){
        Object resultValue = RedisUtils.getKeysByPattern(jedisCluster, pattern);
        return Response.success(resultValue);
    }

    /**
     * 根据pattern模糊查询所有的value
     * @param pattern
     * @return
     */
    @GetMapping("/value/pattern")
    @ApiOperation("根据pattern查询values")
    public Response getValueByPattern(@RequestParam("pattern") String pattern){
        Set<String> keysByPattern = RedisUtils.getKeysByPattern(jedisCluster, pattern);
        List<String> keys = new ArrayList<>();
        keys.addAll(keysByPattern);
        Map<String, Object> resultValue = RedisUtils.batchGet(jedisCluster, keys, Object.class);
        return Response.success(resultValue);
    }


//    @DeleteMapping("key")
//    public Response deleteByKey(){
//
//    }

}
