package com.ruralmedical.backend.service.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.inventory.*;
import com.ruralmedical.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class StockService {
    private static final Logger log = LoggerFactory.getLogger(StockService.class);
    private final StockRepository stockRepository;

    @Autowired
    InventoryInboundRepository inventoryInboundRepository;

    @Autowired
    InventoryOutboundRepository inventoryOutboundRepository;

    @Autowired
    InventoryInDetailRepository inventoryDetailRepository;

    @Autowired
    InventoryOutDetailRepository inventoryDetailRepositoryOut;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock findStock(Organization orgId, MaterialBase materialId, InventoryInDetail batchNo) {
        return stockRepository.findByOrganizationAndMaterialAndBatchNo(orgId, materialId, batchNo);
    }

    public boolean increaseStockQuantity(String batchNoKeyword) {
        log.info("batchNo:{}", batchNoKeyword);
        int rowsAffected = 0;
        List<InventoryInDetail> inventoryInDetailList = inventoryDetailRepository.findListByBatchNo(batchNoKeyword);
        for (InventoryInDetail inventoryInDetail : inventoryInDetailList) {
            InventoryInbound inventoryInbound = inventoryInDetail.getInventoryIn();
            Organization orgId = inventoryInbound.getOrg();
            MaterialBase materialId = inventoryInDetail.getMaterial();
            BigDecimal quantity = inventoryInDetail.getQuantity();
            Date lastUpdateTime = new Date();

            // 检查入库单状态
            Byte status = inventoryInbound.getStatus();
            if (status == 2) {
                log.error("此入库单已经入库，入库单 ID: {}", inventoryInbound.getId());
                throw new RuntimeException("此入库单已经入库");
            }

            // 查找库存记录d
            List<Stock> list = stockRepository.findByOrganizationAndMaterial(orgId, materialId);
//            Stock stock = stockRepository.findByOrganizationAndMaterial(orgId, materialId).get(0);
            if (list.isEmpty()) {
                // 初次入库
                Stock stock = new Stock();
                stock.setOrganization(orgId);
                stock.setMaterial(materialId);
                stock.setBatchNo(inventoryInDetail);
                stock.setCurrentQuantity(quantity);
                stock.setLastUpdateTime(lastUpdateTime);
                stock.setExpiryDate(inventoryInDetail.getExpiryDate());
                stockRepository.save(stock);
                rowsAffected++;
            } else {
                // 增量入库
                try {
                    stockRepository.increaseQuantity(orgId, materialId, quantity, lastUpdateTime);
                    rowsAffected++;
                } catch (Exception e) {
                    log.error("增加库存信息失败", e);
                    return false;
                }
            }
        }

        // 更新入库单状态
        if (rowsAffected > 0) {
            for (InventoryInDetail inventoryInDetail : inventoryInDetailList) {
                InventoryInbound inventoryInbound = inventoryInDetail.getInventoryIn();
                inventoryInbound.setStatus((byte) 2);
                inventoryInboundRepository.save(inventoryInbound);
            }
        }

        return rowsAffected > 0;
    }

    public boolean decreaseStockQuantityByBatchNo(String batchNoKeyword) {
        log.info("batchNo for出库:{}", batchNoKeyword);
        int rowsAffected = 0;
        List<InventoryOutDetail> inventoryOutDetailList = inventoryDetailRepositoryOut.findListByBatchNo(batchNoKeyword);
        for (InventoryOutDetail inventoryOutDetail : inventoryOutDetailList) {
            InventoryOutbound inventoryOutbound = inventoryOutDetail.getInventoryOut();
            Organization orgId = inventoryOutbound.getOrg();
            MaterialBase materialId = inventoryOutDetail.getMaterial();
            BigDecimal quantity = inventoryOutDetail.getQuantity();
            Date lastUpdateTime = new Date();

            // 查找库存记录
            Stock stock = stockRepository.findByOrganizationAndMaterial(orgId, materialId).get(0);
            if (stock == null) {
                log.error("库存记录不存在，无法出库，批次号: {}", inventoryOutDetail.getBatchNo());
                return false;
            }

            // 检查库存是否足够
            if (stock.getCurrentQuantity().compareTo(quantity) < 0) {
                log.error("库存不足，无法出库，批次号: {}, 当前库存: {}, 出库数量: {}", inventoryOutDetail.getBatchNo(), stock.getCurrentQuantity(), quantity);
                throw new RuntimeException("库存不足");
            }

            try {
                // 执行出库操作
                stockRepository.decreaseQuantity(orgId, materialId, quantity, lastUpdateTime);
                rowsAffected++;
            } catch (Exception e) {
                log.error("减少库存信息失败", e);
                return false;
            }
        }

        if (rowsAffected > 0) {
            for (InventoryOutDetail inventoryOutDetail : inventoryOutDetailList) {
                InventoryOutbound inventoryOutbound = inventoryOutDetail.getInventoryOut();
                inventoryOutbound.setStatus((byte) 2);
                inventoryOutboundRepository.save(inventoryOutbound);
            }
        }
        return rowsAffected > 0;
    }

    public List<Stock> findStocksByOrgIdAndMaterialName(Organization orgId, MaterialBase materialName) {
        return stockRepository.findByOrgIdAndMaterialName(orgId, materialName);
    }

    public List<Stock> findStocksByOrgTypeAndExpiryDate(int orgType, Date startDate, Date endDate) {
        return stockRepository.findByOrgTypeAndExpiryDate(orgType, startDate, endDate);
    }

    public BigDecimal getTotalQuantityByOrgIdAndMaterialId(Organization orgId, MaterialBase materialId) {
        return stockRepository.getTotalQuantityByOrgIdAndMaterialId(orgId, materialId);
    }

    public BigDecimal getTotalInventoryQuantity() {
        return stockRepository.getTotalInventoryQuantity();
    }

    public ResponseMessage<Page<Stock>> getStocks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stocks = stockRepository.findAll(pageable);
        return ResponseMessage.success("获取库存列表成功", stocks);
    }


    public ResponseMessage<Page<Stock>> getRecentInbound(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseMessage.success("获取最近入库的10条记录成功", stockRepository.getRecentInbound(pageable));
    }
}
