<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import { getPointLedger, getPointSummary, type PointLedgerItem, type PointSummary } from '@/api/points'

const loading = ref(false)
const summary = ref<PointSummary | null>(null)
const ledger = ref<PointLedgerItem[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const [summaryRes, ledgerRes] = await Promise.all([getPointSummary(), getPointLedger()])
    summary.value = summaryRes.data
    ledger.value = ledgerRes.data
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
        <p class="desc">项目 ID：{{ summary?.projectId ?? '-' }}</p>
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
