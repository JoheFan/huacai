package com.huacai.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class ModuleAccessFilterTest {

    private final ModuleAccessFilter filter = new ModuleAccessFilter(
            new CurrentUserProvider(),
            new ModuleAccessRegistry(),
            new RestAccessDeniedHandler(new ObjectMapper())
    );

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void blocksBusinessApiWhenModuleIsNotGranted() throws ServletException, IOException {
        authenticate(new AuthUser(
                7L,
                "zhangyiyi",
                "张一一",
                2L,
                "财务",
                "NORMAL_USER",
                "STAFF",
                "普通用户",
                List.of("STAFF"),
                List.of("/welcome", "/customers"),
                List.of(),
                java.util.Map.of("customers", "SELF"),
                "仅本人数据权限"
        ));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/repayments");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean proceeded = new AtomicBoolean(false);

        filter.doFilter(request, response, chain(proceeded));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentAsString()).contains("没有访问权限");
        assertThat(proceeded).isFalse();
    }

    @Test
    void blocksWorkbenchApiForNonAdminUsers() throws ServletException, IOException {
        authenticate(new AuthUser(
                7L,
                "zhangyiyi",
                "张一一",
                2L,
                "财务",
                "NORMAL_USER",
                "STAFF",
                "普通用户",
                List.of("STAFF"),
                List.of("/welcome", "/customers", "/repayments"),
                List.of(),
                java.util.Map.of("customers", "SELF", "repayments", "SELF"),
                "仅本人数据权限"
        ));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/workbench/overview");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean proceeded = new AtomicBoolean(false);

        filter.doFilter(request, response, chain(proceeded));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_FORBIDDEN);
        assertThat(proceeded).isFalse();
    }

    @Test
    void allowsGrantedBusinessApiAndAdminOnlyApisForAdminUsers() throws ServletException, IOException {
        authenticate(new AuthUser(
                1L,
                "admin",
                "管理员",
                1L,
                "总部",
                "SUPER_ADMIN",
                "ADMIN",
                "超级管理员",
                List.of("ADMIN"),
                List.of("/dashboard", "/system/users"),
                List.of("system.users:create"),
                java.util.Map.of("customers", "ALL"),
                "全部数据权限"
        ));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/system/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean proceeded = new AtomicBoolean(false);

        filter.doFilter(request, response, chain(proceeded));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_OK);
        assertThat(proceeded).isTrue();
    }

    private void authenticate(AuthUser authUser) {
        List<SimpleGrantedAuthority> authorities = authUser.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private FilterChain chain(AtomicBoolean proceeded) {
        return (request, response) -> proceeded.set(true);
    }
}
