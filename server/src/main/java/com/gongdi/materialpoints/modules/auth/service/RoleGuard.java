package com.gongdi.materialpoints.modules.auth.service;

import com.gongdi.materialpoints.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RoleGuard {

    public CurrentUser requireCurrentUser(HttpServletRequest request) {
        CurrentUser currentUser = (CurrentUser) request.getAttribute("currentUser");
        if (currentUser == null) {
            throw new BusinessException(40101, "未登录或登录已失效");
        }
        return currentUser;
    }

    public CurrentUser requireAnyRole(HttpServletRequest request, String... roles) {
        CurrentUser currentUser = requireCurrentUser(request);
        if (!Set.of(roles).contains(currentUser.roleCode())) {
            throw new BusinessException(40301, "当前角色无权限执行该操作");
        }
        return currentUser;
    }

    public CurrentUser requireWorker(HttpServletRequest request) {
        CurrentUser currentUser = requireAnyRole(request, "WORKER");
        if (currentUser.workerId() == null) {
            throw new BusinessException(40301, "当前账号未绑定工人");
        }
        return currentUser;
    }
}
