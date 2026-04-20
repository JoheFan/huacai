# 华彩系统一期骨架 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建华彩系统的一期前后端工程骨架，并补齐后端接口分组与 DTO 草案。

**Architecture:** 采用 `frontend + backend + sql + docs` 的单仓双应用结构。前端使用 Vue3 管理后台，后端使用 Spring Boot 3 模块化单体，先落地认证、基础框架、占位页面和文档骨架。

**Tech Stack:** Vue 3, TypeScript, Vite, Pinia, Vue Router, Element Plus, Spring Boot 3, Spring Security, MyBatis-Plus, MySQL 8, JUnit, Vitest

---

### Task 1: 环境与目录准备

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend`
- Create: `/Users/edy/Documents/tob /华彩系统1025/sql`
- Modify: `/Users/edy/Documents/tob /华彩系统1025/一期建表SQL草案.sql`

- [ ] 安装 Java 与 Maven
- [ ] 创建前后端目录
- [ ] 将 SQL 脚本整理到 `sql/`
- [ ] 验证 `node`, `npm`, `java`, `mvn` 可用

### Task 2: 前端骨架

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/package.json`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/main.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/router/index.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/stores/auth.ts`
- Create: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/layout/*`
- Test: `/Users/edy/Documents/tob /华彩系统1025/frontend/src/**/*.test.ts`

- [ ] 通过 Vite 初始化 Vue3 + TS 工程
- [ ] 安装 Pinia、Vue Router、Element Plus、Axios、Vitest
- [ ] 先写一个路由/布局测试并确认失败
- [ ] 实现登录页和主框架布局
- [ ] 添加基础菜单路由占位页
- [ ] 运行前端测试并确认通过

### Task 3: 后端骨架

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/pom.xml`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/HuacaiApplication.java`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/resources/application.yml`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/test/java/com/huacai/**/*.java`

- [ ] 用 Maven 创建 Spring Boot 工程
- [ ] 引入 Web、Validation、Security、MyBatis-Plus、MySQL、springdoc
- [ ] 先写一个应用启动测试并确认失败
- [ ] 实现启动主类和基础配置
- [ ] 运行后端测试并确认通过

### Task 4: 认证与基础通用层

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/common/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/security/*`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/auth/*`
- Test: `/Users/edy/Documents/tob /华彩系统1025/backend/src/test/java/com/huacai/auth/*`

- [ ] 先写登录接口与统一响应测试并确认失败
- [ ] 实现统一响应对象与全局异常处理
- [ ] 实现 JWT 工具类与安全配置
- [ ] 提供登录接口占位实现
- [ ] 运行测试并确认通过

### Task 5: 接口分组与 DTO 草案

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/docs/后端接口分组与DTO草案.md`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/java/com/huacai/**/dto/*.java`

- [ ] 基于一期接口清单整理模块级 DTO 草案
- [ ] 为 auth/system/customer/opportunity/loan/finance/file 分组
- [ ] 落地关键请求 DTO 与分页查询 DTO 占位类

### Task 6: SQL 与配置整理

**Files:**
- Create: `/Users/edy/Documents/tob /华彩系统1025/sql/init/001_schema.sql`
- Create: `/Users/edy/Documents/tob /华彩系统1025/backend/src/main/resources/db/`

- [ ] 复制并整理一期 SQL 到 `sql/init/001_schema.sql`
- [ ] 保留原草案文档不删除
- [ ] 配置后端数据库连接占位

### Task 7: 验证

**Files:**
- Modify: `/Users/edy/Documents/tob /华彩系统1025/README.md`

- [ ] 运行前端测试
- [ ] 运行前端构建
- [ ] 运行后端测试
- [ ] 启动后端应用
- [ ] 补一份项目启动说明
