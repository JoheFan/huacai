# 华彩系统 代码质量评审报告

## 项目概览

| 维度 | 详情 |
|------|------|
| **前端** | Vue 3.5 + Vite 8 + TypeScript 5.9 + Element Plus + Pinia |
| **后端** | Spring Boot 3.5 + Java 21 + MyBatis-Plus 3.5 + MySQL 8.4 |
| **后端 Java 文件数** | 143 个源文件 / 9 个测试文件 |
| **前端视图页** | 14 个 `.vue` 页面 + 14 个单元测试 |
| **DevOps** | Docker Compose + Shell 脚本本地联调 |

---

## 总体评价

> [!TIP]
> **整体水平：中上 (7/10)**，作为一期 MVP 闭环阶段，项目在架构分层、技术选型一致性和前端工程化上表现不错。以下评审重点指出 **当前可快速改进的项目** 和 **随规模增长必须提前解决的结构性风险**。

---

## ✅ 做得好的方面

### 1. 前后端分离架构清晰
- 前端 Vite + Vue 3 Composition API + TypeScript 全覆盖
- 后端按业务域分包（`auth / customer / loan / opportunity / system / workbench / finance`），层次结构标准（`controller → service → mapper → entity → dto → vo → query`）
- API 层前后端约定了统一的 `ApiResponse<T>` 信封格式 + `PageData<T>` 分页模型

