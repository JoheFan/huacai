# 华彩系统一期第一阶段最小闭环

基于原型整理后的 `Vue 3 + Spring Boot + MySQL` 一期工程，当前已经完成第一阶段最小可用闭环：

- `frontend/` 已接入真实登录、工作台统计、客户列表、借贷列表、还款列表
- `backend/` 已补齐 `auth / customer / loan / repayment / workbench` 最小真实实现
- `sql/init/001_schema.sql` 可用于初始化一期核心表结构
- `docker-compose.local.yml` 和 `scripts/dev/*.sh` 可用于本地拉起稳定联调链路
- `docs/` 保留需求、PRD、技术方案和一期实现计划

## 目录说明

- `frontend`：前端工程，已接入 Vue Router、Pinia、Element Plus
- `backend`：后端工程，已按 `auth / system / file / customer / opportunity / loan / finance` 分域建好接口骨架
- `sql/init`：数据库初始化脚本
- `docs`：需求和设计文档

## 启动方式

### 0. 准备环境变量

后端与前端都补了示例配置文件：

- [backend/.env.example](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/backend/.env.example)
- [frontend/.env.example](/Users/edy/Documents/tob%20/%E5%8D%8E%E5%BD%A9%E7%B3%BB%E7%BB%9F1025/frontend/.env.example)

最少需要注意：

- `HUACAI_JWT_SECRET` 现在必须显式提供，且必须是至少 32 字节的 Base64 密钥
- 前端开发代理可通过 `VITE_API_PROXY_TARGET` 覆盖

### 1. 启动本地联调链路

```bash
./scripts/dev/up-local-stack.sh
```

默认结果：

- MySQL 运行在 `3306`
- Spring Boot 运行在 `http://127.0.0.1:18081`
- 启动时会自动清理旧的 `huacai-backend-proxy-live`
- 如果检测到旧 `huacai-mysql` 挂的是匿名 volume，会先迁移到命名卷 `1025_mysql-data`

如果旧容器已经不存在，但仍需要手工指定待迁移的 MySQL 卷，可这样启动：

```bash
HUACAI_MYSQL_SOURCE_VOLUME=<旧卷名> ./scripts/dev/up-local-stack.sh
```

停止联调链路：

```bash
./scripts/dev/down-local-stack.sh
```

如果需要连数据卷一起清理：

```bash
./scripts/dev/down-local-stack.sh --volumes
```

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认地址：`http://127.0.0.1:5173`

说明：

- 当前仓库里前端开发代理默认指向 `http://127.0.0.1:18081`，现在是直接命中 `huacai-backend-test`，不再依赖额外 nginx 代理容器。
- 如果你直接把 Spring Boot 跑在本机 `8080`，可以在启动前端时临时指定：

```bash
cd frontend
VITE_API_PROXY_TARGET=http://127.0.0.1:8080 npm run dev
```

### 3. 旧的 MySQL-only 启动方式

仓库里仍保留 `docker-compose.mysql.yml` 作为 MySQL 单独启动方案：

```bash
docker compose -f docker-compose.mysql.yml up -d
```

但本地联调默认应优先使用 `docker-compose.local.yml + scripts/dev/up-local-stack.sh`。

## 当前状态

- 登录已切到真实用户表和 JWT。
- 工作台首页已接入真实统计和最近业务记录。
- 客户列表、借贷列表、还款列表已支持真实查询和新增/编辑落库。
- 当前联调范围严格限定在一期第一阶段：`auth + customer + loan + repayment + workbench`。
- 审批流、导入导出、复杂报表、细粒度权限仍未进入本阶段。
