<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  fetchOrgTree,
  createOrg,
  updateOrg,
  deleteOrg,
  updateOrgStatus,
  type OrgRecord,
  type OrgSavePayload,
} from '../../../api/system'

const loading = ref(false)
const submitting = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number | null>(null)
const treeData = ref<OrgRecord[]>([])

const form = ref<OrgSavePayload>({
  parentId: undefined,
  orgName: '',
  orgType: 'DEPT',
  sortNo: 0,
  status: 'ENABLE',
  remark: '',
})

const defaultProps = {
  children: 'children',
  label: 'orgName',
}

const formatOrgType = (orgType: string) => (orgType === 'COMPANY' ? '公司' : '部门')
const isEnabled = (status: string) => status === 'ENABLE'
const formatOrgStatus = (status: string) => (isEnabled(status) ? '启用' : '停用')

const loadTree = async () => {
  loading.value = true
  try {
    treeData.value = await fetchOrgTree()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '组织树加载失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    parentId: undefined,
    orgName: '',
    orgType: 'DEPT',
    sortNo: 0,
    status: 'ENABLE',
    remark: '',
  }
}

const openCreate = (parent?: OrgRecord) => {
  resetForm()
  if (parent) {
    form.value.parentId = parent.id
  }
  drawerVisible.value = true
}

const openEdit = (node: OrgRecord) => {
  resetForm()
  editingId.value = node.id
  form.value = {
    parentId: node.parentId || undefined,
    orgName: node.orgName,
    orgType: node.orgType,
    sortNo: node.sortNo,
    status: node.status,
    remark: node.remark || '',
  }
  drawerVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.orgName?.trim()) {
    ElMessage.warning('请输入组织名称')
    return
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await updateOrg(editingId.value, form.value)
      ElMessage.success('组织已更新')
    } else {
      await createOrg(form.value)
      ElMessage.success('组织已创建')
    }
    drawerVisible.value = false
    await loadTree()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '组织保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (node: OrgRecord) => {
  try {
    await ElMessageBox.confirm('确定要删除该组织吗？删除后不可恢复。', '确认删除', {
      type: 'warning',
    })
    await deleteOrg(node.id)
    ElMessage.success('组织已删除')
    await loadTree()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

const handleStatusChange = async (node: OrgRecord, status: string) => {
  try {
    await updateOrgStatus(node.id, status)
    ElMessage.success('状态已更新')
    await loadTree()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '状态更新失败')
  }
}

onMounted(loadTree)
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section list-toolbar">
        <div class="list-toolbar__actions">
          <el-button type="primary" plain @click="openCreate()">新增根组织</el-button>
        </div>
      </div>
    </section>

    <section class="card">
      <div class="card__section">
        <el-tree
          v-loading="loading"
          class="org-tree"
          :data="treeData"
          :props="defaultProps"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
        >
          <template #default="{ data }">
            <div class="tree-node">
              <div class="tree-node__content">
                <div class="tree-node__headline">
                  <div class="tree-node__title-group">
                    <span class="tree-node__label">{{ data.orgName }}</span>
                    <span class="tree-node__info">
                      <el-tag size="small" :type="data.orgType === 'COMPANY' ? 'primary' : 'info'">
                        {{ formatOrgType(data.orgType) }}
                      </el-tag>
                      <el-tag size="small" :type="isEnabled(data.status) ? 'success' : 'danger'">
                        {{ formatOrgStatus(data.status) }}
                      </el-tag>
                    </span>
                  </div>
                </div>
              </div>
              <div class="tree-node__actions">
                <el-button text type="primary" size="small" @click.stop="openCreate(data)">新增子组织</el-button>
                <el-button text type="primary" size="small" @click.stop="openEdit(data)">编辑</el-button>
                <el-button
                  text
                  :type="isEnabled(data.status) ? 'warning' : 'success'"
                  size="small"
                  @click.stop="handleStatusChange(data, isEnabled(data.status) ? 'DISABLE' : 'ENABLE')"
                >
                  {{ isEnabled(data.status) ? '停用' : '启用' }}
                </el-button>
                <el-button text type="danger" size="small" @click.stop="handleDelete(data)">删除</el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </section>

    <el-drawer v-model="drawerVisible" :title="editingId ? '编辑组织' : '新增组织'" size="480px">
      <div class="drawer-shell">
        <el-form label-position="top">
          <el-form-item label="上级部门">
            <el-tree-select
              v-model="form.parentId"
              :data="treeData"
              :props="{ children: 'children', label: 'orgName', value: 'id' }"
              placeholder="顶层组织"
              clearable
              check-strictly
            />
          </el-form-item>
          <el-form-item label="组织名称" required>
            <el-input v-model="form.orgName" placeholder="请输入组织名称" />
          </el-form-item>
          <el-form-item label="组织类型" required>
            <el-select v-model="form.orgType" placeholder="请选择组织类型">
              <el-option label="公司" value="COMPANY" />
              <el-option label="部门" value="DEPT" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序号">
            <el-input-number v-model="form.sortNo" :min="0" controls-position="right" />
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
  </section>
</template>

<style scoped>
.org-tree {
  background: transparent;
}

.org-tree :deep(.el-tree-node__content) {
  height: auto;
  min-height: 0;
  align-items: flex-start;
  padding: 6px 0;
}

.org-tree :deep(.el-tree-node__content > .el-tree-node__expand-icon) {
  margin-top: 8px;
  padding: 4px;
  border-radius: 999px;
  color: var(--hc-text-soft);
  transition: color 0.2s ease, background-color 0.2s ease;
}

.org-tree :deep(.el-tree-node__content:hover > .el-tree-node__expand-icon) {
  color: var(--hc-primary);
  background: var(--hc-primary-soft);
}

.org-tree :deep(.el-tree-node__children) {
  margin-left: 10px;
  padding-left: 16px;
  border-left: 1px solid rgba(148, 163, 184, 0.24);
}

.tree-node {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  width: 100%;
  padding: 8px 0;
}

.tree-node__content {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 6px;
}

.tree-node__headline {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.tree-node__title-group {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.tree-node__label {
  color: var(--hc-text);
  font-size: 15px;
  line-height: 1.4;
  font-weight: 600;
}

.tree-node__info {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tree-node__actions {
  display: flex;
  min-width: 240px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 2px 4px;
  padding-top: 2px;
}

.tree-node__actions :deep(.el-button) {
  min-height: 28px;
  padding: 2px 4px;
}

.tree-node__actions :deep(.el-button + .el-button) {
  margin-left: 0;
}

.drawer-shell {
  display: flex;
  height: 100%;
  flex-direction: column;
}

.drawer-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 12px;
}

@media (max-width: 1024px) {
  .tree-node__actions {
    min-width: 0;
    justify-content: flex-start;
    padding-top: 0;
  }
}

@media (max-width: 768px) {
  .org-tree :deep(.el-tree-node__children) {
    margin-left: 8px;
    padding-left: 14px;
  }

  .tree-node {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
