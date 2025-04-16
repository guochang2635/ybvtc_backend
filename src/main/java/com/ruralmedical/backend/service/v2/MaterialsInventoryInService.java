package com.ruralmedical.backend.service.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.User;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInbound;
import com.ruralmedical.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service
public class MaterialsInventoryInService {

    private static final Logger log = LoggerFactory.getLogger(MaterialsInventoryInService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryInboundRepository inventoryInboundRepository;

    @Autowired
    private InventoryInDetailRepository inventoryInDetailRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    MaterialBaseRepository materialBaseRepository;

    /*
    *
    * 4. 入库单表（t_inventory_in）
        描述：记录入库明细，区分不同入库类型（采购 / 调拨 / 盘盈等）
        字段：
        in_id（主键，VARCHAR (32)）：入库单号（如 “RK20250405001”）
        org_id（VARCHAR (32)，外键，非空）：入库机构 ID
        in_type（TINYINT，非空）：入库类型（1 = 采购，2 = 调拨，3 = 盘盈，4 = 其他）
        order_id（VARCHAR (32)）：关联采购订单号（采购入库时必填）
        operator（VARCHAR (32)，非空）：操作人账户（关联用户表）
        in_time（DATETIME，非空）：入库时间（默认当前时间）
        status（TINYINT）：状态（1 = 暂存，2 = 已提交，3 = 已审核）
        关联表：通过in_id关联in_detail表（入库明细）。


        5. 入库明细表（t_inventory_in_detail）
          描述：记录入库单中具体物资的批次及数量（一对多关联入库单）
          字段：
          detail_id（主键，VARCHAR (32)）：明细唯一 ID
          in_id（VARCHAR (32)，外键，非空）：关联入库单号
          material_id（VARCHAR (32)，外键，非空）：物资 ID（关联物资基础表）
          batch_no（VARCHAR (50)，非空）：批次号（唯一索引，同物资 + 批次号不可重复入库）
          quantity（DECIMAL (10,2)，非空）：入库数量（支持小数，如 0.5 盒）
          expiry_date（DATE，非空）：有效期至（用于效期预警）
          storage_location（VARCHAR (50)）：存储位置（如 “西药库 A 区 3 号货架”）
          photo_url（TEXT）：物资包装照片存储路径（扫码时拍照上传）
          唯一性：material_id + batch_no组合唯一（同一批次物资不可重复入库）。
    *
    * */

    // 入库单
    public ResponseMessage<InventoryInbound> addInventoryInbound(InventoryInbound inventoryInbound) {
        User user = userRepository.findByUsername(inventoryInbound.getUser().getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        try {
            Organization organization = organizationRepository.findByOrgId(inventoryInbound.getOrg().getOrgId());
            log.info("用户：{}", user);
            log.info("inventoryInbound：{}", inventoryInbound);
            log.info("机构：{}", organization);
            inventoryInbound.setOrg(organization);
            inventoryInbound.setUser(user);
            inventoryInbound.setStatus((byte) 1);
            inventoryInboundRepository.save(inventoryInbound);
            return ResponseMessage.success("添加入库单成功", inventoryInbound);
        } catch (Exception e) {
            throw new RuntimeException("参数错误");
        }

    }

    public ResponseMessage<InventoryInbound> getInventoryInbound(Long inId) {
        InventoryInbound inventoryInbound = inventoryInboundRepository.findByInId(inId);
        return ResponseMessage.success("获取入库单成功", inventoryInbound);
    }

    public ResponseMessage<Page<InventoryInbound>> getInventoryInbounds(int page, int size, String orgaddressKeyword, String statusKeyword, String idKeyword, String orgNameKeyword) {
        Page<InventoryInbound> inventoryInbounds;
        Pageable pageable = PageRequest.of(page, size);

        // 搜索
        if (orgaddressKeyword != null && !orgaddressKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByOrgAddressContaining(orgaddressKeyword, pageable);
        } else if (statusKeyword != null && !statusKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByStatus(Byte.parseByte(statusKeyword), pageable);
        } else if (idKeyword != null && !idKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByInIdContaining(Long.valueOf(idKeyword), pageable);
        } else if (orgNameKeyword != null && !orgNameKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByOrgNameContaining(orgNameKeyword, pageable);
        } else {
            inventoryInbounds = inventoryInboundRepository.findAllNotDeleted(pageable);
        }

        Page<InventoryInbound> dtoPage = inventoryInbounds.map(m -> {
            InventoryInbound inventoryInbound = new InventoryInbound();
            BeanUtils.copyProperties(m, inventoryInbound);
            return inventoryInbound;
        });
        return ResponseMessage.success("获取入库单列表成功", dtoPage);
    }

    public ResponseMessage<Page<InventoryInbound>> getInventoryInboundsDialog(int page, int size, String orgaddressKeyword, String idKeyword, String orgNameKeyword) {
        Page<InventoryInbound> inventoryInbounds;
        Pageable pageable = PageRequest.of(page, size);

        // 搜索
        if (orgaddressKeyword != null && !orgaddressKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByOrgAddressContainingDialog(orgaddressKeyword, pageable);
        } else if (idKeyword != null && !idKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByInIdContainingDialog(Long.valueOf(idKeyword), pageable);
        } else if (orgNameKeyword != null && !orgNameKeyword.isEmpty()) {
            inventoryInbounds = inventoryInboundRepository.findByOrgNameContainingDialog(orgNameKeyword, pageable);
        } else {
            inventoryInbounds = inventoryInboundRepository.findAllNotDeletedDialog(pageable);
        }

        Page<InventoryInbound> dtoPage = inventoryInbounds.map(m -> {
            InventoryInbound inventoryInbound = new InventoryInbound();
            BeanUtils.copyProperties(m, inventoryInbound);
            return inventoryInbound;
        });
        return ResponseMessage.success("获取入库单列表成功", dtoPage);
    }


    // 入库单详情
    public ResponseMessage<InventoryInDetail> addInventoryInboundDetail(InventoryInDetail inventoryInDetail) {
        if (inventoryInDetail.getInventoryIn() == null || inventoryInDetail.getMaterial() == null) {
            throw new RuntimeException("输入参数无效");
        }
        boolean exists = inventoryInDetailRepository.existsByMaterial_MaterialIdAndBatchNo(inventoryInDetail.getMaterial().getMaterialId(), inventoryInDetail.getBatchNo());

        if (exists) {
            throw new RuntimeException("该批次物资已入库");
        }
        try {
            InventoryInbound inventoryInbound = getInventoryInboundById(inventoryInDetail.getInventoryIn().getId());
            if (inventoryInbound == null) {
                throw new RuntimeException("未找到入库单");
            }
            Organization storageOrganization = getOrganizationByOrgId(inventoryInbound.getOrg().getOrgId());
            if (storageOrganization == null) {
                throw new RuntimeException("未找到隶属机构");
            }
            MaterialBase material = getMaterialByMaterialId(inventoryInDetail.getMaterial().getMaterialId());
            if (material == null) {
                throw new RuntimeException("未找到药品信息");
            }

            inventoryInDetail.setMaterial(material);
            inventoryInDetail.setInventoryIn(inventoryInbound);
            inventoryInDetailRepository.save(inventoryInDetail);

            return ResponseMessage.success("success", inventoryInDetail);
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
            throw new RuntimeException("参数错误");
        }
    }

    // 封装查询入库单的方法
    private InventoryInbound getInventoryInboundById(Long id) {
        return inventoryInboundRepository.findByInId(id);
    }

    // 封装查询组织的方法
    private Organization getOrganizationByOrgId(Long orgId) {
        return organizationRepository.findByOrgId(orgId);
    }

    // 封装查询物料的方法
    private MaterialBase getMaterialByMaterialId(Long materialId) {
        return materialBaseRepository.findByMaterialId(materialId);
    }

    public ResponseMessage<InventoryInDetail> getInventoryInDetail(Long inId) {
        return ResponseMessage.success("success", inventoryInDetailRepository.findByInId(inId));
    }

    public ResponseMessage<InventoryInbound> updateInventoryInbound(Long inId, InventoryInbound inventoryInbound) {
        InventoryInbound inventory = inventoryInboundRepository.findByInId(inId);
        log.info("inventoryInbound1：{}", inventory);
        inventory.setOrg(inventoryInbound.getOrg());
        inventory.setInType(inventoryInbound.getInType());
        inventoryInboundRepository.save(inventory);
        return ResponseMessage.success("success", inventory);
    }

    public ResponseMessage<Page<InventoryInDetail>> getInventoryInDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryInDetail> inventoryInDetails = inventoryInDetailRepository.findAllNotDeleted(pageable);
        Page<InventoryInDetail> dtoPage = inventoryInDetails.map(m -> {
            InventoryInDetail inventoryInDetail = new InventoryInDetail();
            BeanUtils.copyProperties(m, inventoryInDetail);
            return inventoryInDetail;
        });
        return ResponseMessage.success("success", dtoPage);
    }

    public ResponseMessage<Page<InventoryInDetail>> getInventoryInDetailsDialog(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryInDetail> inventoryInDetails = inventoryInDetailRepository.findAllNotDeletedDialog(pageable);
        Page<InventoryInDetail> dtoPage = inventoryInDetails.map(m -> {
            InventoryInDetail inventoryInDetail = new InventoryInDetail();
            BeanUtils.copyProperties(m, inventoryInDetail);
            return inventoryInDetail;
        });
        return ResponseMessage.success("success", dtoPage);
    }

    public ResponseMessage<InventoryInDetail> updateInventoryInboundDetail(Long inId, InventoryInDetail inventoryInbound) {
        // 查询入库明细
        try {
            InventoryInDetail inventoryInDetail = inventoryInDetailRepository.findByInId(inId);
            if (inventoryInDetail == null) {
                throw new RuntimeException("未找到入库单明细");
            }

            // 验证物资基础信息是否存在
            MaterialBase material = materialBaseRepository.findByMaterialId(inventoryInbound.getMaterial().getMaterialId());
            if (material == null) {
                throw new RuntimeException("未找到物资基础信息");
            }

            // 验证输入参数
            if (BigDecimal.valueOf(10).compareTo(inventoryInbound.getQuantity()) > 0) {
                throw new RuntimeException("入库数量必须大于0");
            }
            if (inventoryInbound.getBatchNo() == null) {
                throw new RuntimeException("入批次号不能为空");
            }

            // 更新入库明细
            inventoryInDetail.setMaterial(material);
            inventoryInDetail.setBatchNo(inventoryInbound.getBatchNo());
            inventoryInDetail.setQuantity(inventoryInbound.getQuantity());
            inventoryInDetail.setExpiryDate(new java.sql.Date(System.currentTimeMillis())); // 使用系统时间戳
//            inventoryInDetail.setStorageOrganization(inventoryInbound.getStorageOrganization());

            // 保存更新后的入库明细
            inventoryInDetailRepository.save(inventoryInDetail);

            return ResponseMessage.success("success", inventoryInDetail);

        } catch (Exception e) {
            // 捕获异常并返回通用错误信息
            if (e.getCause() != null && e.getCause().getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")
                    && e.getCause().getMessage().contains("batch_no")) {
                throw new RuntimeException("入库单号重复");
            } else {
                throw new RuntimeException("error"+e.getMessage());
            }
        }
    }


    public ResponseMessage<String> deleteInventoryInbound(Long inId) {
        InventoryInbound inventoryInbound = inventoryInboundRepository.findByInId(inId);
        if (inventoryInbound == null) {
            throw new RuntimeException("未找到入库单");
        }
        inventoryInbound.setDeleted(true);
        inventoryInboundRepository.save(inventoryInbound);
        return ResponseMessage.success("删除成功", null);
    }

    public ResponseMessage<String> deleteInventoryInbounds(Long[] ids) {
        for (Long id : ids) {
            InventoryInbound inventoryInbound = inventoryInboundRepository.findById(id).orElse(null);
            if (inventoryInbound == null) {
                throw new RuntimeException("材料不存在");
            }
            inventoryInbound.setDeleted(true);
            inventoryInboundRepository.save(inventoryInbound);
        }
        return ResponseMessage.success("删除成功", null);
    }

    public ResponseMessage<String> deleteInventoryInboundDetail(Long inId) {
        InventoryInDetail inventoryInDetail = inventoryInDetailRepository.findByInId(inId);
        if (inventoryInDetail == null) {
            throw new RuntimeException("未找到入库单明细");
        }
        inventoryInDetail.setDeleted(true);
        inventoryInDetailRepository.save(inventoryInDetail);
        return ResponseMessage.success("删除成功", null);
    }

    public ResponseMessage<String> deleteInventoryInboundDetails(Long[] ids) {
        for (Long id : ids) {
            InventoryInDetail inventory = inventoryInDetailRepository.findById(id).orElse(null);
            if (inventory == null) {
                throw new RuntimeException("材料不存在");
            }
            inventory.setDeleted(true);
            inventoryInDetailRepository.save(inventory);
        }
        return ResponseMessage.success("删除成功", null);
    }
}
