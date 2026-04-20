<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createUser,
  deleteUser,
  fetchOrgTree,
  fetchPermissionCatalog,
  fetchRoleOptions,
  fetchUserDetail,
  fetchUserPage,
  fetchUserPermissionProfile,
  resetUserPassword,
  updateUser,
  updateUserPermissionProfile,
  updateUserStatus,
  type OrgRecord,
  type PermissionCatalogRecord,
  type RoleRecord,
  type UserPermissionProfile,
  type UserQuery,
  type UserRecord,
  type UserSavePayload,
  type UserUpdatePayload,
} from '../../../api/system'
import {
  buildPermissionPayload,
  buildScopedModuleItems,
  createPermissionDraft,
  formatIdentityType,
  summarizeRoleSource,
} from './userPermissionModel'

const loading = ref(false)
const submitting = ref(false)
const permissionLoading = ref(false)
const permissionSubmitting = ref(false)
const drawerVisible = ref(false)
const permissionDrawerVisible = ref(false)
const editingId = ref<number | null>(null)
const rows = ref<UserRecord[]>([])
const total = ref(0)
const orgTree = ref<OrgRecord[]>([])
const roleOptions = ref<RoleRecord[]>([])
const permissionCatalog = ref<PermissionCatalogRecord | null>(null)
const permissionProfile = ref<UserPermissionProfile | null>(null)
const permissionTarget = ref<UserRecord | null>(null)

const filters = reactive<UserQuery>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  accountStatus: '',
  employmentStatus: '',
})

const form = reactive<UserSavePayload & { password: string }>({
  username: '',
  password: '',
  employeeCode: '',
  realName: '',
  phone: '',
  email: '',
  orgId: undefined,
  jobTitle: '',
  employmentStatus: 'ON_JOB',
  accountStatus: 'ENABLE',
  remark: '',
  primaryRoleId: 0,
})

const permissionDraft = reactive({
  pagePermissions: [] as string[],
  buttonPermissions: [] as string[],
  dataScopes: {} as Record<string, string>,
})

const drawerTitle = ref('新增用户')

const currentRole = computed(
  () => roleOptions.value.find((item) => item.id === form.primaryRoleId) ?? null,
)
const currentIdentityLabel = computed(() => formatIdentityType(currentRole.value?.identityType))
const pageItems = computed(() => permissionCatalog.value?.pageItems ?? [])
const buttonItems = computed(() => permissionCatalog.value?.buttonItems ?? [])

const buttonGroups = computed(() => {
  const groups = new Map<string, { title: string; items: typeof buttonItems.value }>()
  for (const item of buttonItems.value) {
    const moduleName = pageItems.value.find((page) => page.moduleCode === item.moduleCode)?.permissionName ?? item.moduleCode
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
  buildScopedModuleItems(permissionDraft.pagePermissions, pageItems.value, permissionDraft.dataScopes),
)

const permissionSourceSummary = computed(() =>
  permissionProfile.value ? summarizeRoleSource(permissionProfile.value) : '',
)

const resetForm = () => {
  editingId.value = null
  drawerTitle.value = '新增用户'
  form.username = ''
  form.password = ''
  form.employeeCode = ''
  form.realName = ''
  form.phone = ''
  form.email = ''
  form.orgId = undefined
  form.jobTitle = ''
  form.employmentStatus = 'ON_JOB'
  form.accountStatus = 'ENABLE'
  form.remark = ''
  form.primaryRoleId = 0
}

const fillForm = (row: UserRecord) => {
  form.username = row.username
  form.password = ''
  form.employeeCode = row.employeeCode || ''
  form.realName = row.realName
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.orgId = row.orgId || undefined
  form.jobTitle = row.jobTitle || ''
  form.employmentStatus = row.employmentStatus || 'ON_JOB'
  form.accountStatus = row.accountStatus || 'ENABLE'
  form.remark = row.remark || ''
  form.primaryRoleId = row.primaryRoleId || 0
}

const assignPermissionDraft = (profile: UserPermissionProfile) => {
  const draft = createPermissionDraft(profile)
  permissionDraft.pagePermissions = draft.pagePermissions
  permissionDraft.buttonPermissions = draft.buttonPermissions
  permissionDraft.dataScopes = draft.dataScopes
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await fetchUserPage(filters)
    rows.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '用户列表加载失败')
  } finally {
    loading.value = false
  }
}

const loadOrgTree = async () => {
  orgTree.value = await fetchOrgTree()
}

