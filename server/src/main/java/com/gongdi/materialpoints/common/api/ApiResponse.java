package com.gongdi.materialpoints.common.api;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        int code,
        String message,
        T data,
        String traceId,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data, null, LocalDateTime.now());
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "success", null, null, LocalDateTime.now());
    }
}
