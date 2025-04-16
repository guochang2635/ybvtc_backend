package com.ruralmedical.backend.service;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.User;
import com.ruralmedical.backend.pojo.entity.dto.MaterialBaseDTO;
import com.ruralmedical.backend.pojo.entity.dto.UserDTO;
import com.ruralmedical.backend.pojo.entity.dto.UserTokenDTO;
import com.ruralmedical.backend.repository.UserRepository;
import com.ruralmedical.backend.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseMessage<String> registerUser(User user) {
        String password = user.getPassword();
        String passwordHash = passwordEncoder.encode(password);
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setUsername(username);
        user.setPassword(passwordHash);

        userRepository.save(user);
        return ResponseMessage.success("注册成功");
    }

    public ResponseMessage<UserTokenDTO> loginUser(User user) {
        UserTokenDTO userPojo = new UserTokenDTO();
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        if (!passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        //  生成 token
        String token = jwtUtil.generateToken(userDetails);
        BeanUtils.copyProperties(user, userPojo);
        userPojo.setToken(token);
        return ResponseMessage.success(userPojo);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名从数据库中查找用户
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户未找到");
        }
        // 创建 Spring Security 的 UserDetails 对象
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public ResponseMessage<Page<UserDTO>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> user;
        user = userRepository.findAll(pageable);

        Page<UserDTO> dtoPage = user.map(m -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(m, dto);
            return dto;
        });
        return ResponseMessage.success(dtoPage);
    }

    public ResponseMessage<String> updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        existingUser.setFullName(user.getFullName());
        existingUser.setPhone(user.getPhone());
        userRepository.save(existingUser);
        return ResponseMessage.success("用户信息更新成功");
    }

    public ResponseMessage<String> updateUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        existingUser.setFullName(user.getFullName());
        existingUser.setPhone(user.getPhone());
        userRepository.save(existingUser);
        return ResponseMessage.success("用户信息更新成功");
    }

    public ResponseMessage<String> deleteUser(String u) {
        User user = userRepository.findByUsername(u);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(user.getUserId());
        return ResponseMessage.success("删除成功");
    }
}
