<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createRole,
  deleteRole,
  fetchPermissionCatalog,
  fetchRoleDetail,
  fetchRolePage,
  fetchRolePermissionProfile,
  updateRole,
  updateRolePermissionProfile,
  updateRoleStatus,
  type PermissionCatalogRecord,
  type RolePermissionProfile,
  type RoleQuery,
  type RoleRecord,
  type RoleSavePayload,
  type RoleUpdatePayload,
} from '../../../api/system'
import { formatIdentityType } from '../user/userPermissionModel'
import {
  buildRolePermissionPayload,
  buildRoleScopedModuleItems,
  createRolePermissionDraft,
  summarizeRolePermission,
} from './rolePermissionModel'

const loading = ref(false)
const submitting = ref(false)
const permissionLoading = ref(false)
const permissionSubmitting = ref(false)
const drawerVisible = ref(false)
const permissionDrawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<RoleRecord[]>([])
const total = ref(0)
const permissionCatalog = ref<PermissionCatalogRecord | null>(null)
const permissionProfile = ref<RolePermissionProfile | null>(null)
const permissionTarget = ref<RoleRecord | null>(null)

const filters = reactive<RoleQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: '',
})

const form = reactive<RoleSavePayload>({
  roleCode: '',
  roleName: '',
  identityType: 'NORMAL_USER',
  status: 'ENABLE',
  remark: '',
})

const permissionDraft = reactive({
  pagePermissions: [] as string[],
  buttonPermissions: [] as string[],
  dataScopes: {} as Record<string, string>,
})

const drawerTitle = ref('新增角色')
const pageItems = computed(() => permissionCatalog.value?.pageItems ?? [])
const buttonItems = computed(() => permissionCatalog.value?.buttonItems ?? [])

const buttonGroups = computed(() => {
  const groups = new Map<string, { title: string; items: typeof buttonItems.value }>()
  for (const item of buttonItems.value) {
    const moduleName =
      pageItems.value.find((page) => page.moduleCode === item.moduleCode)?.permissionName ?? item.moduleCode
    const existing = groups.get(item.moduleCode) ?? { title: moduleName, items: [] }
    existing.items.push(item)
    groups.set(item.moduleCode, existing)
  }
  return [...groups.entries()].map(([moduleCode, group]) => ({
    moduleCode,
    title: group.title,
    items: group.items,
  }))
})

const scopedModuleItems = computed(() =>
  buildRoleScopedModuleItems(permissionDraft.pagePermissions, pageItems.value, permissionDraft.dataScopes),
)

const permissionSourceSummary = computed(() =>
  permissionProfile.value ? summarizeRolePermission(permissionProfile.value) : '',
)

const resetForm = () => {
  editingId.value = null
  drawerTitle.value = '新增角色'
  form.roleCode = ''
  form.roleName = ''
  form.identityType = 'NORMAL_USER'
  form.status = 'ENABLE'
  form.remark = ''
}

const fillForm = (row: RoleRecord) => {
  form.roleCode = row.roleCode
  form.roleName = row.roleName
  form.identityType = row.identityType || 'NORMAL_USER'
  form.status = row.status || 'ENABLE'
  form.remark = row.remark || ''
}

const assignPermissionDraft = (profile: RolePermissionProfile) => {
  const draft = createRolePermissionDraft(profile)
  permissionDraft.pagePermissions = draft.pagePermissions
  permissionDraft.buttonPermissions = draft.buttonPermissions
  permissionDraft.dataScopes = draft.dataScopes
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchRolePage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '角色列表加载失败')
  } finally {
    loading.value = false
  }
}

const ensurePermissionCatalog = async () => {
  if (!permissionCatalog.value) {
    permissionCatalog.value = await fetchPermissionCatalog()
  }
}

const handleSearch = async () => {
  filters.pageNum = 1
  await fetchList()
}

