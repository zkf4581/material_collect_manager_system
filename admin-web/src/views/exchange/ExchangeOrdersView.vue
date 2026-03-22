<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  approveExchangeOrder,
  getExchangeOrders,
  getRewardItems,
  rejectExchangeOrder,
  type ExchangeOrder,
  type RewardItem,
} from '@/api/exchange'

const loading = ref(false)
const processingId = ref<number | null>(null)
const orders = ref<ExchangeOrder[]>([])
const rewards = ref<RewardItem[]>([])

const rewardMap = computed(() => new Map(rewards.value.map((item) => [item.id, item.name])))

async function loadData() {
  loading.value = true
  try {
    const [orderRes, rewardRes] = await Promise.all([getExchangeOrders(), getRewardItems()])
    orders.value = orderRes.data
    rewards.value = rewardRes.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载兑换审核数据失败')
  } finally {
    loading.value = false
  }
}

async function onApprove(id: number) {
  processingId.value = id
  try {
    await approveExchangeOrder(id)
    ElMessage.success('审核通过，已扣积分并扣库存')
    await loadData()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '审核失败')
  } finally {
    processingId.value = null
  }
}

async function onReject(id: number) {
  processingId.value = id
  try {
    await rejectExchangeOrder(id)
    ElMessage.success('已驳回兑换申请')
    await loadData()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '驳回失败')
  } finally {
    processingId.value = null
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <section class="hero">
      <div>
        <p class="label">兑换审核</p>
        <h2>积分兑换申请处理</h2>
        <p class="desc">当前页面用于查看工人的兑换申请，并执行通过或驳回。</p>
      </div>
      <el-button type="primary" plain @click="loadData">刷新</el-button>
    </section>

    <el-table :data="orders" v-loading="loading" border>
      <el-table-column prop="id" label="申请 ID" min-width="90" />
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="workerId" label="工人 ID" min-width="90" />
      <el-table-column label="商品" min-width="160">
        <template #default="{ row }">{{ rewardMap.get(row.rewardItemId) || row.rewardItemId }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" min-width="80" />
      <el-table-column prop="totalPoints" label="积分" min-width="80" />
      <el-table-column label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag
            :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'"
          >
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <div v-if="row.status === 'SUBMITTED'" class="actions">
            <el-button size="small" type="primary" :loading="processingId === row.id" @click="onApprove(row.id)">
              通过
            </el-button>
            <el-button size="small" plain :loading="processingId === row.id" @click="onReject(row.id)">
              驳回
            </el-button>
          </div>
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

.actions {
  display: flex;
  gap: 8px;
}

.done-text {
  color: var(--adm-text-muted);
  font-size: 13px;
}
</style>
