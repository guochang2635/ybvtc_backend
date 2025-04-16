package com.ruralmedical.backend.service.v2;

import com.ruralmedical.backend.exception.GlobalExceptionHandlerAdvice;
import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.MaterialBase;
import com.ruralmedical.backend.pojo.entity.User;
import com.ruralmedical.backend.pojo.entity.dto.MaterialBaseDTO;
import com.ruralmedical.backend.repository.MaterialBaseRepository;
import com.ruralmedical.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MaterialBaseService {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    @Autowired
    private MaterialBaseRepository materialBaseRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseMessage<Page<MaterialBaseDTO>> getMaterials(
            int page,
            int size,
            String nameKeyword,
            String userNameKeyword,
            String categoryKeyword,
            String storageCondition,
            String unitKeyword,
            String specKeyword){
        Pageable pageable = PageRequest.of(page, size);
        Page<MaterialBase> materials;
        //TODO 修复搜索
        if (Objects.nonNull(nameKeyword) && !nameKeyword.isEmpty()) {
            materials = materialBaseRepository.findByName(nameKeyword, pageable);
        } else if (Objects.nonNull(userNameKeyword) && !userNameKeyword.isEmpty()) {
            materials = materialBaseRepository.findByUserName(userNameKeyword, pageable);
        } else if (Objects.nonNull(categoryKeyword) && !categoryKeyword.isEmpty()) {
            materials = materialBaseRepository.findByCategory(categoryKeyword, pageable);
        } else if (Objects.nonNull(storageCondition) && !storageCondition.isEmpty()){
            materials = materialBaseRepository.findByStorageCondition(storageCondition, pageable);
        } else if (Objects.nonNull(unitKeyword) && !unitKeyword.isEmpty()){
            materials = materialBaseRepository.findByUnit(unitKeyword, pageable);
        } else if (Objects.nonNull(specKeyword) && !specKeyword.isEmpty()){
            materials = materialBaseRepository.findBySpecification(specKeyword, pageable);
        } else {
            materials = materialBaseRepository.findAllNotDeleted(pageable);
        }

        Page<MaterialBaseDTO> dtoPage = materials.map(m -> {
            MaterialBaseDTO dto = new MaterialBaseDTO();
            BeanUtils.copyProperties(m, dto);
            if (m.getUser() != null) {
                dto.setUserName(m.getUser().getUsername());
            } else {
                dto.setUserName(null);
            }
            return dto;
        });
        return ResponseMessage.success(dtoPage);
    }

    public ResponseMessage<MaterialBaseDTO> addMaterialBase(MaterialBase material) {
        //TODO: 添加唯一名称校验
        User user = userRepository.findByUsername(material.getUser().getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        MaterialBaseDTO dto = new MaterialBaseDTO();
        material.setUser(user);
        BeanUtils.copyProperties(material, dto);
        materialBaseRepository.save(material);
        return ResponseMessage.success("添加成功",dto);
    }

    public ResponseMessage<MaterialBaseDTO> updateMaterialBase(Long id, MaterialBase material) {
        MaterialBaseDTO dto = new MaterialBaseDTO();
        MaterialBase materialBase = materialBaseRepository.findById(id).orElse(null);
        if (materialBase == null) {
            throw new RuntimeException("材料不存在");
        }
        materialBase.setMaterialName(material.getMaterialName());
        materialBase.setCategory(material.getCategory());
        materialBase.setStorageCondition(material.getStorageCondition());
        materialBase.setSpecification(material.getSpecification());
        materialBase.setUnit(material.getUnit());
        materialBaseRepository.save(materialBase);
        BeanUtils.copyProperties(materialBase, dto);
        return ResponseMessage.success("修改成功",dto);
    }

    public ResponseMessage<String> deleteMaterialBase(Long id) {
        MaterialBase materialBase = materialBaseRepository.findById(id).orElse(null);
        if (materialBase == null) {
            throw new RuntimeException("材料不存在");
        }
        materialBase.setDeleted(true);
        materialBaseRepository.save(materialBase);
        return ResponseMessage.success("删除成功");
    }


    public ResponseMessage<String> deleteMaterialBases(Long[] ids) {
        for (Long id : ids) {
            MaterialBase materialBase = materialBaseRepository.findById(id).orElse(null);
            if (materialBase == null) {
                throw new RuntimeException("材料不存在");
            }
            materialBase.setDeleted(true);
            materialBaseRepository.save(materialBase);
        }
        return ResponseMessage.success("删除成功",null);
    }
}
