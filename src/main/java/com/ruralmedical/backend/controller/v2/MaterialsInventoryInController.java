package com.ruralmedical.backend.controller.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInDetail;
import com.ruralmedical.backend.pojo.entity.inventory.InventoryInbound;
import com.ruralmedical.backend.service.v2.MaterialsInventoryInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/materials/inventory/in")
public class MaterialsInventoryInController {

    private static final Logger log = LoggerFactory.getLogger(MaterialsInventoryInController.class);
    @Autowired
    MaterialsInventoryInService materialsInventoryInService;

    // 新增入库单
    @PostMapping
    public ResponseMessage<InventoryInbound> addInventoryInbound(@RequestBody InventoryInbound inventoryInbound) {
        return materialsInventoryInService.addInventoryInbound(inventoryInbound);
    }

    @PostMapping("/detail")
    public ResponseMessage<InventoryInDetail> addInventoryInboundDetail(@RequestBody InventoryInDetail inventoryInbound) {
        log.info("inventoryInbound：{}", inventoryInbound);
        return materialsInventoryInService.addInventoryInboundDetail(inventoryInbound);
    }

    @PutMapping("/detail/{inId}")
    public ResponseMessage<InventoryInDetail> updateInventoryInboundDetail(@PathVariable Long inId, @RequestBody InventoryInDetail inventoryInbound) {
        return materialsInventoryInService.updateInventoryInboundDetail(inId,inventoryInbound);
    }

    @GetMapping("/{inId}")
    public ResponseMessage<InventoryInbound> getInventoryInbound(@PathVariable Long inId) {
        return materialsInventoryInService.getInventoryInbound(inId);
    }

    @GetMapping
    public ResponseMessage<Page<InventoryInbound>> getInventoryInbounds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orgaddressKeyword,
            @RequestParam(required = false) String statusKeyword,
            @RequestParam(required = false) String idKeyword,
            @RequestParam(required = false) String orgNameKeyword) {
        return materialsInventoryInService.getInventoryInbounds(page,size,orgaddressKeyword,statusKeyword,idKeyword,orgNameKeyword);
    }

    @GetMapping("/dialog")
    public ResponseMessage<Page<InventoryInbound>> getInventoryInboundsDialog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orgaddressKeyword,
            @RequestParam(required = false) String idKeyword,
            @RequestParam(required = false) String orgNameKeyword) {
        return materialsInventoryInService.getInventoryInboundsDialog(page,size,orgaddressKeyword,idKeyword,orgNameKeyword);
    }

    @PutMapping("/{inId}")
    public ResponseMessage<InventoryInbound> updateInventoryInbound(@PathVariable Long inId, @RequestBody InventoryInbound inventoryInbound) {
        return materialsInventoryInService.updateInventoryInbound(inId,inventoryInbound);
    }

    //获取入库详情单
    @GetMapping("/detail/{inId}")
    public ResponseMessage<InventoryInDetail> getInventoryInDetail(@PathVariable Long inId) {
        return materialsInventoryInService.getInventoryInDetail(inId);
    }

    @GetMapping("/detail")
    public ResponseMessage<Page<InventoryInDetail>> getInventoryInDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return materialsInventoryInService.getInventoryInDetails(page,size);
    }

    @GetMapping("/detail/dialog")
    public ResponseMessage<Page<InventoryInDetail>> getInventoryInDetailsDialog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return materialsInventoryInService.getInventoryInDetailsDialog(page,size);
    }

    @DeleteMapping("/{inId}")
    public ResponseMessage<String> deleteInventoryInbound(@PathVariable Long inId) {
        return materialsInventoryInService.deleteInventoryInbound(inId);
    }

    @DeleteMapping("/batch")
    public ResponseMessage<String> deleteInventoryInbounds(@RequestBody Long[] ids) {
        return materialsInventoryInService.deleteInventoryInbounds(ids);
    }

    @DeleteMapping("/detail/{inId}")
    public ResponseMessage<String> deleteInventoryInboundDetail(@PathVariable Long inId) {
        return materialsInventoryInService.deleteInventoryInboundDetail(inId);
    }

    @DeleteMapping("/detail/batch")
    public ResponseMessage<String> deleteInventoryInboundDetails(@RequestBody Long[] ids) {
        return materialsInventoryInService.deleteInventoryInboundDetails(ids);
    }

}
