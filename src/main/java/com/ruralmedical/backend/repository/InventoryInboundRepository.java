package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.inventory.InventoryInbound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryInboundRepository extends JpaRepository <InventoryInbound, Long>{

    @Query("SELECT i FROM InventoryInbound i WHERE i.id = :inId AND i.isDeleted = false")
    InventoryInbound findByInId(Long inId);

    @Query("SELECT COUNT(i) FROM InventoryInbound i WHERE i.isDeleted = false")
    int getInboundCount();

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false")
    Page<InventoryInbound> findAllNotDeleted(Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND i.status = 1")
    Page<InventoryInbound> findAllNotDeletedDialog(Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND i.org.address LIKE %:orgaddressKeyword%")
    Page<InventoryInbound> findByOrgAddressContaining(String orgaddressKeyword, Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND i.status = :status")
    Page<InventoryInbound> findByStatus(byte status, Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND i.org.orgName LIKE %:orgNameKeyword%")
    Page<InventoryInbound> findByOrgNameContaining(String orgNameKeyword, Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND i.id = :idKeyword")
    Page<InventoryInbound> findByInIdContaining(Long idKeyword, Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND  i.status = 1 AND i.org.address LIKE %:orgaddressKeyword%")
    Page<InventoryInbound> findByOrgAddressContainingDialog(String orgaddressKeyword, Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND  i.status = 1 AND i.org.orgId=:idKeyword")
    Page<InventoryInbound> findByInIdContainingDialog(Long idKeyword, Pageable pageable);

    @Query("SELECT i FROM InventoryInbound i WHERE i.isDeleted = false AND  i.status = 1 AND i.org.orgName LIKE %:orgNameKeyword%")
    Page<InventoryInbound> findByOrgNameContainingDialog(String orgNameKeyword, Pageable pageable);
}
