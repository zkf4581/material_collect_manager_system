package com.gongdi.materialpoints.modules.recycle.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.auth.service.CurrentUser;
import com.gongdi.materialpoints.modules.auth.service.RoleGuard;
import com.gongdi.materialpoints.modules.recycle.domain.RecycleRecord;
import com.gongdi.materialpoints.modules.recycle.service.RecycleRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/recycle-records")
public class RecycleRecordController {

    private final RecycleRecordService recycleRecordService;
    private final RoleGuard roleGuard;

    public RecycleRecordController(RecycleRecordService recycleRecordService, RoleGuard roleGuard) {
        this.recycleRecordService = recycleRecordService;
        this.roleGuard = roleGuard;
    }

    @PostMapping
    public ApiResponse<RecycleRecord> create(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody CreateRecycleRecordRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN", "KEEPER");
        RecycleRecord recycleRecord = recycleRecordService.create(new RecycleRecordService.CreateRecycleRecordCommand(
                request.projectId(),
                request.teamId(),
                request.workerId(),
                request.materialItemId(),
                request.quantity(),
                request.unitCode(),
                request.conditionCode(),
                request.remark(),
                request.photoIds()
        ));
        return ApiResponse.success(recycleRecord);
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<RecycleRecord> approve(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN", "KEEPER");
        return ApiResponse.success(recycleRecordService.approve(id));
    }

    @GetMapping
    public ApiResponse<List<RecycleRecord>> list(HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = roleGuard.requireCurrentUser(httpServletRequest);
        if ("WORKER".equals(currentUser.roleCode())) {
            return ApiResponse.success(recycleRecordService.listByWorker(currentUser.workerId()));
        }
        return ApiResponse.success(recycleRecordService.listAll());
    }

    public record CreateRecycleRecordRequest(
            @NotNull(message = "项目不能为空")
            Long projectId,
            @NotNull(message = "班组不能为空")
            Long teamId,
            @NotNull(message = "工人不能为空")
            Long workerId,
            @NotNull(message = "材料不能为空")
            Long materialItemId,
            @NotNull(message = "数量不能为空")
            @DecimalMin(value = "0.01", message = "数量必须大于 0")
            BigDecimal quantity,
            @NotBlank(message = "单位不能为空")
            String unitCode,
            @NotBlank(message = "完好度不能为空")
            String conditionCode,
            String remark,
            @NotEmpty(message = "至少上传一张照片")
            List<Long> photoIds
    ) {
    }
}
