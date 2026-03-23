<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createRewardItem, getRewardItems, updateRewardItem, type RewardItem } from '@/api/goods'

const loading = ref(false)
const saving = ref(false)
const items = ref<RewardItem[]>([])
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  pointsCost: 50,
  stock: 10,
  status: 'ENABLED',
})

async function loadItems() {
  loading.value = true
  try {
    const response = await getRewardItems()
    items.value = response.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载商品失败')
  } finally {
    loading.value = false
  }
}

function fillForm(item: RewardItem) {
  editingId.value = item.id
  form.name = item.name
  form.pointsCost = item.pointsCost
  form.stock = item.stock
  form.status = item.status
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.pointsCost = 50
  form.stock = 10
  form.status = 'ENABLED'
}

async function onSubmit() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateRewardItem(editingId.value, { ...form })
      ElMessage.success('商品更新成功')
    } else {
      await createRewardItem({ ...form })
      ElMessage.success('商品创建成功')
    }
    resetForm()
    await loadItems()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadItems)
</script>

<template>
  <div class="page">
    <section class="hero">
      <div>
        <p class="label">商品管理</p>
        <h2>积分兑换商品维护</h2>
        <p class="desc">用于维护兑换商品、所需积分、库存和启用状态。</p>
      </div>
      <el-button type="primary" plain @click="loadItems">刷新</el-button>
    </section>

    <section class="grid">
      <el-card shadow="never">
        <template #header>
          <div class="card-title">{{ editingId ? '编辑商品' : '新增商品' }}</div>
        </template>
        <el-form label-position="top">
          <el-form-item label="商品名称">
            <el-input v-model="form.name" placeholder="请输入商品名称" />
          </el-form-item>
          <el-form-item label="所需积分">
            <el-input-number v-model="form.pointsCost" :min="1" />
          </el-form-item>
          <el-form-item label="库存">
            <el-input-number v-model="form.stock" :min="0" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="启用" value="ENABLED" />
              <el-option label="禁用" value="DISABLED" />
            </el-select>
          </el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="onSubmit">保存</el-button>
            <el-button @click="resetForm">重置</el-button>
          </div>
        </el-form>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <div class="card-title">商品列表</div>
        </template>
        <el-table :data="items" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="商品名称" min-width="140" />
          <el-table-column prop="pointsCost" label="积分" width="90" />
          <el-table-column prop="stock" label="库存" width="90" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="primary" plain @click="fillForm(row)">编辑</el-button>
            </template>
          </el-table-column>
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

.grid {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 16px;
}

.card-title {
  font-weight: 600;
}

.form-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 1100px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
