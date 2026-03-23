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
        {
          path: 'recycle-records',
          name: 'recycle-records',
          component: () => import('@/views/recycle/RecycleRecordsView.vue'),
          meta: { title: '回收审核' },
        },
        {
          path: 'exchange-orders',
          name: 'exchange-orders',
          component: () => import('@/views/exchange/ExchangeOrdersView.vue'),
          meta: { title: '兑换审核' },
        },
        {
          path: 'goods',
          name: 'goods',
          component: () => import('@/views/goods/GoodsManageView.vue'),
          meta: { title: '商品管理' },
        },
        {
          path: 'projects',
          name: 'projects',
          component: () => import('@/views/project/ProjectManageView.vue'),
          meta: { title: '项目管理' },
        },
        {
          path: 'teams',
          name: 'teams',
          component: () => import('@/views/team/TeamManageView.vue'),
          meta: { title: '班组管理' },
        },
        {
          path: 'workers',
          name: 'workers',
          component: () => import('@/views/worker/WorkerManageView.vue'),
          meta: { title: '工人管理' },
        },
        {
          path: 'point-rules',
          name: 'point-rules',
          component: () => import('@/views/rule/PointRuleManageView.vue'),
          meta: { title: '积分规则' },
        },
        {
          path: 'materials',
          name: 'materials',
          component: () => import('@/views/material/MaterialManageView.vue'),
          meta: { title: '材料管理' },
        },
        {
          path: 'reports',
          name: 'reports',
          component: () => import('@/views/report/ReportView.vue'),
          meta: { title: '基础报表' },
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
