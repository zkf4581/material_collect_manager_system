package com.gongdi.materialpoints.modules.report.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.auth.service.RoleGuard;
import com.gongdi.materialpoints.modules.exchange.domain.ExchangeOrder;
import com.gongdi.materialpoints.modules.exchange.repository.ExchangeOrderRepository;
import com.gongdi.materialpoints.modules.material.domain.MaterialItem;
import com.gongdi.materialpoints.modules.material.repository.MaterialItemRepository;
import com.gongdi.materialpoints.modules.point.domain.PointAccount;
import com.gongdi.materialpoints.modules.point.repository.PointAccountRepository;
import com.gongdi.materialpoints.modules.recycle.domain.RecycleRecord;
import com.gongdi.materialpoints.modules.recycle.repository.RecycleRecordRepository;
import com.gongdi.materialpoints.modules.reward.domain.RewardItem;
import com.gongdi.materialpoints.modules.reward.repository.RewardItemRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final RecycleRecordRepository recycleRecordRepository;
    private final PointAccountRepository pointAccountRepository;
    private final ExchangeOrderRepository exchangeOrderRepository;
    private final RewardItemRepository rewardItemRepository;
    private final MaterialItemRepository materialItemRepository;
    private final RoleGuard roleGuard;

    public ReportController(
            RecycleRecordRepository recycleRecordRepository,
            PointAccountRepository pointAccountRepository,
            ExchangeOrderRepository exchangeOrderRepository,
            RewardItemRepository rewardItemRepository,
            MaterialItemRepository materialItemRepository,
            RoleGuard roleGuard
    ) {
        this.recycleRecordRepository = recycleRecordRepository;
        this.pointAccountRepository = pointAccountRepository;
        this.exchangeOrderRepository = exchangeOrderRepository;
        this.rewardItemRepository = rewardItemRepository;
        this.materialItemRepository = materialItemRepository;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/overview")
    public ApiResponse<OverviewReportResponse> overview(HttpServletRequest request) {
        roleGuard.requireAnyRole(request, "ADMIN", "KEEPER");
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
    public ApiResponse<List<WorkerRankingItem>> rankings(HttpServletRequest request) {
        roleGuard.requireAnyRole(request, "ADMIN", "KEEPER");
        List<WorkerRankingItem> rankings = pointAccountRepository.findAll().stream()
                .sorted(Comparator.comparing(PointAccount::getBalance).reversed())
                .map(item -> new WorkerRankingItem(item.getWorkerId(), item.getProjectId(), item.getBalance()))
                .toList();
        return ApiResponse.success(rankings);
    }

    @GetMapping("/materials")
    public ApiResponse<List<MaterialRankingItem>> materialRankings(HttpServletRequest request) {
        roleGuard.requireAnyRole(request, "ADMIN", "KEEPER");
        Map<Long, String> materialNameMap = materialItemRepository.findAll().stream()
                .collect(Collectors.toMap(MaterialItem::getId, MaterialItem::getName));

        List<MaterialRankingItem> rankings = recycleRecordRepository.findAll().stream()
                .filter(item -> "APPROVED".equals(item.getStatus()))
                .collect(Collectors.groupingBy(
                        RecycleRecord::getMaterialItemId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            double totalQuantity = list.stream()
                                    .map(RecycleRecord::getQuantity)
                                    .mapToDouble(value -> value.doubleValue())
                                    .sum();
                            int totalPoints = list.stream()
                                    .mapToInt(RecycleRecord::getCalculatedPoints)
                                    .sum();
                            RecycleRecord sample = list.get(0);
                            return new MaterialRankingItem(
                                    sample.getMaterialItemId(),
                                    materialNameMap.getOrDefault(sample.getMaterialItemId(), "未知材料"),
                                    sample.getUnitCode(),
                                    totalQuantity,
                                    totalPoints
                            );
                        })
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(MaterialRankingItem::totalPoints).reversed())
                .toList();
        return ApiResponse.success(rankings);
    }

    @GetMapping("/rewards")
    public ApiResponse<List<RewardRankingItem>> rewardRankings(HttpServletRequest request) {
        roleGuard.requireAnyRole(request, "ADMIN", "KEEPER");
        Map<Long, RewardItem> rewardItemMap = rewardItemRepository.findAll().stream()
                .collect(Collectors.toMap(RewardItem::getId, Function.identity()));

        List<RewardRankingItem> rankings = exchangeOrderRepository.findAll().stream()
                .filter(item -> "APPROVED".equals(item.getStatus()) || "DELIVERED".equals(item.getStatus()))
                .collect(Collectors.groupingBy(
                        ExchangeOrder::getRewardItemId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            int totalQuantity = list.stream().mapToInt(ExchangeOrder::getQuantity).sum();
                            int totalPoints = list.stream().mapToInt(ExchangeOrder::getTotalPoints).sum();
                            RewardItem rewardItem = rewardItemMap.get(list.get(0).getRewardItemId());
                            return new RewardRankingItem(
                                    list.get(0).getRewardItemId(),
                                    rewardItem == null ? "未知商品" : rewardItem.getName(),
                                    totalQuantity,
                                    totalPoints
                            );
                        })
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(RewardRankingItem::totalQuantity).reversed())
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

    public record MaterialRankingItem(
            Long materialItemId,
            String materialName,
            String unitCode,
            double totalQuantity,
            int totalPoints
    ) {
    }

    public record RewardRankingItem(
            Long rewardItemId,
            String rewardName,
            int totalQuantity,
            int totalPoints
    ) {
    }
}
