package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT m FROM Material m WHERE m.user.userId = :userId")
    Page<Material> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT m FROM Material m WHERE m.entryDate >= :startDate")
    Page<Material> findRecentMaterials(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT m FROM Material m WHERE m.name LIKE %:name%")
    Page<Material> findByName(@Param("name") String name, Pageable pageable);
}
