package com.ruralmedical.backend.pojo.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialDTO {

    private Long id;
    private String name;
    private String userName;
    private Integer quantity;
    private Integer threshold;
    private LocalDateTime entryDate;
}
