package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryOutDetailRepository extends JpaRepository<InventoryOutDetail, Long> {

    @Query("SELECT i FROM InventoryOutDetail i WHERE i.detailId = :inId AND i.isDeleted = false")
    InventoryOutDetail findByInId(Long inId);

    @Query("SELECT i FROM InventoryOutDetail i WHERE i.batchNo = :batchNo AND i.isDeleted = false")
    List<InventoryOutDetail> findListByBatchNo(String batchNo);

    boolean existsByMaterial_MaterialIdAndBatchNo(Long materialId, String batchNo);

    @Query("SELECT i FROM InventoryOutDetail i WHERE i.isDeleted = false")
    Page<InventoryOutDetail> findAllNotDeleted(Pageable pageable);

    @Query("SELECT i FROM InventoryOutDetail i WHERE i.isDeleted = false AND i.inventoryOut.status = 1")
    Page<InventoryOutDetail> findAllNotDeletedDialog(Pageable pageable);
}
