import { createRouter, createWebHistory } from 'vue-router'
import { useRouteStore } from '@/stores/index.ts'
import HomeView from '../views/HomeView.vue'
import  NProgress  from '@/utils/progress/index.ts'

const staticRoutes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/AboutView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: staticRoutes
})


// 路由拦截
router.beforeEach((to, from, next) => {
  const store = useRouteStore()
  // 获取pinia中储存的路由数据（1、固定值 2、从登录接口中获取储存的）
  // 若是使用vuex,取值方法类似

  const list = router.getRoutes()
  // 获取当前路由中routes信息，和routes长度对比，若是长度相等，则需要加载动态的路由
  // if (list.length == staticRoutes.length) {
  //   store.menuList.forEach((v) => {
  //     const val = {
  //       ...v,
  //       name: v.perms,
  //       meta: { title: v.menuName },
  //       component: eval(`() => import("${v.component}")`)
  //     }
  //     // perms、menuName、component是和后端约定的菜单信息字段，可能是其他值，根据具体情况觉得
  //     // component字段内容是‘/src/views/Personal/index.vue’。
  //     // 建议：perms、menuName字段直接使用name、title更方便。
  //     // 注意：component的引用方法eval()和routes中的引用区别
  //     router.addRoute(val)
  //   })
  // }

  // if (!!store.userInfo?.access_token) {
  //   // 根据是否有token(即store.userInfo.access_token)
  //   // 有登录信息
  //   if (to.matched.length > 0) {
  //     // 页面路径存在
  //     next()
  //   } else {
  //     // 页面路径不存在(判断是否是刷新的页面)
  //     const obj = router.getRoutes().find(v => v.path == to.path)
  //     // 此处判断可以用from中path判断，也可以判断obj是否存在
  //     if (!!obj) next(obj.path)
  //     else next('/NotFound')
  //     // 注意：此处判断必须做
  //     // 不做不影响第一次加载，若是刷新页面，则所有动态页面都会跳转404页面
  //   }
  // } else {
  //   // 无token登录信息，去登录页
  //   if (to.path == '/Login') {
  //     // 当前页面为登录页
  //     next()
  //   } else {
  //     // 当前页面为非登录页
  //     next('/Login')
  //   }
  // }
  NProgress.start()
  next()
})

router.afterEach((to, from) => {
  NProgress.done()
})



export default router
