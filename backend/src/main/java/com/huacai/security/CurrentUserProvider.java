package com.huacai.security;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Optional<AuthUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
            return Optional.empty();
        }
        return Optional.of(authUser);
    }

    public Long getCurrentUserId() {
        return getCurrentUser().map(AuthUser::getUserId).orElse(null);
    }
}
