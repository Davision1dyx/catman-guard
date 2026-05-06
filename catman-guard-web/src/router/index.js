import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/chat'
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('../views/ChatView.vue')
  },
  {
    path: '/knowledge-base',
    name: 'KnowledgeBase',
    component: () => import('../views/KnowledgeBaseView.vue')
  },
  {
    path: '/ticket-center',
    name: 'TicketCenter',
    component: () => import('../views/TicketCenterView.vue')
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: () => import('../views/ScheduleView.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/SettingView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
