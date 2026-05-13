<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
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

interface SubmitResultSummary {
  id: number
  projectName: string
  teamName: string
  workerName: string
  materialName: string
  quantityText: string
  conditionName: string
  calculatedPoints: number
  remark: string
}

const router = useRouter()
const loading = ref(false)
const uploading = ref(false)
const projects = ref<ProjectOption[]>([])
const teams = ref<TeamOption[]>([])
const workers = ref<WorkerOption[]>([])
const materials = ref<MaterialItemOption[]>([])
const units = ref<UnitOption[]>([])
const conditions = ref<ConditionOption[]>([])
const uploadedFiles = ref<UploadedFileInfo[]>([])
const fileInputRef = ref<HTMLInputElement | null>(null)
const submitResult = ref<SubmitResultSummary | null>(null)

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

const filteredWorkers = computed(() =>
  workers.value.filter((worker) => !form.teamId || String(worker.teamId) === form.teamId),
)

const selectedMaterial = computed(() =>
  materials.value.find((item) => String(item.id) === form.materialItemId),
)

watch(selectedMaterial, (value) => {
  if (value && !form.unitCode) {
    form.unitCode = value.unitCode
  }
})

watch(
  () => form.projectId,
  (nextProjectId, prevProjectId) => {
    if (prevProjectId && nextProjectId !== prevProjectId) {
      form.teamId = ''
      form.workerId = ''
    }
  },
)

watch(
  () => form.teamId,
  (nextTeamId, prevTeamId) => {
    if (prevTeamId && nextTeamId !== prevTeamId) {
      form.workerId = ''
    }
  },
)

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

function getProjectName(projectId: string) {
  return projects.value.find((item) => String(item.id) === projectId)?.name ?? `项目 #${projectId}`
}

function getTeamName(teamId: string) {
  return teams.value.find((item) => String(item.id) === teamId)?.name ?? `班组 #${teamId}`
}

function getWorkerName(workerId: string) {
  return workers.value.find((item) => String(item.id) === workerId)?.name ?? `工人 #${workerId}`
}

function getMaterialName(materialItemId: string) {
  return materials.value.find((item) => String(item.id) === materialItemId)?.name ?? `材料 #${materialItemId}`
}

function getConditionName(conditionCode: string) {
  return conditions.value.find((item) => item.code === conditionCode)?.name ?? conditionCode
}

function buildQuantityText(quantity: string, unitCode: string) {
  const unitName = units.value.find((item) => item.code === unitCode)?.name ?? unitCode
  return `${quantity} ${unitName}`
}

function openFilePicker() {
  fileInputRef.value?.click()
}

function resetForm(keepProject = true) {
  const projectId = keepProject ? form.projectId : ''
  form.projectId = projectId
  form.teamId = ''
  form.workerId = ''
  form.materialItemId = ''
  form.quantity = ''
  form.unitCode = ''
  form.conditionCode = ''
  form.remark = ''
  uploadedFiles.value = []
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

function continueCreate() {
  submitResult.value = null
}

function viewSubmittedRecords() {
  if (!submitResult.value) {
    router.push('/records')
    return
  }

  router.push({
    path: '/records',
    query: {
      submitted: '1',
      recordId: String(submitResult.value.id),
    },
  })
}

function goHome() {
  router.push('/')
}

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
    const summary = {
      projectName: getProjectName(form.projectId),
      teamName: getTeamName(form.teamId),
      workerName: getWorkerName(form.workerId),
      materialName: getMaterialName(form.materialItemId),
      quantityText: buildQuantityText(form.quantity, form.unitCode),
      conditionName: getConditionName(form.conditionCode),
      remark: form.remark,
    }

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

    submitResult.value = {
      id: response.data.id,
      projectName: summary.projectName,
      teamName: summary.teamName,
      workerName: summary.workerName,
      materialName: summary.materialName,
      quantityText: summary.quantityText,
      conditionName: summary.conditionName,
      calculatedPoints: response.data.calculatedPoints,
      remark: summary.remark,
    }

    showToast(`提交成功，预计积分 ${response.data.calculatedPoints}`)
    resetForm()
  } catch (error) {
    showToast(error instanceof Error ? error.message : '提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <MobileShell title="回收登记">
    <section v-if="submitResult" class="success-panel">
      <p class="success-badge">提交成功</p>
      <h2>刚刚这条材料登记已经提交完成</h2>
      <p class="success-desc">
        已为 {{ submitResult.workerName }} 登记 {{ submitResult.materialName }}，预计积分
        {{ submitResult.calculatedPoints }}。
      </p>

      <div class="summary-grid">
        <article class="summary-item">
          <span>项目</span>
          <strong>{{ submitResult.projectName }}</strong>
        </article>
        <article class="summary-item">
          <span>班组</span>
          <strong>{{ submitResult.teamName }}</strong>
        </article>
        <article class="summary-item">
          <span>登记对象</span>
          <strong>{{ submitResult.workerName }}</strong>
        </article>
        <article class="summary-item">
          <span>材料与数量</span>
          <strong>{{ submitResult.materialName }} / {{ submitResult.quantityText }}</strong>
        </article>
        <article class="summary-item">
          <span>完好度</span>
          <strong>{{ submitResult.conditionName }}</strong>
        </article>
        <article class="summary-item">
          <span>记录编号</span>
          <strong>#{{ submitResult.id }}</strong>
        </article>
      </div>

      <p v-if="submitResult.remark" class="success-note">备注：{{ submitResult.remark }}</p>

      <div class="success-actions">
        <button class="btn-primary" type="button" @click="continueCreate">继续添加一条</button>
        <button class="btn-secondary" type="button" @click="viewSubmittedRecords">查看提交记录</button>
        <button class="btn-ghost" type="button" @click="goHome">完成，返回首页</button>
      </div>
    </section>

    <div v-else class="panel">
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
          <option v-for="item in filteredWorkers" :key="item.id" :value="String(item.id)">{{ item.name }}</option>
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
        <input
          ref="fileInputRef"
          class="file-input-hidden"
          type="file"
          accept="image/jpeg,image/png,image/webp"
          multiple
          @change="onChooseFiles"
        />
        <button class="btn-secondary upload-trigger" type="button" :disabled="uploading" @click="openFilePicker">
          {{ uploading ? '上传中...' : '选择图片' }}
        </button>
        <p class="tip">
          支持多张图片，单张不超过 5MB。{{ uploadedFiles.length > 0 ? `已上传 ${uploadedFiles.length} 张。` : '' }}
        </p>
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
.success-panel {
  display: grid;
  gap: 16px;
  padding: 18px;
  border: 1px solid #d8ead9;
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(33, 151, 79, 0.12), transparent 36%),
    #f7fcf8;
}

.success-badge {
  width: fit-content;
  margin: 0;
  padding: 6px 10px;
  border-radius: 999px;
  color: #17663a;
  background: #dff3e3;
  font-size: 12px;
  font-weight: 600;
}

.success-panel h2 {
  margin: 0;
  font-size: 22px;
}

.success-desc,
.success-note {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.6;
}

.summary-grid {
  display: grid;
  gap: 10px;
}

.summary-item {
  display: grid;
  gap: 4px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
}

.summary-item span {
  font-size: 12px;
  color: var(--color-muted);
}

.summary-item strong {
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.5;
}

.success-actions {
  display: grid;
  gap: 10px;
}

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

.file-input-hidden {
  display: none;
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

.upload-trigger {
  width: 100%;
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
