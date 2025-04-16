package com.ruralmedical.backend.controller.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.service.v2.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

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

    // 添加机构
    @RequestMapping("")
    public ResponseMessage<Organization> addOrganization(@RequestBody Organization organization) {
        return organizationService.addOrganization(organization);
    }

    @GetMapping
    public ResponseMessage<Page<Organization>> getOrganization(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orgNameKeyword,
            @RequestParam(required = false) String  addressKeyword,
            @RequestParam(required = false) String contactPersonKeyword){
        return organizationService.getOrganization(page, size, orgNameKeyword, addressKeyword, contactPersonKeyword);
    }

    @GetMapping("/{orgId}")
    public ResponseMessage<Organization> getOrganizationById(@PathVariable String orgId) {
        return organizationService.getOrganizationById(orgId);
    }

    @PutMapping("/{orgId}")
    public ResponseMessage<Organization> updateOrganization(@PathVariable String orgId, @RequestBody Organization organization) {
        return organizationService.updateOrganization(orgId, organization);
    }

    @DeleteMapping("/{orgId}")
    public ResponseMessage<String> deleteOrganization(@PathVariable String orgId) {
        return organizationService.deleteOrganization(orgId);
    }
}
