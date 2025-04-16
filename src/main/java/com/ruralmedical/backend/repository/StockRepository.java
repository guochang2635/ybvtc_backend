package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import com.ruralmedical.backend.pojo.entity.inventory.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {

    List<Stock> findByOrganizationAndMaterial(Organization organization, MaterialBase material);

    Stock findByOrganizationAndMaterialAndBatchNo(Organization organization, MaterialBase material, InventoryInDetail batchNo);

    @Transactional
    @Modifying
    @Query("UPDATE Stock s SET s.currentQuantity = s.currentQuantity + :quantity, s.lastUpdateTime = :lastUpdateTime WHERE s.organization = :orgId AND s.material = :materialId")
    int increaseQuantity(@Param("orgId") Organization orgId, @Param("materialId") MaterialBase materialId, @Param("quantity") BigDecimal quantity, @Param("lastUpdateTime") Date lastUpdateTime);

    @Transactional
    @Modifying
    @Query("UPDATE Stock s SET s.currentQuantity = s.currentQuantity - :quantity, s.lastUpdateTime = :lastUpdateTime WHERE s.organization = :orgId AND s.material = :materialId AND s.currentQuantity >= :quantity")
    int decreaseQuantity(@Param("orgId") Organization orgId, @Param("materialId") MaterialBase materialId, @Param("quantity") BigDecimal quantity, @Param("lastUpdateTime") Date lastUpdateTime);

    @Query("SELECT s FROM Stock s JOIN MaterialBase mb ON s.material = mb WHERE s.organization = :orgId AND mb.materialName LIKE %:materialName%")
    List<Stock> findByOrgIdAndMaterialName(@Param("orgId") Organization orgId, @Param("materialName") MaterialBase materialName);

    @Query("SELECT s FROM Stock s JOIN MaterialBase mb ON s.material = mb JOIN Organization org ON s.organization = org WHERE org.orgType = :orgType AND s.expiryDate BETWEEN :startDate AND :endDate")
    List<Stock> findByOrgTypeAndExpiryDate(@Param("orgType") int orgType, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT SUM(s.currentQuantity) FROM Stock s WHERE s.organization = :orgId AND s.material = :materialId")
    BigDecimal getTotalQuantityByOrgIdAndMaterialId(@Param("orgId") Organization orgId, @Param("materialId") MaterialBase materialId);

    @Query("SELECT SUM(s.currentQuantity) FROM Stock s")
    BigDecimal getTotalInventoryQuantity();

    @Query("SELECT s FROM Stock s ORDER BY s.lastUpdateTime DESC")
    Page<Stock> getRecentInbound(Pageable pageable);
}
