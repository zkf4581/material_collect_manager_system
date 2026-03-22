<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser } from '@/api/auth'
import MobileShell from '@/components/layout/MobileShell.vue'
import { useAppStore } from '@/stores/app'
import { clearToken } from '@/utils/storage'

const appStore = useAppStore()
const router = useRouter()

onMounted(async () => {
  if (!appStore.username) {
    try {
      const response = await getCurrentUser()
      appStore.setCurrentUser({
        username: response.data.username,
        roleCode: response.data.roleCode,
      })
    } catch {
      goLogin()
    }
  }
})

function goLogin() {
  clearToken()
  router.push('/login')
}

function goRecycle() {
  router.push('/recycle')
}

function goRecords() {
  router.push('/records')
}

function goPoints() {
  router.push('/points')
}

function goRedeem() {
  router.push('/redeem')
}
</script>

<template>
  <MobileShell title="首页">
    <section class="card">
      <p class="label">系统名称</p>
      <h2>{{ appStore.appName }}</h2>
      <p class="desc">当前项目：{{ appStore.projectName }}</p>
      <p class="desc">当前账号：{{ appStore.username || '未获取' }}</p>
      <p class="desc">当前角色：{{ appStore.roleCode || '未获取' }}</p>
      <p class="desc">这是手机端 H5 的最小可运行骨架页面。</p>
    </section>

    <section class="actions">
      <button class="btn-primary" type="button" @click="goRecycle">回收登记</button>
      <button class="btn-secondary" type="button" @click="goRecords">回收记录</button>
      <button class="btn-secondary" type="button" @click="goPoints">我的积分</button>
      <button class="btn-secondary" type="button" @click="goRedeem">积分兑换</button>
      <button class="btn-ghost" type="button" @click="goLogin">退出登录</button>
    </section>
  </MobileShell>
</template>

<style scoped>
.card {
  padding: 16px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 14px;
}

.label {
  margin: 0 0 8px;
  color: var(--color-muted);
  font-size: 12px;
}

h2 {
  margin: 0;
  font-size: 20px;
  color: var(--color-text);
}

.desc {
  margin: 10px 0 0;
  color: var(--color-muted);
  font-size: 14px;
  line-height: 1.5;
}

.actions {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}
</style>
