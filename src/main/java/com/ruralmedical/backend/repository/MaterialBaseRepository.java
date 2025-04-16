package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.MaterialBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MaterialBaseRepository extends JpaRepository<MaterialBase, Long> {

    @Query("SELECT m FROM MaterialBase m WHERE m.materialName LIKE %:name% AND m.isDeleted = false")
    Page<MaterialBase> findByName(String name, Pageable pageable);

    @Query("SELECT m FROM MaterialBase m WHERE m.user.userId = :userId AND m.isDeleted = false")
    Page<MaterialBase> findByUserId(Long userId, Pageable pageable);


    @Query("SELECT m FROM MaterialBase m WHERE m.user.username LIKE %:username% AND m.isDeleted = false")
    Page<MaterialBase> findByUserName(String username, Pageable pageable);

    @Query("SELECT m FROM MaterialBase m WHERE m.storageCondition LIKE %:storageCondition% AND m.isDeleted = false")
    Page<MaterialBase> findByStorageCondition(String storageCondition, Pageable pageable);

    @Query("SELECT m FROM MaterialBase m WHERE m.category LIKE %:category% AND m.isDeleted = false")
    Page<MaterialBase> findByCategory(String category, Pageable pageable);

    @Query("SELECT m FROM MaterialBase m WHERE m.unit LIKE %:unit% AND m.isDeleted = false")
    Page<MaterialBase> findByUnit(String unit, Pageable pageable);

    @Query("SELECT m FROM MaterialBase m WHERE m.specification LIKE %:specification% AND m.isDeleted = false")
    Page<MaterialBase> findBySpecification(String specification, Pageable pageable);

    @Query("SELECT m FROM MaterialBase m WHERE m.materialId = :id AND m.isDeleted = false")
    MaterialBase findByMaterialId(Long id);

    @Query("SELECT m FROM MaterialBase m WHERE m.isDeleted = false")
    Page<MaterialBase> findAllNotDeleted(Pageable pageable);

}
