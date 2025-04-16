package com.ruralmedical.backend.pojo.entity.dto;

import lombok.Data;

@Data
public class UserTokenDTO {
    private String username;
    private String token;
}
