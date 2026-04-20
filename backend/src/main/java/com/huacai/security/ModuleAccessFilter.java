package com.huacai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ModuleAccessFilter extends OncePerRequestFilter {

    private final CurrentUserProvider currentUserProvider;
    private final ModuleAccessRegistry moduleAccessRegistry;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public ModuleAccessFilter(
            CurrentUserProvider currentUserProvider,
            ModuleAccessRegistry moduleAccessRegistry,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.currentUserProvider = currentUserProvider;
        this.moduleAccessRegistry = moduleAccessRegistry;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        AuthUser authUser = currentUserProvider.getCurrentUser().orElse(null);
        if (authUser == null || authUser.isSuperAdmin()) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        String requiredPagePermission = moduleAccessRegistry.resolveRequiredPagePermission(requestUri).orElse(null);
        if (requiredPagePermission != null && !authUser.getPagePermissions().contains(requiredPagePermission)) {
            deny(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void deny(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        restAccessDeniedHandler.handle(request, response, new AccessDeniedException("没有访问权限"));
    }
}