const loadRoleOptions = async () => {
  roleOptions.value = await fetchRoleOptions()
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
  filters.accountStatus = ''
  filters.employmentStatus = ''
  filters.pageNum = 1
  await fetchList()
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const openEdit = async (row: UserRecord) => {
  resetForm()
  editingId.value = row.id
  drawerTitle.value = '编辑用户'
  try {
    fillForm(await fetchUserDetail(row.id))
  } catch {
    fillForm(row)
  }
  drawerVisible.value = true
}

const openPermissionDrawer = async (row: UserRecord) => {
  permissionTarget.value = row
  permissionDrawerVisible.value = true
  permissionLoading.value = true
  try {
    await ensurePermissionCatalog()
    const profile = await fetchUserPermissionProfile(row.id)
    permissionProfile.value = profile
    assignPermissionDraft(profile)
  } catch (error) {
    permissionDrawerVisible.value = false
    ElMessage.error(error instanceof Error ? error.message : '权限配置加载失败')
  } finally {
    permissionLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.username.trim()) {
    ElMessage.warning('请输入登录账号')
    return
  }
  if (!editingId.value && !form.password.trim()) {
    ElMessage.warning('请输入登录密码')
    return
  }
  if (!form.realName.trim()) {
    ElMessage.warning('请输入真实姓名')
    return
  }
  if (!form.primaryRoleId) {
    ElMessage.warning('请选择主角色')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      const updatePayload: UserUpdatePayload = {
        username: form.username.trim(),
        employeeCode: form.employeeCode,
        realName: form.realName.trim(),
        phone: form.phone,
        email: form.email,
        orgId: form.orgId,
        jobTitle: form.jobTitle,
        employmentStatus: form.employmentStatus,
        accountStatus: form.accountStatus,
        remark: form.remark,
        primaryRoleId: form.primaryRoleId,
      }
      await updateUser(editingId.value, updatePayload)
      ElMessage.success('用户已更新')
    } else {
      const createPayload: UserSavePayload = {
        username: form.username.trim(),
        password: form.password,
        employeeCode: form.employeeCode,
        realName: form.realName.trim(),
        phone: form.phone,
        email: form.email,
        orgId: form.orgId,
        jobTitle: form.jobTitle,
        employmentStatus: form.employmentStatus,
        accountStatus: form.accountStatus,
        remark: form.remark,
        primaryRoleId: form.primaryRoleId,
      }
      await createUser(createPayload)
      ElMessage.success('用户已创建')
    }
    drawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '用户保存失败')
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
    const payload = buildPermissionPayload(
      permissionDraft.pagePermissions,
      permissionDraft.buttonPermissions,
      permissionDraft.dataScopes,
      permissionCatalog.value,
    )
    await updateUserPermissionProfile(permissionTarget.value.id, payload)
    ElMessage.success('权限配置已更新')
    permissionDrawerVisible.value = false
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '权限配置保存失败')
  } finally {
    permissionSubmitting.value = false
  }
}

const handleDelete = async (row: UserRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '确认删除', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('用户已删除')
    await fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handleStatusChange = async (row: UserRecord, status: string) => {
  try {
    await updateUserStatus(row.id, status)
    ElMessage.success('状态已更新')
    await fetchList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '状态更新失败')
  }
}

const handleResetPassword = async (row: UserRecord) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新密码', '重置密码', {
      inputType: 'password',
      inputPattern: /^.{6,}$/,
      inputErrorMessage: '密码至少6位',
    })
    await resetUserPassword(row.id, value)
    ElMessage.success('密码已重置')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '重置失败')
    }
  }
}

const handlePageChange = async (page: number) => {
  filters.pageNum = page
  await fetchList()
}

const setModuleScope = (moduleCode: string, scopeType: string) => {
  permissionDraft.dataScopes = {
    ...permissionDraft.dataScopes,
    [moduleCode]: scopeType,
  }
}

const isInheritedPage = (permissionCode: string) =>
  Boolean(permissionProfile.value?.rolePagePermissions.includes(permissionCode))

const isInheritedButton = (permissionCode: string) =>
  Boolean(permissionProfile.value?.roleButtonPermissions.includes(permissionCode))

const isInheritedScope = (moduleCode: string, scopeType: string) =>
  permissionProfile.value?.roleDataScopes?.[moduleCode] === scopeType

