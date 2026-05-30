<template>
  <div class="setting-container">
    <Sidebar />
    <main class="setting-main">
      <!-- 顶部状态栏 -->
      <TopHeader title="系统设置">
      </TopHeader>

      <!-- 主工作区 -->
      <main class="setting-content">
        <div class="setting-layout">
          <!-- 侧边导航 -->
          <aside class="setting-sidebar">
            <el-menu 
              :default-active="activeTab" 
              class="setting-menu"
              @select="handleTabChange"
            >
              <el-menu-item index="system">
                <el-icon><Setting /></el-icon>
                <span>系统设置</span>
              </el-menu-item>
              <el-menu-item index="users">
                <el-icon><UserFilled /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
              <el-menu-item index="services">
                <el-icon><Service /></el-icon>
                <span>服务管理</span>
              </el-menu-item>
              <el-menu-item index="notifications">
                <el-icon><Bell /></el-icon>
                <span>通知设置</span>
              </el-menu-item>
              <el-menu-item index="logs">
                <el-icon><Files /></el-icon>
                <span>日志管理</span>
              </el-menu-item>
            </el-menu>
          </aside>

          <!-- 内容区域 -->
          <div class="setting-panel">
            <!-- 系统设置 -->
            <div v-show="activeTab === 'system'" class="tab-content">
              <div class="panel-header">
                <h2>系统设置</h2>
                <p>配置系统基本参数和AI相关设置</p>
              </div>

              <el-form :model="systemSettings" label-width="140px" class="setting-form">
                <el-form-item label="系统名称">
                  <el-input v-model="systemSettings.systemName" placeholder="请输入系统名称" />
                </el-form-item>
                <el-form-item label="系统描述">
                  <el-textarea v-model="systemSettings.systemDescription" rows="3" placeholder="请输入系统描述" />
                </el-form-item>
                <el-form-item label="系统主题">
                  <el-select v-model="systemSettings.theme">
                    <el-option label="明亮主题" value="light" />
                    <el-option label="暗黑主题" value="dark" />
                    <el-option label="自动切换" value="auto" />
                  </el-select>
                </el-form-item>

                <div class="section-divider">
                  <h3>AI配置</h3>
                </div>

                <el-form-item label="LLM服务地址">
                  <el-input v-model="systemSettings.llmEndpoint" placeholder="请输入LLM服务地址" />
                </el-form-item>
                <el-form-item label="API密钥">
                  <el-input 
                    v-model="systemSettings.apiKey" 
                    type="password" 
                    placeholder="请输入API密钥"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="模型名称">
                  <el-input v-model="systemSettings.modelName" placeholder="请输入模型名称" />
                </el-form-item>
                <el-form-item label="最大响应长度">
                  <el-input-number v-model="systemSettings.maxTokens" :min="100" :max="10000" />
                </el-form-item>
                <el-form-item label="温度系数">
                  <el-slider v-model="systemSettings.temperature" :min="0" :max="1" :step="0.1" />
                  <span class="slider-value">{{ systemSettings.temperature }}</span>
                </el-form-item>

                <div class="section-divider">
                  <h3>向量数据库配置</h3>
                </div>

                <el-form-item label="数据库类型">
                  <el-select v-model="systemSettings.vectorDbType">
                    <el-option label="PGVector" value="pgvector" />
                    <el-option label="Milvus" value="milvus" />
                    <el-option label="Pinecone" value="pinecone" />
                  </el-select>
                </el-form-item>
                <el-form-item label="数据库地址">
                  <el-input v-model="systemSettings.dbHost" placeholder="请输入数据库地址" />
                </el-form-item>
                <el-form-item label="端口">
                  <el-input-number v-model="systemSettings.dbPort" :min="1" :max="65535" />
                </el-form-item>
                <el-form-item label="数据库名称">
                  <el-input v-model="systemSettings.dbName" placeholder="请输入数据库名称" />
                </el-form-item>
                <el-form-item label="用户名">
                  <el-input v-model="systemSettings.dbUsername" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码">
                  <el-input 
                    v-model="systemSettings.dbPassword" 
                    type="password" 
                    placeholder="请输入密码"
                    show-password
                  />
                </el-form-item>

                <div class="form-actions">
                  <el-button type="primary" @click="saveSystemSettings">保存设置</el-button>
                  <el-button @click="resetSystemSettings">重置</el-button>
                </div>
              </el-form>
            </div>

            <!-- 用户管理 -->
            <div v-show="activeTab === 'users'" class="tab-content">
              <div class="panel-header">
                <h2>用户管理</h2>
                <p>管理系统用户和角色权限</p>
              </div>

              <div class="toolbar">
                <div class="search-box">
                  <el-icon><Search /></el-icon>
                  <input type="text" v-model="userSearchQuery" placeholder="搜索用户..." />
                </div>
                <el-button type="primary" @click="openAddUserModal">
                  <el-icon><Plus /></el-icon>添加用户
                </el-button>
              </div>

              <el-table :data="filteredUsers" border style="width: 100%">
                <el-table-column prop="name" label="姓名" width="120" />
                <el-table-column prop="email" label="邮箱" width="200" />
                <el-table-column prop="phone" label="电话" width="140" />
                <el-table-column prop="role" label="角色" width="120">
                  <template #default="scope">
                    <el-tag :type="getRoleTagType(scope.row.role)">
                      {{ getRoleText(scope.row.role) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">
                      {{ scope.row.status === 'ACTIVE' ? '活跃' : '禁用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="创建时间" width="160" />
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
                    <el-button 
                      size="small" 
                      :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'"
                      @click="toggleUserStatus(scope.row)"
                    >
                      {{ scope.row.status === 'ACTIVE' ? '禁用' : '启用' }}
                    </el-button>
                    <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>

              <!-- 添加用户模态框 -->
              <el-dialog 
                v-model="showAddUserModal" 
                :title="editingUser ? '编辑用户' : '添加用户'" 
                width="400px"
              >
                <el-form :model="newUser" label-width="100px">
                  <el-form-item label="姓名">
                    <el-input v-model="newUser.name" placeholder="请输入姓名" />
                  </el-form-item>
                  <el-form-item label="邮箱">
                    <el-input v-model="newUser.email" placeholder="请输入邮箱" />
                  </el-form-item>
                  <el-form-item label="电话">
                    <el-input v-model="newUser.phone" placeholder="请输入电话" />
                  </el-form-item>
                  <el-form-item label="角色">
                    <el-select v-model="newUser.role">
                      <el-option label="一线运维人员" value="staff" />
                      <el-option label="值班人员" value="oncall" />
                      <el-option label="管理人员" value="manager" />
                      <el-option label="系统管理员" value="admin" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="密码" v-if="!editingUser">
                    <el-input 
                      v-model="newUser.password" 
                      type="password" 
                      placeholder="请输入密码"
                    />
                  </el-form-item>
                </el-form>
                <template #footer>
                  <el-button @click="closeAddUserModal">取消</el-button>
                  <el-button type="primary" @click="saveUser">保存</el-button>
                </template>
              </el-dialog>
            </div>

            <!-- 服务管理 -->
            <div v-show="activeTab === 'services'" class="tab-content">
              <div class="panel-header">
                <h2>服务管理</h2>
                <p>管理运维服务目录和SLA配置</p>
              </div>

              <div class="toolbar">
                <div class="search-box">
                  <el-icon><Search /></el-icon>
                  <input type="text" v-model="serviceSearchQuery" placeholder="搜索服务..." />
                </div>
                <el-button type="primary" @click="openAddServiceModal">
                  <el-icon><Plus /></el-icon>添加服务
                </el-button>
              </div>

              <el-table :data="filteredServices" border style="width: 100%">
                <el-table-column prop="name" label="服务名称" width="180" />
                <el-table-column prop="code" label="服务编码" width="120" />
                <el-table-column prop="category" label="服务分类" width="120">
                  <template #default="scope">
                    <el-tag type="purple">{{ scope.row.category }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="sla" label="SLA" width="100">
                  <template #default="scope">
                    <el-tag :type="getSlaTagType(scope.row.sla)">{{ scope.row.sla }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" width="200" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">
                      {{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button size="small" @click="editService(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteService(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>

              <!-- 添加服务模态框 -->
              <el-dialog 
                v-model="showAddServiceModal" 
                :title="editingService ? '编辑服务' : '添加服务'" 
                width="400px"
              >
                <el-form :model="newService" label-width="100px">
                  <el-form-item label="服务名称">
                    <el-input v-model="newService.name" placeholder="请输入服务名称" />
                  </el-form-item>
                  <el-form-item label="服务编码">
                    <el-input v-model="newService.code" placeholder="请输入服务编码" />
                  </el-form-item>
                  <el-form-item label="服务分类">
                    <el-select v-model="newService.category">
                      <el-option label="基础设施" value="infrastructure" />
                      <el-option label="应用服务" value="application" />
                      <el-option label="数据库" value="database" />
                      <el-option label="网络" value="network" />
                      <el-option label="安全" value="security" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="SLA级别">
                    <el-select v-model="newService.sla">
                      <el-option label="P0-紧急" value="P0" />
                      <el-option label="P1-高" value="P1" />
                      <el-option label="P2-中" value="P2" />
                      <el-option label="P3-低" value="P3" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="描述">
                    <el-textarea v-model="newService.description" rows="3" placeholder="请输入服务描述" />
                  </el-form-item>
                </el-form>
                <template #footer>
                  <el-button @click="closeAddServiceModal">取消</el-button>
                  <el-button type="primary" @click="saveService">保存</el-button>
                </template>
              </el-dialog>
            </div>

            <!-- 通知设置 -->
            <div v-show="activeTab === 'notifications'" class="tab-content">
              <div class="panel-header">
                <h2>通知设置</h2>
                <p>配置值班提醒和通知方式</p>
              </div>

              <el-form :model="notificationSettings" label-width="140px" class="setting-form">
                <div class="section-divider">
                  <h3>值班提醒设置</h3>
                </div>

                <el-form-item label="值班前提醒时间">
                  <el-select v-model="notificationSettings.oncallRemindBefore">
                    <el-option label="15分钟前" value="15" />
                    <el-option label="30分钟前" value="30" />
                    <el-option label="1小时前" value="60" />
                    <el-option label="2小时前" value="120" />
                  </el-select>
                </el-form-item>
                <el-form-item label="每日值班提醒">
                  <el-switch v-model="notificationSettings.dailyReminder" />
                </el-form-item>
                <el-form-item label="提醒时间">
                  <el-time-picker v-model="notificationSettings.dailyReminderTime" />
                </el-form-item>

                <div class="section-divider">
                  <h3>通知方式</h3>
                </div>

                <el-form-item label="邮件通知">
                  <el-switch v-model="notificationSettings.emailEnabled" />
                </el-form-item>
                <el-form-item label="SMTP服务器">
                  <el-input v-model="notificationSettings.smtpHost" placeholder="请输入SMTP服务器地址" />
                </el-form-item>
                <el-form-item label="SMTP端口">
                  <el-input-number v-model="notificationSettings.smtpPort" :min="1" :max="65535" />
                </el-form-item>
                <el-form-item label="发送邮箱">
                  <el-input v-model="notificationSettings.emailFrom" placeholder="请输入发送邮箱" />
                </el-form-item>
                <el-form-item label="邮箱密码">
                  <el-input 
                    v-model="notificationSettings.emailPassword" 
                    type="password" 
                    placeholder="请输入邮箱密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item label="短信通知">
                  <el-switch v-model="notificationSettings.smsEnabled" />
                </el-form-item>
                <el-form-item label="短信API地址">
                  <el-input v-model="notificationSettings.smsApiUrl" placeholder="请输入短信API地址" />
                </el-form-item>
                <el-form-item label="短信API密钥">
                  <el-input 
                    v-model="notificationSettings.smsApiKey" 
                    type="password" 
                    placeholder="请输入短信API密钥"
                    show-password
                  />
                </el-form-item>

                <el-form-item label="企业微信通知">
                  <el-switch v-model="notificationSettings.wechatEnabled" />
                </el-form-item>
                <el-form-item label="企业微信Webhook">
                  <el-input v-model="notificationSettings.wechatWebhook" placeholder="请输入企业微信Webhook地址" />
                </el-form-item>

                <div class="form-actions">
                  <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
                  <el-button @click="resetNotificationSettings">重置</el-button>
                </div>
              </el-form>
            </div>

            <!-- 日志管理 -->
            <div v-show="activeTab === 'logs'" class="tab-content">
              <div class="panel-header">
                <h2>日志管理</h2>
                <p>查看系统操作日志</p>
              </div>

              <div class="toolbar">
                <div class="filter-row">
                  <el-select v-model="logFilter.type" placeholder="日志类型" class="filter-select">
                    <el-option label="全部" value="" />
                    <el-option label="登录" value="login" />
                    <el-option label="操作" value="operation" />
                    <el-option label="错误" value="error" />
                    <el-option label="系统" value="system" />
                  </el-select>
                  <el-select v-model="logFilter.level" placeholder="日志级别" class="filter-select">
                    <el-option label="全部" value="" />
                    <el-option label="INFO" value="info" />
                    <el-option label="WARN" value="warn" />
                    <el-option label="ERROR" value="error" />
                  </el-select>
                  <el-date-picker 
                    v-model="logFilter.dateRange" 
                    type="daterange" 
                    range-separator="至" 
                    start-placeholder="开始日期" 
                    end-placeholder="结束日期"
                    class="filter-date"
                  />
                  <input type="text" v-model="logFilter.keyword" placeholder="搜索关键词..." class="filter-input" />
                  <el-button type="primary" @click="searchLogs">搜索</el-button>
                  <el-button @click="exportLogs">导出日志</el-button>
                </div>
              </div>

              <el-table :data="logs" border style="width: 100%">
                <el-table-column prop="timestamp" label="时间" width="180" />
                <el-table-column prop="type" label="类型" width="100">
                  <template #default="scope">
                    <el-tag :type="getLogTypeTagType(scope.row.type)">{{ getLogTypeText(scope.row.type) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="level" label="级别" width="80">
                  <template #default="scope">
                    <el-tag :type="getLogLevelTagType(scope.row.level)">{{ scope.row.level }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="user" label="操作用户" width="120" />
                <el-table-column prop="action" label="操作描述" width="300" />
                <el-table-column prop="ip" label="IP地址" width="140" />
                <el-table-column prop="details" label="详情" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="showLogDetail(scope.row)">查看详情</el-button>
                  </template>
                </el-table-column>
              </el-table>

              <el-pagination
                @size-change="handleLogSizeChange"
                @current-change="handleLogCurrentChange"
                :current-page="logPage.current"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="logPage.size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="logPage.total"
              />

              <!-- 日志详情模态框 -->
              <el-dialog v-model="showLogDetailModal" title="日志详情" width="600px">
                <div v-if="currentLog" class="log-detail">
                  <div class="detail-row">
                    <label>时间:</label>
                    <span>{{ currentLog.timestamp }}</span>
                  </div>
                  <div class="detail-row">
                    <label>类型:</label>
                    <span>{{ getLogTypeText(currentLog.type) }}</span>
                  </div>
                  <div class="detail-row">
                    <label>级别:</label>
                    <span>{{ currentLog.level }}</span>
                  </div>
                  <div class="detail-row">
                    <label>用户:</label>
                    <span>{{ currentLog.user }}</span>
                  </div>
                  <div class="detail-row">
                    <label>IP地址:</label>
                    <span>{{ currentLog.ip }}</span>
                  </div>
                  <div class="detail-row">
                    <label>操作:</label>
                    <span>{{ currentLog.action }}</span>
                  </div>
                  <div class="detail-row">
                    <label>详情:</label>
                    <pre>{{ currentLog.details }}</pre>
                  </div>
                </div>
              </el-dialog>
            </div>
          </div>
        </div>
      </main>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Sidebar from '../components/Sidebar.vue'
import TopHeader from '../components/TopHeader.vue'
import { 
  Setting, UserFilled, Service, Bell, Files, 
  Search, Plus 
} from '@element-plus/icons-vue'

// 当前激活的标签页
const activeTab = ref('system')

// 系统设置
const systemSettings = reactive({
  systemName: '智维 Agent',
  systemDescription: '智能运维Agent系统，提供智能化的运维问答服务',
  theme: 'light',
  llmEndpoint: 'http://localhost:9988/processing/catman/conversation/chat/chat',
  apiKey: '',
  modelName: 'gpt-4',
  maxTokens: 2000,
  temperature: 0.7,
  vectorDbType: 'pgvector',
  dbHost: 'localhost',
  dbPort: 5432,
  dbName: 'catman',
  dbUsername: 'admin',
  dbPassword: ''
})

// 用户管理
const userSearchQuery = ref('')
const showAddUserModal = ref(false)
const editingUser = ref(false)
const newUser = reactive({
  name: '',
  email: '',
  phone: '',
  role: 'staff',
  password: ''
})

const users = ref([
  { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', phone: '13800138001', role: 'admin', status: 'ACTIVE', createdAt: '2024-01-15' },
  { id: 'user-2', name: '李娜', email: 'lina@example.com', phone: '13800138002', role: 'manager', status: 'ACTIVE', createdAt: '2024-01-16' },
  { id: 'user-3', name: '王强', email: 'wangqiang@example.com', phone: '13800138003', role: 'oncall', status: 'ACTIVE', createdAt: '2024-01-17' },
  { id: 'user-4', name: '陈丽', email: 'chenli@example.com', phone: '13800138004', role: 'staff', status: 'ACTIVE', createdAt: '2024-01-18' },
  { id: 'user-5', name: '赵刚', email: 'zhaogang@example.com', phone: '13800138005', role: 'staff', status: 'INACTIVE', createdAt: '2024-01-19' }
])

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch = !userSearchQuery.value ||
      user.name.toLowerCase().includes(userSearchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(userSearchQuery.value.toLowerCase()) ||
      user.phone.includes(userSearchQuery.value)
    return matchesSearch
  })
})

// 服务管理
const serviceSearchQuery = ref('')
const showAddServiceModal = ref(false)
const editingService = ref(false)
const newService = reactive({
  name: '',
  code: '',
  category: 'infrastructure',
  sla: 'P2',
  description: '',
  status: 'ACTIVE'
})

const services = ref([
  { id: 'svc-1', name: 'API网关服务', code: 'APIGW', category: 'infrastructure', sla: 'P1', description: '提供API路由和负载均衡', status: 'ACTIVE' },
  { id: 'svc-2', name: '数据库服务', code: 'DB', category: 'database', sla: 'P0', description: 'PostgreSQL数据库服务', status: 'ACTIVE' },
  { id: 'svc-3', name: '认证服务', code: 'AUTH', category: 'application', sla: 'P1', description: '用户认证和权限管理', status: 'ACTIVE' },
  { id: 'svc-4', name: '监控服务', code: 'MON', category: 'infrastructure', sla: 'P2', description: '系统监控和告警', status: 'ACTIVE' },
  { id: 'svc-5', name: '安全服务', code: 'SEC', category: 'security', sla: 'P1', description: '安全审计和防护', status: 'INACTIVE' }
])

const filteredServices = computed(() => {
  return services.value.filter(service => {
    const matchesSearch = !serviceSearchQuery.value ||
      service.name.toLowerCase().includes(serviceSearchQuery.value.toLowerCase()) ||
      service.code.toLowerCase().includes(serviceSearchQuery.value.toLowerCase())
    return matchesSearch
  })
})

// 通知设置
const notificationSettings = reactive({
  oncallRemindBefore: '30',
  dailyReminder: true,
  dailyReminderTime: '08:00',
  emailEnabled: true,
  smtpHost: 'smtp.example.com',
  smtpPort: 587,
  emailFrom: 'notifications@example.com',
  emailPassword: '',
  smsEnabled: false,
  smsApiUrl: '',
  smsApiKey: '',
  wechatEnabled: true,
  wechatWebhook: 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx'
})

// 日志管理
const logFilter = reactive({
  type: '',
  level: '',
  dateRange: [],
  keyword: ''
})

const logPage = reactive({
  current: 1,
  size: 10,
  total: 100
})

const showLogDetailModal = ref(false)
const currentLog = ref(null)

const logs = ref([
  { id: '1', timestamp: '2024-01-20 15:30:25', type: 'login', level: 'INFO', user: '张伟', action: '用户登录系统', ip: '192.168.1.100', details: '{ "userId": "user-1", "loginTime": "2024-01-20 15:30:25" }' },
  { id: '2', timestamp: '2024-01-20 15:28:10', type: 'operation', level: 'INFO', user: '李娜', action: '创建知识库: API网关文档', ip: '192.168.1.101', details: '{ "knowledgeBaseId": "kb-1", "name": "API网关文档" }' },
  { id: '3', timestamp: '2024-01-20 15:25:45', type: 'operation', level: 'WARN', user: '王强', action: '工单转派失败', ip: '192.168.1.102', details: '{ "ticketId": "TKT-001", "error": "目标用户不存在" }' },
  { id: '4', timestamp: '2024-01-20 15:20:00', type: 'system', level: 'INFO', user: 'system', action: '系统定时任务执行', ip: 'localhost', details: '{ "task": "向量库同步", "status": "success" }' },
  { id: '5', timestamp: '2024-01-20 15:15:30', type: 'error', level: 'ERROR', user: '陈丽', action: '文件上传失败', ip: '192.168.1.103', details: '{ "fileName": "test.pdf", "error": "文件大小超过限制" }' }
])

// 方法
const handleTabChange = (tab) => {
  activeTab.value = tab
}

// 系统设置
const saveSystemSettings = () => {
  ElMessage.success('系统设置已保存')
}

const resetSystemSettings = () => {
  systemSettings.systemName = '智维 Agent'
  systemSettings.systemDescription = '智能运维Agent系统，提供智能化的运维问答服务'
  systemSettings.theme = 'light'
  systemSettings.llmEndpoint = 'http://localhost:9988/processing/catman/conversation/chat/chat'
  systemSettings.apiKey = ''
  systemSettings.modelName = 'gpt-4'
  systemSettings.maxTokens = 2000
  systemSettings.temperature = 0.7
  systemSettings.vectorDbType = 'pgvector'
  systemSettings.dbHost = 'localhost'
  systemSettings.dbPort = 5432
  systemSettings.dbName = 'catman'
  systemSettings.dbUsername = 'admin'
  systemSettings.dbPassword = ''
}

// 用户管理
const getRoleText = (role) => {
  switch (role) {
    case 'staff': return '一线运维人员'
    case 'oncall': return '值班人员'
    case 'manager': return '管理人员'
    case 'admin': return '系统管理员'
    default: return role
  }
}

const getRoleTagType = (role) => {
  switch (role) {
    case 'admin': return 'danger'
    case 'manager': return 'warning'
    case 'oncall': return 'success'
    default: return 'info'
  }
}

const openAddUserModal = () => {
  editingUser.value = false
  newUser.name = ''
  newUser.email = ''
  newUser.phone = ''
  newUser.role = 'staff'
  newUser.password = ''
  showAddUserModal.value = true
}

const editUser = (user) => {
  editingUser.value = true
  newUser.name = user.name
  newUser.email = user.email
  newUser.phone = user.phone
  newUser.role = user.role
  newUser.id = user.id
  showAddUserModal.value = true
}

const saveUser = () => {
  if (!newUser.name || !newUser.email || !newUser.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!editingUser.value && !newUser.password) {
    ElMessage.warning('请设置密码')
    return
  }
  
  if (editingUser.value) {
    const index = users.value.findIndex(u => u.id === newUser.id)
    if (index !== -1) {
      users.value[index] = {
        ...users.value[index],
        name: newUser.name,
        email: newUser.email,
        phone: newUser.phone,
        role: newUser.role
      }
    }
  } else {
    users.value.push({
      id: `user-${Date.now()}`,
      name: newUser.name,
      email: newUser.email,
      phone: newUser.phone,
      role: newUser.role,
      status: 'ACTIVE',
      createdAt: new Date().toLocaleDateString('zh-CN')
    })
  }
  
  closeAddUserModal()
  ElMessage.success('保存成功')
}

const closeAddUserModal = () => {
  showAddUserModal.value = false
  editingUser.value = false
  newUser.id = ''
}

const toggleUserStatus = (user) => {
  user.status = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  ElMessage.success(`用户${user.status === 'ACTIVE' ? '已启用' : '已禁用'}`)
}

const deleteUser = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    users.value = users.value.filter(u => u.id !== id)
    ElMessage.success('删除成功')
  })
}

// 服务管理
const getSlaTagType = (sla) => {
  switch (sla) {
    case 'P0': return 'danger'
    case 'P1': return 'warning'
    case 'P2': return 'info'
    case 'P3': return 'success'
    default: return 'info'
  }
}

const openAddServiceModal = () => {
  editingService.value = false
  newService.name = ''
  newService.code = ''
  newService.category = 'infrastructure'
  newService.sla = 'P2'
  newService.description = ''
  showAddServiceModal.value = true
}

const editService = (service) => {
  editingService.value = true
  newService.id = service.id
  newService.name = service.name
  newService.code = service.code
  newService.category = service.category
  newService.sla = service.sla
  newService.description = service.description
  showAddServiceModal.value = true
}

const saveService = () => {
  if (!newService.name || !newService.code) {
    ElMessage.warning('请填写服务名称和编码')
    return
  }
  
  if (editingService.value) {
    const index = services.value.findIndex(s => s.id === newService.id)
    if (index !== -1) {
      services.value[index] = {
        ...services.value[index],
        name: newService.name,
        code: newService.code,
        category: newService.category,
        sla: newService.sla,
        description: newService.description
      }
    }
  } else {
    services.value.push({
      id: `svc-${Date.now()}`,
      name: newService.name,
      code: newService.code,
      category: newService.category,
      sla: newService.sla,
      description: newService.description,
      status: 'ACTIVE'
    })
  }
  
  closeAddServiceModal()
  ElMessage.success('保存成功')
}

const closeAddServiceModal = () => {
  showAddServiceModal.value = false
  editingService.value = false
  newService.id = ''
}

const deleteService = (id) => {
  ElMessageBox.confirm('确定要删除该服务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    services.value = services.value.filter(s => s.id !== id)
    ElMessage.success('删除成功')
  })
}

// 通知设置
const saveNotificationSettings = () => {
  ElMessage.success('通知设置已保存')
}

const resetNotificationSettings = () => {
  notificationSettings.oncallRemindBefore = '30'
  notificationSettings.dailyReminder = true
  notificationSettings.dailyReminderTime = '08:00'
  notificationSettings.emailEnabled = true
  notificationSettings.smtpHost = 'smtp.example.com'
  notificationSettings.smtpPort = 587
  notificationSettings.emailFrom = 'notifications@example.com'
  notificationSettings.emailPassword = ''
  notificationSettings.smsEnabled = false
  notificationSettings.smsApiUrl = ''
  notificationSettings.smsApiKey = ''
  notificationSettings.wechatEnabled = true
  notificationSettings.wechatWebhook = 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx'
}

// 日志管理
const getLogTypeText = (type) => {
  switch (type) {
    case 'login': return '登录'
    case 'operation': return '操作'
    case 'error': return '错误'
    case 'system': return '系统'
    default: return type
  }
}

const getLogTypeTagType = (type) => {
  switch (type) {
    case 'error': return 'danger'
    case 'operation': return 'primary'
    case 'login': return 'success'
    case 'system': return 'info'
    default: return 'info'
  }
}

const getLogLevelTagType = (level) => {
  switch (level) {
    case 'ERROR': return 'danger'
    case 'WARN': return 'warning'
    case 'INFO': return 'success'
    default: return 'info'
  }
}

const searchLogs = () => {
  ElMessage.info('日志搜索功能')
}

const exportLogs = () => {
  ElMessage.info('日志导出功能')
}

const showLogDetail = (log) => {
  currentLog.value = log
  showLogDetailModal.value = true
}

const handleLogSizeChange = (size) => {
  logPage.size = size
}

const handleLogCurrentChange = (page) => {
  logPage.current = page
}
</script>

<style scoped>
.setting-container {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.setting-main {
  flex: 1;
  margin-left: 256px;
}

.setting-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
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
}

.setting-content {
  padding: 24px;
}

.setting-layout {
  display: flex;
  gap: 24px;
}

.setting-sidebar {
  width: 200px;
  flex-shrink: 0;
}

.setting-menu {
  background: #fff;
  border-radius: 12px;
  padding: 8px;
}

.setting-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

.panel-header {
  margin-bottom: 24px;
}

.panel-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.panel-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.setting-form {
  max-width: 600px;
}

.section-divider {
  margin: 24px 0;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.section-divider h3 {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f3f4f6;
  border-radius: 8px;
  padding: 8px 12px;
  width: 240px;
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-select {
  width: 140px;
}

.filter-date {
  width: 260px;
}

.filter-input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  width: 200px;
}

.slider-value {
  margin-left: 12px;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.tab-content {
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.log-detail {
  font-size: 14px;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
}

.detail-row label {
  width: 80px;
  font-weight: 500;
  color: #6b7280;
}

.detail-row span {
  flex: 1;
  color: #1f2937;
}

.detail-row pre {
  flex: 1;
  background: #f3f4f6;
  padding: 12px;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
}
</style>