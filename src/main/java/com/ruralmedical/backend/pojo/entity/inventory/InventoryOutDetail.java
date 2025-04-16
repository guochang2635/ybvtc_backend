package com.ruralmedical.backend.pojo.entity.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
*
* 5. 入库明细表（t_inventory_in_detail）
描述：记录入库单中具体物资的批次及数量（一对多关联入库单）
字段：
detail_id（主键，VARCHAR (32)）：明细唯一 ID
in_id（VARCHAR (32)，外键，非空）：关联入库单号
material_id（VARCHAR (32)，外键，非空）：物资 ID（关联物资基础表）
batch_no（VARCHAR (50)，非空）：批次号（唯一索引，同物资 + 批次号不可重复入库）
quantity（DECIMAL (10,2)，非空）：入库数量（支持小数，如 0.5 盒）
expiry_date（DATE，非空）：有效期至（用于效期预警）
storage_location（VARCHAR (50)）：存储位置（如 “西药库 A 区 3 号货架”）
photo_url（TEXT）：物资包装照片存储路径（扫码时拍照上传）
唯一性：material_id + batch_no组合唯一（同一批次物资不可重复入库）。*/
@Entity
@Table(name = "t_inventory_out_detail",
        uniqueConstraints = @UniqueConstraint(columnNames = {"material_id", "batch_no"}))
@Data
public class InventoryOutDetail extends InventoryDetailEntity {
    @Id
    @Column(name = "detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String detailId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "i_id", nullable = false)
    private InventoryOutbound inventoryOut;
}
