package com.gongdi.materialpoints.modules.reward.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.reward.domain.RewardItem;
import com.gongdi.materialpoints.modules.reward.repository.RewardItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reward-items")
public class RewardController {

    private final RewardItemRepository rewardItemRepository;

    public RewardController(RewardItemRepository rewardItemRepository) {
        this.rewardItemRepository = rewardItemRepository;
    }

    @GetMapping
    public ApiResponse<List<RewardItem>> list() {
        return ApiResponse.success(rewardItemRepository.findAllByStatusOrderByIdAsc("ENABLED"));
    }
}
