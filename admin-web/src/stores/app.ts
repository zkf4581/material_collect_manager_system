import { defineStore } from 'pinia'

export const useAppStore = defineStore('admin-app', {
  state: () => ({
    appName: '工地材料回收积分系统',
    projectScope: '默认项目组',
    username: '',
    roleCode: '',
  }),
  actions: {
    setCurrentUser(payload: { username: string; roleCode: string; projectScope?: string }) {
      this.username = payload.username
      this.roleCode = payload.roleCode
      if (payload.projectScope) {
        this.projectScope = payload.projectScope
      }
    },
  },
})
