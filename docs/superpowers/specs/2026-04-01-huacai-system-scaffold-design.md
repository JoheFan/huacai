# 华彩系统一期骨架设计

## 目标

在当前原型和一期范围的基础上，搭建一套可以持续开发的工程骨架，覆盖：

- `frontend/` Vue3 管理后台
- `backend/` Spring Boot API 服务
- `sql/` 数据库脚本
- `docs/` 设计文档、计划文档、接口与 DTO 草案

## 推荐架构

采用前后端分离的模块化单体方案。

- 前端：Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus
- 后端：Spring Boot 3 + Spring Security + MyBatis-Plus + MySQL
- 鉴权：JWT
- 后端按业务域拆分包：
  - `auth`
  - `system`
  - `customer`
  - `opportunity`
  - `loan`
  - `finance`
  - `file`

## 一期实现边界

一期先完成：

- 项目工程骨架
- 基础认证与鉴权骨架
- 统一响应结构、异常处理、基础配置
- 菜单路由与基础布局
- 后端接口分组与 DTO 草案
- 一期建表 SQL 脚本整理入库

一期不在本轮骨架实现中的内容：

- 审批流完整实现
- 增量经营模块
- 员工与人事完整模块
- 复杂分析报表

## 前端设计

前端目录建议：

- `src/layout`
- `src/router`
- `src/stores`
- `src/api`
- `src/types`
- `src/views/system`
- `src/views/customer`
- `src/views/opportunity`
- `src/views/loan`
- `src/views/finance`
- `src/components`
- `src/utils`

首轮页面骨架包括：

- 登录页
- 主框架页
- 仪表盘占位页
- 系统管理占位页
- 客户管理占位页
- 商机管理占位页
- 借贷管理占位页
- 财务管理占位页

## 后端设计

后端目录建议：

- `src/main/java/com/huacai`
- `common`
- `config`
- `security`
- `auth`
- `system`
- `customer`
- `opportunity`
- `loan`
- `finance`
- `file`

首轮后端能力包括：

- Spring Boot 启动骨架
- 配置文件分环境
- JWT 工具与安全过滤器
- 统一返回结构
- 全局异常处理
- 基础健康检查接口
- 登录接口占位
- 菜单路由接口占位

## DTO 草案策略

DTO 按模块维护在 `docs/` 中，先形成明确字段边界，再逐步落到 Java 类：

- Auth DTO
- System DTO
- Customer DTO
- Opportunity DTO
- Loan DTO
- Finance DTO
- File DTO

## 风险与说明

- 当前目录不是 git 仓库，本轮不走 worktree，也无法提交设计文档 commit。
- 当前机器缺少 Java 和 Maven，需要先安装开发环境。
- 当前接口与 SQL 草案以“一期范围”优先，后续业务确认后可能调整字段和唯一约束。
