package com.ruralmedical.backend.controller.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryOutbound;
import com.ruralmedical.backend.service.v2.MaterialsInventoryOutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/materials/inventory/out")
public class MaterialsInventoryOutController {

    private static final Logger log = LoggerFactory.getLogger(MaterialsInventoryOutController.class);
    @Autowired
    MaterialsInventoryOutService materialsOutventoryService;

    // 新增入库单
    @PostMapping
    public ResponseMessage<InventoryOutbound> addInventory(@RequestBody InventoryOutbound inventory) {
        return materialsOutventoryService.addInventory(inventory);
    }

    @PostMapping("/detail")
    public ResponseMessage<InventoryOutDetail> addInventoryDetail(@RequestBody InventoryOutDetail inventory) {
        log.info("inventoryInbound：{}", inventory);
        return materialsOutventoryService.addInventoryDetail(inventory);
    }

    @PutMapping("/detail/{inId}")
    public ResponseMessage<InventoryOutDetail> updateInventoryDetail(@PathVariable Long inId, @RequestBody InventoryOutDetail inventory) {
        return materialsOutventoryService.updateInventoryDetail(inId,inventory);
    }

    @GetMapping("/{inId}")
    public ResponseMessage<InventoryOutbound> getInventory(@PathVariable Long inId) {
        return materialsOutventoryService.getInventory(inId);
    }

    @GetMapping
    public ResponseMessage<Page<InventoryOutbound>> getInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return materialsOutventoryService.getInventory(page,size);
    }

    @GetMapping("/dialog")
    public ResponseMessage<Page<InventoryOutbound>> getInventoryDialog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return materialsOutventoryService.getInventoryDialog(page,size);
    }

    @PutMapping("/{inId}")
    public ResponseMessage<InventoryOutbound> updateInventory(@PathVariable Long inId, @RequestBody InventoryOutbound inventory) {
        return materialsOutventoryService.updateInventory(inId,inventory);
    }

    //获取入库详情单
    @GetMapping("/detail/{inId}")
    public ResponseMessage<InventoryOutDetail> getInventoryDetail(@PathVariable Long inId) {
        return materialsOutventoryService.getInventoryDetail(inId);
    }

    @GetMapping("/detail")
    public ResponseMessage<Page<InventoryOutDetail>> getInventoryDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return materialsOutventoryService.getInventoryDetails(page,size);
    }

    @GetMapping("/detail/dialog")
    public ResponseMessage<Page<InventoryOutDetail>> getInventoryDetailsDialog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return materialsOutventoryService.getInventoryDetailsDialog(page,size);
    }

    @DeleteMapping("/{inId}")
    public ResponseMessage<String> deleteInventory(@PathVariable Long inId) {
        return materialsOutventoryService.deleteInventory(inId);
    }

    @DeleteMapping("/batch")
    public ResponseMessage<String> deleteInventorys(@RequestBody Long[] ids) {
        return materialsOutventoryService.deleteInventorys(ids);
    }

    @DeleteMapping("/detail/{inId}")
    public ResponseMessage<String> deleteInventoryInboundDetail(@PathVariable Long inId) {
        return materialsOutventoryService.deleteInventoryOutboundDetail(inId);
    }

    @DeleteMapping("/detail/batch")
    public ResponseMessage<String> deleteInventoryInboundDetails(@RequestBody Long[] ids) {
        return materialsOutventoryService.deleteInventoryOutboundDetail(ids);
    }

}
