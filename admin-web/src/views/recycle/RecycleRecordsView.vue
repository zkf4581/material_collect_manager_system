<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { approveRecycleRecord, getRecycleRecords, type RecycleRecordItem } from '@/api/recycle'

const loading = ref(false)
const approvingId = ref<number | null>(null)
const records = ref<RecycleRecordItem[]>([])

async function loadRecords() {
  loading.value = true
  try {
    const response = await getRecycleRecords()
    records.value = response.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载回收记录失败')
  } finally {
    loading.value = false
  }
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
      <el-table-column prop="id" label="记录 ID" min-width="90" />
      <el-table-column prop="projectId" label="项目 ID" min-width="90" />
      <el-table-column prop="teamId" label="班组 ID" min-width="90" />
      <el-table-column prop="workerId" label="工人 ID" min-width="90" />
      <el-table-column prop="materialItemId" label="材料 ID" min-width="90" />
      <el-table-column label="数量" min-width="120">
        <template #default="{ row }">{{ row.quantity }} {{ row.unitCode }}</template>
      </el-table-column>
      <el-table-column prop="conditionCode" label="完好度" min-width="90" />
      <el-table-column prop="calculatedPoints" label="积分" min-width="80" />
      <el-table-column label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'">{{ row.status }}</el-tag>
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

.done-text {
  color: var(--adm-text-muted);
  font-size: 13px;
}
</style>
