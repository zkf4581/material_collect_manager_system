<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import { getCurrentUser, type CurrentUser } from '@/api/auth'
import { createExchangeOrder, getMyExchangeOrders, getRewardItems, type ExchangeOrder, type RewardItem } from '@/api/exchange'
import {
  getProjects,
  getTeams,
  getWorkers,
  type ProjectOption,
  type TeamOption,
  type WorkerOption,
} from '@/api/recycle'

const loading = ref(false)
const submittingId = ref<number | null>(null)
const currentUser = ref<CurrentUser | null>(null)
const rewards = ref<RewardItem[]>([])
const orders = ref<ExchangeOrder[]>([])
const projects = ref<ProjectOption[]>([])
const teams = ref<TeamOption[]>([])
const workers = ref<WorkerOption[]>([])

const currentWorker = computed(() =>
  workers.value.find((item) => item.id === currentUser.value?.workerId) ?? null,
)
const currentTeam = computed(() =>
  teams.value.find((item) => item.id === currentWorker.value?.teamId) ?? null,
)
const currentProject = computed(() =>
  projects.value.find((item) => item.id === (currentUser.value?.projectId ?? currentTeam.value?.projectId)) ?? null,
)
const currentWorkerName = computed(() => currentWorker.value?.name ?? currentUser.value?.username ?? '未获取')
const currentTeamName = computed(() => currentTeam.value?.name ?? '未绑定班组')
const currentProjectName = computed(() => currentProject.value?.name ?? '未绑定项目')

async function loadData() {
  loading.value = true
  try {
    const [profileRes, rewardRes, orderRes, projectRes, teamRes, workerRes] = await Promise.all([
      getCurrentUser(),
      getRewardItems(),
      getMyExchangeOrders(),
      getProjects(),
      getTeams(),
      getWorkers(),
    ])
    currentUser.value = profileRes.data
    rewards.value = rewardRes.data
    orders.value = orderRes.data
    projects.value = projectRes.data
    teams.value = teamRes.data
    workers.value = workerRes.data
  } catch (error) {
    showToast(error instanceof Error ? error.message : '加载兑换数据失败')
  } finally {
    loading.value = false
  }
}

async function onRedeem(item: RewardItem) {
  submittingId.value = item.id
  try {
    await createExchangeOrder({
      rewardItemId: item.id,
      quantity: 1,
    })
    showToast('兑换申请已提交')
    await loadData()
  } catch (error) {
    showToast(error instanceof Error ? error.message : '兑换失败')
  } finally {
    submittingId.value = null
  }
}

function getRewardName(rewardItemId: number) {
  return rewards.value.find((item) => item.id === rewardItemId)?.name ?? `商品 #${rewardItemId}`
}

function getWorkerName(workerId: number) {
  return workers.value.find((item) => item.id === workerId)?.name ?? `工人 #${workerId}`
}

function getWorkerTeamName(workerId: number) {
  const worker = workers.value.find((item) => item.id === workerId)
  if (!worker?.teamId) {
    return '未绑定班组'
  }
  return teams.value.find((item) => item.id === worker.teamId)?.name ?? `班组 #${worker.teamId}`
}

function getProjectName(projectId: number) {
  return projects.value.find((item) => item.id === projectId)?.name ?? `项目 #${projectId}`
}

function getStatusName(status: string) {
  return (
    {
      SUBMITTED: '待审核',
      APPROVED: '审核通过',
      REJECTED: '已驳回',
      DELIVERED: '已发放',
    }[status] ?? status
  )
}

onMounted(loadData)
</script>

<template>
  <MobileShell title="积分兑换">
    <div v-if="loading" class="empty">加载中...</div>
    <template v-else>
      <section class="identity-card">
        <p class="label">当前兑换人</p>
        <h3>{{ currentWorkerName }}</h3>
        <p>登录账号：{{ currentUser?.username ?? '-' }}</p>
        <p>项目班组：{{ currentProjectName }} / {{ currentTeamName }}</p>
      </section>

      <section class="reward-list">
        <article v-for="item in rewards" :key="item.id" class="reward-card">
          <div>
            <h3>{{ item.name }}</h3>
            <p>所需积分：{{ item.pointsCost }}</p>
            <p>库存：{{ item.stock }}</p>
          </div>
          <button class="btn-primary" type="button" :disabled="submittingId === item.id" @click="onRedeem(item)">
            {{ submittingId === item.id ? '提交中...' : '申请兑换' }}
          </button>
        </article>
      </section>

      <section class="order-list">
        <h3 class="section-title">我的兑换记录</h3>
        <article v-for="item in orders" :key="item.id" class="order-card">
          <div class="top">
            <strong>{{ item.orderNo }}</strong>
            <span>{{ getStatusName(item.status) }}</span>
          </div>
          <p>兑换商品：{{ getRewardName(item.rewardItemId) }}</p>
          <p>申请人：{{ getWorkerName(item.workerId) }} / {{ getWorkerTeamName(item.workerId) }}</p>
          <p>所属项目：{{ getProjectName(item.projectId) }}</p>
          <p>数量：{{ item.quantity }}</p>
          <p>积分：{{ item.totalPoints }}</p>
          <p v-if="item.approvedAt">审核时间：{{ item.approvedAt }}</p>
          <p v-if="item.deliveredAt">发放时间：{{ item.deliveredAt }}</p>
        </article>
        <div v-if="orders.length === 0" class="empty">暂无兑换申请</div>
      </section>
    </template>
  </MobileShell>
</template>

<style scoped>
.reward-list,
.order-list {
  display: grid;
  gap: 12px;
}

.identity-card,
.reward-card,
.order-card {
  display: grid;
  gap: 10px;
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: var(--color-surface);
}

.identity-card {
  margin-bottom: 12px;
}

.identity-card h3 {
  margin: 0;
}

.identity-card p {
  margin: 0;
  color: var(--color-muted);
  font-size: 13px;
}

.reward-card h3,
.section-title {
  margin: 0;
}

.reward-card p,
.order-card p {
  margin: 6px 0 0;
  color: var(--color-muted);
  font-size: 13px;
}

.order-list {
  margin-top: 18px;
}

.top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.empty {
  padding: 18px;
  color: var(--color-muted);
  text-align: center;
}
</style>
