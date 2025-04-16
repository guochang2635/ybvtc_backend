package com.ruralmedical.backend.pojo.entity.inventory;

import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class InventoryEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "org_id",referencedColumnName= "org_id", nullable = false)
    private Organization org;

    // 入库类型（1 = 采购，2 = 调拨，3 = 盘盈，4 = 其他）
    @Column
    private Byte inType;

    // 关联采购订单号（采购入库时必填）
//    private String orderId;

    // 操作人账户（关联用户表）
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 入库时间（默认当前时间）
    @Column(name = "entry_date")
    private LocalDateTime entryDate;

    // 状态（1 = 暂存，2 = 已提交，3 = 已审核）
    @Column
    private Byte status;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
