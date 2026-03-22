<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import { getRecycleRecords, type RecycleRecordItem } from '@/api/recycle'

const loading = ref(false)
const records = ref<RecycleRecordItem[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const response = await getRecycleRecords()
    records.value = response.data
  } catch (error) {
    showToast(error instanceof Error ? error.message : '加载记录失败')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <MobileShell title="回收记录">
    <div v-if="loading" class="empty">加载中...</div>
    <div v-else-if="records.length === 0" class="empty">暂无回收记录</div>
    <div v-else class="record-list">
      <article v-for="item in records" :key="item.id" class="record-card">
        <div class="top">
          <strong>#{{ item.id }}</strong>
          <span class="status">{{ item.status }}</span>
        </div>
        <p>材料 ID：{{ item.materialItemId }}</p>
        <p>工人 ID：{{ item.workerId }}</p>
        <p>数量：{{ item.quantity }} {{ item.unitCode }}</p>
        <p>积分：{{ item.calculatedPoints }}</p>
        <p v-if="item.remark">备注：{{ item.remark }}</p>
      </article>
    </div>
  </MobileShell>
</template>

<style scoped>
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
