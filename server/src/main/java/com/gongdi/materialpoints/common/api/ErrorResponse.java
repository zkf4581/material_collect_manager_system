package com.gongdi.materialpoints.common.api;

import java.time.LocalDateTime;

public record ErrorResponse(
        int code,
        String message,
        String traceId,
        LocalDateTime timestamp
) {

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, null, LocalDateTime.now());
    }
}