const handleReset = async () => {
  filters.keyword = ''
  filters.status = ''
  filters.pageNum = 1
  await fetchList()
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = async (row: RoleRecord) => {
  resetForm()
  editingId.value = row.id
  drawerTitle.value = '编辑角色'
  try {
    fillForm(await fetchRoleDetail(row.id))
  } catch {
    fillForm(row)
  }
  drawerVisible.value = true
}

const openPermissionDrawer = async (row: RoleRecord) => {
  permissionTarget.value = row
  permissionDrawerVisible.value = true
  permissionLoading.value = true
  try {
    await ensurePermissionCatalog()
    const profile = await fetchRolePermissionProfile(row.id)
    permissionProfile.value = profile
    assignPermissionDraft(profile)
  } catch (error) {
    permissionDrawerVisible.value = false
    ElMessage.error(error instanceof Error ? error.message : '角色权限加载失败')
  } finally {
    permissionLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.roleCode.trim()) {
    ElMessage.warning('请输入角色编码')
    return
  }
  if (!form.roleName.trim()) {
    ElMessage.warning('请输入角色名称')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      const payload: RoleUpdatePayload = {
        roleCode: form.roleCode.trim(),
        roleName: form.roleName.trim(),
        identityType: form.identityType,
        status: form.status,
        remark: form.remark,
      }
      await updateRole(editingId.value, payload)
      ElMessage.success('角色已更新')
    } else {
      await createRole({
        roleCode: form.roleCode.trim(),
        roleName: form.roleName.trim(),
        identityType: form.identityType,
        status: form.status,
        remark: form.remark,
      })
      ElMessage.success('角色已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '角色保存失败')
  } finally {
    submitting.value = false
  }
}

const handlePermissionSubmit = async () => {
  if (!permissionTarget.value || !permissionCatalog.value) {
    return
  }
  permissionSubmitting.value = true
  try {
    await updateRolePermissionProfile(
      permissionTarget.value.id,
      buildRolePermissionPayload(
        permissionDraft.pagePermissions,
        permissionDraft.buttonPermissions,
        permissionDraft.dataScopes,
        permissionCatalog.value,
      ),
    )
    ElMessage.success('角色权限已更新')
    permissionDrawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '角色权限保存失败')
  } finally {
    permissionSubmitting.value = false
  }
}

const handleDelete = async (row: RoleRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '确认删除', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('角色已删除')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handleStatusChange = async (row: RoleRecord, status: string) => {
  try {
    await updateRoleStatus(row.id, status)
    ElMessage.success('状态已更新')
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '状态更新失败')
  }
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

onMounted(fetchList)
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy">
          <span class="page-intro__eyebrow">系统管理</span>
          <h2 class="page-intro__title">角色管理</h2>
          <p class="page-intro__desc">统一维护角色身份、默认权限和数据范围策略，支持按角色维度查看状态、编辑信息和配置权限。</p>
        </div>

        <div class="page-intro__actions">
          <el-button type="primary" @click="openCreate">新增角色</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="角色编码 / 角色名称" clearable />
          <el-select v-model="filters.status" placeholder="状态" clearable>
            <el-option label="启用" value="ENABLE" />
            <el-option label="停用" value="DISABLE" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="roleCode" label="角色编码" min-width="140" />
          <el-table-column prop="roleName" label="角色名称" min-width="150" />
          <el-table-column label="角色身份" min-width="130">
            <template #default="{ row }">
              {{ formatIdentityType(row.identityType) }}
            </template>
          </el-table-column>
          <el-table-column prop="permissionSummary" label="默认数据权限" min-width="160" />
          <el-table-column prop="status" label="状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ENABLE' ? 'success' : 'danger'">
                {{ row.status === 'ENABLE' ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" min-width="170" />
          <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button text type="primary" @click="openPermissionDrawer(row)">权限</el-button>
              <el-button
                text
                :type="row.status === 'ENABLE' ? 'warning' : 'success'"
                @click="handleStatusChange(row, row.status === 'ENABLE' ? 'DISABLE' : 'ENABLE')"
              >
                {{ row.status === 'ENABLE' ? '停用' : '启用' }}
              </el-button>
              <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="list-pagination">
          <el-pagination
            layout="prev, pager, next"
            :total="total"
            :page-size="filters.pageSize"
            :current-page="filters.pageNum"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </section>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="480px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <el-form-item label="角色编码" required>
            <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
          </el-form-item>
          <el-form-item label="角色名称" required>
            <el-input v-model="form.roleName" placeholder="请输入角色名称" />
          </el-form-item>
          <el-form-item label="角色身份" required>
            <el-select v-model="form.identityType" placeholder="请选择角色身份">
              <el-option label="超级管理员" value="SUPER_ADMIN" />
              <el-option label="部门管理员" value="DEPT_ADMIN" />
              <el-option label="普通用户" value="NORMAL_USER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" required>
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="启用" value="ENABLE" />
              <el-option label="停用" value="DISABLE" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
        </el-form>

        <div class="drawer-actions">
          <el-button plain @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </div>
      </div>
    </el-drawer>

    <el-drawer v-model="permissionDrawerVisible" title="角色权限配置" size="720px">
      <div v-loading="permissionLoading" class="drawer-shell">
        <template v-if="permissionTarget && permissionProfile">
          <div class="permission-summary">
            <div class="permission-summary__meta">
              <span class="permission-summary__title">{{ permissionTarget.roleName }}</span>
              <el-tag>{{ formatIdentityType(permissionProfile.identityType) }}</el-tag>
            </div>
            <p class="permission-summary__desc">{{ permissionSourceSummary }}</p>
          </div>

          <el-form label-position="top" class="permission-form">
            <div class="permission-section">
              <div class="permission-section__header">
                <span>页面权限</span>
                <small>菜单显示会根据这里的页面权限自动推导。</small>
              </div>
              <el-checkbox-group v-model="permissionDraft.pagePermissions" class="permission-grid">
                <el-checkbox v-for="item in pageItems" :key="item.permissionCode" :label="item.permissionCode">
                  {{ item.permissionName }}
                </el-checkbox>
              </el-checkbox-group>
            </div>

            <div class="permission-section">
              <div class="permission-section__header">
                <span>按钮权限</span>
                <small>用于控制页面内新增、编辑、删除、配置等动作。</small>
              </div>
              <div class="permission-groups">
                <section v-for="group in buttonGroups" :key="group.moduleCode" class="permission-group">
                  <header>{{ group.title }}</header>
                  <el-checkbox-group v-model="permissionDraft.buttonPermissions" class="permission-grid">
                    <el-checkbox v-for="item in group.items" :key="item.permissionCode" :label="item.permissionCode">
                      {{ item.permissionName }}
                    </el-checkbox>
                  </el-checkbox-group>
                </section>
              </div>
            </div>

            <div class="permission-section">
              <div class="permission-section__header">
                <span>数据权限</span>
                <small>仅对具备页面权限的业务模块生效。「本部门」「本部门及下级」尚未启用，选择后按「仅本人」生效。</small>
              </div>
              <div class="permission-scope-list">
                <section v-for="item in scopedModuleItems" :key="item.moduleCode" class="permission-scope-row">
                  <div class="permission-scope-row__meta">
                    <span>{{ item.moduleName }}</span>
                    <small>{{ item.moduleCode }}</small>
                  </div>
                  <el-select v-model="permissionDraft.dataScopes[item.moduleCode]" placeholder="请选择数据范围">
                    <el-option label="全部数据" value="ALL" />
                    <el-option label="本部门及下级（未启用）" value="ORG_AND_SUB" disabled />
                    <el-option label="本部门数据（未启用）" value="ORG" disabled />
                    <el-option label="仅本人数据" value="SELF" />
                  </el-select>
                </section>
                <el-empty
                  v-if="scopedModuleItems.length === 0"
                  description="先勾选业务页面权限，再配置对应数据范围。"
                />
              </div>
            </div>
          </el-form>

          <div class="drawer-actions">
            <el-button plain @click="permissionDrawerVisible = false">取消</el-button>
            <el-button type="primary" :loading="permissionSubmitting" @click="handlePermissionSubmit">
              保存权限
            </el-button>
          </div>
        </template>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.permission-summary {
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 16px;
  background: var(--el-fill-color-extra-light);
}

.permission-summary__meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.permission-summary__title {
  font-size: 18px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.permission-summary__desc {
  margin: 10px 0 0;
  color: var(--el-text-color-secondary);
}

.permission-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.permission-section {
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 16px;
}

.permission-section__header {
  margin-bottom: 12px;
}

.permission-section__header span {
  display: block;
  font-size: 15px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.permission-section__header small {
  color: var(--el-text-color-secondary);
}

.permission-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px 16px;
}

.permission-groups {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.permission-group {
  padding-top: 12px;
  border-top: 1px dashed var(--el-border-color-lighter);
}

.permission-group:first-child {
  padding-top: 0;
  border-top: none;
}

.permission-group header {
  margin-bottom: 10px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.permission-scope-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.permission-scope-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 220px;
  gap: 16px;
  align-items: center;
}

.permission-scope-row__meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.permission-scope-row__meta span {
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.permission-scope-row__meta small {
  color: var(--el-text-color-secondary);
}
</style>
