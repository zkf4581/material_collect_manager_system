import { defineStore } from 'pinia'

export const useAppStore = defineStore('admin-app', {
  state: () => ({
    appName: '工地材料回收积分系统',
    projectScope: '默认项目组',
  }),
})
