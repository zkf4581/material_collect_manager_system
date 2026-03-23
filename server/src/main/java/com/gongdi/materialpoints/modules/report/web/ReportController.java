package com.gongdi.materialpoints.modules.report.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.exchange.domain.ExchangeOrder;
import com.gongdi.materialpoints.modules.exchange.repository.ExchangeOrderRepository;
import com.gongdi.materialpoints.modules.point.domain.PointAccount;
import com.gongdi.materialpoints.modules.point.repository.PointAccountRepository;
import com.gongdi.materialpoints.modules.recycle.domain.RecycleRecord;
import com.gongdi.materialpoints.modules.recycle.repository.RecycleRecordRepository;
import com.gongdi.materialpoints.modules.reward.repository.RewardItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final RecycleRecordRepository recycleRecordRepository;
    private final PointAccountRepository pointAccountRepository;
    private final ExchangeOrderRepository exchangeOrderRepository;
    private final RewardItemRepository rewardItemRepository;

    public ReportController(
            RecycleRecordRepository recycleRecordRepository,
            PointAccountRepository pointAccountRepository,
            ExchangeOrderRepository exchangeOrderRepository,
            RewardItemRepository rewardItemRepository
    ) {
        this.recycleRecordRepository = recycleRecordRepository;
        this.pointAccountRepository = pointAccountRepository;
        this.exchangeOrderRepository = exchangeOrderRepository;
        this.rewardItemRepository = rewardItemRepository;
    }

    @GetMapping("/overview")
    public ApiResponse<OverviewReportResponse> overview() {
        List<RecycleRecord> recycleRecords = recycleRecordRepository.findAll();
        List<ExchangeOrder> exchangeOrders = exchangeOrderRepository.findAll();

        long approvedRecycleCount = recycleRecords.stream()
                .filter(item -> "APPROVED".equals(item.getStatus()))
                .count();
        int totalAwardedPoints = recycleRecords.stream()
                .filter(item -> "APPROVED".equals(item.getStatus()))
                .mapToInt(RecycleRecord::getCalculatedPoints)
                .sum();
        int totalExchangePoints = exchangeOrders.stream()
                .filter(item -> "APPROVED".equals(item.getStatus()))
                .mapToInt(ExchangeOrder::getTotalPoints)
                .sum();

        return ApiResponse.success(new OverviewReportResponse(
                recycleRecords.size(),
                approvedRecycleCount,
                totalAwardedPoints,
                exchangeOrders.size(),
                totalExchangePoints,
                rewardItemRepository.count()
        ));
    }

    @GetMapping("/rankings")
    public ApiResponse<List<WorkerRankingItem>> rankings() {
        List<WorkerRankingItem> rankings = pointAccountRepository.findAll().stream()
                .sorted(Comparator.comparing(PointAccount::getBalance).reversed())
                .map(item -> new WorkerRankingItem(item.getWorkerId(), item.getProjectId(), item.getBalance()))
                .toList();
        return ApiResponse.success(rankings);
    }

    public record OverviewReportResponse(
            long recycleRecordCount,
            long approvedRecycleCount,
            int totalAwardedPoints,
            long exchangeOrderCount,
            int approvedExchangePoints,
            long rewardItemCount
    ) {
    }

    public record WorkerRankingItem(Long workerId, Long projectId, Integer balance) {
    }
}
