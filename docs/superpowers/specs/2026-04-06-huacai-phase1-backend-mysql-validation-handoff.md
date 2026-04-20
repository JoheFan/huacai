# 华彩系统一期后端与 MySQL 闭环验证交接

## 本轮目标

按 2026-04-06 接手要求，只做以下 4 件事：

1. 修复 `docker-compose.mysql.yml`，让 MySQL 正常启动并健康。
2. 修复后端显式编译错误，并完成一次可复现的后端编译验证。
3. 基于真实 MySQL 验证以下接口可用：
   - `/api/v1/auth/login`
   - `/api/v1/workbench/overview`
   - `/api/v1/customers`
   - `/api/v1/loan-orders`
   - `/api/v1/repayments`
4. 记录本轮修改、验证命令和剩余阻塞。

## 本轮结论

- MySQL 已通过 `docker-compose.mysql.yml` 正常启动并达到 `healthy`。
- 后端已通过 Docker Maven 完成 `compile` 验证。
- 后端已通过 Docker 方式启动，并连接真实 MySQL。
- 以上 5 个接口均已拿到真实返回结果。
- 本轮执行过一次**本地开发 MySQL named volume 重建**，用于清理此前被错误参数打坏的初始化数据目录。

## 2026-04-15 补充：本地稳定启动链

后续又补了一轮本地启动链稳定化，目的是把之前的手工容器方式收成可重复执行的固定链路：

- 新增 `docker-compose.local.yml`
- 新增 `scripts/dev/up-local-stack.sh`
- 新增 `scripts/dev/down-local-stack.sh`

当前推荐的本地联调启动方式已经变成：

```bash
./scripts/dev/up-local-stack.sh
```

这条链路会：

- 自动清理旧的 `huacai-backend-proxy-live`
- 将旧 `huacai-mysql` 容器上的匿名 volume 迁移到命名卷 `1025_mysql-data`
- 用 Docker Compose 正式托管 `huacai-mysql + huacai-backend-test`
- 在 `http://127.0.0.1:18081/api/v1/health` 完成健康检查后再返回

已额外完成一次 `./scripts/dev/down-local-stack.sh -> ./scripts/dev/up-local-stack.sh` 回归验证，确认：

- MySQL 仍从命名卷 `1025_mysql-data` 恢复
- `huacai-backend-test` 可再次启动并恢复到 `healthy`
- 登录和数据库查询链路不依赖旧代理容器即可继续工作

因此，本地联调已经不再需要依赖“容器里手工后台启动 Spring Boot + 额外 nginx 反代”这条脆弱路径。

## 修改文件

- `docker-compose.mysql.yml`
- `backend/pom.xml`
- `backend/src/main/java/com/huacai/loan/controller/LoanController.java`
- `backend/src/main/java/com/huacai/config/SecurityConfig.java`
- `backend/src/main/java/com/huacai/config/MybatisMetaObjectHandler.java`
- `backend/src/main/java/com/huacai/system/entity/SysUserRole.java`
- `backend/src/main/java/com/huacai/security/JwtTokenService.java`
- `backend/src/main/java/com/huacai/common/exception/GlobalExceptionHandler.java`

## 修改说明

### 1. MySQL 容器修复

`docker-compose.mysql.yml` 做了 3 个最小修复：

- 删除 `mysql:8.4` 已不支持的 `--default-authentication-plugin=mysql_native_password`
- 增加 `MYSQL_ROOT_HOST: "%"`，确保后端容器可通过 Docker 网络连入
- 健康检查改为 `mysqladmin ping -h127.0.0.1 -uroot -proot --silent`

### 2. 后端编译修复

- `backend/src/main/java/com/huacai/loan/controller/LoanController.java`
  - 补充 `PageQuery` 导入，修复显式编译错误
- `backend/pom.xml`
  - 增加 `mybatis-plus-jsqlparser`，补齐 `PaginationInnerInterceptor` 所需依赖

### 3. 启动链路修复

- `backend/src/main/java/com/huacai/config/SecurityConfig.java`
  - 将 `JwtAuthenticationFilter` 从构造注入改为 `securityFilterChain(...)` 方法参数注入，打破循环依赖
- `backend/src/main/java/com/huacai/config/MybatisMetaObjectHandler.java`
  - 新增 MyBatis-Plus 自动填充处理器，统一填充 `createdAt`、`updatedAt`、`deletedFlag`
- `backend/src/main/java/com/huacai/system/entity/SysUserRole.java`
  - 为 `createdAt` 增加 `FieldFill.INSERT`
- `backend/src/main/java/com/huacai/security/JwtTokenService.java`
  - 默认 JWT secret 不是 Base64 时，补充 `DecodingException` 兜底，回退到明文字节
- `backend/src/main/java/com/huacai/common/exception/GlobalExceptionHandler.java`
  - 记录未处理异常堆栈，避免 500 只返回通用文案却无日志

## MySQL 重建说明

因为旧 `mysql-data` volume 已被错误启动参数打成半初始化状态，日志明确出现：

- `Table 'mysql.plugin' doesn't exist`
- `Table 'mysql.user' doesn't exist`

所以本轮执行了**本地开发数据重建**：

```bash
docker compose -f docker-compose.mysql.yml down -v
docker compose -f docker-compose.mysql.yml up -d mysql
```

这是本机开发环境的数据重建，不是线上数据操作。

## 实际验证命令

### A. MySQL 启动与健康验证

```bash
docker compose -f docker-compose.mysql.yml up -d --force-recreate mysql
docker inspect --format '{{json .State.Health}}' huacai-mysql
docker run --rm --network 1025_default mysql:8.4 mysql -h huacai-mysql -uroot -proot -e "SELECT CURRENT_USER(); SHOW DATABASES; USE huacai_system; SHOW TABLES LIKE 'sys_user';"
```

