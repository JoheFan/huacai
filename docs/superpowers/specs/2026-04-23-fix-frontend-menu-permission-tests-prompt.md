# 修复前端单测剩余 3 个菜单/权限失败

你只处理 `npm run test:unit` 剩余的 3 个失败，不要改 UI 样式，不要改后端，不要改接口。

---

## 目标

让下面 3 个测试通过：

- `frontend/tests/customer-menu-structure.test.ts`
- `frontend/tests/hr-finance-entry-binding.test.ts`
- `frontend/tests/module-access-routing.test.ts`

---

## 必须先确认失败原因

运行：

```bash
cd frontend && npm run test:unit
```

然后只针对失败点修改。

---

## 修复方向

按测试代表真实业务要求处理，优先改业务配置，不要改测试。

### 1. `customer-menu-structure.test.ts`

检查：

- `frontend/src/router/menu.ts`

要求：

- 客户管理组第一个菜单项标题必须是 `客户管理`
- 不要再显示为 `客户档案页`

### 2. `hr-finance-entry-binding.test.ts`

检查：

- `frontend/src/router/menu.ts`

要求：

- 在人事相关菜单中补齐 `/hr/management-records`
- view 必须是 `hr/ManagementRecordView`

### 3. `module-access-routing.test.ts`

检查：

- `frontend/src/access/moduleAccess.ts`
- 必要时检查 `frontend/src/router/menu.ts`

要求：

- 普通用户可见菜单里必须包含 `/welcome`
- `getDefaultHomePath(staffUser)` 仍应返回 `/welcome`
- `canAccessPath('/welcome', staffUser)` 仍应为 `true`
- 优先在 `frontend/src/access/moduleAccess.ts` 或菜单配置中修复，不要改测试

---

## 禁止

- 不要修改测试文件
- 不要改 UI 改版相关 CSS
- 不要改接口、store、后端
- 不要新增依赖
- 不要扩大重构

---

## 验收

完成后必须运行：

```bash
cd frontend && npm run test:unit
cd frontend && npm run build
```

---

## 最终汇报

最终汇报必须包含：

1. 改了哪些文件
2. 3 个失败分别怎么修
3. `npm run test:unit` 是否全通过
4. `npm run build` 是否通过
