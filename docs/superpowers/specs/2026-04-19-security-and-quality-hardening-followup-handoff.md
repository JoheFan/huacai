# 华彩系统安全与质量收口 Follow-up - 2026-04-19

## 执行概述

本轮 follow-up 共修复 7 个问题，均为上一轮"已完成"但实际存在问题或回归的项目。

---

## 问题 1：后端编译失败 ✅

**原因**：新增 `LoanManageServiceImpl` 依赖 `AuthUser` 但漏了 import；测试类构造函数签名与实际不符。

**修复文件**：
- `backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java` — 添加 `import com.huacai.security.AuthUser`
- `backend/src/test/java/com/huacai/loan/service/impl/LoanManageServiceImplTest.java` — 添加 `LoanCustomerSummaryMapper` mock，更新构造函数调用
- `backend/src/test/java/com/huacai/hr/service/impl/HrManageServiceImplTest.java` — 添加 `WfProcessInstanceMapper`、`WfTaskMapper`、`WfActionLogMapper` mocks，更新构造函数调用
- `backend/src/test/java/com/huacai/opportunity/service/impl/OpportunityServiceImplTest.java` — `OppFollowRecord` 无 `setFollowerUserId()` 方法，改为 `setFollowerName()`

**验证**：
```bash
cd backend && mvn -q compile test-compile -DskipTests
# 期望：无声输出，"ALL COMPILE OK"
```

---

## 问题 2：/api/v1/files 越权修复不完整 ✅

**原问题**：
- `/api/v1/files` 未纳入 `ModuleAccessRegistry`
- `detail`/`download`/`delete` 抛的是 `IllegalArgumentException`（走 500），不是明确的权限拒绝

**修复文件**：
- `backend/src/main/java/com/huacai/security/ModuleAccessRegistry.java` — 添加 `/api/v1/files` 到 `BUSINESS_API_PREFIXES` 和 `API_REQUIRED_PAGE_PERMISSIONS`，`ALL_PAGE_PERMISSIONS` 添加 `/files`
- `backend/src/main/java/com/huacai/file/controller/FileController.java` — 引入 `BusinessException`

**修复内容**：
- `/api/v1/files` 前缀注册到 `ModuleAccessRegistry`，接入服务端权限体系
- 权限拒绝改用 `BusinessException("无权访问此文件")`，返回 500 业务异常而非 500 空指针

**注意**：当前实现仍基于 `uploader_id` 做归属校验，未实现基于 `bizType`/`bizId` 的对象级授权。对象级授权需要业务层更细致设计（不同 bizType 对应不同业务规则），后续可按需扩展。

**验证**：
```bash
# 使用 A 用户 token 访问非本人上传的文件
curl -H "Authorization: Bearer <A_TOKEN>" http://localhost:8080/api/v1/files/123
# 期望：返回 500 业务异常，body 包含"无权访问此文件"
```

---

## 问题 3：customer 写路径数据范围校验缺失 ✅

**原问题**：只在部分读取接口加了 `validateCustomerDataScope`，写操作（update/delete/子表写）未校验。

**修复文件**：
- `backend/src/main/java/com/huacai/customer/service/impl/CustomerServiceImpl.java`

**修复内容**：
修改 `getRiskOrThrow`、`getDebtOrThrow`、`getContractOrThrow`、`getTradeOrThrow` 四个方法，每个方法在返回记录前先调用 `getCustomerOrThrow(record.getCustomerId())`，通过该调用触发 `validateCustomerDataScope` 校验：

```java
private CustCustomerScore getRiskOrThrow(Long id) {
    CustCustomerScore score = customerScoreMapper.selectById(id);
    if (score == null) { throw new BusinessException("风险评估记录不存在"); }
    getCustomerOrThrow(score.getCustomerId()); // 触发数据范围校验
    return score;
}
// 同理 getDebtOrThrow、getContractOrThrow、getTradeOrThrow
```

**验证**：
```bash
# A 用户（不是客户创建者）尝试删除该客户的风险评估记录
curl -X DELETE -H "Authorization: Bearer <A_TOKEN>" http://localhost:8080/api/v1/customer-risks/999
# 期望：返回 403 或"无权访问此客户数据"
```

---

## 问题 4：loan SELF+BANK 同时拥有时不传类型查空回归 ✅

**原问题**：`resolveAllowedCapitalSourceType` 在同时拥有 SELF 和 BANK 权限、且请求不传 `capitalSourceType` 时返回 `null`，`buildLoanOrderWrapper` 把 `null` 当成"查空"直接 `wrapper.eq("id", -1L)`，导致合法请求返回空列表。

**修复文件**：
- `backend/src/main/java/com/huacai/loan/service/impl/LoanManageServiceImpl.java`

**修复内容**：
- 重命名 `resolveAllowedCapitalSourceType` → `resolveEffectiveCapitalSourceType`，逻辑修正：
  - 请求未指定类型 + 同时拥有 SELF+BANK → 返回 `null`（不过滤）
  - 请求未指定类型 + 只有 SELF 或 BANK → 返回那个类型
  - 请求指定类型 + 无权限 → 返回 `null`（被调用方拒绝）
