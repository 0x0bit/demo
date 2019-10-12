package com.shinebo.mybatisplus;

import com.shinebo.mybatisplus.database.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Test
    public void GetAllEmployee() {
        System.out.println(("----- selectAll method test ------"));
        List userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }

    @Test
    public void buildCountQuerySql() {
        List list = new ArrayList();
        list.add("one");
        list.add("two");
        String indexName = "wahaha";
        StringBuilder querySql = new StringBuilder("select count(*) from ");
        querySql.append(indexName).append(" where ");
        String str =  querySql.toString();
        System.out.println(str);
    }
}
