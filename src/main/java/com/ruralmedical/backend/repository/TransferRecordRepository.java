package com.ruralmedical.backend.repository;

import com.ruralmedical.backend.pojo.entity.TransferRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRecordRepository extends JpaRepository<TransferRecord, Long> {
}
