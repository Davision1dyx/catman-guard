<template>
  <aside class="sidebar">
    <!-- Logo 区 -->
    <div class="sidebar-logo">
      <div class="logo-container">
        <div class="logo-icon">
                  <img src="/catman.png" alt="CATMAN" />
                </div>
        <h1 class="logo-text">CATMAN</h1>
      </div>
    </div>

    <!-- 核心菜单 -->
    <nav class="sidebar-nav">
      <ul class="nav-list">
        <li>
          <a 
            href="/chat" 
            :class="['nav-item', { active: currentRoute === '/chat' }]"
          >
            <el-icon><Message /></el-icon>
            <span>智能问答</span>
          </a>
        </li>
        <li>
          <a
            href="/knowledge-base"
            :class="['nav-item', { active: currentRoute === '/knowledge-base' }]"
          >
            <el-icon><Collection /></el-icon>
            <span>知识库管理</span>
          </a>
        </li>
        <li>
          <a
            href="/ticket-center"
            :class="['nav-item', { active: currentRoute === '/ticket-center' }]"
          >
            <el-icon><List /></el-icon>
            <span>工单中心</span>
          </a>
        </li>
        <li>
          <a 
            href="/dashboard" 
            :class="['nav-item', { active: currentRoute === '/dashboard' }]"
          >
            <el-icon><PieChart /></el-icon>
            <span>数据看板</span>
          </a>
        </li>
        <li>
          <a
            href="/schedule"
            :class="['nav-item', { active: currentRoute === '/schedule' }]"
          >
            <el-icon><Calendar /></el-icon>
            <span>值班排班</span>
          </a>
        </li>
        <li>
          <a
            href="/settings"
            :class="['nav-item', { active: currentRoute === '/settings' }]"
          >
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </a>
        </li>
      </ul>
    </nav>

    <!-- 用户信息区 -->
    <div class="sidebar-footer">
      <div class="user-info">
        <div class="user-avatar">
          <el-icon><User /></el-icon>
        </div>
        <div class="user-details">
          <p class="user-name">张伟</p>
          <p class="user-role">值班人员</p>
        </div>
        <div class="online-status"></div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Message,
  Collection,
  PieChart,
  Calendar,
  Setting,
  User,
  Cpu,
  List
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const currentRoute = ref(route.path)

onMounted(() => {
  router.afterEach((to) => {
    currentRoute.value = to.path
  })
})
</script>

<style scoped>
.sidebar {
  width: 256px;
  height: 100vh;
  background: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: fixed;
  left: 0;
  top: 0;
  overflow-y: auto;
}

.sidebar-logo {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.logo-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.sidebar-nav {
  padding: 16px;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 8px;
  color: #4b5563;
  text-decoration: none;
  transition: all 0.15s ease;
  font-size: 14px;
}

.nav-item:hover {
  background: #f3f4f6;
  color: #374151;
}

.nav-item.active {
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 500;
}

.nav-item el-icon {
  font-size: 18px;
}

.sidebar-footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: #dbeafe;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  font-size: 16px;
}

.user-details {
  flex: 1;
}

.user-name {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.user-role {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.online-status {
  width: 10px;
  height: 10px;
  background: #22c55e;
  border-radius: 50%;
}
</style>
