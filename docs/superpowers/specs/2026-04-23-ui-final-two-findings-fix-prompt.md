# UI 改版最终两项 finding 强制修复提示词

你是负责前端 UI 改版收尾的 agent。  
本轮只允许处理下面两个 review finding，不要扩大范围。

必须先读相关文件，再修改。不要只口头说明。

---

## Finding 1：删除调试残留文件

文件：

- `frontend/src/test.txt`

问题：

该文件仍然存在，内容是：

```css
.test-class { color: red; }
```

这是临时调试文件，不属于 UI 改版范围，会污染提交。

### 必须执行

1. 第一件事就是确认 `frontend/src/test.txt` 是否存在。
2. 如果存在，必须删除它。
3. 删除后再次用命令确认文件不存在。
4. 不要把它改成空文件，不要移动到别处，必须删除。

验收命令：

```bash
test ! -e frontend/src/test.txt
```

该命令必须通过。

---

## Finding 2：修复折叠态隐藏分组图标

文件：

- `frontend/src/layout/AppLayout.vue`

当前错误代码：

```css
.sidebar.is-collapsed .nav-group__label > span:first-child {
  /* Keep icon, hide text label */
  display: none;
}
```

问题：

`.nav-group__label` 内第一个 `span` 是 `.nav-group__icon`，这段 CSS 实际隐藏了图标。  
折叠态下分组按钮会变成空按钮或缺少图标，导航不可用。

### 必须修复

1. 折叠态必须保留 `.nav-group__icon` 可见。
2. 只隐藏分组文字，不隐藏图标。
3. 不允许空按钮。
4. 折叠态下当前路由 active 状态仍可识别。
5. 子菜单 icon-only 链接继续可见、可点击。

### 推荐修改

把错误选择器替换为类似下面的写法：

```css
.sidebar.is-collapsed .nav-group__label > span:not(.nav-group__icon) {
  display: none;
}
```

或者给分组文字单独加 class，例如：

```vue
<span class="nav-group__text">{{ group.title }}</span>
```

然后写：

```css
.sidebar.is-collapsed .nav-group__text {
  display: none;
}
```

优先推荐加 `.nav-group__text`，语义更清楚，后续不容易再次写错。

---

## 禁止事项

- 不要改图标体系。
- 不要改菜单配置。
- 不要改表格列宽。
- 不要改商机 page intro。
- 不要改业务逻辑。
- 不要新增任何临时文件。
- 不要改测试来绕过问题。

---

## 完成后必须验证

必须运行：

```bash
test ! -e frontend/src/test.txt
npm run build
npm run test:unit
```

完成后按下面格式汇报：

1. `frontend/src/test.txt` 是否已删除。
2. 折叠态分组图标如何保证不被隐藏。
3. `test ! -e frontend/src/test.txt` 结果。
4. `npm run build` 结果。
5. `npm run test:unit` 结果。
