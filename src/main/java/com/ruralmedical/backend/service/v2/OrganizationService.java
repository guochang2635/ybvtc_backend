package com.ruralmedical.backend.service.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.dto.OrganizationDTO;
import com.ruralmedical.backend.repository.OrganizationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrganizationService {
    /*
    * 1. 机构表（t_organization）
         描述：记录县 / 乡 / 村三级医疗机构及仓库信息
         字段：
         org_id（主键，VARCHAR (32)）：机构唯一编码（如 “CX001” 代表某村卫生室）
         org_name（VARCHAR (50)，非空）：机构名称（如 “XX 镇中心卫生院”）
         org_type（TINYINT，非空）：机构类型（1 = 县级医院，2 = 乡镇卫生院，3 = 村卫生室，4 = 仓库）
         address（VARCHAR (100)）：地址（省 / 市 / 县 / 村）
         parent_org_id（VARCHAR (32)）：上级机构 ID（如村卫生室的上级为所属卫生院）
         contact_person（VARCHAR (20)）：负责人姓名
         phone（VARCHAR (15)）：联系电话
         关联场景：入库 / 出库时关联机构，支持多级调拨统计（如县级向乡镇调拨物资）。
    * */

    @Autowired
    private OrganizationRepository organizationRepository;

    //添加机构
    public ResponseMessage<Organization> addOrganization(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();

        BeanUtils.copyProperties(organization, dto);
        organizationRepository.save(organization);
        return ResponseMessage.success("添加机构成功", organization);
    }

    public ResponseMessage<Page<Organization>> getOrganization(int page, int size, String orgNameKeyword, String addressKeyword, String contactPersonKeyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Organization> organizations;
        // 搜索
        if (Objects.nonNull(orgNameKeyword) && !orgNameKeyword.isEmpty()) {
            organizations = organizationRepository.findAllByOrgNameContaining(orgNameKeyword, pageable);
        } else if (Objects.nonNull(addressKeyword) && !addressKeyword.isEmpty()) {
            organizations = organizationRepository.findAllByAddressContaining(addressKeyword, pageable);
        } else if (Objects.nonNull(contactPersonKeyword) && !contactPersonKeyword.isEmpty()) {
            organizations = organizationRepository.findAllByContactPersonContaining(contactPersonKeyword, pageable);
        } else {
            organizations = organizationRepository.findAllNotDeleted(pageable);
        }
        return ResponseMessage.success("查询机构成功", organizations);
    }

    public ResponseMessage<Organization> getOrganizationById(String orgId) {
        return ResponseMessage.success("查询机构成功", organizationRepository.findByOrgId(Long.parseLong(orgId)));
    }

    public ResponseMessage<Organization> updateOrganization(String orgId, Organization organization) {
        return ResponseMessage.success("更新机构成功", organizationRepository.save(organization));
    }

    public ResponseMessage<String> deleteOrganization(String orgId) {
        Organization organization = organizationRepository.findByOrgId(Long.parseLong(orgId));
        if (organization == null) {
            throw new RuntimeException("材料不存在");
        }
        organization.setDeleted(true);
        organizationRepository.save(organization);
        return ResponseMessage.success("删除机构成功");
    }
}
