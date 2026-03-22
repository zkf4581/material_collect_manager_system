package com.gongdi.materialpoints.modules.auth.service;

public record CurrentUser(
        Long id,
        String username,
        String roleCode,
        Long projectId
) {
}
