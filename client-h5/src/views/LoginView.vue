<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { setToken } from '@/utils/storage'

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
    errorText.value = '请先输入账号和密码'
    return
  }
  loading.value = true
  await new Promise((resolve) => setTimeout(resolve, 300))
  setToken('demo-token')
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
  router.replace(redirect)
  loading.value = false
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <p class="title-sub">工地现场端 H5</p>
      <h1>材料回收积分系统</h1>
      <form class="form" @submit.prevent="onSubmit">
        <label>
          <span>账号</span>
          <input v-model.trim="form.username" type="text" placeholder="请输入账号" />
        </label>
        <label>
          <span>密码</span>
          <input v-model.trim="form.password" type="password" placeholder="请输入密码" />
        </label>
        <p v-if="errorText" class="error">{{ errorText }}</p>
        <button class="btn-primary" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100dvh;
  display: grid;
  place-items: center;
  padding: 20px;
  background:
    radial-gradient(circle at 20% 20%, rgba(255, 194, 132, 0.35), transparent 45%),
    linear-gradient(180deg, #fff8ef 0%, #f7fbff 100%);
}

.login-card {
  width: min(100%, 420px);
  padding: 20px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(4px);
}

.title-sub {
  margin: 0 0 6px;
  color: var(--color-muted);
  font-size: 12px;
}

h1 {
  margin: 0 0 18px;
  font-size: 22px;
  color: var(--color-text);
}

.form {
  display: grid;
  gap: 12px;
}

label {
  display: grid;
  gap: 6px;
}

span {
  font-size: 13px;
  color: var(--color-text);
}

input {
  height: 42px;
  border-radius: 10px;
  border: 1px solid var(--color-border);
  padding: 0 12px;
  font-size: 14px;
}

.error {
  margin: 0;
  font-size: 13px;
  color: #d02828;
}
</style>
