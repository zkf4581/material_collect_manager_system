<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import { getCurrentUser, type CurrentUser } from '@/api/auth'
import { getPointLedger, getPointSummary, type PointLedgerItem, type PointSummary } from '@/api/points'
import { getProjects, getTeams, getWorkers, type ProjectOption, type TeamOption, type WorkerOption } from '@/api/recycle'

const loading = ref(false)
const currentUser = ref<CurrentUser | null>(null)
const summary = ref<PointSummary | null>(null)
const ledger = ref<PointLedgerItem[]>([])
const projects = ref<ProjectOption[]>([])
const teams = ref<TeamOption[]>([])
const workers = ref<WorkerOption[]>([])

const currentWorker = computed(() =>
  workers.value.find((item) => item.id === (summary.value?.workerId ?? currentUser.value?.workerId)) ?? null,
)
const currentTeam = computed(() =>
  teams.value.find((item) => item.id === currentWorker.value?.teamId) ?? null,
)
const currentProject = computed(() =>
  projects.value.find((item) => item.id === (summary.value?.projectId ?? currentUser.value?.projectId)) ?? null,
)
const currentWorkerName = computed(() => currentWorker.value?.name ?? currentUser.value?.username ?? '未获取')
const currentTeamName = computed(() => currentTeam.value?.name ?? '未绑定班组')
const currentProjectName = computed(() => currentProject.value?.name ?? '未绑定项目')

onMounted(async () => {
  loading.value = true
  try {
    const [profileRes, summaryRes, ledgerRes, projectRes, teamRes, workerRes] = await Promise.all([
      getCurrentUser(),
      getPointSummary(),
      getPointLedger(),
      getProjects(),
      getTeams(),
      getWorkers(),
    ])
    currentUser.value = profileRes.data
    summary.value = summaryRes.data
    ledger.value = ledgerRes.data
    projects.value = projectRes.data
    teams.value = teamRes.data
    workers.value = workerRes.data
  } catch (error) {
    showToast(error instanceof Error ? error.message : '加载积分失败')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <MobileShell title="我的积分">
    <div v-if="loading" class="empty">加载中...</div>
    <template v-else>
      <section class="summary-card">
        <p class="label">当前可用积分</p>
        <h2>{{ summary?.balance ?? 0 }}</h2>
        <p class="desc">当前工人：{{ currentWorkerName }}</p>
        <p class="desc">项目班组：{{ currentProjectName }} / {{ currentTeamName }}</p>
      </section>

      <section class="ledger-list">
        <article v-for="item in ledger" :key="item.id" class="ledger-card">
          <div class="top">
            <strong>{{ item.bizType }}</strong>
            <span :class="item.changeAmount >= 0 ? 'plus' : 'minus'">
              {{ item.changeAmount >= 0 ? '+' : '' }}{{ item.changeAmount }}
            </span>
          </div>
          <p>业务单号：{{ item.bizId }}</p>
          <p>余额：{{ item.balanceAfter }}</p>
          <p v-if="item.remark">说明：{{ item.remark }}</p>
        </article>
        <div v-if="ledger.length === 0" class="empty">暂无积分流水</div>
      </section>
    </template>
  </MobileShell>
</template>

<style scoped>
.summary-card,
.ledger-card {
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: var(--color-surface);
}

.summary-card h2 {
  margin: 8px 0;
  font-size: 28px;
}

.label {
  margin: 0;
  color: var(--color-muted);
  font-size: 12px;
}

.desc {
  margin: 0;
  color: var(--color-muted);
  font-size: 13px;
}

.ledger-list {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ledger-card p {
  margin: 8px 0 0;
  color: var(--color-muted);
  font-size: 13px;
}

.plus {
  color: #1f9d55;
}

.minus {
  color: #d9485d;
}

.empty {
  padding: 18px;
  text-align: center;
  color: var(--color-muted);
}
</style>
