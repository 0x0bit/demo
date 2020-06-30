package com.bo.spring.redis.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 星尘
 * @Date: 2019/11/19 21:06
 * @Version: 1.0
 */

/**
 * 需要实现Serializable接口来序列化na，否则无法存入 redis,内存里面的值必须进行序列化
 */
@Data
public class Student implements Serializable {
    private Integer id;
    private String name;
    private Double socre;
    private Date birthday;
}
