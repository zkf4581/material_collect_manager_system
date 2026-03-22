package com.gongdi.materialpoints.modules.auth.web;

import com.gongdi.materialpoints.common.api.ApiResponse;
import com.gongdi.materialpoints.modules.auth.service.AuthService;
import com.gongdi.materialpoints.modules.auth.service.CurrentUser;
import com.gongdi.materialpoints.modules.auth.service.TokenSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenSessionService tokenSessionService;

    public AuthController(AuthService authService, TokenSessionService tokenSessionService) {
        this.authService = authService;
        this.tokenSessionService = tokenSessionService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(request.username(), request.password());
        return ApiResponse.success(new LoginResponse(result.token(), result.currentUser()));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUser> me(HttpServletRequest request) {
        CurrentUser currentUser = (CurrentUser) request.getAttribute("currentUser");
        return ApiResponse.success(currentUser);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token != null) {
            tokenSessionService.remove(token);
        }
        return ApiResponse.success();
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }

    public record LoginRequest(
            @NotBlank(message = "账号不能为空")
            String username,
            @NotBlank(message = "密码不能为空")
            String password
    ) {
    }

    public record LoginResponse(String token, CurrentUser user) {
    }
}
