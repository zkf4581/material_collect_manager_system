package com.gongdi.materialpoints.modules.material.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.auth.service.RoleGuard;
import com.gongdi.materialpoints.modules.material.domain.MaterialItem;
import com.gongdi.materialpoints.modules.material.domain.PointRule;
import com.gongdi.materialpoints.modules.material.service.MaterialManageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MaterialController {

    private final MaterialManageService materialManageService;
    private final RoleGuard roleGuard;

    public MaterialController(MaterialManageService materialManageService, RoleGuard roleGuard) {
        this.materialManageService = materialManageService;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/material-items")
    public ApiResponse<List<MaterialItem>> materialItems() {
        return ApiResponse.success(materialManageService.listMaterialItems());
    }

    @PostMapping("/material-items")
    public ApiResponse<MaterialItem> createMaterialItem(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody SaveMaterialItemRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN");
        return ApiResponse.success(materialManageService.createMaterialItem(new MaterialManageService.SaveMaterialItemCommand(
                request.name(),
                request.unitCode(),
                request.status()
        )));
    }

    @PutMapping("/material-items/{id}")
    public ApiResponse<MaterialItem> updateMaterialItem(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id,
            @Valid @RequestBody SaveMaterialItemRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN");
        return ApiResponse.success(materialManageService.updateMaterialItem(id, new MaterialManageService.SaveMaterialItemCommand(
                request.name(),
                request.unitCode(),
                request.status()
        )));
    }

    @GetMapping("/point-rules")
    public ApiResponse<List<PointRule>> pointRules() {
        return ApiResponse.success(materialManageService.listPointRules());
    }

    @PostMapping("/point-rules")
    public ApiResponse<PointRule> createPointRule(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody SavePointRuleRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN");
        return ApiResponse.success(materialManageService.createPointRule(new MaterialManageService.SavePointRuleCommand(
                request.materialItemId(),
                request.unitCode(),
                request.basePoint(),
                request.conditionCode(),
                request.conditionFactor(),
                request.status()
        )));
    }

    @PutMapping("/point-rules/{id}")
    public ApiResponse<PointRule> updatePointRule(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id,
            @Valid @RequestBody SavePointRuleRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN");
        return ApiResponse.success(materialManageService.updatePointRule(id, new MaterialManageService.SavePointRuleCommand(
                request.materialItemId(),
                request.unitCode(),
                request.basePoint(),
                request.conditionCode(),
                request.conditionFactor(),
                request.status()
        )));
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

    public record SaveMaterialItemRequest(
            @NotBlank(message = "材料名称不能为空")
            String name,
            @NotBlank(message = "单位不能为空")
            String unitCode,
            @NotBlank(message = "状态不能为空")
            String status
    ) {
    }

    public record SavePointRuleRequest(
            @NotNull(message = "材料不能为空")
            Long materialItemId,
            @NotBlank(message = "单位不能为空")
            String unitCode,
            @NotNull(message = "基础分值不能为空")
            @DecimalMin(value = "0.01", message = "基础分值必须大于 0")
            BigDecimal basePoint,
            @NotBlank(message = "完好度不能为空")
            String conditionCode,
            @NotNull(message = "完好度系数不能为空")
            @DecimalMin(value = "0.01", message = "完好度系数必须大于 0")
            BigDecimal conditionFactor,
            @NotBlank(message = "状态不能为空")
            String status
    ) {
    }
}
