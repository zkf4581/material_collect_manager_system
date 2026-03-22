package com.gongdi.materialpoints.modules.exchange.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.common.exception.BusinessException;
import com.gongdi.materialpoints.modules.auth.service.CurrentUser;
import com.gongdi.materialpoints.modules.exchange.domain.ExchangeOrder;
import com.gongdi.materialpoints.modules.exchange.service.ExchangeOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchange-orders")
public class ExchangeOrderController {

    private final ExchangeOrderService exchangeOrderService;

    public ExchangeOrderController(ExchangeOrderService exchangeOrderService) {
        this.exchangeOrderService = exchangeOrderService;
    }

    @PostMapping
    public ApiResponse<ExchangeOrder> create(
            HttpServletRequest request,
            @Valid @RequestBody CreateExchangeOrderRequest body
    ) {
        return ApiResponse.success(exchangeOrderService.create(
                requiredCurrentUser(request),
                new ExchangeOrderService.CreateExchangeOrderCommand(body.rewardItemId(), body.quantity())
        ));
    }

    @GetMapping("/me")
    public ApiResponse<List<ExchangeOrder>> myOrders(HttpServletRequest request) {
        return ApiResponse.success(exchangeOrderService.listForCurrentUser(requiredCurrentUser(request)));
    }

    @GetMapping
    public ApiResponse<List<ExchangeOrder>> list() {
        return ApiResponse.success(exchangeOrderService.listAll());
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<ExchangeOrder> approve(@PathVariable Long id) {
        return ApiResponse.success(exchangeOrderService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<ExchangeOrder> reject(@PathVariable Long id) {
        return ApiResponse.success(exchangeOrderService.reject(id));
    }

    private CurrentUser requiredCurrentUser(HttpServletRequest request) {
        CurrentUser currentUser = (CurrentUser) request.getAttribute("currentUser");
        if (currentUser == null) {
            throw new BusinessException(40101, "未登录或登录已失效");
        }
        return currentUser;
    }

    public record CreateExchangeOrderRequest(
            @NotNull(message = "商品不能为空")
            Long rewardItemId,
            @NotNull(message = "数量不能为空")
            @Min(value = 1, message = "数量至少为 1")
            Integer quantity
    ) {
    }
}
