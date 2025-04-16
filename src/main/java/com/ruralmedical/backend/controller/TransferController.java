package com.ruralmedical.backend.controller;

import com.ruralmedical.backend.pojo.entity.Material;
import com.ruralmedical.backend.pojo.entity.TransferRecord;
import com.ruralmedical.backend.repository.MaterialRepository;
import com.ruralmedical.backend.repository.TransferRecordRepository;
import com.ruralmedical.backend.service.DecisionTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private TransferRecordRepository transferRecordRepository;

    @Autowired
    private DecisionTreeService decisionTreeService;

    @GetMapping("/suggestions")
    public List<TransferRecord> getTransferSuggestions() {
        List<Material> materials = materialRepository.findAll();
        return materials.stream().map(m -> {
            TransferRecord tr = new TransferRecord();
            tr.setMaterial(m);
            int suggestedQuantity = decisionTreeService.predictTransferQuantity(m);
            tr.setSuggestedQuantity(suggestedQuantity);
            return tr;
        }).filter(tr -> tr.getSuggestedQuantity() > 0).toList();
    }

    @PostMapping
    public ResponseEntity<String> addTransfer(@RequestBody TransferRecord transfer) {
        Material material = materialRepository.findById(transfer.getMaterial().getId()).orElse(null);
        if (material == null) {
            return ResponseEntity.badRequest().body("物资不存在");
        }
        material.setQuantity(material.getQuantity() + transfer.getQuantity());
        material.setEntryDate(LocalDateTime.now());
        materialRepository.save(material);

        transfer.setMaterial(material);
        transfer.setTransferDate(LocalDateTime.now());
        transferRecordRepository.save(transfer);
        return ResponseEntity.ok("调拨成功");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransfer(@PathVariable Long id) {
        transferRecordRepository.deleteById(id);
        return ResponseEntity.ok("删除成功");
    }
}
