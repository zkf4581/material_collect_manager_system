package com.gongdi.materialpoints.modules.point.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.auth.service.RoleGuard;
import com.gongdi.materialpoints.modules.point.domain.PointAccount;
import com.gongdi.materialpoints.modules.point.domain.PointLedger;
import com.gongdi.materialpoints.modules.point.repository.PointAccountRepository;
import com.gongdi.materialpoints.modules.point.repository.PointLedgerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final PointAccountRepository pointAccountRepository;
    private final PointLedgerRepository pointLedgerRepository;
    private final RoleGuard roleGuard;

    public PointController(
            PointAccountRepository pointAccountRepository,
            PointLedgerRepository pointLedgerRepository,
            RoleGuard roleGuard
    ) {
        this.pointAccountRepository = pointAccountRepository;
        this.pointLedgerRepository = pointLedgerRepository;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/summary")
    public ApiResponse<PointSummaryResponse> summary(HttpServletRequest request) {
        var currentUser = roleGuard.requireWorker(request);
        PointAccount account = pointAccountRepository
                .findByProjectIdAndWorkerId(currentUser.projectId(), currentUser.workerId())
                .orElseGet(() -> {
                    PointAccount empty = new PointAccount();
                    empty.setProjectId(currentUser.projectId());
                    empty.setWorkerId(currentUser.workerId());
                    empty.setBalance(0);
                    return empty;
                });

        return ApiResponse.success(new PointSummaryResponse(
                account.getBalance() == null ? 0 : account.getBalance(),
                currentUser.projectId(),
                currentUser.workerId()
        ));
    }

    @GetMapping("/ledger")
    public ApiResponse<List<PointLedger>> ledger(HttpServletRequest request) {
        var currentUser = roleGuard.requireWorker(request);
        return ApiResponse.success(pointLedgerRepository.findAllByWorkerIdOrderByIdDesc(currentUser.workerId()));
    }

    public record PointSummaryResponse(Integer balance, Long projectId, Long workerId) {
    }
}
