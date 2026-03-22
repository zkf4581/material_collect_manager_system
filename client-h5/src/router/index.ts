import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: '首页', requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录', requiresAuth: false },
    },
    {
      path: '/recycle',
      name: 'recycle-create',
      component: () => import('@/views/RecycleCreateView.vue'),
      meta: { title: '回收登记', requiresAuth: true },
    },
    {
      path: '/records',
      name: 'recycle-records',
      component: () => import('@/views/RecycleRecordsView.vue'),
      meta: { title: '回收记录', requiresAuth: true },
    },
    {
      path: '/points',
      name: 'points',
      component: () => import('@/views/PointsView.vue'),
      meta: { title: '我的积分', requiresAuth: true },
    },
    {
      path: '/redeem',
      name: 'redeem',
      component: () => import('@/views/RedeemView.vue'),
      meta: { title: '积分兑换', requiresAuth: true },
    },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('h5_token')
  const requiresAuth = Boolean(to.meta.requiresAuth)
  if (requiresAuth && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && token) {
    return { path: '/' }
  }
  return true
})

export default router
