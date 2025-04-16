package com.ruralmedical.backend.pojo.entity.dto;

import lombok.Data;

@Data
public class MaterialBaseDTO {
    private Long materialId;
    private String materialName;
    private String userName;
    // 规格（如 “10 只 / 盒”）
    private String specification;
    // 单位（盒、瓶、件）
    private String unit;
    // 分类（药品 / 器械 / 耗材 / 消毒用品）
    private String category;
    // 存储条件（常温 / 冷藏 / 避光）
    private String storageCondition;
}
