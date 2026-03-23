<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getOverviewReport, getRankingReport, type OverviewReport, type RankingItem } from '@/api/report'

const loading = ref(false)
const overview = ref<OverviewReport | null>(null)
const rankings = ref<RankingItem[]>([])

async function loadData() {
  loading.value = true
  try {
    const [overviewRes, rankingRes] = await Promise.all([getOverviewReport(), getRankingReport()])
    overview.value = overviewRes.data
    rankings.value = rankingRes.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载报表失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <section class="hero">
      <div>
        <p class="label">基础报表</p>
        <h2>项目概况与积分排行</h2>
        <p class="desc">展示当前系统中的回收、兑换和积分关键指标。</p>
      </div>
      <el-button type="primary" plain @click="loadData">刷新</el-button>
    </section>

    <section class="stats" v-if="overview">
      <article class="stat-card">
        <span>回收记录总数</span>
        <strong>{{ overview.recycleRecordCount }}</strong>
      </article>
      <article class="stat-card">
        <span>已通过回收</span>
        <strong>{{ overview.approvedRecycleCount }}</strong>
      </article>
      <article class="stat-card">
        <span>累计发放积分</span>
        <strong>{{ overview.totalAwardedPoints }}</strong>
      </article>
      <article class="stat-card">
        <span>兑换申请总数</span>
        <strong>{{ overview.exchangeOrderCount }}</strong>
      </article>
      <article class="stat-card">
        <span>已扣兑换积分</span>
        <strong>{{ overview.approvedExchangePoints }}</strong>
      </article>
      <article class="stat-card">
        <span>商品种类数</span>
        <strong>{{ overview.rewardItemCount }}</strong>
      </article>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="card-title">积分排行</div>
      </template>
      <el-table :data="rankings" v-loading="loading" border>
        <el-table-column type="index" label="排名" width="80" />
        <el-table-column prop="workerId" label="工人 ID" min-width="120" />
        <el-table-column prop="projectId" label="项目 ID" min-width="120" />
        <el-table-column prop="balance" label="积分余额" min-width="140" />
      </el-table>
    </el-card>
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

.stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.stat-card {
  display: grid;
  gap: 8px;
  padding: 18px;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(12, 45, 64, 0.06);
}

.stat-card span {
  color: var(--adm-text-muted);
  font-size: 12px;
}

.stat-card strong {
  font-size: 28px;
}

.card-title {
  font-weight: 600;
}

@media (max-width: 1100px) {
  .stats {
    grid-template-columns: 1fr;
  }
}
</style>
