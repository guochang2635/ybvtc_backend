package com.ruralmedical.backend.pojo.entity.inventory;

import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_stock",
        indexes = {
                @Index(name = "idx_org_material_batch", columnList = "org_id, material_id, batch_no"),
                @Index(name = "idx_expiry_date", columnList = "expiry_date")
        })
@Data
public class Stock {
    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String stockId;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialBase material;

    @ManyToOne
    @JoinColumn(name = "batch_no", nullable = false)
    private InventoryInDetail batchNo;

    @Column(name = "current_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentQuantity;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;


    @Column(name = "last_update_time", nullable = false)
    private Date lastUpdateTime;
}
