package com.ruralmedical.backend.controller;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.User;
import com.ruralmedical.backend.pojo.entity.dto.UserDTO;
import com.ruralmedical.backend.pojo.entity.dto.UserTokenDTO;
import com.ruralmedical.backend.repository.UserRepository;
import com.ruralmedical.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // 获取所有用户（查）
    @GetMapping
    public ResponseMessage<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    // 注册（增）
    @PostMapping("/register")
    public ResponseMessage<String> register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // 登录（查）
    @PostMapping("/login")
    public ResponseMessage<UserTokenDTO> login(@RequestBody User user) {
        return userService.loginUser(user);
    }

    // 更新用户（改）
    @PutMapping("/{id}")
    public ResponseMessage<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @PutMapping("/edit")
    public ResponseMessage<String> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    // 删除用户（删）
    @DeleteMapping("/{user}")
    public ResponseMessage<String> deleteUser(@PathVariable String user) {
        userService.deleteUser(user);
        return ResponseMessage.success("删除成功");
    }
}
