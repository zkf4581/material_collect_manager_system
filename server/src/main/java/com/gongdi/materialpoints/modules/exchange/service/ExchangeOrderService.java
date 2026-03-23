package com.gongdi.materialpoints.modules.exchange.service;

import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.auth.service.CurrentUser;
import com.gongdi.materialpoints.modules.exchange.domain.ExchangeOrder;
import com.gongdi.materialpoints.modules.exchange.repository.ExchangeOrderRepository;
import com.gongdi.materialpoints.modules.point.domain.PointAccount;
import com.gongdi.materialpoints.modules.point.domain.PointLedger;
import com.gongdi.materialpoints.modules.point.repository.PointAccountRepository;
import com.gongdi.materialpoints.modules.point.repository.PointLedgerRepository;
import com.gongdi.materialpoints.modules.reward.domain.RewardItem;
import com.gongdi.materialpoints.modules.reward.repository.RewardItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ExchangeOrderService {

    private final ExchangeOrderRepository exchangeOrderRepository;
    private final RewardItemRepository rewardItemRepository;
    private final PointAccountRepository pointAccountRepository;
    private final PointLedgerRepository pointLedgerRepository;

    public ExchangeOrderService(
            ExchangeOrderRepository exchangeOrderRepository,
            RewardItemRepository rewardItemRepository,
            PointAccountRepository pointAccountRepository,
            PointLedgerRepository pointLedgerRepository
    ) {
        this.exchangeOrderRepository = exchangeOrderRepository;
        this.rewardItemRepository = rewardItemRepository;
        this.pointAccountRepository = pointAccountRepository;
        this.pointLedgerRepository = pointLedgerRepository;
    }

    @Transactional
    public ExchangeOrder create(CurrentUser currentUser, CreateExchangeOrderCommand command) {
        if (currentUser.workerId() == null) {
            throw new BusinessException(40301, "当前账号未绑定工人，不能发起兑换");
        }

        RewardItem rewardItem = rewardItemRepository.findById(command.rewardItemId())
                .filter(item -> "ENABLED".equals(item.getStatus()))
                .orElseThrow(() -> new BusinessException(40401, "兑换商品不存在"));

        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setOrderNo(generateOrderNo());
        exchangeOrder.setProjectId(currentUser.projectId());
        exchangeOrder.setWorkerId(currentUser.workerId());
        exchangeOrder.setRewardItemId(rewardItem.getId());
        exchangeOrder.setQuantity(command.quantity());
        exchangeOrder.setTotalPoints(rewardItem.getPointsCost() * command.quantity());
        exchangeOrder.setStatus("SUBMITTED");
        exchangeOrder.setSubmittedAt(LocalDateTime.now());
        return exchangeOrderRepository.save(exchangeOrder);
    }

    @Transactional
    public ExchangeOrder approve(Long exchangeOrderId) {
        ExchangeOrder exchangeOrder = exchangeOrderRepository.findById(exchangeOrderId)
                .orElseThrow(() -> new BusinessException(40401, "兑换申请不存在"));
        if (!"SUBMITTED".equals(exchangeOrder.getStatus())) {
            throw new BusinessException(40901, "当前状态不允许审核通过");
        }

        RewardItem rewardItem = rewardItemRepository.findById(exchangeOrder.getRewardItemId())
                .orElseThrow(() -> new BusinessException(40401, "兑换商品不存在"));
        if (rewardItem.getStock() < exchangeOrder.getQuantity()) {
            throw new BusinessException(40903, "商品库存不足");
        }

        PointAccount pointAccount = pointAccountRepository
                .findByProjectIdAndWorkerId(exchangeOrder.getProjectId(), exchangeOrder.getWorkerId())
                .orElseThrow(() -> new BusinessException(40902, "积分账户不存在"));
        if (pointAccount.getBalance() < exchangeOrder.getTotalPoints()) {
            throw new BusinessException(40902, "积分不足");
        }

        pointAccount.setBalance(pointAccount.getBalance() - exchangeOrder.getTotalPoints());
        pointAccountRepository.save(pointAccount);

        rewardItem.setStock(rewardItem.getStock() - exchangeOrder.getQuantity());
        rewardItemRepository.save(rewardItem);

        PointLedger pointLedger = new PointLedger();
        pointLedger.setPointAccountId(pointAccount.getId());
        pointLedger.setWorkerId(exchangeOrder.getWorkerId());
        pointLedger.setProjectId(exchangeOrder.getProjectId());
        pointLedger.setBizType("EXCHANGE_DEDUCT");
        pointLedger.setBizId(exchangeOrder.getId());
        pointLedger.setChangeAmount(-exchangeOrder.getTotalPoints());
        pointLedger.setBalanceAfter(pointAccount.getBalance());
        pointLedger.setRemark("兑换申请审核通过扣减积分");
        pointLedgerRepository.save(pointLedger);

        exchangeOrder.setStatus("APPROVED");
        exchangeOrder.setApprovedAt(LocalDateTime.now());
        return exchangeOrderRepository.save(exchangeOrder);
    }

    @Transactional
    public ExchangeOrder reject(Long exchangeOrderId) {
        ExchangeOrder exchangeOrder = exchangeOrderRepository.findById(exchangeOrderId)
                .orElseThrow(() -> new BusinessException(40401, "兑换申请不存在"));
        if (!"SUBMITTED".equals(exchangeOrder.getStatus())) {
            throw new BusinessException(40901, "当前状态不允许驳回");
        }
        exchangeOrder.setStatus("REJECTED");
        return exchangeOrderRepository.save(exchangeOrder);
    }

    @Transactional
    public ExchangeOrder deliver(Long exchangeOrderId) {
        ExchangeOrder exchangeOrder = exchangeOrderRepository.findById(exchangeOrderId)
                .orElseThrow(() -> new BusinessException(40401, "兑换申请不存在"));
        if (!"APPROVED".equals(exchangeOrder.getStatus())) {
            throw new BusinessException(40901, "当前状态不允许发放确认");
        }
        exchangeOrder.setStatus("DELIVERED");
        exchangeOrder.setDeliveredAt(LocalDateTime.now());
        return exchangeOrderRepository.save(exchangeOrder);
    }

    public List<ExchangeOrder> listForCurrentUser(CurrentUser currentUser) {
        if (currentUser.workerId() == null) {
            throw new BusinessException(40301, "当前账号未绑定工人");
        }
        return exchangeOrderRepository.findAllByWorkerIdOrderByIdDesc(currentUser.workerId());
    }

    public List<ExchangeOrder> listAll() {
        return exchangeOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    private String generateOrderNo() {
        String prefix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.CHINA)
                .format(LocalDateTime.now());
        return "EX" + prefix + (int) (Math.random() * 1000);
    }

    public record CreateExchangeOrderCommand(Long rewardItemId, Integer quantity) {
    }
}
