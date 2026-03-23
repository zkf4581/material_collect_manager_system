package com.gongdi.materialpoints.modules.reward.service;

import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.reward.domain.RewardItem;
import com.gongdi.materialpoints.modules.reward.repository.RewardItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RewardItemService {

    private final RewardItemRepository rewardItemRepository;

    public RewardItemService(RewardItemRepository rewardItemRepository) {
        this.rewardItemRepository = rewardItemRepository;
    }

    public List<RewardItem> listAll() {
        return rewardItemRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional
    public RewardItem create(CreateRewardItemCommand command) {
        RewardItem rewardItem = new RewardItem();
        rewardItem.setName(command.name());
        rewardItem.setPointsCost(command.pointsCost());
        rewardItem.setStock(command.stock());
        rewardItem.setStatus(command.status());
        return rewardItemRepository.save(rewardItem);
    }

    @Transactional
    public RewardItem update(Long rewardItemId, UpdateRewardItemCommand command) {
        RewardItem rewardItem = rewardItemRepository.findById(rewardItemId)
                .orElseThrow(() -> new BusinessException(40401, "商品不存在"));
        rewardItem.setName(command.name());
        rewardItem.setPointsCost(command.pointsCost());
        rewardItem.setStock(command.stock());
        rewardItem.setStatus(command.status());
        return rewardItemRepository.save(rewardItem);
    }

    public record CreateRewardItemCommand(String name, Integer pointsCost, Integer stock, String status) {
    }

    public record UpdateRewardItemCommand(String name, Integer pointsCost, Integer stock, String status) {
    }
}
