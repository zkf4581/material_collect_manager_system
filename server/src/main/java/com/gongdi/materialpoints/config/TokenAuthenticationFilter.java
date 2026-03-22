package com.gongdi.materialpoints.config;

import com.gongdi.materialpoints.modules.auth.service.CurrentUser;
import com.gongdi.materialpoints.modules.auth.service.TokenSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenSessionService tokenSessionService;

    public TokenAuthenticationFilter(TokenSessionService tokenSessionService) {
        this.tokenSessionService = tokenSessionService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            Optional<CurrentUser> currentUserOptional = tokenSessionService.getCurrentUser(token);
            if (currentUserOptional.isPresent()) {
                CurrentUser currentUser = currentUserOptional.get();
                request.setAttribute("currentUser", currentUser);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                currentUser,
                                token,
                                List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.roleCode()))
                        );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
