<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createPointRule,
  getConditionFactors,
  getMaterialItems,
  getPointRules,
  getUnitDict,
  updatePointRule,
  type DictOption,
  type MaterialItem,
  type PointRule,
} from '@/api/material'

const loading = ref(false)
const saving = ref(false)
const rules = ref<PointRule[]>([])
const materialItems = ref<MaterialItem[]>([])
const unitOptions = ref<DictOption[]>([])
const conditionOptions = ref<DictOption[]>([])
const editingId = ref<number | null>(null)

const form = reactive({
  materialItemId: undefined as number | undefined,
  unitCode: '',
  basePoint: 1,
  conditionCode: '',
  conditionFactor: 1,
  status: 'ENABLED',
})

const materialMap = computed(() => new Map(materialItems.value.map((item) => [item.id, item.name])))

async function loadData() {
  loading.value = true
  try {
    const [ruleRes, materialRes, unitRes, conditionRes] = await Promise.all([
      getPointRules(),
      getMaterialItems(),
      getUnitDict(),
      getConditionFactors(),
    ])
    rules.value = ruleRes.data
    materialItems.value = materialRes.data
    unitOptions.value = unitRes.data
    conditionOptions.value = conditionRes.data
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载积分规则失败')
  } finally {
    loading.value = false
  }
}

function fillForm(item: PointRule) {
  editingId.value = item.id
  form.materialItemId = item.materialItemId
  form.unitCode = item.unitCode
  form.basePoint = item.basePoint
  form.conditionCode = item.conditionCode
  form.conditionFactor = item.conditionFactor
  form.status = item.status
}

function resetForm() {
  editingId.value = null
  form.materialItemId = undefined
  form.unitCode = ''
  form.basePoint = 1
  form.conditionCode = ''
  form.conditionFactor = 1
  form.status = 'ENABLED'
}

async function onSubmit() {
  if (!form.materialItemId) {
    ElMessage.error('请先选择材料')
    return
  }

  saving.value = true
  try {
    const payload = {
      materialItemId: form.materialItemId,
      unitCode: form.unitCode,
      basePoint: Number(form.basePoint),
      conditionCode: form.conditionCode,
      conditionFactor: Number(form.conditionFactor),
      status: form.status,
    }
    if (editingId.value) {
      await updatePointRule(editingId.value, payload)
      ElMessage.success('积分规则更新成功')
    } else {
      await createPointRule(payload)
      ElMessage.success('积分规则创建成功')
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
        <p class="label">积分规则</p>
        <h2>材料积分规则维护</h2>
        <p class="desc">维护材料、单位、基础分值、完好度系数与启用状态。</p>
      </div>
      <el-button type="primary" plain @click="loadData">刷新</el-button>
    </section>

    <section class="grid">
      <el-card shadow="never">
        <template #header>
          <div class="card-title">{{ editingId ? '编辑规则' : '新增规则' }}</div>
        </template>
        <el-form label-position="top">
          <el-form-item label="材料">
            <el-select v-model="form.materialItemId" placeholder="请选择材料">
              <el-option v-for="item in materialItems" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="单位">
            <el-select v-model="form.unitCode" placeholder="请选择单位">
              <el-option v-for="item in unitOptions" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="基础分值">
            <el-input-number v-model="form.basePoint" :min="0.01" :step="0.5" />
          </el-form-item>
          <el-form-item label="完好度">
            <el-select v-model="form.conditionCode" placeholder="请选择完好度">
              <el-option v-for="item in conditionOptions" :key="item.code" :label="item.name" :value="item.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="完好度系数">
            <el-input-number v-model="form.conditionFactor" :min="0.01" :step="0.1" />
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
          <div class="card-title">规则列表</div>
        </template>
        <el-table :data="rules" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="材料" min-width="160">
            <template #default="{ row }">{{ materialMap.get(row.materialItemId) || row.materialItemId }}</template>
          </el-table-column>
          <el-table-column prop="unitCode" label="单位" min-width="100" />
          <el-table-column prop="basePoint" label="基础分值" min-width="100" />
          <el-table-column prop="conditionCode" label="完好度" min-width="100" />
          <el-table-column prop="conditionFactor" label="系数" min-width="100" />
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
  grid-template-columns: 380px 1fr;
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
