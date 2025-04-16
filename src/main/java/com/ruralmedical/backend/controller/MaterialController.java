package com.ruralmedical.backend.controller;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.Material;
import com.ruralmedical.backend.pojo.entity.dto.MaterialDTO;
import com.ruralmedical.backend.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/page")
    public ResponseMessage<Page<MaterialDTO>> getMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String userIdKeyword)
    {
        return materialService.getMaterials(page, size, nameKeyword, userIdKeyword);
    }


    @GetMapping("/user/{userId}")
    public Page<Material> getMaterialsByUser(@PathVariable Long userId) {
        return materialService.getMaterialsByUser(userId);
    }


    @GetMapping("/recent")
    public Page<Material> getRecentMaterials(@RequestParam(defaultValue = "7") int days) {
        return materialService.getRecentMaterials(days);
    }

    @GetMapping("/all")
    public ResponseMessage<Integer> getAllMaterialsCount() {
        return materialService.getAllMaterialsCount();
    }

    @PostMapping
    public ResponseMessage<String> addMaterial(@RequestBody Material material) {
        return materialService.addMaterial(material);
    }

    @PutMapping("/{id}")
    public ResponseMessage<String> updateMaterial(@PathVariable Long id, @RequestBody Material material) {
        return materialService.updateMaterial(id, material);
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<String> deleteMaterial(@PathVariable Long id) {
        return materialService.deleteMaterial(id);
    }

    @DeleteMapping("/batch")
    public ResponseMessage<String> deleteMaterialsBatch(@RequestBody List<Long> ids) {
        return materialService.deleteMaterialsBatch(ids);
    }
}
