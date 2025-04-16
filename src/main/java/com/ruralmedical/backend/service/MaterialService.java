package com.ruralmedical.backend.service;

import com.ruralmedical.backend.controller.MaterialController;
import com.ruralmedical.backend.pojo.ResponseMessage;
import com.ruralmedical.backend.pojo.entity.Material;
import com.ruralmedical.backend.pojo.entity.User;
import com.ruralmedical.backend.pojo.entity.dto.MaterialDTO;
import com.ruralmedical.backend.repository.MaterialRepository;
import com.ruralmedical.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MaterialService {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    /**
     * 获取材料列表
     *
     * @param page 页面编号，从0开始
     * @param size 每页的记录数
     * @param nameKeyword 材料名称的关键字，用于模糊查询
     * @param userIdKeyword 用户ID的关键字，用于模糊查询
     * @return 包含材料DTO的分页响应消息
     */
    public ResponseMessage<Page<MaterialDTO>> getMaterials(
            int page,
            int size,
            String nameKeyword,
            String userIdKeyword){
        Pageable pageable = PageRequest.of(page, size);
        Page<Material> materials;

        if (Objects.nonNull(nameKeyword) && !nameKeyword.isEmpty()) {
            materials = materialRepository.findByName(nameKeyword, pageable);
        } else if (Objects.nonNull(userIdKeyword) && !userIdKeyword.isEmpty()) {
            User user = userRepository.findByUsername(userIdKeyword);
            materials = materialRepository.findByUserId(user.getUserId(), pageable);
        } else {
            materials = materialRepository.findAll(pageable);
        }
        Page<MaterialDTO> dtoPage = materials.map(m -> {
            MaterialDTO dto = new MaterialDTO();
            BeanUtils.copyProperties(m, dto);
            if (m.getUser() != null) { // 添加空值检查
                dto.setUserName(m.getUser().getUsername());
            } else {
                dto.setUserName(null); // 或者设置一个默认值，例如 "Unknown"
            }
            return dto;
        });
        return ResponseMessage.success(dtoPage);
    }

    /**
     * 根据用户ID获取材料列表
     *
     * @param userId 用户ID，用于查询特定用户的材料
     * @return 返回该用户的材料列表
     */
    public Page<Material> getMaterialsByUser(Long userId) {
        return materialRepository.findByUserId(userId, PageRequest.of(0, 10));
    }

    /**
     * 获取最近一定天数内的材料信息
     *
     * @param days 获取最近材料的天数，默认为7天
     * @return 返回一个Material对象的Page，包含最近指定天数内的材料信息
     */
    public Page<Material> getRecentMaterials(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return materialRepository.findRecentMaterials(startDate, PageRequest.of(0, 10));
    }

    /**
     * 添加材料信息
     * <p>
     * 此方法主要用于将材料信息添加到系统中它首先根据材料对象中用户用户名查询用户信息，
     * 如果用户不存在，则返回失败消息如果用户存在，则更新材料对象的用户信息和入库日期，
     * 然后保存材料信息到数据库最后，返回成功消息
     *
     * @param material 要添加的材料对象，包含材料的相关信息和关联的用户信息
     * @return 返回一个ResponseMessage对象，包含操作结果和消息
     */
    public ResponseMessage<String> addMaterial(Material material) {
        User user = userRepository.findByUsername(material.getUser().getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        material.setUser(user);
        material.setEntryDate(LocalDateTime.now());
        materialRepository.save(material);
        return ResponseMessage.success("添加成功");
    }

    /**
     * 更新物资信息
     * <p>
     * 此方法首先尝试根据提供的ID查找数据库中的物资如果物资不存在，则抛出运行时异常
     * 如果找到物资，则将其信息（名称、数量、阈值和入库日期）更新为新提供的物资对象中的信息
     * 更新后的物资将被保存回数据库此方法返回一个表示操作成功地响应消息对象
     *
     * @param id 物资的唯一标识符，用于查找要更新的物资
     * @param material 包含要更新的新物资信息的对象
     * @return ResponseMessage<String> 一个表示操作结果的成功消息
     * @throws RuntimeException 如果未找到指定ID的物资，则抛出此异常
     */
    public ResponseMessage<String> updateMaterial(Long id, Material material) {
        Material existing = materialRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new RuntimeException("物资不存在");
        }
        existing.setName(material.getName());
        existing.setQuantity(material.getQuantity());
        existing.setThreshold(material.getThreshold());
        existing.setEntryDate(material.getEntryDate() != null ? material.getEntryDate() : LocalDateTime.now());
        materialRepository.save(existing);
        return ResponseMessage.success("更新成功");
    }

    /**
     * 删除指定的材料
     * 通过材料的ID进行数据库中的删除操作
     * 如果删除成功，返回HTTP状态码200和删除成功的消息
     *
     * @param id 材料的唯一标识符
     * @return 返回表示删除成功的HTTP响应实体
     */
    public ResponseMessage<String> deleteMaterial(Long id) {
        materialRepository.deleteById(id);
        return ResponseMessage.success("删除成功");
    }

    @Transactional
    public ResponseMessage<String> deleteMaterialsBatch(List<Long> ids) {
        log.info("批量删除物资，ID列表：{}", ids);
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }
        materialRepository.deleteAllById(ids);
        return ResponseMessage.success("批量删除成功");
    }

    public ResponseMessage<Integer> getAllMaterialsCount() {
        int totalCount = materialRepository.findAll().size();
         return ResponseMessage.success(totalCount);
    }
}
