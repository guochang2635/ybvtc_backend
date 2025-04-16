package com.ruralmedical.backend.pojo.entity.dto;

import lombok.Data;

@Data
public class OrganizationDTO {

    private Long orgId;

    private String orgName;

    private Integer orgType;

    private String address;

    private String parent_org_id;

    private String contact_person;

    private String phone;
}
