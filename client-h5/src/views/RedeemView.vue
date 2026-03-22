<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import { createExchangeOrder, getMyExchangeOrders, getRewardItems, type ExchangeOrder, type RewardItem } from '@/api/exchange'

const loading = ref(false)
const submittingId = ref<number | null>(null)
const rewards = ref<RewardItem[]>([])
const orders = ref<ExchangeOrder[]>([])

async function loadData() {
  loading.value = true
  try {
    const [rewardRes, orderRes] = await Promise.all([getRewardItems(), getMyExchangeOrders()])
    rewards.value = rewardRes.data
    orders.value = orderRes.data
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

onMounted(loadData)
</script>

<template>
  <MobileShell title="积分兑换">
    <div v-if="loading" class="empty">加载中...</div>
    <template v-else>
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
            <span>{{ item.status }}</span>
          </div>
          <p>商品 ID：{{ item.rewardItemId }}</p>
          <p>数量：{{ item.quantity }}</p>
          <p>积分：{{ item.totalPoints }}</p>
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

.reward-card,
.order-card {
  display: grid;
  gap: 10px;
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: var(--color-surface);
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
