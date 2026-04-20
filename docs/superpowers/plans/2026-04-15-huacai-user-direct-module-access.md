# Huacai User Direct Module Access Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add per-user business module visibility with a normal-user welcome page, filtered menus/routes, and backend module access enforcement.

**Architecture:** Keep the current static menu structure, and add a lightweight `module_key` registry shared conceptually by frontend and backend. Persist normal-user module grants in `sys_user_module`, return `visibleModuleKeys` through user/auth APIs, filter frontend navigation from that list, and enforce API access centrally in backend request filtering. `ADMIN` remains full access and bypasses module checks.

**Tech Stack:** Vue 3, Vite, Pinia, Vue Router, Element Plus, Spring Boot 3, Spring Security, MyBatis-Plus, MySQL 8, Node test runner, JUnit 5

---

### Task 1: Persistence And Backend Module Registry

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/sql/init/001_schema.sql`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/entity/SysUserModule.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/mapper/UserModuleMapper.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java`
- Test: `/Users/edy/Documents/tob /华彩系统1025/backend/src/test/java/com/huacai/security/ModuleAccessRegistryTest.java`

- [ ] **Step 1: Write the failing registry test**

```java
package com.huacai.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ModuleAccessRegistryTest {

    @Test
    void resolvesBusinessModuleByApiPath() {
        assertEquals("customers", ModuleAccessRegistry.resolveModuleKey("/api/v1/customers"));
        assertEquals("opportunities", ModuleAccessRegistry.resolveModuleKey("/api/v1/opportunities/1"));
        assertEquals("loan-orders", ModuleAccessRegistry.resolveModuleKey("/api/v1/loan-orders"));
        assertEquals("repayments", ModuleAccessRegistry.resolveModuleKey("/api/v1/repayments"));
        assertNull(ModuleAccessRegistry.resolveModuleKey("/api/v1/auth/me"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=ModuleAccessRegistryTest test`
Expected: FAIL with `ModuleAccessRegistry` not found.

- [ ] **Step 3: Add the new table and backend registry**

```sql
CREATE TABLE `sys_user_module` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `module_key` VARCHAR(64) NOT NULL COMMENT '模块标识',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_module_user_module` (`user_id`, `module_key`),
  KEY `idx_sys_user_module_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户模块关联表';
```

```java
package com.huacai.security;

import java.util.List;

public final class ModuleAccessRegistry {

    public static final List<String> NORMAL_USER_MODULE_KEYS = List.of(
            "customers",
            "opportunities",
            "loan-orders",
            "repayments"
    );

    private ModuleAccessRegistry() {
    }

    public static String resolveModuleKey(String requestUri) {
        if (requestUri == null) {
            return null;
        }
        if (requestUri.startsWith("/api/v1/customers")) {
            return "customers";
        }
        if (requestUri.startsWith("/api/v1/opportunities")) {
            return "opportunities";
        }
        if (requestUri.startsWith("/api/v1/loan-orders")) {
            return "loan-orders";
        }
        if (requestUri.startsWith("/api/v1/repayments")) {
            return "repayments";
        }
        return null;
    }
}
```

```java
@TableName("sys_user_module")
public class SysUserModule {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String moduleKey;
    private Long createdBy;
    private LocalDateTime createdAt;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=ModuleAccessRegistryTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add sql/init/001_schema.sql \
  backend/src/main/java/com/huacai/system/entity/SysUserModule.java \
  backend/src/main/java/com/huacai/system/mapper/UserModuleMapper.java \
  backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java \
  backend/src/test/java/com/huacai/security/ModuleAccessRegistryTest.java
git commit -m "feat: add user module persistence and registry"
```

### Task 2: User/Auth DTOs And Backend Module Enforcement

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/dto/UserCreateRequest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/dto/UserUpdateRequest.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/vo/UserVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/vo/CurrentUserInfoVO.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/AuthUser.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/service/impl/AuthServiceImpl.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/ModuleAccessFilter.java`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/config/SecurityConfig.java`
- Test: `/Users/edy/Documents/tob /华彩系统1025/backend/src/test/java/com/huacai/security/ModuleAccessFilterTest.java`

- [ ] **Step 1: Write the failing filter test**

```java
package com.huacai.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ModuleAccessFilterTest {

    @Test
    void deniesNormalUserWhenModuleIsMissing() {
        AuthUser authUser = new AuthUser(2L, "staff", "员工", 1L, "总部", List.of("STAFF"), List.of("customers"));
        String denied = ModuleAccessFilter.resolveDeniedModule(authUser, "/api/v1/loan-orders");
        assertEquals("loan-orders", denied);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=ModuleAccessFilterTest test`
Expected: FAIL with `AuthUser` constructor mismatch or `ModuleAccessFilter` missing.

