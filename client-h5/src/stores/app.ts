import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    appName: '工地材料回收积分',
    projectName: '演示项目',
  }),
})
