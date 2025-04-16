package com.ruralmedical.backend.service;

import com.ruralmedical.backend.pojo.entity.Material;
import com.ruralmedical.backend.pojo.entity.TransferRecord;
import com.ruralmedical.backend.repository.TransferRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DecisionTreeService {

    @Autowired
    private TransferRecordRepository transferRecordRepository;

    public int predictTransferQuantity(Material material) {
        // 获取该物资最近30天的调拨记录
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<TransferRecord> recentTransfers = transferRecordRepository.findAll().stream()
                .filter(tr -> tr.getMaterial().getId().equals(material.getId())
                        && tr.getTransferDate().isAfter(thirtyDaysAgo))
                .toList();

        int transferCount = recentTransfers.size();
        int quantity = material.getQuantity();
        int threshold = material.getThreshold();

        // 决策树逻辑
        if (quantity < threshold) {
            if (transferCount > 2) { // 频繁调拨
                return (int) ((threshold - quantity) * 1.5);
            } else { // 正常调拨
                return threshold - quantity;
            }
        }
        return 0; // 无需调拨
    }
}
