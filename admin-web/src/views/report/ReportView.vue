<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMaterialRankingReport,
  getOverviewReport,
  getRankingReport,
  getRewardRankingReport,
  type MaterialRankingItem,
  type OverviewReport,
  type RankingItem,
  type RewardRankingItem,
} from '@/api/report'

const loading = ref(false)
const overview = ref<OverviewReport | null>(null)
const rankings = ref<RankingItem[]>([])
const materialRankings = ref<MaterialRankingItem[]>([])
const rewardRankings = ref<RewardRankingItem[]>([])

async function loadData() {
  loading.value = true
  try {
    const [overviewRes, rankingRes, materialRes, rewardRes] = await Promise.all([
      getOverviewReport(),
      getRankingReport(),
      getMaterialRankingReport(),
      getRewardRankingReport(),
    ])
    overview.value = overviewRes.data
    rankings.value = rankingRes.data
    materialRankings.value = materialRes.data
    rewardRankings.value = rewardRes.data
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

    <section class="double-grid">
      <el-card shadow="never">
        <template #header>
          <div class="card-title">材料回收排行</div>
        </template>
        <el-table :data="materialRankings" v-loading="loading" border>
          <el-table-column type="index" label="排名" width="70" />
          <el-table-column prop="materialName" label="材料" min-width="140" />
          <el-table-column label="数量" min-width="120">
            <template #default="{ row }">{{ row.totalQuantity }} {{ row.unitCode }}</template>
          </el-table-column>
          <el-table-column prop="totalPoints" label="积分" min-width="100" />
        </el-table>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div class="card-title">商品兑换排行</div>
        </template>
        <el-table :data="rewardRankings" v-loading="loading" border>
          <el-table-column type="index" label="排名" width="70" />
          <el-table-column prop="rewardName" label="商品" min-width="140" />
          <el-table-column prop="totalQuantity" label="兑换数量" min-width="100" />
          <el-table-column prop="totalPoints" label="兑换积分" min-width="100" />
        </el-table>
      </el-card>
    </section>
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

.double-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 1100px) {
  .stats {
    grid-template-columns: 1fr;
  }

  .double-grid {
    grid-template-columns: 1fr;
  }
}
</style>
