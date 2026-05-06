<template>
  <header class="top-header">
    <div class="header-left">
      <nav class="breadcrumb">
        <ol>
          <li>
            <el-icon><House /></el-icon>
            <span>首页</span>
          </li>
          <li>
            <el-icon><ArrowRight /></el-icon>
            <span>{{ title }}</span>
          </li>
        </ol>
      </nav>
    </div>
    <div class="header-right">
      <slot name="search"></slot>
      <slot name="actions"></slot>
    </div>
  </header>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  }
})

import { House, ArrowRight } from '@element-plus/icons-vue'
</script>

<style scoped>
.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  flex: 1;
}

.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #6b7280;
}

.breadcrumb ol {
  display: flex;
  align-items: center;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 8px;
}

.breadcrumb li {
  display: flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb li:first-child {
  color: #374151;
  cursor: pointer;
}

.breadcrumb li:first-child:hover {
  color: #2563eb;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>