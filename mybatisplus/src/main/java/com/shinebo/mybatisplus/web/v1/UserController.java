package com.shinebo.mybatisplus.web.v1;

import com.shinebo.mybatisplus.database.entity.User;
import com.shinebo.mybatisplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 插入一个用户
     * @param user
     * @return Map
     */
    @PostMapping("user")
    public Map insertUser(@RequestBody @Valid User user) {
        Map res = userService.insertOneUser(user);
        return res;
    }

    /**
     * 获取所有 user 列表
     * @return Map
     */
    @GetMapping("/user")
    public Map getUserLists() {
        Map users = userService.getUserLists();
        return users;
    }

    /**
     * 通过用户 id 获取一个 user
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable("id") String id) {
        User user = userService.getUserById(id);
        return user;
    }

    /**
     * 通过用户 id 修改用户信息
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/user/{id}")
    public Map updateUser(
            @PathVariable(value = "id") String id,
            @RequestBody @Valid User user) {
        Map res = userService.updateUser(id, user);
        return res;
    }

    /**
     * 通过用户 id 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/user/{id}")
    public Map delUserByUserId(@PathVariable(value = "id") String id) {
        Map res = userService.delUserByUserId(id);
        return res;
    }
}