- [ ] **Step 3: Add visibleModuleKeys through user/auth flows and enforce module access**

```java
public record UserCreateRequest(
    @NotBlank(message = "用户名不能为空")
    String username,
    @NotBlank(message = "密码不能为空")
    String password,
    ...
    List<Long> roleIds,
    List<String> visibleModuleKeys
) {
}
```

```java
public record CurrentUserInfoVO(
        Long id,
        String username,
        String realName,
        Long orgId,
        String orgName,
        List<String> roles,
        List<String> permissions,
        List<String> visibleModuleKeys
) {
}
```

```java
public class AuthUser {
    ...
    private final List<String> visibleModuleKeys;

    public boolean isAdmin() {
        return roles.contains("ADMIN");
    }
}
```

```java
public class ModuleAccessFilter extends OncePerRequestFilter {

    static String resolveDeniedModule(AuthUser authUser, String requestUri) {
        if (authUser == null || authUser.isAdmin()) {
            return null;
        }
        String requiredModule = ModuleAccessRegistry.resolveModuleKey(requestUri);
        if (requiredModule == null) {
            return null;
        }
        return authUser.getVisibleModuleKeys().contains(requiredModule) ? null : requiredModule;
    }
}
```

```java
http
    .addFilterAfter(moduleAccessFilter, JwtAuthenticationFilter.class)
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/system/**").hasAuthority("ADMIN")
        .requestMatchers(...).permitAll()
        .anyRequest().authenticated()
    );
```

- [ ] **Step 4: Run backend tests to verify they pass**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/backend && mvn -Dtest=ModuleAccessRegistryTest,ModuleAccessFilterTest,MybatisMetaObjectHandlerTest,WorkbenchServiceImplTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/huacai/system/dto/UserCreateRequest.java \
  backend/src/main/java/com/huacai/system/dto/UserUpdateRequest.java \
  backend/src/main/java/com/huacai/system/vo/UserVO.java \
  backend/src/main/java/com/huacai/auth/vo/CurrentUserInfoVO.java \
  backend/src/main/java/com/huacai/security/AuthUser.java \
  backend/src/main/java/com/huacai/auth/service/impl/AuthServiceImpl.java \
  backend/src/main/java/com/huacai/system/service/impl/SystemManageServiceImpl.java \
  backend/src/main/java/com/huacai/security/ModuleAccessFilter.java \
  backend/src/main/java/com/huacai/config/SecurityConfig.java \
  backend/src/test/java/com/huacai/security/ModuleAccessFilterTest.java
git commit -m "feat: enforce user module access on backend"
```

### Task 3: Frontend Auth, Menu Filtering, And Welcome Page

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/auth.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/stores/auth.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/types/navigation.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/menu.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/layout/AppLayout.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/common/WelcomeView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/tests/module-access-routing.test.ts`

- [ ] **Step 1: Write the failing frontend access test**

```ts
import assert from 'node:assert/strict'
import test from 'node:test'
import { canAccessModuleRoute, getDefaultSignedInPath } from '../src/router/moduleAccess.ts'

test('normal user defaults to welcome page', () => {
  assert.equal(getDefaultSignedInPath(['customers'], false), '/welcome')
})

test('normal user cannot access unauthorized route', () => {
  assert.equal(canAccessModuleRoute('/loan-orders', ['customers'], false), false)
})
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && node --test --experimental-strip-types tests/module-access-routing.test.ts`
Expected: FAIL with missing helper module.

- [ ] **Step 3: Implement frontend module access flow**

```ts
export interface CurrentUserInfo {
  ...
  visibleModuleKeys: string[]
}
```

```ts
export interface MenuItem extends ModuleRouteMeta {
  path: string
  icon: string
  moduleKey?: string
  adminOnly?: boolean
}
```

```ts
{
  path: '/customers',
  title: '客户管理',
  icon: 'UserFilled',
  moduleKey: 'customers'
}
```

```ts
if (to.path === '/dashboard' && !authStore.user?.roles.includes('ADMIN')) {
  return '/welcome'
}
```

```vue
<section class="welcome-page">
  <header class="welcome-page__hero">
    <h1>欢迎回来，{{ userName }}</h1>
    <p>根据当前账号权限，你可以从下面的模块开始工作。</p>
  </header>
</section>
```

- [ ] **Step 4: Run frontend tests and build**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && npm run test:unit && node --test --experimental-strip-types tests/module-access-routing.test.ts && npm run build`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/api/auth.ts \
  frontend/src/stores/auth.ts \
  frontend/src/types/navigation.ts \
  frontend/src/router/menu.ts \
  frontend/src/router/index.ts \
  frontend/src/layout/AppLayout.vue \
  frontend/src/views/common/WelcomeView.vue \
  frontend/tests/module-access-routing.test.ts
git commit -m "feat: filter menus and add normal user welcome page"
```