onMounted(async () => {
  await Promise.all([fetchList(), loadOrgTree(), loadRoleOptions()])
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__filters">
          <el-input v-model="filters.keyword" placeholder="账号 / 姓名 / 手机号" clearable />
          <el-select v-model="filters.accountStatus" placeholder="账号状态" clearable>
            <el-option label="启用" value="ENABLE" />
            <el-option label="停用" value="DISABLE" />
          </el-select>
          <el-select v-model="filters.employmentStatus" placeholder="任职状态" clearable>
            <el-option label="在职" value="ON_JOB" />
            <el-option label="离职" value="OFF_JOB" />
          </el-select>
        </div>
        <div class="list-toolbar__actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button plain @click="handleReset">重置</el-button>
          <el-button type="primary" plain @click="openCreate">新增用户</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-table v-loading="loading" :data="rows" table-layout="fixed">
          <el-table-column prop="employeeCode" label="员工编号" min-width="120" />
          <el-table-column prop="realName" label="姓名" min-width="100" />
          <el-table-column prop="phone" label="联系电话" min-width="130" />
          <el-table-column prop="orgName" label="部门" min-width="140" />
          <el-table-column prop="permissionSummary" label="权限状态" min-width="140" />
          <el-table-column prop="employmentStatus" label="任职状态" min-width="100">
            <template #default="{ row }">
              {{ row.employmentStatus === 'OFF_JOB' ? '离职' : '在职' }}
            </template>
          </el-table-column>
          <el-table-column prop="accountStatus" label="账号状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.accountStatus === 'ENABLE' ? 'success' : 'danger'">
                {{ row.accountStatus === 'ENABLE' ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button text type="primary" @click="openPermissionDrawer(row)">权限</el-button>
              <el-button
                text
                :type="row.accountStatus === 'ENABLE' ? 'warning' : 'success'"
                @click="handleStatusChange(row, row.accountStatus === 'ENABLE' ? 'DISABLE' : 'ENABLE')"
              >
                {{ row.accountStatus === 'ENABLE' ? '停用' : '启用' }}
              </el-button>
              <el-button text type="primary" @click="handleResetPassword(row)">重置密码</el-button>
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

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="560px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <div class="form-grid">
            <el-form-item label="登录账号" required>
              <el-input v-model="form.username" placeholder="请输入登录账号" />
            </el-form-item>
            <el-form-item label="登录密码" :required="!editingId">
              <el-input
                v-model="form.password"
                type="password"
                :placeholder="editingId ? '留空则不修改' : '请输入登录密码'"
                show-password
              />
            </el-form-item>
            <el-form-item label="工号">
              <el-input v-model="form.employeeCode" placeholder="请输入工号" />
            </el-form-item>
            <el-form-item label="真实姓名" required>
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="所属组织">
              <el-tree-select
                v-model="form.orgId"
                :data="orgTree"
                :props="{ children: 'children', label: 'orgName', value: 'id' }"
                placeholder="请选择所属组织"
                clearable
                check-strictly
              />
            </el-form-item>
            <el-form-item label="主角色" required>
              <el-select v-model="form.primaryRoleId" placeholder="请选择主角色">
                <el-option
                  v-for="role in roleOptions"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="角色身份">
              <el-input :model-value="currentIdentityLabel" readonly />
            </el-form-item>
            <el-form-item label="岗位">
              <el-input v-model="form.jobTitle" placeholder="请输入岗位" />
            </el-form-item>
            <el-form-item label="任职状态">
              <el-select v-model="form.employmentStatus" placeholder="请选择">
                <el-option label="在职" value="ON_JOB" />
                <el-option label="离职" value="OFF_JOB" />
              </el-select>
            </el-form-item>
            <el-form-item label="账号状态">
              <el-select v-model="form.accountStatus" placeholder="请选择">
                <el-option label="启用" value="ENABLE" />
                <el-option label="停用" value="DISABLE" />
              </el-select>
            </el-form-item>
            <el-form-item label="备注" class="form-grid__full">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </div>
        </el-form>

        <div class="drawer-actions">
          <el-button plain @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
        </div>
      </div>
    </el-drawer>

    <el-drawer v-model="permissionDrawerVisible" title="权限配置" size="720px">
      <div v-loading="permissionLoading" class="drawer-shell">
        <div v-if="permissionProfile" class="permission-shell">
          <section class="permission-summary">
            <div>
              <p class="permission-summary__eyebrow">当前用户</p>
              <h3 class="permission-summary__title">{{ permissionTarget?.realName || permissionTarget?.username }}</h3>
              <p class="permission-summary__desc">{{ permissionSourceSummary }}</p>
            </div>
            <el-tag>{{ formatIdentityType(permissionProfile.identityType) }}</el-tag>
          </section>

          <section class="permission-section">
            <div class="permission-section__header">
              <div>
                <h4>页面访问权限</h4>
                <p>菜单显示会跟随这里的页面权限自动推导。</p>
              </div>
            </div>
            <el-checkbox-group v-model="permissionDraft.pagePermissions" class="permission-grid">
              <label
                v-for="item in pageItems"
                :key="item.permissionCode"
                class="permission-card"
              >
                <el-checkbox :value="item.permissionCode">
                  <span class="permission-card__title">{{ item.permissionName }}</span>
                </el-checkbox>
                <span class="permission-card__code">{{ item.permissionCode }}</span>
                <el-tag v-if="isInheritedPage(item.permissionCode)" size="small" effect="plain">主角色默认</el-tag>
              </label>
            </el-checkbox-group>
          </section>

          <section class="permission-section">
            <div class="permission-section__header">
              <div>
                <h4>按钮权限</h4>
                <p>按钮权限只影响提交动作，后端会继续二次鉴权。</p>
              </div>
            </div>
            <div class="permission-groups">
              <section
                v-for="group in buttonGroups"
                :key="group.moduleCode"
                class="permission-group"
              >
                <h5>{{ group.title }}</h5>
                <el-checkbox-group v-model="permissionDraft.buttonPermissions" class="permission-grid permission-grid--compact">
                  <label
                    v-for="item in group.items"
                    :key="item.permissionCode"
                    class="permission-card permission-card--compact"
                  >
                    <el-checkbox :value="item.permissionCode">
                      <span class="permission-card__title">{{ item.permissionName }}</span>
                    </el-checkbox>
                    <el-tag v-if="isInheritedButton(item.permissionCode)" size="small" effect="plain">主角色默认</el-tag>
                  </label>
                </el-checkbox-group>
              </section>
            </div>
          </section>

          <section class="permission-section">
            <div class="permission-section__header">
              <div>
                <h4>数据权限</h4>
                <p>只对已开放页面的业务模块生效。</p>
              </div>
            </div>
            <div class="scope-grid">
              <section
                v-for="item in scopedModuleItems"
                :key="item.moduleCode"
                class="scope-card"
              >
                <div class="scope-card__header">
                  <strong>{{ item.moduleName }}</strong>
                  <el-tag
                    v-if="isInheritedScope(item.moduleCode, item.scopeType)"
                    size="small"
                    effect="plain"
                  >
                    主角色默认
                  </el-tag>
                </div>
                <el-select
                  :model-value="item.scopeType"
                  @update:model-value="setModuleScope(item.moduleCode, $event)"
                >
                  <el-option label="全部数据" value="ALL" />
                  <el-option label="本部门及下级" value="ORG_AND_SUB" />
                  <el-option label="本部门数据" value="ORG" />
                  <el-option label="仅本人数据" value="SELF" />
                </el-select>
              </section>
              <el-empty
                v-if="scopedModuleItems.length === 0"
                description="先勾选业务页面权限，再配置对应的数据权限。"
              />
            </div>
          </section>
        </div>

        <div class="drawer-actions">
          <el-button plain @click="permissionDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="permissionSubmitting" @click="handlePermissionSubmit">
            保存权限
          </el-button>
        </div>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.list-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.list-toolbar__filters,
.list-toolbar__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.list-toolbar__filters {
  flex: 1 1 680px;
}

.list-toolbar__filters :deep(.el-input),
.list-toolbar__filters :deep(.el-select) {
  width: 180px;
}

.list-pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 18px;
}

.drawer-shell {
  display: flex;
  height: 100%;
  flex-direction: column;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.form-grid__full {
  grid-column: 1 / -1;
}

.drawer-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
}

.permission-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.permission-summary {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(246, 249, 255, 0.95), #fff);
}

.permission-summary__eyebrow {
  margin: 0 0 6px;
  color: var(--hc-text-soft);
  font-size: 12px;
  font-weight: 600;
}

.permission-summary__title {
  margin: 0;
  font-size: 20px;
}

.permission-summary__desc {
  margin: 6px 0 0;
  color: var(--hc-text-soft);
  line-height: 1.6;
}

.permission-section {
  padding: 16px 18px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 18px;
  background: #fff;
}

.permission-section__header h4,
.permission-group h5 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
}

.permission-section__header p {
  margin: 6px 0 0;
  color: var(--hc-text-soft);
  line-height: 1.6;
}

.permission-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.permission-grid--compact {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.permission-card {
  display: flex;
  min-height: 88px;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
  padding: 14px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.7);
}

.permission-card--compact {
  min-height: 72px;
}

.permission-card__title {
  font-weight: 600;
}

.permission-card__code {
  color: var(--hc-text-soft);
  font-size: 12px;
}

.permission-groups {
  display: grid;
  gap: 16px;
  margin-top: 14px;
}

.permission-group {
  padding-top: 4px;
}

.scope-grid {
  display: grid;
  gap: 12px;
  margin-top: 14px;
}

.scope-card {
  padding: 14px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.7);
}

.scope-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .form-grid,
  .permission-grid,
  .permission-grid--compact {
    grid-template-columns: 1fr;
  }

  .permission-summary {
    flex-direction: column;
  }
}
</style>
