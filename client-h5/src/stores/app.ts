import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    appName: '工地材料回收积分',
    projectName: '演示项目',
    username: '',
    roleCode: '',
  }),
  actions: {
    setCurrentUser(payload: { username: string; roleCode: string; projectName?: string }) {
      this.username = payload.username
      this.roleCode = payload.roleCode
      if (payload.projectName) {
        this.projectName = payload.projectName
      }
    },
  },
})
