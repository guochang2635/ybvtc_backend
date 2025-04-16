package com.ruralmedical.backend.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "material_base", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"material_name", "specification", "unit"})
})
public class MaterialBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;

    @Column(nullable = false)
    private String materialName;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",nullable = false)
    private User user;

    // 短缺阈值
    @Column(nullable = false)
    private int minQuantity;

    // 规格（如 “10 只 / 盒”）
    @Column
    private String specification;

    // 单位（盒、瓶、件）
    @Column
    private String unit;

    // 分类（药品 / 器械 / 耗材 / 消毒用品）
    @Column
    private String category;

    // 存储条件（常温 / 冷藏 / 避光）
    @Column
    private String storageCondition;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
