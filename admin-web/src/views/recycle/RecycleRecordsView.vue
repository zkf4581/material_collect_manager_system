<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { approveRecycleRecord, getRecycleRecords, type RecycleRecordItem } from '@/api/recycle'
import { getProjects, getTeams, getWorkers, type ProjectItem, type TeamItem, type WorkerItem } from '@/api/project'
import { getConditionFactors, getMaterialItems, getUnitDict, type DictOption, type MaterialItem } from '@/api/material'

const loading = ref(false)
const approvingId = ref<number | null>(null)
const records = ref<RecycleRecordItem[]>([])
const projects = ref<ProjectItem[]>([])
const teams = ref<TeamItem[]>([])
const workers = ref<WorkerItem[]>([])
const materials = ref<MaterialItem[]>([])
const units = ref<DictOption[]>([])
const conditions = ref<DictOption[]>([])

const projectMap = computed(() => new Map(projects.value.map((item) => [item.id, item.name])))
const teamMap = computed(() => new Map(teams.value.map((item) => [item.id, item.name])))
const workerMap = computed(() => new Map(workers.value.map((item) => [item.id, item.name])))
const materialMap = computed(() => new Map(materials.value.map((item) => [item.id, item.name])))
const unitMap = computed(() => new Map(units.value.map((item) => [item.code, item.name])))
const conditionMap = computed(() => new Map(conditions.value.map((item) => [item.code, item.name])))

async function loadRecords() {
  loading.value = true
  try {
    const [recordRes, projectRes, teamRes, workerRes, materialRes, unitRes, conditionRes] = await Promise.all([
      getRecycleRecords(),
      getProjects(),
      getTeams(),
      getWorkers(),
      getMaterialItems(),
      getUnitDict(),
      getConditionFactors(),
    ])
    records.value = recordRes.data
    projects.value = projectRes.data
    teams.value = teamRes.data
    workers.value = workerRes.data
    materials.value = materialRes.data
    units.value = unitRes.data
    conditions.value = conditionRes.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载回收记录失败')
  } finally {
    loading.value = false
  }
}

function getProjectName(id: number) {
  return projectMap.value.get(id) ?? `项目 #${id}`
}

function getTeamName(id: number) {
  return teamMap.value.get(id) ?? `班组 #${id}`
}

function getWorkerName(id: number) {
  return workerMap.value.get(id) ?? `工人 #${id}`
}

function getMaterialName(id: number) {
  return materialMap.value.get(id) ?? `材料 #${id}`
}

function getUnitName(code: string) {
  return unitMap.value.get(code) ?? code
}

function getConditionName(code: string) {
  return conditionMap.value.get(code) ?? code
}

function getStatusName(status: string) {
  return (
    {
      SUBMITTED: '待审核',
      APPROVED: '已通过',
      REJECTED: '已驳回',
    }[status] ?? status
  )
}

function getStatusType(status: string) {
  return status === 'APPROVED' ? 'success' : status === 'REJECTED' ? 'danger' : 'warning'
}

async function onApprove(id: number) {
  approvingId.value = id
  try {
    await approveRecycleRecord(id)
    ElMessage.success('审核通过，积分已入账')
    await loadRecords()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '审核失败')
  } finally {
    approvingId.value = null
  }
}

onMounted(loadRecords)
</script>

<template>
  <div class="page">
    <section class="hero">
      <div>
        <p class="label">回收审核</p>
        <h2>回收记录审核与发分</h2>
        <p class="desc">当前页面用于查看现场提交的回收记录，并执行审核通过操作。</p>
      </div>
      <el-button type="primary" plain @click="loadRecords">刷新</el-button>
    </section>

    <el-table :data="records" v-loading="loading" border>
      <el-table-column label="记录" min-width="90">
        <template #default="{ row }">#{{ row.id }}</template>
      </el-table-column>
      <el-table-column label="谁上交" min-width="150">
        <template #default="{ row }">
          <div class="cell-main">{{ getWorkerName(row.workerId) }}</div>
          <div class="cell-sub">工人 ID：{{ row.workerId }}</div>
        </template>
      </el-table-column>
      <el-table-column label="上交材料" min-width="180">
        <template #default="{ row }">
          <div class="cell-main">{{ getMaterialName(row.materialItemId) }}</div>
          <div class="cell-sub">材料 ID：{{ row.materialItemId }}</div>
        </template>
      </el-table-column>
      <el-table-column label="项目 / 班组" min-width="220">
        <template #default="{ row }">
          <div class="cell-main">{{ getProjectName(row.projectId) }}</div>
          <div class="cell-sub">{{ getTeamName(row.teamId) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="数量" min-width="120">
        <template #default="{ row }">{{ row.quantity }} {{ getUnitName(row.unitCode) }}</template>
      </el-table-column>
      <el-table-column label="完好度" min-width="110">
        <template #default="{ row }">{{ getConditionName(row.conditionCode) }}</template>
      </el-table-column>
      <el-table-column prop="calculatedPoints" label="积分" min-width="80" />
      <el-table-column label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'SUBMITTED'"
            type="primary"
            size="small"
            :loading="approvingId === row.id"
            @click="onApprove(row.id)"
          >
            审核通过
          </el-button>
          <span v-else class="done-text">已处理</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped lang="scss">
.page {
  display: grid;
  gap: 16px;
}

.hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 22px;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 12px 36px rgba(12, 45, 64, 0.08);
}

.label {
  margin: 0 0 8px;
  color: var(--adm-accent);
  font-size: 12px;
}

h2 {
  margin: 0 0 8px;
  font-size: 24px;
}

.desc {
  margin: 0;
  color: var(--adm-text-muted);
}

.cell-main {
  color: var(--adm-text);
  font-weight: 600;
}

.cell-sub {
  margin-top: 4px;
  color: var(--adm-text-muted);
  font-size: 12px;
}

.done-text {
  color: var(--adm-text-muted);
  font-size: 13px;
}
</style>
