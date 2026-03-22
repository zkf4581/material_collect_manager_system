package com.gongdi.materialpoints.modules.point.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.auth.service.CurrentUser;
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

    public PointController(PointAccountRepository pointAccountRepository, PointLedgerRepository pointLedgerRepository) {
        this.pointAccountRepository = pointAccountRepository;
        this.pointLedgerRepository = pointLedgerRepository;
    }

    @GetMapping("/summary")
    public ApiResponse<PointSummaryResponse> summary(HttpServletRequest request) {
        CurrentUser currentUser = requiredCurrentUser(request);
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
        CurrentUser currentUser = requiredCurrentUser(request);
        return ApiResponse.success(pointLedgerRepository.findAllByWorkerIdOrderByIdDesc(currentUser.workerId()));
    }

    private CurrentUser requiredCurrentUser(HttpServletRequest request) {
        CurrentUser currentUser = (CurrentUser) request.getAttribute("currentUser");
        if (currentUser == null || currentUser.workerId() == null) {
            throw new BusinessException(40101, "当前账号未绑定工人或未登录");
        }
        return currentUser;
    }

    public record PointSummaryResponse(Integer balance, Long projectId, Long workerId) {
    }
}
