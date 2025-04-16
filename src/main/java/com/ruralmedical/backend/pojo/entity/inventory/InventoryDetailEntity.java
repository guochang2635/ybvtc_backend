package com.ruralmedical.backend.pojo.entity.inventory;

import com.ruralmedical.backend.pojo.entity.MaterialBase;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@MappedSuperclass
public abstract class InventoryDetailEntity{

@NotNull
@ManyToOne
@JoinColumn(name = "material_id", nullable = false)
private MaterialBase material;

@NotNull
@Column(name = "batch_no", length = 50, nullable = false)
private String batchNo;

@Column(name = "quantity", nullable = false, precision = 10, scale = 2)
private BigDecimal quantity;

@NotNull
@Column(name = "storage_organization", length = 50, nullable = false)
private String storageOrganization;

@Column(name = "is_deleted", nullable = false)
private boolean isDeleted = false;
}
