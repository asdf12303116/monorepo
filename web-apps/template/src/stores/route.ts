import { defineStore } from 'pinia'

export const useRouteStore = defineStore('route', {
  state: () => {
    return { menuList: [] }
  },
  actions: {
    initRoute() {
      this.menuList = []
    },
  },
})
