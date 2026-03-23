<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createWorker, getWorkers, updateWorker, type WorkerItem } from '@/api/project'

const loading = ref(false)
const saving = ref(false)
const items = ref<WorkerItem[]>([])
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  phone: '',
  status: 'ENABLED',
})

async function loadData() {
  loading.value = true
  try {
    const response = await getWorkers()
    items.value = response.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载工人失败')
  } finally {
    loading.value = false
  }
}

function fillForm(item: WorkerItem) {
  editingId.value = item.id
  form.name = item.name
  form.phone = item.phone || ''
  form.status = item.status
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.phone = ''
  form.status = 'ENABLED'
}

async function onSubmit() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateWorker(editingId.value, { ...form })
      ElMessage.success('工人更新成功')
    } else {
      await createWorker({ ...form })
      ElMessage.success('工人创建成功')
    }
    resetForm()
    await loadData()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <section class="hero">
      <div>
        <p class="label">工人管理</p>
        <h2>工人档案维护</h2>
        <p class="desc">维护工人姓名、手机号和启用状态，供回收积分与兑换业务使用。</p>
      </div>
      <el-button type="primary" plain @click="loadData">刷新</el-button>
    </section>

    <section class="grid">
      <el-card shadow="never">
        <template #header><div class="card-title">{{ editingId ? '编辑工人' : '新增工人' }}</div></template>
        <el-form label-position="top">
          <el-form-item label="姓名">
            <el-input v-model="form.name" placeholder="请输入工人姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
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
        <template #header><div class="card-title">工人列表</div></template>
        <el-table :data="items" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="姓名" min-width="140" />
          <el-table-column prop="phone" label="手机号" min-width="150" />
          <el-table-column prop="status" label="状态" min-width="100" />
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
.page { display: grid; gap: 16px; }
.hero { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; padding: 20px 22px; border-radius: 20px; background: #fff; box-shadow: 0 12px 36px rgba(12,45,64,.08); }
.label { margin: 0 0 8px; color: var(--adm-accent); font-size: 12px; }
h2 { margin: 0 0 8px; font-size: 24px; }
.desc { margin: 0; color: var(--adm-text-muted); }
.grid { display: grid; grid-template-columns: 360px 1fr; gap: 16px; }
.card-title { font-weight: 600; }
.form-actions { display: flex; gap: 10px; }
@media (max-width: 1100px) { .grid { grid-template-columns: 1fr; } }
</style>
