package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryInDetailRepository extends JpaRepository<InventoryInDetail, Long> {

    @Query("SELECT i FROM InventoryInDetail i WHERE i.detailId = :inId AND i.isDeleted = false")
    InventoryInDetail findByInId(Long inId);

    @Query("SELECT i FROM InventoryInDetail i WHERE i.batchNo = :batchNo AND i.isDeleted = false")
    InventoryInDetail findByBatchNo(String batchNo);

    @Query("SELECT i FROM InventoryInDetail i WHERE i.batchNo = :batchNo AND i.isDeleted = false")
    List<InventoryInDetail> findListByBatchNo(String batchNo);

    boolean existsByMaterial_MaterialIdAndBatchNo(Long materialId, String batchNo);

    @Query("SELECT i FROM InventoryInDetail i WHERE i.isDeleted = false")
    Page<InventoryInDetail> findAllNotDeleted(Pageable pageable);

    @Query("SELECT i FROM InventoryInDetail i WHERE i.isDeleted = false AND i.inventoryIn.status = 1")
    Page<InventoryInDetail> findAllNotDeletedDialog(Pageable pageable);
}
