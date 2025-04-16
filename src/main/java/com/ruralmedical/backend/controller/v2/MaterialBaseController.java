package com.ruralmedical.backend.controller.v2;

import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.dto.MaterialBaseDTO;
import com.ruralmedical.backend.repository.MaterialBaseRepository;
import com.ruralmedical.backend.service.v2.MaterialBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/materials/base")
public class MaterialBaseController {

    @Autowired
    MaterialBaseRepository materialBaseRepository;
    @Autowired
    private MaterialBaseService materialBaseService;

    @GetMapping()
    public ResponseMessage<Page<MaterialBaseDTO>> getMaterialBases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) String userNameKeyword,
            @RequestParam(required = false) String categoryKeyword,
            @RequestParam(required = false) String storageConditionKeyword,
            @RequestParam(required = false) String unitKeyword,
            @RequestParam(required = false) String specKeyword) {
        return materialBaseService.getMaterials(page, size, nameKeyword, userNameKeyword, categoryKeyword, storageConditionKeyword,unitKeyword,specKeyword);
    }

    @PostMapping
    public ResponseMessage<MaterialBaseDTO> addMaterialBase(@RequestBody MaterialBase materialBase) {
        return materialBaseService.addMaterialBase(materialBase);
    }

    @PutMapping("/{id}")
    public ResponseMessage<MaterialBaseDTO> updateMaterialBase(@PathVariable Long id, @RequestBody MaterialBase materialBase) {
        return materialBaseService.updateMaterialBase(id, materialBase);
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<String> deleteMaterialBase(@PathVariable Long id) {
        return materialBaseService.deleteMaterialBase(id);
    }

    @DeleteMapping("/batch")
    public ResponseMessage<String> deleteMaterialBases(@RequestBody Long[] ids) {
        return materialBaseService.deleteMaterialBases(ids);
    }
}
