package com.gongdi.materialpoints.modules.reward.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.auth.service.RoleGuard;
import com.gongdi.materialpoints.modules.reward.domain.RewardItem;
import com.gongdi.materialpoints.modules.reward.service.RewardItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reward-items")
public class RewardController {

    private final RewardItemService rewardItemService;
    private final RoleGuard roleGuard;

    public RewardController(RewardItemService rewardItemService, RoleGuard roleGuard) {
        this.rewardItemService = rewardItemService;
        this.roleGuard = roleGuard;
    }

    @GetMapping
    public ApiResponse<List<RewardItem>> list() {
        return ApiResponse.success(rewardItemService.listAll());
    }

    @PostMapping
    public ApiResponse<RewardItem> create(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody SaveRewardItemRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN");
        return ApiResponse.success(rewardItemService.create(new RewardItemService.CreateRewardItemCommand(
                request.name(),
                request.pointsCost(),
                request.stock(),
                request.status()
        )));
    }

    @PutMapping("/{id}")
    public ApiResponse<RewardItem> update(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id,
            @Valid @RequestBody SaveRewardItemRequest request
    ) {
        roleGuard.requireAnyRole(httpServletRequest, "ADMIN");
        return ApiResponse.success(rewardItemService.update(id, new RewardItemService.UpdateRewardItemCommand(
                request.name(),
                request.pointsCost(),
                request.stock(),
                request.status()
        )));
    }

    public record SaveRewardItemRequest(
            @NotBlank(message = "商品名称不能为空")
            String name,
            @NotNull(message = "所需积分不能为空")
            @Min(value = 1, message = "所需积分至少为 1")
            Integer pointsCost,
            @NotNull(message = "库存不能为空")
            @Min(value = 0, message = "库存不能小于 0")
            Integer stock,
            @NotBlank(message = "状态不能为空")
            String status
    ) {
    }
}
