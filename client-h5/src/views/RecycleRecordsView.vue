<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import {
  getConditionOptions,
  getMaterialItems,
  getProjects,
  getRecycleRecords,
  getTeams,
  getUnitOptions,
  getWorkers,
  type ConditionOption,
  type MaterialItemOption,
  type ProjectOption,
  type RecycleRecordItem,
  type TeamOption,
  type UnitOption,
  type WorkerOption,
} from '@/api/recycle'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const records = ref<RecycleRecordItem[]>([])
const projects = ref<ProjectOption[]>([])
const teams = ref<TeamOption[]>([])
const workers = ref<WorkerOption[]>([])
const materials = ref<MaterialItemOption[]>([])
const conditions = ref<ConditionOption[]>([])
const units = ref<UnitOption[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const [recordRes, projectRes, teamRes, workerRes, materialRes, conditionRes, unitRes] = await Promise.all([
      getRecycleRecords(),
      getProjects(),
      getTeams(),
      getWorkers(),
      getMaterialItems(),
      getConditionOptions(),
      getUnitOptions(),
    ])
    records.value = recordRes.data
    projects.value = projectRes.data
    teams.value = teamRes.data
    workers.value = workerRes.data
    materials.value = materialRes.data
    conditions.value = conditionRes.data
    units.value = unitRes.data
  } catch (error) {
    showToast(error instanceof Error ? error.message : '加载记录失败')
  } finally {
    loading.value = false
  }
})

const highlightRecordId = computed(() => {
  const value = Number(route.query.recordId)
  return Number.isFinite(value) && value > 0 ? value : null
})

const highlightRecord = computed(() =>
  records.value.find((item) => item.id === highlightRecordId.value) ?? null,
)

const showSubmitBanner = computed(() => route.query.submitted === '1' && Boolean(highlightRecord.value))

function goRecycle() {
  router.push('/recycle')
}

function goHome() {
  router.push('/')
}

function getProjectName(projectId: number) {
  return projects.value.find((item) => item.id === projectId)?.name ?? `项目 #${projectId}`
}

function getTeamName(teamId: number) {
  return teams.value.find((item) => item.id === teamId)?.name ?? `班组 #${teamId}`
}

function getWorkerName(workerId: number) {
  return workers.value.find((item) => item.id === workerId)?.name ?? `工人 #${workerId}`
}

function getMaterialName(materialItemId: number) {
  return materials.value.find((item) => item.id === materialItemId)?.name ?? `材料 #${materialItemId}`
}

function getConditionName(conditionCode: string) {
  return conditions.value.find((item) => item.code === conditionCode)?.name ?? conditionCode
}

function getUnitName(unitCode: string) {
  return units.value.find((item) => item.code === unitCode)?.name ?? unitCode
}

function getStatusName(status: string) {
  return (
    {
      SUBMITTED: '待审核',
      APPROVED: '已通过',
      REJECTED: '已驳回',
      CANCELLED: '已作废',
    }[status] ?? status
  )
}

function formatDateTime(value?: string) {
  if (!value) {
    return '未记录'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return date.toLocaleString('zh-CN', { hour12: false })
}
</script>

<template>
  <MobileShell title="回收记录">
    <section v-if="showSubmitBanner && highlightRecord" class="success-banner">
      <p class="success-tag">刚刚已提交成功</p>
      <h2>你刚刚提交的登记已经在记录里了</h2>
      <p class="success-desc">
        你刚刚为 {{ getWorkerName(highlightRecord.workerId) }} 登记了
        {{ getMaterialName(highlightRecord.materialItemId) }}，当前状态为
        {{ getStatusName(highlightRecord.status) }}。
      </p>
      <div class="banner-actions">
        <button class="btn-primary" type="button" @click="goRecycle">继续添加</button>
        <button class="btn-ghost" type="button" @click="goHome">完成，返回首页</button>
      </div>
    </section>

    <div v-if="loading" class="empty">加载中...</div>
    <div v-else-if="records.length === 0" class="empty">暂无回收记录</div>
    <div v-else class="record-list">
      <article
        v-for="item in records"
        :key="item.id"
        class="record-card"
        :class="{ 'record-card--highlight': item.id === highlightRecordId }"
      >
        <div class="top">
          <strong>#{{ item.id }} · {{ getWorkerName(item.workerId) }}</strong>
          <span class="status">{{ getStatusName(item.status) }}</span>
        </div>
        <p>登记内容：{{ getMaterialName(item.materialItemId) }} / {{ item.quantity }} {{ getUnitName(item.unitCode) }}</p>
        <p>登记对象：{{ getWorkerName(item.workerId) }}</p>
        <p>项目班组：{{ getProjectName(item.projectId) }} / {{ getTeamName(item.teamId) }}</p>
        <p>完好度：{{ getConditionName(item.conditionCode) }}</p>
        <p>预计积分：{{ item.calculatedPoints }}</p>
        <p>提交时间：{{ formatDateTime(item.submittedAt) }}</p>
        <p v-if="item.approvedAt">审核时间：{{ formatDateTime(item.approvedAt) }}</p>
        <p v-if="item.remark">备注：{{ item.remark }}</p>
      </article>
    </div>
  </MobileShell>
</template>

<style scoped>
.success-banner {
  display: grid;
  gap: 12px;
  margin-bottom: 16px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #d8ead9;
  background:
    radial-gradient(circle at top right, rgba(33, 151, 79, 0.1), transparent 38%),
    #f7fcf8;
}

.success-tag {
  width: fit-content;
  margin: 0;
  padding: 4px 10px;
  border-radius: 999px;
  color: #17663a;
  background: #dff3e3;
  font-size: 12px;
  font-weight: 600;
}

.success-banner h2,
.success-desc {
  margin: 0;
}

.success-desc {
  color: var(--color-muted);
  line-height: 1.6;
}

.banner-actions {
  display: grid;
  gap: 10px;
}

.empty {
  padding: 24px 16px;
  border-radius: 16px;
  background: var(--color-surface);
  text-align: center;
  color: var(--color-muted);
}

.record-list {
  display: grid;
  gap: 12px;
}

.record-card {
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: var(--color-surface);
}

.record-card--highlight {
  border-color: rgba(34, 114, 255, 0.35);
  box-shadow: 0 10px 24px rgba(34, 114, 255, 0.08);
}

.record-card p {
  margin: 8px 0 0;
  color: var(--color-muted);
  font-size: 13px;
}

.top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status {
  font-size: 12px;
  color: var(--color-primary);
}
</style>
