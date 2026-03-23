package com.gongdi.materialpoints.modules.material.service;

import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.material.domain.MaterialItem;
import com.gongdi.materialpoints.modules.material.domain.PointRule;
import com.gongdi.materialpoints.modules.material.repository.MaterialItemRepository;
import com.gongdi.materialpoints.modules.material.repository.PointRuleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MaterialManageService {

    private final MaterialItemRepository materialItemRepository;
    private final PointRuleRepository pointRuleRepository;

    public MaterialManageService(
            MaterialItemRepository materialItemRepository,
            PointRuleRepository pointRuleRepository
    ) {
        this.materialItemRepository = materialItemRepository;
        this.pointRuleRepository = pointRuleRepository;
    }

    public List<MaterialItem> listMaterialItems() {
        return materialItemRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<PointRule> listPointRules() {
        return pointRuleRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional
    public MaterialItem createMaterialItem(SaveMaterialItemCommand command) {
        MaterialItem materialItem = new MaterialItem();
        materialItem.setName(command.name());
        materialItem.setUnitCode(command.unitCode());
        materialItem.setStatus(command.status());
        return materialItemRepository.save(materialItem);
    }

    @Transactional
    public MaterialItem updateMaterialItem(Long id, SaveMaterialItemCommand command) {
        MaterialItem materialItem = materialItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "材料不存在"));
        materialItem.setName(command.name());
        materialItem.setUnitCode(command.unitCode());
        materialItem.setStatus(command.status());
        return materialItemRepository.save(materialItem);
    }

    @Transactional
    public PointRule createPointRule(SavePointRuleCommand command) {
        validateMaterialExists(command.materialItemId());
        PointRule pointRule = new PointRule();
        pointRule.setMaterialItemId(command.materialItemId());
        pointRule.setUnitCode(command.unitCode());
        pointRule.setBasePoint(command.basePoint());
        pointRule.setConditionCode(command.conditionCode());
        pointRule.setConditionFactor(command.conditionFactor());
        pointRule.setStatus(command.status());
        return pointRuleRepository.save(pointRule);
    }

    @Transactional
    public PointRule updatePointRule(Long id, SavePointRuleCommand command) {
        validateMaterialExists(command.materialItemId());
        PointRule pointRule = pointRuleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "积分规则不存在"));
        pointRule.setMaterialItemId(command.materialItemId());
        pointRule.setUnitCode(command.unitCode());
        pointRule.setBasePoint(command.basePoint());
        pointRule.setConditionCode(command.conditionCode());
        pointRule.setConditionFactor(command.conditionFactor());
        pointRule.setStatus(command.status());
        return pointRuleRepository.save(pointRule);
    }

    private void validateMaterialExists(Long materialItemId) {
        if (!materialItemRepository.existsById(materialItemId)) {
            throw new BusinessException(40401, "材料不存在");
        }
    }

    public record SaveMaterialItemCommand(String name, String unitCode, String status) {
    }

    public record SavePointRuleCommand(
            Long materialItemId,
            String unitCode,
            BigDecimal basePoint,
            String conditionCode,
            BigDecimal conditionFactor,
            String status
    ) {
    }
}
