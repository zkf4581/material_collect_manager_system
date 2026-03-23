<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createTeam, getProjects, getTeams, updateTeam, type ProjectItem, type TeamItem } from '@/api/project'

const loading = ref(false)
const saving = ref(false)
const items = ref<TeamItem[]>([])
const projects = ref<ProjectItem[]>([])
const editingId = ref<number | null>(null)

const projectMap = computed(() => new Map(projects.value.map((item) => [item.id, item.name])))

const form = reactive({
  projectId: undefined as number | undefined,
  name: '',
  status: 'ENABLED',
})

async function loadData() {
  loading.value = true
  try {
    const [teamRes, projectRes] = await Promise.all([getTeams(), getProjects()])
    items.value = teamRes.data
    projects.value = projectRes.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载班组失败')
  } finally {
    loading.value = false
  }
}

function fillForm(item: TeamItem) {
  editingId.value = item.id
  form.projectId = item.projectId
  form.name = item.name
  form.status = item.status
}

function resetForm() {
  editingId.value = null
  form.projectId = undefined
  form.name = ''
  form.status = 'ENABLED'
}

async function onSubmit() {
  if (!form.projectId) {
    ElMessage.error('请先选择项目')
    return
  }
  saving.value = true
  try {
    const payload = {
      projectId: form.projectId,
      name: form.name,
      status: form.status,
    }
    if (editingId.value) {
      await updateTeam(editingId.value, payload)
      ElMessage.success('班组更新成功')
    } else {
      await createTeam(payload)
      ElMessage.success('班组创建成功')
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
        <p class="label">班组管理</p>
        <h2>班组基础信息维护</h2>
        <p class="desc">维护班组与项目的归属关系及启用状态。</p>
      </div>
      <el-button type="primary" plain @click="loadData">刷新</el-button>
    </section>

    <section class="grid">
      <el-card shadow="never">
        <template #header><div class="card-title">{{ editingId ? '编辑班组' : '新增班组' }}</div></template>
        <el-form label-position="top">
          <el-form-item label="所属项目">
            <el-select v-model="form.projectId" placeholder="请选择项目">
              <el-option v-for="item in projects" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="班组名称">
            <el-input v-model="form.name" placeholder="请输入班组名称" />
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
        <template #header><div class="card-title">班组列表</div></template>
        <el-table :data="items" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="所属项目" min-width="160">
            <template #default="{ row }">{{ projectMap.get(row.projectId) || row.projectId }}</template>
          </el-table-column>
          <el-table-column prop="name" label="班组名称" min-width="160" />
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