实际结果：

- `huacai-mysql` 状态为 `healthy`
- `CURRENT_USER()` 返回 `root@%`
- `huacai_system` 已创建
- `sys_user` 表存在

### B. 后端编译验证

```bash
docker run --rm \
  -v "$PWD/backend":/workspace \
  -v "$HOME/.m2":/root/.m2 \
  -w /workspace \
  maven:3.9.11-eclipse-temurin-21 \
  mvn -DskipTests compile
```

实际结果：

```text
[INFO] BUILD SUCCESS
```

### C. 后端启动验证

```bash
docker run --rm --name huacai-backend-test \
  --network 1025_default \
  -e HUACAI_DB_URL='jdbc:mysql://huacai-mysql:3306/huacai_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false' \
  -e HUACAI_DB_USERNAME=root \
  -e HUACAI_DB_PASSWORD=root \
  -v "$PWD/backend":/workspace \
  -v "$HOME/.m2":/root/.m2 \
  -w /workspace \
  maven:3.9.11-eclipse-temurin-21 \
  mvn -Dmaven.test.skip=true spring-boot:run
```

实际结果：

```text
Tomcat started on port 8080 (http)
Started HuacaiApplication
HikariPool-1 - Start completed.
```

### D. 接口验证

说明：

- 宿主机 `127.0.0.1:8080` 被本地 Python SimpleHTTP 占用，所以接口验证走 Docker 网络直连 `huacai-backend-test:8080`
- 以下请求都命中真实 Spring Boot + 真实 MySQL

#### 1. 登录

```bash
docker run --rm --network 1025_default curlimages/curl:8.12.1 \
  -s -i -X POST http://huacai-backend-test:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

实际结果：

```text
HTTP/1.1 200
{"code":0,"message":"success","data":{"token":"<省略>","tokenType":"Bearer","expiresIn":7200,"userInfo":{"id":1,"username":"admin","realName":"管理员","orgId":1,"orgName":"总部","roles":["ADMIN"],"permissions":["*:*:*"]}},"traceId":"todo-trace-id"}
```

#### 2. 工作台概览

```bash
docker run --rm --network 1025_default curlimages/curl:8.12.1 \
  -s -i http://huacai-backend-test:8080/api/v1/workbench/overview \
  -H 'Authorization: Bearer <login-token>'
```

实际结果：

```text
HTTP/1.1 200
{"code":0,"message":"success","data":{"metricCards":[{"title":"客户总数","value":"0"},...],"focusRows":[],"todoItems":[...],"reminderItems":[...]},"traceId":"todo-trace-id"}
```

#### 3. 客户列表

```bash
docker run --rm --network 1025_default curlimages/curl:8.12.1 \
  -s -i 'http://huacai-backend-test:8080/api/v1/customers?pageNum=1&pageSize=20' \
  -H 'Authorization: Bearer <login-token>'
```

实际结果：

```text
HTTP/1.1 200
{"code":0,"message":"success","data":{"records":[],"total":0,"pageNum":1,"pageSize":20},"traceId":"todo-trace-id"}
```

#### 4. 借贷单列表

```bash
docker run --rm --network 1025_default curlimages/curl:8.12.1 \
  -s -i 'http://huacai-backend-test:8080/api/v1/loan-orders?pageNum=1&pageSize=20' \
  -H 'Authorization: Bearer <login-token>'
```

实际结果：

```text
HTTP/1.1 200
{"code":0,"message":"success","data":{"records":[],"total":0,"pageNum":1,"pageSize":20},"traceId":"todo-trace-id"}
```

#### 5. 还款列表

```bash
docker run --rm --network 1025_default curlimages/curl:8.12.1 \
  -s -i 'http://huacai-backend-test:8080/api/v1/repayments?pageNum=1&pageSize=20' \
  -H 'Authorization: Bearer <login-token>'
```

实际结果：

```text
HTTP/1.1 200
{"code":0,"message":"success","data":{"records":[],"total":0,"pageNum":1,"pageSize":20},"traceId":"todo-trace-id"}
```

## 本轮踩到的真实问题

按出现顺序记录：

1. `mysql:8.4` 不支持 `--default-authentication-plugin=mysql_native_password`
2. 旧 `mysql-data` volume 被打坏，导致系统表缺失，容器持续重启
3. `LoanController` 缺少 `PageQuery` 导入
4. 缺少 `mybatis-plus-jsqlparser`，`PaginationInnerInterceptor` 无法编译
5. `SecurityConfig` / `JwtAuthenticationFilter` / `AuthServiceImpl` 形成循环依赖
6. 审计时间字段未自动填充，`DataInitializer` 首次插入时把 `created_at`/`updated_at` 写成 `null`
7. `JwtTokenService` 对默认 secret 的 Base64 兜底异常类型捕获不完整，登录直接 500

## 剩余阻塞与注意事项

### 非阻塞但需要记住

1. 宿主机 `8080` 当前被本地 Python 进程占用
   - 如果下次要从宿主机直接 `curl`，先释放端口或把后端映射到其他端口
2. `spring-boot:run` 需要加 `-Dmaven.test.skip=true`
   - 当前 `pom.xml` 没有 `spring-boot-starter-test`
   - 所以 `spring-boot:run -DskipTests` 仍会在 `testCompile` 阶段失败
3. 当前真实 MySQL 里只有初始化的组织、角色、管理员账号
   - 客户、借贷单、还款接口已经验证通，但返回空列表是预期结果

### 后续建议顺序

1. 增加最小测试依赖，让 `spring-boot:run` 和测试链路更顺
2. 补一组最小真实样例数据，继续验证新增/编辑/联动计算接口
3. 如果要从宿主机联调前端，先统一解决本地 `8080` 端口冲突
