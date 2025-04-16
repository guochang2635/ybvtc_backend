package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Query("SELECT o FROM Organization o WHERE o.orgId = :orgId AND o.isDeleted = false")
    Organization findByOrgId(Long orgId);

    //统计管理机构数
    @Query("SELECT COUNT(o) FROM Organization o WHERE o.isDeleted = false")
    int getOrgCount();

    @Query("SELECT o FROM Organization o WHERE o.isDeleted = false")
    void findAllNotDeleted();

    @Query("SELECT o FROM Organization o WHERE o.isDeleted = false")
    Page<Organization> findAllNotDeleted(Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE o.orgName LIKE %:orgNameKeyword% AND o.isDeleted = false")
    Page<Organization> findAllByOrgNameContaining(String orgNameKeyword, Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE o.address LIKE %:addressKeyword% AND o.isDeleted = false")
    Page<Organization> findAllByAddressContaining(String addressKeyword, Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE o.contact_person LIKE %:contactPersonKeyword% AND o.isDeleted = false")
    Page<Organization> findAllByContactPersonContaining(String contactPersonKeyword, Pageable pageable);
}
