package com.ruralmedical.backend.controller.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.Organization;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import com.ruralmedical.backend.pojo.entity.inventory.Stock;
import com.ruralmedical.backend.service.v2.StockService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v2/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{orgId}/{materialId}/{batchNo}")
    public Stock getStock(@PathVariable Organization orgId, @PathVariable MaterialBase materialId, @PathVariable InventoryInDetail batchNo) {
        return stockService.findStock(orgId, materialId, batchNo);
    }

    @PostMapping("/increase")
    public boolean increaseStock(@RequestParam String batchNoKeyword) {
        return stockService.increaseStockQuantity(batchNoKeyword);
    }

    @PostMapping("/decrease")
    public boolean decreaseStock(@RequestParam String batchNoKeyword) {
        return stockService.decreaseStockQuantityByBatchNo(batchNoKeyword);
    }

    @GetMapping
    public ResponseMessage<Page<Stock>> getStocks(@RequestParam int page, @RequestParam int size) {
        return stockService.getStocks(page, size);
    }

    @GetMapping("/org/{orgId}/material/{materialName}")
    public List<Stock> getStocksByOrgIdAndMaterialName(@PathVariable Organization orgId, @PathVariable MaterialBase materialName) {
        return stockService.findStocksByOrgIdAndMaterialName(orgId, materialName);
    }

    @GetMapping("/orgType/{orgType}/expiry/{startDate}/{endDate}")
    public List<Stock> getStocksByOrgTypeAndExpiryDate(@PathVariable int orgType, @PathVariable Date startDate, @PathVariable Date endDate) {
        return stockService.findStocksByOrgTypeAndExpiryDate(orgType, startDate, endDate);
    }

    @GetMapping("/totalQuantity/{orgId}/{materialId}")
    public BigDecimal getTotalQuantity(@PathVariable Organization orgId, @PathVariable MaterialBase materialId) {
        return stockService.getTotalQuantityByOrgIdAndMaterialId(orgId, materialId);
    }

    @GetMapping("/totalInventoryQuantity")
    public BigDecimal getTotalInventoryQuantity() {
        return stockService.getTotalInventoryQuantity();
    }

    // 查询最近入库的10条记录
    @GetMapping("/recentInbound")
    public ResponseMessage<Page<Stock>> getRecentInbound(@RequestParam int page, @RequestParam int size) {
        return stockService.getRecentInbound(page, size);
    }
}
