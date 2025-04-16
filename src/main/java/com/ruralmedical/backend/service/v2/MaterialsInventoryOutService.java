package com.ruralmedical.backend.service.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.User;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutbound;
import com.ruralmedical.backend.pojo.entity.inventory.Stock;
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

@Service
public class MaterialsInventoryOutService {

    private static final Logger log = LoggerFactory.getLogger(MaterialsInventoryOutService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryOutboundRepository inventoryRepository;

    @Autowired
    private InventoryOutDetailRepository inventoryDetailRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    MaterialBaseRepository materialBaseRepository;

    @Autowired
    StockRepository stockRepository;

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
    public ResponseMessage<InventoryOutbound> addInventory(InventoryOutbound inventory) {
        User user = userRepository.findByUsername(inventory.getUser().getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        try {
            Organization organization = organizationRepository.findByOrgId(inventory.getOrg().getOrgId());
            log.info("用户：{}", user);
            log.info("inventoryOutbound：{}", inventory);
            log.info("机构：{}", organization);
            inventory.setOrg(organization);
            inventory.setUser(user);
            inventory.setStatus((byte) 1);
            inventoryRepository.save(inventory);
            return ResponseMessage.success("添加入库单成功", inventory);
        } catch (Exception e) {
            throw new RuntimeException("参数错误");
        }

    }

    public ResponseMessage<InventoryOutbound> getInventory(Long inId) {
        InventoryOutbound inventory = inventoryRepository.findByOutId(inId);
        return ResponseMessage.success("获取入库单成功", inventory);
    }

    public ResponseMessage<Page<InventoryOutbound>> getInventory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryOutbound> inventorys = inventoryRepository.findAllNotDeleted(pageable);

        Page<InventoryOutbound> dtoPage = inventorys.map(m -> {
            InventoryOutbound inventory = new InventoryOutbound();
            BeanUtils.copyProperties(m, inventory);
            return inventory;
        });
        return ResponseMessage.success("获取入库单列表成功",dtoPage);
    }

    public ResponseMessage<Page<InventoryOutbound>> getInventoryDialog(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryOutbound> inventorys = inventoryRepository.findAllNotDeletedDialog(pageable);

        Page<InventoryOutbound> dtoPage = inventorys.map(m -> {
            InventoryOutbound inventory = new InventoryOutbound();
            BeanUtils.copyProperties(m, inventory);
            return inventory;
        });
        return ResponseMessage.success("获取入库单列表成功",dtoPage);
    }

    // 出库单详情
    public ResponseMessage<InventoryOutDetail> addInventoryDetail(InventoryOutDetail inventoryDetail) {
        if (inventoryDetail.getInventoryOut() == null || inventoryDetail.getMaterial() == null) {
            throw new RuntimeException("输入参数无效");
        }
        boolean exists = inventoryDetailRepository.existsByMaterial_MaterialIdAndBatchNo(inventoryDetail.getMaterial().getMaterialId(), inventoryDetail.getBatchNo());

        if (exists) {
            throw new RuntimeException("该批次物资已入库");
        }
        try {
            InventoryOutbound inventoryOutbound = getInventoryOutboundById(inventoryDetail.getInventoryOut().getId());
            if (inventoryOutbound == null) {
                throw new RuntimeException("未找到入库单");
            }
            Organization storageOrganization = getOrganizationByOrgId(inventoryOutbound.getOrg().getOrgId());
            if (storageOrganization == null) {
                throw new RuntimeException("未找到隶属机构");
            }
            MaterialBase material = getMaterialByMaterialId(inventoryDetail.getMaterial().getMaterialId());
            if (material == null) {
                throw new RuntimeException("未找到药品信息");
            }
            BigDecimal quantity = inventoryDetail.getQuantity();
            Stock stock = stockRepository.findByOrganizationAndMaterial(storageOrganization, material).get(0);
            // 判断出库数量是非大于库存数
            if (stock.getCurrentQuantity().compareTo(quantity) < 0) {
                log.error("库存不足，无法出库，批次号: {}, 当前库存: {}, 出库数量: {}", inventoryDetail.getBatchNo(), stock.getCurrentQuantity(), quantity);
                throw new RuntimeException("出库数量大于库存数量");
            }

            inventoryDetail.setMaterial(material);
            inventoryDetail.setInventoryOut(inventoryOutbound);
            inventoryDetailRepository.save(inventoryDetail);

            return ResponseMessage.success("success", inventoryDetail);
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
            throw new RuntimeException("参数错误");
        }
    }

    // 封装查询入库单的方法
    private InventoryOutbound getInventoryOutboundById(Long id) {
        return inventoryRepository.findByOutId(id);
    }

    // 封装查询组织的方法
    private Organization getOrganizationByOrgId(Long orgId) {
        return organizationRepository.findByOrgId(orgId);
    }

    // 封装查询物料的方法
    private MaterialBase getMaterialByMaterialId(Long materialId) {
        return materialBaseRepository.findByMaterialId(materialId);
    }

    public ResponseMessage<InventoryOutDetail> getInventoryDetail(Long inId){
        return ResponseMessage.success("success", inventoryDetailRepository.findByInId(inId));
    }

    public ResponseMessage<InventoryOutbound> updateInventory(Long inId, InventoryOutbound inventoryOutbound) {
        InventoryOutbound inventory = inventoryRepository.findByOutId(inId);
        log.info("inventoryOutbound1：{}", inventory);
        inventory.setOrg(inventoryOutbound.getOrg());
        inventory.setInType(inventoryOutbound.getInType());
        inventoryRepository.save(inventory);
        return ResponseMessage.success("success", inventory);
    }

    public ResponseMessage<Page<InventoryOutDetail>> getInventoryDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryOutDetail> inventorys = inventoryDetailRepository.findAllNotDeleted(pageable);
        Page<InventoryOutDetail> dtoPage = inventorys.map(m -> {
            InventoryOutDetail inventory = new InventoryOutDetail();
            BeanUtils.copyProperties(m, inventory);
            return inventory;
        });
        return ResponseMessage.success("success", dtoPage);
    }

