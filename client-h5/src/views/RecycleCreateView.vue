<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { showToast } from 'vant'
import MobileShell from '@/components/layout/MobileShell.vue'
import {
  createRecycleRecord,
  getConditionOptions,
  getMaterialItems,
  getProjects,
  getTeams,
  getUnitOptions,
  getWorkers,
  uploadImage,
  type ConditionOption,
  type MaterialItemOption,
  type ProjectOption,
  type TeamOption,
  type UnitOption,
  type UploadedFileInfo,
  type WorkerOption,
} from '@/api/recycle'

const loading = ref(false)
const uploading = ref(false)
const projects = ref<ProjectOption[]>([])
const teams = ref<TeamOption[]>([])
const workers = ref<WorkerOption[]>([])
const materials = ref<MaterialItemOption[]>([])
const units = ref<UnitOption[]>([])
const conditions = ref<ConditionOption[]>([])
const uploadedFiles = ref<UploadedFileInfo[]>([])

const form = reactive({
  projectId: '',
  teamId: '',
  workerId: '',
  materialItemId: '',
  quantity: '',
  unitCode: '',
  conditionCode: '',
  remark: '',
})

const filteredTeams = computed(() =>
  teams.value.filter((team) => !form.projectId || String(team.projectId) === form.projectId),
)

const selectedMaterial = computed(() =>
  materials.value.find((item) => String(item.id) === form.materialItemId),
)

watch(selectedMaterial, (value) => {
  if (value && !form.unitCode) {
    form.unitCode = value.unitCode
  }
})

onMounted(async () => {
  const [projectRes, teamRes, workerRes, materialRes, unitRes, conditionRes] = await Promise.all([
    getProjects(),
    getTeams(),
    getWorkers(),
    getMaterialItems(),
    getUnitOptions(),
    getConditionOptions(),
  ])
  projects.value = projectRes.data
  teams.value = teamRes.data
  workers.value = workerRes.data
  materials.value = materialRes.data
  units.value = unitRes.data
  conditions.value = conditionRes.data
  if (projects.value.length > 0) {
    form.projectId = String(projects.value[0].id)
  }
})

async function onChooseFiles(event: Event) {
  const input = event.target as HTMLInputElement
  const files = Array.from(input.files || [])
  if (files.length === 0) {
    return
  }
  uploading.value = true
  try {
    for (const file of files) {
      const response = await uploadImage(file)
      uploadedFiles.value.push(response.data)
    }
    showToast('图片上传成功')
  } catch (error) {
    showToast(error instanceof Error ? error.message : '图片上传失败')
  } finally {
    uploading.value = false
    input.value = ''
  }
}

async function onSubmit() {
  if (
    !form.projectId ||
    !form.teamId ||
    !form.workerId ||
    !form.materialItemId ||
    !form.quantity ||
    !form.unitCode ||
    !form.conditionCode
  ) {
    showToast('请先填写完整信息')
    return
  }
  if (uploadedFiles.value.length === 0) {
    showToast('请至少上传一张照片')
    return
  }

  loading.value = true
  try {
    const response = await createRecycleRecord({
      projectId: Number(form.projectId),
      teamId: Number(form.teamId),
      workerId: Number(form.workerId),
      materialItemId: Number(form.materialItemId),
      quantity: Number(form.quantity),
      unitCode: form.unitCode,
      conditionCode: form.conditionCode,
      remark: form.remark,
      photoIds: uploadedFiles.value.map((item) => item.fileId),
    })
    showToast(`提交成功，预计积分 ${response.data.calculatedPoints}`)
    form.teamId = ''
    form.workerId = ''
    form.materialItemId = ''
    form.quantity = ''
    form.unitCode = ''
    form.conditionCode = ''
    form.remark = ''
    uploadedFiles.value = []
  } catch (error) {
    showToast(error instanceof Error ? error.message : '提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <MobileShell title="回收登记">
    <div class="panel">
      <label class="field">
        <span>项目</span>
        <select v-model="form.projectId">
          <option value="">请选择项目</option>
          <option v-for="item in projects" :key="item.id" :value="String(item.id)">{{ item.name }}</option>
        </select>
      </label>

      <label class="field">
        <span>班组</span>
        <select v-model="form.teamId">
          <option value="">请选择班组</option>
          <option v-for="item in filteredTeams" :key="item.id" :value="String(item.id)">{{ item.name }}</option>
        </select>
      </label>

      <label class="field">
        <span>工人</span>
        <select v-model="form.workerId">
          <option value="">请选择工人</option>
          <option v-for="item in workers" :key="item.id" :value="String(item.id)">{{ item.name }}</option>
        </select>
      </label>

      <label class="field">
        <span>材料</span>
        <select v-model="form.materialItemId">
          <option value="">请选择材料</option>
          <option v-for="item in materials" :key="item.id" :value="String(item.id)">{{ item.name }}</option>
        </select>
      </label>

      <div class="double-grid">
        <label class="field">
          <span>数量</span>
          <input v-model="form.quantity" type="number" min="0.01" step="0.01" placeholder="请输入数量" />
        </label>

        <label class="field">
          <span>单位</span>
          <select v-model="form.unitCode">
            <option value="">请选择单位</option>
            <option v-for="item in units" :key="item.code" :value="item.code">{{ item.name }}</option>
          </select>
        </label>
      </div>

      <label class="field">
        <span>完好度</span>
        <select v-model="form.conditionCode">
          <option value="">请选择完好度</option>
          <option v-for="item in conditions" :key="item.code" :value="item.code">{{ item.name }}</option>
        </select>
      </label>

      <label class="field">
        <span>备注</span>
        <textarea v-model="form.remark" rows="3" placeholder="补充说明，可不填" />
      </label>

      <label class="field">
        <span>上传照片</span>
        <input type="file" accept="image/jpeg,image/png,image/webp" multiple @change="onChooseFiles" />
        <p class="tip">支持多张图片，单张不超过 5MB。{{ uploading ? '上传中...' : '' }}</p>
      </label>

      <ul v-if="uploadedFiles.length > 0" class="upload-list">
        <li v-for="item in uploadedFiles" :key="item.fileId">
          <span>#{{ item.fileId }}</span>
          <a :href="item.url" target="_blank" rel="noreferrer">查看图片</a>
        </li>
      </ul>

      <button class="btn-primary submit-btn" type="button" :disabled="loading || uploading" @click="onSubmit">
        {{ loading ? '提交中...' : '提交回收记录' }}
      </button>
    </div>
  </MobileShell>
</template>

<style scoped>
.panel {
  display: grid;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: var(--color-surface);
}

.field {
  display: grid;
  gap: 8px;
}

.field span {
  font-size: 13px;
  color: var(--color-text);
}

.field input,
.field select,
.field textarea {
  width: 100%;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 14px;
  background: #fff;
}

.double-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.tip {
  margin: 0;
  color: var(--color-muted);
  font-size: 12px;
}

.upload-list {
  display: grid;
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.upload-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f7f9fc;
  font-size: 13px;
}

.submit-btn {
  margin-top: 8px;
}
</style>
