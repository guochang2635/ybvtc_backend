package com.ruralmedical.backend.pojo.entity.inventory;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory_out")
public class InventoryOutbound extends InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
