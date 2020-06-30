package com.bo.spring.redis.Controller;

import com.bo.spring.redis.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: 星尘
 * @Date: 2019/11/19 21:10
 * @Version: 1.0
 */

@RestController
public class StudentHandler {
    //redis 连接对象
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * RequestBody 注解是将客户端传过来的json 数据转换成 java 对象
     */
    @PostMapping("set")
    public void set(@RequestBody Student student) {
        System.out.println(student);
        redisTemplate.opsForValue().set("student", student);
    }

    @GetMapping("/get/{key}")
    public Student get(@PathVariable("key") String key) {
        return (Student) redisTemplate.opsForValue().get(key);
    }

    @DeleteMapping("/delete/{key}")
    public boolean deleteKey(@PathVariable("key") String key) {
        redisTemplate.delete(key);
        return redisTemplate.hasKey(key);
    }

    /**-----------------数据类型的存取--------------**/
    // redis 存取 string
    @GetMapping("string")
    public String  StringTest(){
        redisTemplate.opsForValue().set("str", "hello");
        String str = (String) redisTemplate.opsForValue().get("str");
        return str;
    }

    // redis 存取 列表，对应 java 里面的 list
    @GetMapping("list")
    public List<String> listTest() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        // 存
        listOperations.leftPush("list", "Hello");
        listOperations.leftPush("list", "World");
        listOperations.leftPush("list", "Java Redis");
        // 取值(key，取值的范围)
        List<String> list = listOperations.range("list", 0,2);
        return list;
    }

    // 集合，对应 java 里面的 set, set 里面的值是惟一的。后面的值会覆盖前面的值
    @GetMapping("set")
    public Set<String> setTest() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add("set", "hello");
        setOperations.add("set", "World");
        setOperations.add("set","javaRedis");
        setOperations.add("set", "hello");
        Set<String> strings = setOperations.members("set");
        return strings;
    }

    // 有序集合 zset
    @GetMapping("zset")
    public Set<String> zSetTest() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        // 有序是根据我们传入的第三个值进行排序的
        zSetOperations.add("zset", "hello", 1);
        zSetOperations.add("zset", "world", 3);
        zSetOperations.add("zset", "javaRedis", 3);
        Set<String> set =zSetOperations.range("zset", 0, 2);
        return set;
    }

    /**
     * hash
     * hash 里面需要三个参数， key，hashKey,value
     * key 是每一组数据的 ID，hashKey 和 value 是一组完整的 HashMap 数据，通过 key 来区分不同的 HashMap
     * @return
     */
    @GetMapping("/hash")
    public String hashTest() {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("key", "hashkey", "hello");
        return hashOperations.get("key", "hashkey");
    }

}