### 2. 前端工程质量
- HTTP 客户端 [http.ts](file:///Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/frontend/src/api/http.ts) 实现了统一的请求拦截、错误映射、泛型拆包，干净利落
- 类型定义覆盖完整（`types/navigation.ts`, `api/*.ts` 均有具体 interface）
- CSS 设计系统（[style.css](file:///Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/frontend/src/style.css)）使用 CSS Custom Properties 定义了完整 token，与 Element Plus 主题变量也做了对齐
- 已有 14 个前端单测，覆盖权限、路由、数据模型等关键逻辑
- Vite build 配置了 `manualChunks`，做了合理的代码分割
- 多档响应式断点（1280px / 1180px / 1024px / 960px / 720px），对大屏和小屏都有适配

### 3. 后端安全基础
- Spring Security + JWT 无状态认证实现完整
- 有模块级权限过滤器 `ModuleAccessFilter` + `ModuleAccessRegistry`
- 全局异常处理 [GlobalExceptionHandler](file:///Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/src/main/java/com/huacai/common/exception/GlobalExceptionHandler.java) 覆盖了参数校验、业务异常、唯一约束冲突、权限不足和兜底异常

### 4. 数据库设计
- [001_schema.sql](file:///Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/sql/init/001_schema.sql) 结构清晰，525 行覆盖 20+ 张表
- 统一了审计字段（`created_by/created_at/updated_by/updated_at`）、逻辑删除（`deleted_flag`）和乐观锁（`version`）
- 索引策略合理，常用查询字段均有覆盖

### 5. DevOps
- `docker-compose.local.yml` 实现了 MySQL + 后端一键联调
- 健康检查、启动等待、命名卷持久化都有考虑

---

## 🔴 高优先级改进项

### 1. 后端测试覆盖率极低

> [!CAUTION]
> 143 个 Java 源文件，仅 9 个测试文件。Controller 层 **零测试**，Service 层也仅部分覆盖。

**风险**：业务逻辑回归无保障，重构和迭代时会产生隐性 Bug。

**建议**：
- 为每个 Controller 补充集成测试（`@WebMvcTest` 或 `@SpringBootTest`），验证请求参数校验、权限拦截、返回结构
- Service 层用 MockBean 做单元测试，覆盖核心业务规则（如借贷金额计算、客户状态流转）
- 引入 `JaCoCo` 做覆盖率报告，设定最低阈值（如 60%）

---

### 2. `traceId` 硬编码为 `"todo-trace-id"`

```java
// ApiResponse.java
public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(0, "success", data, "todo-trace-id");
}
```

> [!WARNING]
> 所有 API 返回的 `traceId` 都是同一个字符串。生产环境排查问题时将无法定位具体请求。

**建议**：
- 引入 `MDC` + 请求拦截器，在请求入口生成 UUID 作为 traceId
- `GlobalExceptionHandler` 和 `ApiResponse` 中从 MDC 取值

---

### 3. JWT Secret 安全隐患

```yaml
# application.yml
huacai:
  jwt:
    secret: ${HUACAI_JWT_SECRET:huacai-phase1-real-mysql-jwt-secret-please-change}
```

> [!CAUTION]
> 默认 secret 在配置文件中明文写死，长度不足，且注释 "please-change" 说明可能在部署时被遗忘。

**建议**：
- 移除默认值，强制通过环境变量注入
- 使用至少 256-bit（32 字节）的 Base64 编码密钥
- 考虑添加启动校验，secret 不满足要求时拒绝启动

---

### 4. 前端 HTTP 拦截器缺少 401 自动跳转

```typescript
// http.ts — 当前只做了 request 拦截，没有 response 拦截
http.interceptors.request.use((config) => { ... })
```

**风险**：Token 过期后，用户在页面上操作会看到模糊的错误提示，而不是自动跳回登录页。

**建议**：
```typescript
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('huacai-token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

---

### 5. `DataInitializer` 中 `@Transactional` 不会生效

```java
// DataInitializer.java
@Bean
public ApplicationRunner applicationRunner(...) {
    return args -> initAdminData(...);  // 直接调用 this 方法
}

@Transactional
protected void initAdminData(...) { ... }  // ← this 调用，AOP 代理失效
```

> [!WARNING]
> Spring 的 `@Transactional` 依赖 AOP 代理，同一个类内的 this 调用会绕过代理，事务注解不会生效。

**建议**：
- 将 `initAdminData` 抽到单独的 `@Service` 类中
- 或者使用 `TransactionTemplate` 编程式事务

---

## 🟡 中优先级改进项

### 6. 大 Vue 组件需要拆分

| 组件 | 行数 |
|------|------|
| `CustomerArchiveView.vue` | 732 行 |
| `LoanOrderListView.vue` | 645 行 |
| `UserManageView.vue` | 602 行 |
| `WorkbenchView.vue` | 547 行 |

**建议**：
- 将 "筛选区"、"表格区"、"对话框" 拆为子组件
- 表格列的自定义渲染可用 `defineSlots` 或独立组件提取
- 共性的列表页逻辑（加载/分页/搜索/重置）可抽成 composable（`useListPage`）

### 7. 缺少 .env 示例文件

后端和前端都依赖环境变量（`HUACAI_DB_URL`、`VITE_API_PROXY_TARGET` 等），但仓库中没有 `.env.example` 文件。新成员 onboarding 需要翻 README 和 docker-compose 才能拼出完整变量列表。

**建议**：在 `backend/` 和 `frontend/` 各放一个 `.env.example`。

### 8. SQL Schema 缺少变更管理

- 当前只有一个 `001_schema.sql`，每次变更都是全量 DROP + CREATE
- `sql/migration/` 目录存在但为空

**建议**：
- 引入 Flyway 或 Liquibase 做数据库版本管理
- 每次需求新增字段时，写增量迁移脚本而不是改 schema

### 9. 前端缺少统一的 Loading/Error 边界

- 每个页面都在 `script setup` 中重复 `loading.value = true → try/catch → finally → loading.value = false` 的模式
- 没有全局的错误提示策略（有的用 `ElMessage.error`，有的可能直接忽略）

**建议**：
- 封装 `useAsyncAction` 或 `useRequest` composable
- 考虑在 `http.ts` 的 response 拦截器中统一处理 toast 提示

### 10. 后端缺少 Lombok 或 Record 的统一策略

- 部分使用了 Java `record`（如 `ApiResponse`），但 Entity 和 DTO 大量手写 getter/setter
- 没有引入 Lombok

**建议**：
- 统一用 Lombok（`@Data`, `@Builder`）减少样板代码
- 或者对 DTO/VO 统一用 record（不可变，更安全）

### 11. 前端路由权限与菜单的耦合

- [menu.ts](file:///Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/frontend/src/router/menu.ts) 既是菜单配置又决定路由生成，`routeComponentMap` 在 [index.ts](file:///Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/frontend/src/router/index.ts) 中是手动维护的静态映射
- 新增页面需要同时改 `menu.ts` 和 `routeComponentMap`，容易遗漏

**建议**：
- 将 component 信息直接放到 `MenuItem` 定义中
- 或者使用 Vite 的 `import.meta.glob` 按约定自动发现路由组件

---

## 🟢 低优先级 / 长期改进项

### 12. 前端缺少 ESLint / Prettier 配置
- 仓库中没有 `.eslintrc` 或 `.prettierrc`
- 当前代码风格一致性靠自觉维护，多人协作时容易分化

### 13. Docker Compose 跳过了测试
```yaml
command:
  - sh
  - -lc
  - mvn -Dmaven.test.skip=true spring-boot:run
```
- 本地联调跳测试可以理解，但 CI/CD 管道中应该执行测试

### 14. 前端没有区分 `devDependencies` 中的测试工具
- `package.json` 中没有 Playwright/Vitest 等测试框架依赖，但 `tests/` 目录已有测试文件
- 测试使用 Node.js 原生 `--test` runner，这在功能上可以工作，但长期看 Vitest 生态更成熟

### 15. 考虑引入 API 文档 / 契约
- 后端已配置 Springdoc OpenAPI，但前后端类型定义（TS interface vs Java DTO）是手动对齐的
- 长期可考虑用 OpenAPI 生成前端类型定义，减少手动同步成本

### 16. `utils/` 目录为空
- 前端的 `src/utils/` 目录存在但为空，`formatNumber` 等工具函数散落在各 View 中
- 建议将通用工具函数收拢到此目录

---

## 改进路线图建议

| 阶段 | 主要事项 | 预计工期 |
|------|---------|---------|
| **立即** | traceId 真实化、JWT secret 强制环境变量、401 拦截器、`@Transactional` 修复 | 1-2 天 |
| **一周内** | 补充 Controller 集成测试、封装 `useListPage` composable、拆分大组件 | 3-5 天 |
| **两周内** | 引入 Flyway、添加 ESLint/Prettier、编写 `.env.example` | 2-3 天 |
| **持续** | 后端测试覆盖率提升到 60%+、API 契约自动化 | 持续迭代 |