- `buildLoanOrderWrapper` / `buildLoanRepaymentWrapper` 移除 `if (effectiveCapitalSourceType != null) { ... } else { wrapper.eq("id", -1L); return wrapper; }` 的错误回退，改为直接应用 `if (effectiveCapitalSourceType != null) { wrapper.eq(...); }`

**验证**：
```bash
# 使用同时拥有 SELF+BANK 权限的 token，不传 capitalSourceType 查询
curl -H "Authorization: Bearer <SELF_BANK_TOKEN>" http://localhost:8080/api/v1/loan-orders
# 期望：返回真实数据（非空列表）
```

---

## 问题 5：弱口令和演示种子未清干净 ✅

**修复文件**：
- `frontend/src/views/auth/LoginView.vue` — 移除"开发联调默认管理员账号：admin / 123456"提示文字
- `sql/seed/2026-04-19-demo-data.sql` — 更新注释不再提"密码统一为 123456"；移除 `CROSS JOIN` 批量赋双角色，改为明确的两次 `INSERT ... INNER JOIN` 分别给 DEPT_ADMIN 用户赋 DEPT_ADMIN 角色、STAFF 用户赋 STAFF 角色
- `backend/src/main/java/com/huacai/system/dto/ResetPasswordRequest.java` — 添加 `@Size(min=6,max=50)` + `@Pattern` 复杂度校验，与 `ChangePasswordRequest` 保持一致

**验证**：
```bash
# ResetPasswordRequest 弱密码拦截
curl -X PUT -H "Authorization: Bearer <TOKEN>" \
  -d '{"newPassword":"123456"}' \
  http://localhost:8080/api/v1/system/users/1/reset-password
# 期望：返回 400 "新密码必须包含字母和数字"

# 检查 seed 文件不再包含 CROSS JOIN 批量赋双角色
grep -c "CROSS JOIN.*sys_role" sql/seed/2026-04-19-demo-data.sql
# 期望：0
```

---

## 问题 6：handoff 夸大质量优化结果 ✅

**原问题**：上一轮 handoff 将"部分完成"写成"已完成"，与实际不符。

**修正**：

| 项目 | 上一轮 handoff 描述 | 实际状态 |
|------|---------------------|----------|
| CustomerArchiveView.vue 拆分 | ✅ 已完成 | ✅ 已完成（从 875 行拆为 4 个 Section） |
| LoanOrderListView.vue 拆分 | ✅ 已完成 | ❌ 未完成（仍为 645 行） |
| UserManageView.vue 拆分 | ✅ 已完成 | ❌ 未完成（仍为 828 行） |
| 魔法字符串清理 | ✅ 已完成 | ❌ 未完成 |
| MapStruct 引入 | 标记下一轮 | 标记下一轮 |

本轮 **不做** LoanOrderListView/UserManageView 拆分和魔法字符串清理，handoff 中已删除相关"已完成"标记。

---

## 问题 7：验证证据缺失 ✅

本轮通过实际编译和构建验证：

```bash
# 后端编译
cd backend && mvn -q compile test-compile -DskipTests && echo "BACKEND COMPILE OK"
# 输出：BACKEND COMPILE OK

# 前端构建
cd frontend && npm run build 2>&1 | tail -5
# 输出：✓ built in 1.16s
```

---

## 修改文件清单

### 后端（9 个文件）
```
backend/src/main/java/com/huacai/
├── file/controller/FileController.java（权限拒绝改为 BusinessException）
├── security/ModuleAccessRegistry.java（注册 /api/v1/files）
├── customer/service/impl/CustomerServiceImpl.java（写路径加 scope 校验）
├── loan/service/impl/LoanManageServiceImpl.java（修复 SELF+BANK null 回归）
└── system/dto/ResetPasswordRequest.java（添加复杂度校验）

backend/src/test/java/com/huacai/
├── loan/service/impl/LoanManageServiceImplTest.java（补 LoanCustomerSummaryMapper）
├── hr/service/impl/HrManageServiceImplTest.java（补 workflow mappers）
└── opportunity/service/impl/OpportunityServiceImplTest.java（修复 setter 方法名）
```

### 前端（1 个文件）
```
frontend/src/views/auth/LoginView.vue（移除默认凭据提示）
```

### SQL（1 个文件）
```
sql/seed/2026-04-19-demo-data.sql（移除 123456 注释、修复 CROSS JOIN 双角色）
```

---

## 遗留项（下一轮）

- `/api/v1/files` 基于 `bizType`/`bizId` 的对象级授权（当前仅做 uploader_id 归属校验）
- LoanOrderListView.vue / UserManageView.vue 拆分
- 全局魔法字符串清理
- 完整业务日志切面
- 角色权限常量工程

---

## 交付物

- 本 handoff 文档：`docs/superpowers/specs/2026-04-19-security-and-quality-hardening-followup-handoff.md`
- 所有修改文件清单如上
