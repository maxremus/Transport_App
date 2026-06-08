package org.example.transport_saas.auth;

import org.example.transport_saas.entity.Role;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static CustomUserPrincipal getCurrentUser() {
        return (CustomUserPrincipal)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
    }

    public static Long getCurrentCompanyId() {
        return getCurrentUser().getCompanyId();
    }

    public static boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ROLE_ADMIN;
    }
}
