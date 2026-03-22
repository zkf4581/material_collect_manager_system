package com.gongdi.materialpoints.modules.auth.service;

import com.gongdi.materialpoints.modules.auth.domain.AppUser;
import com.gongdi.materialpoints.modules.auth.repository.AppUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final TokenSessionService tokenSessionService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthService(AppUserRepository appUserRepository, TokenSessionService tokenSessionService) {
        this.appUserRepository = appUserRepository;
        this.tokenSessionService = tokenSessionService;
    }

    public LoginResult login(String username, String password) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .filter(user -> "ENABLED".equals(user.getStatus()))
                .orElseThrow(() -> new BadCredentialsException("账号或密码错误"));

        if (!passwordEncoder.matches(password, appUser.getPassword())) {
            throw new BadCredentialsException("账号或密码错误");
        }

        String token = tokenSessionService.createSession(appUser);
        return new LoginResult(
                token,
                new CurrentUser(
                        appUser.getId(),
                        appUser.getUsername(),
                        appUser.getRoleCode(),
                        appUser.getProjectId(),
                        appUser.getWorkerId()
                )
        );
    }

    public record LoginResult(String token, CurrentUser currentUser) {
    }
}
