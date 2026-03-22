package com.gongdi.materialpoints.modules.auth.service;

import com.gongdi.materialpoints.modules.auth.domain.AppUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenSessionService {

    private final Map<String, CurrentUser> sessions = new ConcurrentHashMap<>();

    public String createSession(AppUser appUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, new CurrentUser(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getRoleCode(),
                appUser.getProjectId(),
                appUser.getWorkerId()
        ));
        return token;
    }

    public Optional<CurrentUser> getCurrentUser(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    public void remove(String token) {
        sessions.remove(token);
    }
}
