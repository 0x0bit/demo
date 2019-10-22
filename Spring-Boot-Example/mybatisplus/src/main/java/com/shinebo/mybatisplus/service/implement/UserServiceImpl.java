package com.shinebo.mybatisplus.service.implement;

import com.shinebo.mybatisplus.database.dao.UserMapper;
import com.shinebo.mybatisplus.database.entity.User;
import com.shinebo.mybatisplus.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Map insertOneUser(User user) {
        Map map = new HashMap();
        Integer res = userMapper.insert(user);
        if (res != 1) {
            map.put("msg", "插入失败");
        } else {
            map.put("msg", "插入成功");
        }
        return map;
    }

    @Override
    public Map getUserLists() {
        Map map = new HashMap();
        List<User> users = userMapper.selectList(null);
        Integer count = userMapper.selectCount(null);
        map.put("count", count);
        map.put("users", users);
        return map;
    }

    /**
     * 获取一个用户
     * @param  id
     * @return
     */
    @Override
    public User getUserById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public Map updateUser(String id, User user) {
        Map map = new HashMap();
        Integer res = userMapper.updateById(user);
        if (res == 0) {
            map.put("msg", "修改失败");
        } else {
            map.put("msg", "修改成功");
        }
        return map;
    }

    @Override
    public Map delUserByUserId(String id) {
        Map map = new HashMap();
        Integer res = userMapper.deleteById(id);
        if (res == 0) {
            map.put("msg", "删除失败");
        } else {
            map.put("msg", "删除成功");
        }
        return map;
    }
}
