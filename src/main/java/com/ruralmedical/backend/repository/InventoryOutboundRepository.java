package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutbound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryOutboundRepository extends JpaRepository <InventoryOutbound, Long>{

    @Query("SELECT i FROM InventoryOutbound i WHERE i.id = :inId AND i.isDeleted = false")
    InventoryOutbound findByOutId(Long inId);

    @Query("SELECT i FROM InventoryOutDetail i WHERE i.detailId = :inId AND i.isDeleted = false")
    InventoryOutDetail findDetailByOutId(Long inId);

    @Query("SELECT COUNT(i) FROM InventoryOutbound i WHERE i.isDeleted = false")
    int getOutboundCount();

    @Query("SELECT i FROM InventoryOutbound i WHERE i.isDeleted = false")
    Page<InventoryOutbound> findAllNotDeleted(Pageable pageable);

    @Query("SELECT i FROM InventoryOutbound i WHERE i.isDeleted = false AND i.status = 1")
    Page<InventoryOutbound> findAllNotDeletedDialog(Pageable pageable);
}
