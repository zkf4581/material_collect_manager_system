package com.gongdi.materialpoints.modules.material.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.material.domain.MaterialItem;
import com.gongdi.materialpoints.modules.material.domain.PointRule;
import com.gongdi.materialpoints.modules.material.repository.MaterialItemRepository;
import com.gongdi.materialpoints.modules.material.repository.PointRuleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MaterialController {

    private final MaterialItemRepository materialItemRepository;
    private final PointRuleRepository pointRuleRepository;

    public MaterialController(MaterialItemRepository materialItemRepository, PointRuleRepository pointRuleRepository) {
        this.materialItemRepository = materialItemRepository;
        this.pointRuleRepository = pointRuleRepository;
    }

    @GetMapping("/material-items")
    public ApiResponse<List<MaterialItem>> materialItems() {
        return ApiResponse.success(materialItemRepository.findAllByStatusOrderByIdAsc("ENABLED"));
    }

    @GetMapping("/point-rules")
    public ApiResponse<List<PointRule>> pointRules() {
        return ApiResponse.success(pointRuleRepository.findAllByStatusOrderByIdAsc("ENABLED"));
    }

    @GetMapping("/unit-dict")
    public ApiResponse<List<Map<String, String>>> unitDict() {
        return ApiResponse.success(List.of(
                Map.of("code", "KG", "name", "kg"),
                Map.of("code", "GEN", "name", "根"),
                Map.of("code", "ZHANG", "name", "张"),
                Map.of("code", "MI", "name", "米"),
                Map.of("code", "DAI", "name", "袋")
        ));
    }

    @GetMapping("/condition-factors")
    public ApiResponse<List<Map<String, String>>> conditionFactors() {
        return ApiResponse.success(List.of(
                Map.of("code", "OK", "name", "完好", "factor", "1.0"),
                Map.of("code", "MINOR", "name", "轻微损耗", "factor", "0.8"),
                Map.of("code", "RECYCLE", "name", "仅回收", "factor", "0.5")
        ));
    }
}
