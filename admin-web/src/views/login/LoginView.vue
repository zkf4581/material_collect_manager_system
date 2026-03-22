<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { setAdminToken } from '@/utils/storage'

const router = useRouter()
const route = useRoute()

const form = reactive({
  username: '',
  password: '',
})
const loading = ref(false)
const errorText = ref('')

async function onSubmit() {
  errorText.value = ''
  if (!form.username || !form.password) {
    errorText.value = '请输入后台账号和密码'
    return
  }
  loading.value = true
  await new Promise((resolve) => setTimeout(resolve, 300))
  setAdminToken('admin-demo-token')
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
  router.replace(redirect)
  loading.value = false
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <p class="subtitle">项目后台</p>
      <h1>材料回收积分管理平台</h1>
      <p class="desc">当前是后台最小骨架，后续将在这里扩展项目、人员、规则、审核和报表模块。</p>

      <el-form @submit.prevent="onSubmit">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <p v-if="errorText" class="error">{{ errorText }}</p>
        <el-button class="submit" type="primary" :loading="loading" @click="onSubmit">
          登录后台
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at 0% 0%, rgba(34, 154, 110, 0.24), transparent 40%),
    radial-gradient(circle at 100% 10%, rgba(245, 152, 77, 0.2), transparent 40%),
    linear-gradient(180deg, #f5f8fb 0%, #eef4f8 100%);
}

.login-card {
  width: min(100%, 460px);
  padding: 28px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 24px 60px rgba(15, 51, 74, 0.12);
}

.subtitle {
  margin: 0 0 8px;
  color: var(--adm-accent);
  font-size: 13px;
}

h1 {
  margin: 0 0 10px;
  font-size: 28px;
  line-height: 1.35;
}

.desc {
  margin: 0 0 20px;
  color: var(--adm-text-muted);
  line-height: 1.6;
}

.submit {
  width: 100%;
}

.error {
  margin: 0 0 12px;
  color: #d9485d;
}
</style>