### Task 4: User Drawer Module Configuration UX

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/api/system.ts`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/user/UserManageView.vue`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/views/system/user/userModuleRegistry.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/tests/user-module-selection.test.ts`

- [ ] **Step 1: Write the failing selection-mode test**

```ts
import assert from 'node:assert/strict'
import test from 'node:test'
import { getSelectableUserModules } from '../src/views/system/user/userModuleRegistry.ts'

test('normal user options exclude system modules', () => {
  const groups = getSelectableUserModules(false)
  assert.deepEqual(groups.map(group => group.title), ['客户经营', '经营台账'])
})
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && node --test --experimental-strip-types tests/user-module-selection.test.ts`
Expected: FAIL with missing registry.

- [ ] **Step 3: Implement the drawer section**

```ts
export interface UserSavePayload {
  ...
  visibleModuleKeys?: string[]
}
```

```ts
export const USER_MODULE_GROUPS = [
  {
    title: '客户经营',
    items: [
      { key: 'customers', title: '客户管理' },
      { key: 'opportunities', title: '商机管理' },
    ],
  },
  {
    title: '经营台账',
    items: [
      { key: 'loan-orders', title: '借贷管理' },
      { key: 'repayments', title: '还款明细' },
    ],
  },
]
```

```vue
<section class="module-access-card form-grid__full">
  <div class="module-access-card__header">
    <div>
      <strong>可见模块</strong>
      <p>未勾选的模块不会出现在左侧菜单，对应接口也不可访问</p>
    </div>
    <span>已选 {{ form.visibleModuleKeys.length }} 个模块</span>
  </div>
</section>
```

- [ ] **Step 4: Run frontend tests and build**

Run: `cd /Users/edy/Documents/tob\ /华彩系统1025/frontend && npm run test:unit && node --test --experimental-strip-types tests/module-access-routing.test.ts tests/user-module-selection.test.ts && npm run build`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add frontend/src/api/system.ts \
  frontend/src/views/system/user/UserManageView.vue \
  frontend/src/views/system/user/userModuleRegistry.ts \
  frontend/tests/user-module-selection.test.ts
git commit -m "feat: add user module selection in drawer"
```

### Task 5: Real Integration Verification And Handoff Refresh

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md`

- [ ] **Step 1: Verify admin retains full access**

Run:

```bash
curl -s -X POST http://127.0.0.1:18081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

Expected:

```json
{
  "data": {
    "userInfo": {
      "roles": ["ADMIN"],
      "visibleModuleKeys": ["customers", "opportunities", "loan-orders", "repayments"]
    }
  }
}
```

- [ ] **Step 2: Verify normal-user routing and 403 behavior**

Run:

```bash
curl -s -X POST http://127.0.0.1:18081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"zhangyiyi","password":"123456"}'
```

Then:

```bash
curl -i http://127.0.0.1:18081/api/v1/customers -H "Authorization: Bearer <token>"
curl -i http://127.0.0.1:18081/api/v1/loan-orders -H "Authorization: Bearer <token>"
```

Expected:
- authorized module returns 200
- unauthorized module returns 403

- [ ] **Step 3: Verify browser behavior**

Run:

```bash
cd /Users/edy/Documents/tob\ /华彩系统1025/frontend
npm run dev -- --host 0.0.0.0 --port 4174
```

Expected:
- admin sees current full menu
- normal user lands on `/welcome`
- normal user only sees granted business modules
- normal user direct visit to unauthorized page bounces to `/welcome`

- [ ] **Step 4: Update the handoff**

```md
- 记录 `visibleModuleKeys` 的最终设计
- 记录欢迎页行为
- 记录普通用户 403 校验结果
- 记录当前仍未做数据范围控制
```

- [ ] **Step 5: Commit**

```bash
git add docs/superpowers/specs/2026-04-06-huacai-phase1-frontend-closure-handoff.md
git commit -m "docs: refresh handoff for user module access"
```

## Self-Review

### Spec coverage

- 用户直接勾模块：Task 4
- 普通用户欢迎页：Task 3
- 前端菜单与路由拦截：Task 3
- 后端接口拦截：Task 2
- 管理员保留全量权限：Task 2 + Task 5
- 系统管理保留管理员可见：Task 2 + Task 3

无缺口。

### Placeholder scan

- 已避免 `TODO`、`TBD`、`implement later`
- 每个任务都给出明确文件、代码方向和命令

### Type consistency

- 后端统一使用 `visibleModuleKeys`
- 前端统一使用 `visibleModuleKeys`
- 模块键统一使用：
  - `customers`
  - `opportunities`
  - `loan-orders`
  - `repayments`

无命名冲突。