    public ResponseMessage<Page<InventoryOutDetail>> getInventoryDetailsDialog(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryOutDetail> inventorys = inventoryDetailRepository.findAllNotDeletedDialog(pageable);
        Page<InventoryOutDetail> dtoPage = inventorys.map(m -> {
            InventoryOutDetail inventory = new InventoryOutDetail();
            BeanUtils.copyProperties(m, inventory);
            return inventory;
        });
        return ResponseMessage.success("success", dtoPage);
    }

    public ResponseMessage<InventoryOutDetail> updateInventoryDetail(Long inId, InventoryOutDetail inventory) {
            // 查询入库明细
        try {
            InventoryOutDetail inventoryInDetail = inventoryDetailRepository.findByInId(inId);
            if (inventoryInDetail == null) {
                throw new RuntimeException("未找到入库单明细");
            }

            // 验证物资基础信息是否存在
            MaterialBase material = materialBaseRepository.findByMaterialId(inventory.getMaterial().getMaterialId());
            if (material == null) {
                throw new RuntimeException("未找到物资基础信息");
            }

            // 验证输入参数
            if (BigDecimal.valueOf(10).compareTo(inventory.getQuantity())< 0) {
                throw new RuntimeException("出库数量必须大于0");
            }
            if (inventory.getBatchNo() == null) {
                throw new RuntimeException("出库批次号不能为空");
            }

            // 更新入库明细
            inventoryInDetail.setMaterial(material);
            inventoryInDetail.setBatchNo(inventory.getBatchNo());
            inventoryInDetail.setQuantity(inventory.getQuantity());

            // 保存更新后的入库明细
            inventoryDetailRepository.save(inventoryInDetail);

            return ResponseMessage.success("success", inventoryInDetail);

        } catch (Exception e) {
            // 捕获异常并返回通用错误信息
            if (e.getCause() != null && e.getCause().getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")
                    && e.getCause().getMessage().contains("batch_no")) {
                throw new RuntimeException("入库单号重复");
            } else {
                throw new RuntimeException("error");
            }
        }
    }


    public ResponseMessage<String> deleteInventory(Long inId) {
        InventoryOutbound inventory = inventoryRepository.findByOutId(inId);
        if (inventory == null) {
            throw new RuntimeException("未找到入库单");
        }
        inventory.setDeleted(true);
        inventoryRepository.save(inventory);
        return ResponseMessage.success("删除成功",null);
    }

    public ResponseMessage<String> deleteInventorys(Long[] ids) {
        for (Long id : ids) {
            InventoryOutbound inventory = inventoryRepository.findById(id).orElse(null);
            if (inventory == null) {
                throw new RuntimeException("材料不存在");
            }
            inventory.setDeleted(true);
            inventoryRepository.save(inventory);
        }
        return ResponseMessage.success("删除成功",null);
    }

    public ResponseMessage<String> deleteInventoryOutboundDetail(Long inId) {
        InventoryOutDetail inventoryOutDetail = inventoryDetailRepository.findByInId(inId);
        if (inventoryOutDetail == null) {
            throw new RuntimeException("未找到入库单明细");
        }
        inventoryOutDetail.setDeleted(true);
        inventoryDetailRepository.save(inventoryOutDetail);
        return ResponseMessage.success("删除成功", null);
    }

    public ResponseMessage<String> deleteInventoryOutboundDetail(Long[] ids) {
        for (Long id : ids) {
            InventoryOutDetail inventory = inventoryDetailRepository.findById(id).orElse(null);
            if (inventory == null) {
                throw new RuntimeException("材料不存在");
            }
            inventory.setDeleted(true);
            inventoryDetailRepository.save(inventory);
        }
        return ResponseMessage.success("删除成功", null);
    }
}
