package com.shinebo.mybatisplus.service;

import com.shinebo.mybatisplus.database.entity.User;
import java.util.Map;

public interface UserService {
    /**
     * 插入一条用户数据
     */
    Map insertOneUser(User user);

    /**
     * 获取用户列表
     */
    Map getUserLists();

    /**
     * 通过用户 id 获取用户
     * @param  id
     * @return
     */
    User getUserById(String id);

    /**
     * 通过用户 id 修改用户信息
     * @param id
     * @param user
     * @return
     */
    Map updateUser(String id, User user);

    /**
     * 通过用户 id 删除用户
     * @param id
     * @return
     */
    Map delUserByUserId(String id);
}
