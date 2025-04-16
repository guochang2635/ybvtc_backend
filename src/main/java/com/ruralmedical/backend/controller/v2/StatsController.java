package com.ruralmedical.backend.controller.v2;

import com.ruralmedical.backend.repository.InventoryInboundRepository;
import com.ruralmedical.backend.repository.InventoryOutboundRepository;
import com.ruralmedical.backend.repository.OrganizationRepository;
import com.ruralmedical.backend.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private InventoryInboundRepository inventoryInboundRepository;

    @Autowired
    private InventoryOutboundRepository inventoryOutboundRepository;

    @GetMapping("/materials")
    public BigDecimal getMaterialCount() {
        return stockRepository.getTotalInventoryQuantity();
    }

    @GetMapping("/organizations")
    public int getOrganizationCount() {
        return organizationRepository.getOrgCount();
    }

    @GetMapping("/inbounds")
    public int getInboundCount() {
        return inventoryInboundRepository.getInboundCount();
    }
    @GetMapping("/Outbounds")
    public int getOutboundCount() {
        return inventoryOutboundRepository.getOutboundCount();
    }
}
