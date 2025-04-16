package com.ruralmedical.backend.pojo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "organization", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_name", "org_type"})
})
public class Organization {
    @Id
    @Column(name = "org_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;

    @Column(name = "org_name",nullable = false)
    private String orgName;

    @Column(name = "org_type",nullable = false)
    private Integer orgType;

    private String address;

    private String parent_org_id;

    // 联系人
    private String contact_person;

    private String phone;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
