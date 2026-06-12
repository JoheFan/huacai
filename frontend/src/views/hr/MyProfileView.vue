<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { myProfileApi, type EmployeeDetailVO } from '../../api/hr'

const loading = ref(false)
const detail = ref<EmployeeDetailVO | null>(null)

const fetchDetail = async () => {
  loading.value = true
  try {
    detail.value = await myProfileApi.get()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr: string | undefined | null) => {
  if (!dateStr) return '-'
  return dateStr.split('T')[0]
}

onMounted(() => {
  void fetchDetail()
})
</script>

<template>
  <section class="page-shell">
    <section class="card">
      <div class="card__section page-intro">
        <div class="page-intro__copy profile-intro">
          <div class="profile-intro__avatar">
            <el-avatar :size="80" />
          </div>
          <div class="profile-intro__body">
            <span class="page-intro__eyebrow">人事管理 / 我的信息</span>
            <h2 class="page-intro__title">{{ detail?.realName || '-' }}</h2>
            <p class="page-intro__desc">{{ detail?.orgName || '-' }} | {{ detail?.jobTitle || '-' }}</p>
            <p class="profile-intro__meta">{{ detail?.phone || '-' }} | {{ detail?.email || '-' }}</p>
          </div>
        </div>
      </div>

      <div class="card__section page-stack">
      <el-tabs class="page-tabs">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名">{{ detail?.realName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ detail?.gender === 'MALE' ? '男' : detail?.gender === 'FEMALE' ? '女' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ detail?.idCardNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="出生年月">{{ formatDate(detail?.birthday) }}</el-descriptions-item>
            <el-descriptions-item label="民族">{{ detail?.nation || '-' }}</el-descriptions-item>
            <el-descriptions-item label="政治面貌">{{ detail?.politicalStatus || '-' }}</el-descriptions-item>
            <el-descriptions-item label="籍贯">{{ detail?.hometown || '-' }}</el-descriptions-item>
            <el-descriptions-item label="婚姻状况">{{ detail?.maritalStatus === 'MARRIED' ? '已婚' : detail?.maritalStatus === 'UNMARRIED' ? '未婚' : detail?.maritalStatus === 'DIVORCED' ? '离异' : detail?.maritalStatus === 'WIDOWED' ? '丧偶' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ detail?.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ detail?.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="毕业学校" :span="2">{{ detail?.graduateSchool || '-' }}</el-descriptions-item>
            <el-descriptions-item label="最高学历">{{ detail?.highestEducation || '-' }}</el-descriptions-item>
            <el-descriptions-item label="家庭住址">{{ detail?.homeAddress || '-' }}</el-descriptions-item>
            <el-descriptions-item label="紧急联系人">{{ detail?.emergencyContact || '-' }}</el-descriptions-item>
            <el-descriptions-item label="紧急联系人电话">{{ detail?.emergencyContactPhone || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane v-if="detail?.jobInfo" label="任职信息" name="job">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="入职日期">{{ formatDate(detail.jobInfo?.joinDate) }}</el-descriptions-item>
            <el-descriptions-item label="转正日期">{{ formatDate(detail.jobInfo?.formalDate) }}</el-descriptions-item>
            <el-descriptions-item label="所属单位">{{ detail.jobInfo?.workUnit }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ detail.jobInfo?.department }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ detail.jobInfo?.position }}</el-descriptions-item>
            <el-descriptions-item label="职级">{{ detail.jobInfo?.rankLevel }}</el-descriptions-item>
          </el-descriptions>
          <p style="color: #999; margin-top: 16px">* 任职信息由管理员维护，如有疑问请联系人事部门</p>
        </el-tab-pane>

        <el-tab-pane v-if="detail?.certificates?.length" label="资格证书" name="cert">
          <el-table :data="detail.certificates" border stripe>
            <el-table-column prop="certificateName" label="证书名称" />
            <el-table-column prop="certificateNo" label="证书编号" />
            <el-table-column prop="issueDate" label="发证日期">
              <template #default="{ row }">
                {{ formatDate(row.issueDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="certificateType" label="证书类型" />
            <el-table-column prop="issueOrg" label="发证单位" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane v-if="detail?.assessments?.length" label="考核记录" name="assess">
          <el-table :data="detail.assessments" border stripe>
            <el-table-column prop="assessmentMonth" label="考核年月">
              <template #default="{ row }">
                {{ formatDate(row.assessmentMonth) }}
              </template>
            </el-table-column>
            <el-table-column prop="assessmentScore" label="考核分数" />
            <el-table-column prop="assessmentGrade" label="考核等级" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane v-if="detail?.growthRecords?.length" label="工作成长记录" name="growth">
          <el-table :data="detail.growthRecords" border stripe>
            <el-table-column prop="startDate" label="开始时间">
              <template #default="{ row }">
                {{ formatDate(row.startDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="endDate" label="结束时间">
              <template #default="{ row }">
                {{ formatDate(row.endDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="workName" label="工作/学习名称" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane v-if="detail?.familyMembers?.length" label="家庭成员" name="family">
          <el-table :data="detail.familyMembers" border stripe>
            <el-table-column prop="relation" label="称谓" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="birthday" label="出生年月">
              <template #default="{ row }">
                {{ formatDate(row.birthday) }}
              </template>
            </el-table-column>
            <el-table-column prop="politicalStatus" label="政治面貌" />
            <el-table-column prop="workUnitPosition" label="工作单位及职务" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
      </div>
    </section>
  </section>
</template>

<style scoped>
.profile-intro {
  display: flex;
  align-items: center;
  gap: 24px;
}

.profile-intro__avatar {
  flex-shrink: 0;
}

.profile-intro__body {
  flex: 1;
}

.profile-intro__meta {
  margin: 6px 0 0;
  color: var(--hc-text-muted);
  font-size: var(--hc-font-helper);
}
</style>
