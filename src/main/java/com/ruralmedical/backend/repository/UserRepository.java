package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username); // 用于登录查询
}
