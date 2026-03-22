import { createRouter, createWebHistory } from 'vue-router'
import { getAdminToken } from '@/utils/storage'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'admin-login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { title: '后台登录', requiresAuth: false },
    },
    {
      path: '/',
      component: () => import('@/layouts/AppLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: { title: '工作台' },
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const token = getAdminToken()
  const requiresAuth = Boolean(to.meta.requiresAuth)
  if (requiresAuth && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && token) {
    return { path: '/' }
  }
  return true
})

router.afterEach((to) => {
  if (typeof to.meta.title === 'string') {
    document.title = `${to.meta.title} - 材料回收积分后台`
  }
})

export default router
