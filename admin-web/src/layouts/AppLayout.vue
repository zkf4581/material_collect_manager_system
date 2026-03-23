<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminProfile, logout } from '@/api/auth'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const appStore = useAppStore()
const isAdmin = computed(() => appStore.roleCode === 'ADMIN')
const canReview = computed(() => ['ADMIN', 'KEEPER'].includes(appStore.roleCode))

onMounted(async () => {
  if (!appStore.username) {
    try {
      const response = await getAdminProfile()
      appStore.setCurrentUser({
        username: response.data.username,
        roleCode: response.data.roleCode,
      })
    } catch {
      onLogout()
    }
  }
})

async function onLogout() {
  await logout()
  router.replace('/login')
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">
        <p class="brand-sub">管理后台</p>
        <h1>{{ appStore.appName }}</h1>
      </div>
      <nav class="nav">
        <RouterLink to="/">工作台</RouterLink>
        <RouterLink v-if="isAdmin" to="/projects">项目管理</RouterLink>
        <RouterLink v-if="isAdmin" to="/teams">班组管理</RouterLink>
        <RouterLink v-if="isAdmin" to="/workers">工人管理</RouterLink>
        <RouterLink v-if="isAdmin" to="/materials">材料管理</RouterLink>
        <RouterLink v-if="isAdmin" to="/point-rules">积分规则</RouterLink>
        <RouterLink v-if="canReview" to="/recycle-records">回收审核</RouterLink>
        <RouterLink v-if="canReview" to="/exchange-orders">兑换审核</RouterLink>
        <RouterLink v-if="isAdmin" to="/goods">商品管理</RouterLink>
        <RouterLink v-if="canReview" to="/reports">基础报表</RouterLink>
      </nav>
    </aside>

    <main class="main">
      <header class="header">
        <div>
          <p class="header-label">当前范围</p>
          <strong>{{ appStore.projectScope }}</strong>
          <p class="header-user">当前账号：{{ appStore.username || '未获取' }} / {{ appStore.roleCode || '未获取' }}</p>
        </div>
        <el-button type="primary" plain @click="onLogout">退出登录</el-button>
      </header>
      <section class="content">
        <RouterView />
      </section>
    </main>
  </div>
</template>

<style scoped lang="scss">
.layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  background: var(--adm-bg);
}

.sidebar {
  padding: 24px 18px;
  background:
    linear-gradient(180deg, rgba(9, 53, 77, 0.98), rgba(17, 90, 110, 0.96)),
    var(--adm-panel-strong);
  color: #fff;
}

.brand-sub {
  margin: 0 0 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.brand h1 {
  margin: 0;
  font-size: 22px;
  line-height: 1.4;
}

.nav {
  display: grid;
  gap: 10px;
  margin-top: 28px;
}

.nav a {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.06);
}

.main {
  display: grid;
  grid-template-rows: auto 1fr;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  background: rgba(255, 255, 255, 0.88);
  border-bottom: 1px solid rgba(11, 57, 84, 0.08);
  backdrop-filter: blur(10px);
}

.header-label {
  margin: 0 0 4px;
  font-size: 12px;
  color: var(--adm-text-muted);
}

.header-user {
  margin: 6px 0 0;
  color: var(--adm-text-muted);
  font-size: 13px;
}

.content {
  padding: 24px;
}

@media (max-width: 960px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    padding-bottom: 12px;
  }
}
</style>
