<template>
  <div class="ticket-container">
    <Sidebar />
    <main class="ticket-main">
      <!-- 顶部状态栏 -->
      <TopHeader title="工单中心">
        <template #search>
          <div class="search-box">
            <el-input 
              v-model="searchQuery" 
              placeholder="搜索工单、标题或内容..." 
              prefix-icon="Search"
              class="search-input"
            />
          </div>
        </template>
        <template #actions>
          <button class="notification-btn" @click="toggleNotifications">
            <el-icon><Bell /></el-icon>
            <span class="badge">3</span>
          </button>
        </template>
      </TopHeader>

      <!-- 主工作区 -->
      <main class="ticket-content">
        <!-- 页面头部 -->
        <div class="page-header">
          <div>
            <h1 class="page-title">工单中心</h1>
            <p class="page-subtitle">管理系统中的所有工单，跟踪处理进度和状态</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="createNewTicket">
              <el-icon><Plus /></el-icon>
              新建工单
            </el-button>
          </div>
        </div>

        <!-- 工单筛选器 -->
        <div class="filter-panel">
          <div class="filter-row">
            <div class="filter-item">
              <label>工单状态</label>
              <el-select v-model="filterStatus" placeholder="全部状态">
                <el-option value="" label="全部状态" />
                <el-option value="ASSIGNED" label="已派单" />
                <el-option value="IN_PROGRESS" label="处理中" />
                <el-option value="COMPLETED" label="完成" />
                <el-option value="VECTOR_STORED" label="向量存储" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>优先级</label>
              <el-select v-model="filterPriority" placeholder="全部优先级">
                <el-option value="" label="全部优先级" />
                <el-option value="LOW" label="低" />
                <el-option value="MEDIUM" label="中" />
                <el-option value="HIGH" label="高" />
                <el-option value="CRITICAL" label="紧急" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>负责人</label>
              <el-select v-model="filterAssignee" placeholder="全部负责人">
                <el-option value="" label="全部负责人" />
                <el-option value="张伟" label="张伟" />
                <el-option value="李娜" label="李娜" />
                <el-option value="王强" label="王强" />
                <el-option value="陈丽" label="陈丽" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>工单类型</label>
              <el-select v-model="filterType" placeholder="全部类型">
                <el-option value="" label="全部类型" />
                <el-option value="INCIDENT" label="故障报告" />
                <el-option value="REQUEST" label="服务请求" />
                <el-option value="PROBLEM" label="问题分析" />
                <el-option value="CHANGE" label="变更请求" />
              </el-select>
            </div>
          </div>
        </div>

        <!-- 工单列表 -->
        <div class="ticket-list-panel">
          <div class="panel-header">
            <h2 class="panel-title">工单列表</h2>
          </div>
          
          <el-table 
            :data="filteredTickets" 
            border 
            style="width: 100%"
            :empty-text="'暂无符合条件的工单'"
          >
            <el-table-column prop="id" label="工单编号" width="120">
              <template #default="scope">
                <span class="ticket-id">#{{ scope.row.id }}</span>
              </template>
            </el-table-column>
            <el-table-column label="标题" min-width="200">
              <template #default="scope">
                <div class="ticket-title">{{ scope.row.title }}</div>
                <div class="ticket-desc">{{ scope.row.description }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="getTypeTagType(scope.row.type)">
                  {{ getTypeText(scope.row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="scope">
                <el-tag :type="getPriorityTagType(scope.row.priority)">
                  {{ getPriorityText(scope.row.priority) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提交人" width="100">
              <template #default="scope">{{ scope.row.submitter.name }}</template>
            </el-table-column>
            <el-table-column label="负责人" width="100">
              <template #default="scope">{{ scope.row.assignee.name || '-' }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="160">
              <template #default="scope">{{ formatDate(scope.row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button 
                    size="small" 
                    icon="Eye" 
                    @click="viewTicket(scope.row)"
                    title="查看详情"
                  />
                  <el-button 
                    size="small" 
                    icon="Edit" 
                    @click="editTicket(scope.row)"
                    :disabled="!canEditTicket(scope.row)"
                    :title="canEditTicket(scope.row) ? '编辑工单' : '无编辑权限'"
                  />
                  <el-button 
                    size="small" 
                    icon="UserFilled" 
                    @click="assignTicket(scope.row)"
                    :disabled="!canAssignTicket(scope.row)"
                    :title="canAssignTicket(scope.row) ? '分配工单' : '无分配权限'"
                  />
                  <el-button 
                    size="small" 
                    icon="Refresh" 
                    @click="updateTicketStatus(scope.row)"
                    :disabled="!canUpdateStatus(scope.row)"
                    :title="canUpdateStatus(scope.row) ? '更新状态' : '无更新权限'"
                  />
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </main>

      <!-- 工单详情模态框 -->
      <el-dialog 
        v-model="showTicketDetailModal" 
        :title="`工单详情 - #${currentTicket?.id}`" 
        width="800px"
        :close-on-click-modal="false"
      >
        <div v-if="currentTicket" class="detail-content">
          <div class="detail-grid">
            <div class="detail-item">
              <label>工单编号</label>
              <p class="value">#{{ currentTicket.id }}</p>
            </div>
            <div class="detail-item">
              <label>状态</label>
              <el-tag :type="getStatusTagType(currentTicket.status)">
                {{ getStatusText(currentTicket.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <label>优先级</label>
              <el-tag :type="getPriorityTagType(currentTicket.priority)">
                {{ getPriorityText(currentTicket.priority) }}
              </el-tag>
            </div>
          </div>

          <div class="detail-section">
            <label>标题</label>
            <p class="title-value">{{ currentTicket.title }}</p>
          </div>

          <div class="detail-section">
            <label>类型</label>
            <el-tag :type="getTypeTagType(currentTicket.type)">
              {{ getTypeText(currentTicket.type) }}
            </el-tag>
          </div>

          <div class="detail-section">
            <label>描述</label>
            <div class="desc-box">{{ currentTicket.description }}</div>
          </div>

          <div class="detail-grid">
            <div class="detail-item">
              <label>提交人</label>
              <div class="user-info">
                <div class="avatar submitter">
                  <el-icon><User /></el-icon>
                </div>
                <div class="user-detail">
                  <p>{{ currentTicket.submitter.name }}</p>
                  <p class="email">{{ currentTicket.submitter.email }}</p>
                </div>
              </div>
            </div>
            <div class="detail-item">
              <label>负责人</label>
              <div class="user-info">
                <div class="avatar assignee">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <div class="user-detail">
                  <p>{{ currentTicket.assignee.name || '未分配' }}</p>
                  <p class="email">{{ currentTicket.assignee.email || '-' }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <label>创建时间</label>
            <p>{{ formatDate(currentTicket.createdAt) }}</p>
          </div>

          <div class="detail-section">
            <label>附件</label>
            <div v-if="currentTicket.attachments && currentTicket.attachments.length > 0" class="attachments-list">
              <div v-for="attachment in currentTicket.attachments" :key="attachment.id" class="attachment-item">
                <el-icon><Link /></el-icon>
                <span>{{ attachment.name }}</span>
                <span class="size">({{ formatFileSize(attachment.size) }})</span>
                <el-button size="small" type="text" class="download-btn">
                  <el-icon><Download /></el-icon>下载
                </el-button>
              </div>
            </div>
            <div v-else class="empty-text">无附件</div>
          </div>

          <!-- 工单回复 -->
          <div class="detail-section">
            <label>工单回复</label>
            <div class="replies-list">
              <div v-for="reply in currentTicket.replies" :key="reply.id" class="reply-item">
                <div class="reply-header">
                  <div class="reply-author">
                    <div class="avatar reply">
                      <el-icon><User /></el-icon>
                    </div>
                    <div class="author-detail">
                      <p>{{ reply.author.name }}</p>
                      <p class="time">{{ formatDate(reply.createdAt) }}</p>
                    </div>
                  </div>
                  <el-tag size="small">{{ reply.type === 'COMMENT' ? '评论' : reply.type === 'UPDATE' ? '更新' : '解决' }}</el-tag>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
              </div>
            </div>

            <!-- 添加回复 -->
            <div v-if="canAddReply(currentTicket)" class="reply-form">
              <label>添加回复</label>
              <textarea 
                v-model="newReplyContent" 
                rows="4" 
                placeholder="请输入您的回复..."
                class="reply-input"
              ></textarea>
              <div class="reply-actions">
                <el-button 
                  type="primary" 
                  @click="addReply"
                  :disabled="!newReplyContent.trim()"
                >
                  发送回复
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <template #footer>
          <el-button @click="closeTicketDetailModal">关闭</el-button>
          <el-button 
            v-if="canEditTicket(currentTicket)" 
            type="success" 
            @click="editTicket(currentTicket)"
          >
            编辑工单
          </el-button>
          <el-button 
            v-if="canCloseTicket(currentTicket)" 
            type="danger" 
            @click="closeTicket"
          >
            关闭工单
          </el-button>
          <el-button 
            v-if="canStoreVector(currentTicket)" 
            type="warning" 
            @click="storeVector"
          >
            存储向量
          </el-button>
        </template>
      </el-dialog>

      <!-- 编辑工单模态框 -->
      <el-dialog 
        v-model="showEditTicketModal" 
        title="编辑工单" 
        width="500px"
        :close-on-click-modal="false"
      >
        <form v-if="editedTicket" @submit.prevent="saveEditedTicket">
          <div class="form-item">
            <label>工单标题</label>
            <el-input v-model="editedTicket.title" required />
          </div>
          <div class="form-item">
            <label>工单描述</label>
            <el-textarea v-model="editedTicket.description" rows="4" required />
          </div>
          <div class="form-row">
            <div class="form-item">
              <label>工单类型</label>
              <el-select v-model="editedTicket.type">
                <el-option value="INCIDENT" label="故障报告" />
                <el-option value="REQUEST" label="服务请求" />
                <el-option value="PROBLEM" label="问题分析" />
                <el-option value="CHANGE" label="变更请求" />
              </el-select>
            </div>
            <div class="form-item">
              <label>优先级</label>
              <el-select v-model="editedTicket.priority">
                <el-option value="LOW" label="低" />
                <el-option value="MEDIUM" label="中" />
                <el-option value="HIGH" label="高" />
                <el-option value="CRITICAL" label="紧急" />
              </el-select>
            </div>
          </div>
        </form>

        <template #footer>
          <el-button @click="closeEditTicketModal">取消</el-button>
          <el-button type="primary" @click="saveEditedTicket">保存更改</el-button>
        </template>
      </el-dialog>

      <!-- 分配工单模态框 -->
      <el-dialog 
        v-model="showAssignTicketModal" 
        :title="`分配工单 #${currentTicket?.id}`" 
        width="400px"
        :close-on-click-modal="false"
      >
        <form @submit.prevent="assignSelectedUser">
          <div class="form-item">
            <label>选择负责人</label>
            <el-select v-model="selectedAssignee" required>
              <el-option value="" label="请选择负责人" />
              <el-option v-for="user in users" :key="user.id" :value="user.id">
                {{ user.name }}
              </el-option>
            </el-select>
          </div>
        </form>

        <template #footer>
          <el-button @click="closeAssignTicketModal">取消</el-button>
          <el-button type="primary" @click="assignSelectedUser">确认分配</el-button>
        </template>
      </el-dialog>

      <!-- 状态更新模态框 -->
      <el-dialog 
        v-model="showStatusUpdateModal" 
        :title="`更新工单状态 #${currentTicket?.id}`" 
        width="400px"
        :close-on-click-modal="false"
      >
        <form @submit.prevent="updateSelectedStatus">
          <div class="form-item">
            <label>选择新状态</label>
            <el-select v-model="selectedStatus" required>
              <el-option value="" label="请选择状态" />
              <el-option value="ASSIGNED" label="已派单" />
              <el-option value="IN_PROGRESS" label="处理中" />
              <el-option value="COMPLETED" label="完成" />
              <el-option value="VECTOR_STORED" label="向量存储" />
            </el-select>
          </div>
          <div class="form-item">
            <label>备注</label>
            <el-textarea v-model="statusComment" rows="3" placeholder="请输入状态更新的备注信息..." />
          </div>
        </form>

        <template #footer>
          <el-button @click="closeStatusUpdateModal">取消</el-button>
          <el-button type="primary" @click="updateSelectedStatus">更新状态</el-button>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup lang="ts">import { ref, computed } from 'vue';
import Sidebar from '../components/Sidebar.vue';
import TopHeader from '../components/TopHeader.vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Bell, Plus, Eye, Edit, UserFilled, Refresh, User, Link, Download } from '@element-plus/icons-vue';
// 用户接口定义
interface User {
 id: string;
 name: string;
 email: string;
 role: 'admin' | 'staff' | 'manager';
}
// 工单接口定义
interface Ticket {
 id: string;
 title: string;
 description: string;
 type: 'INCIDENT' | 'REQUEST' | 'PROBLEM' | 'CHANGE';
 priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
 status: 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'VECTOR_STORED';
 submitter: User;
 assignee: User;
 createdAt: Date;
 updatedAt: Date;
 attachments: Attachment[];
 replies: Reply[];
}
interface Attachment {
 id: string;
 name: string;
 size: number;
 type: string;
 uploadedAt: Date;
}
interface Reply {
 id: string;
 author: User;
 content: string;
 type: 'COMMENT' | 'UPDATE' | 'RESOLUTION';
 createdAt: Date;
}
// 响应式数据
const showTicketDetailModal = ref(false);
const showEditTicketModal = ref(false);
const showAssignTicketModal = ref(false);
const showStatusUpdateModal = ref(false);
const currentTicket = ref<Ticket | null>(null);
const editedTicket = ref<Ticket | null>(null);
const newReplyContent = ref('');
const selectedAssignee = ref('');
const selectedStatus = ref('');
const statusComment = ref('');
// 筛选条件
const searchQuery = ref('');
const filterStatus = ref('');
const filterPriority = ref('');
const filterAssignee = ref('');
const filterType = ref('');
// 用户列表
const users: User[] = [
 { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 { id: 'user-2', name: '李娜', email: 'lina@example.com', role: 'staff' },
 { id: 'user-3', name: '王强', email: 'wangqiang@example.com', role: 'staff' },
 { id: 'user-4', name: '陈丽', email: 'chenli@example.com', role: 'manager' },
 { id: 'user-5', name: '赵刚', email: 'zhaogang@example.com', role: 'admin' }
];
// 模拟工单数据
const tickets = ref<Ticket[]>([
 {
 id: 'TKT-001',
 title: 'API网关服务响应超时',
 description: '用户反馈API网关在高峰时段响应时间超过5秒，影响业务正常运行。初步判断可能是负载过高导致。',
 type: 'INCIDENT',
 priority: 'HIGH',
 status: 'IN_PROGRESS',
 submitter: { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 assignee: { id: 'user-2', name: '李娜', email: 'lina@example.com', role: 'staff' },
 createdAt: new Date(Date.now() - 86400000 * 1),
 updatedAt: new Date(Date.now() - 3600000 * 2),
 attachments: [
 { id: 'att-1', name: 'error_logs.txt', size: 10240, type: 'text/plain', uploadedAt: new Date(Date.now() - 86400000 * 1) },
 { id: 'att-2', name: 'performance_report.pdf', size: 204800, type: 'application/pdf', uploadedAt: new Date(Date.now() - 86400000 * 1) }
 ],
 replies: [
 {
 id: 'rep-1',
 author: { id: 'user-2', name: '李娜', email: 'lina@example.com', role: 'staff' },
 content: '已收到工单，正在检查API网关的负载情况。初步发现是某个微服务响应过慢导致的连锁反应。',
 type: 'COMMENT',
 createdAt: new Date(Date.now() - 86400000 * 1 + 3600000 * 2)
 },
 {
 id: 'rep-2',
 author: { id: 'user-2', name: '李娜', email: 'lina@example.com', role: 'staff' },
 content: '已定位问题，是订单服务的数据库查询效率低下导致。已优化查询语句，系统响应恢复正常。',
 type: 'UPDATE',
 createdAt: new Date(Date.now() - 3600000 * 2)
 }
 ]
 },
 {
 id: 'TKT-002',
 title: '新增用户权限管理功能',
 description: '需要开发一套用户权限管理系统，支持角色分配、权限控制等功能。预计需要2周开发时间。',
 type: 'REQUEST',
 priority: 'MEDIUM',
 status: 'ASSIGNED',
 submitter: { id: 'user-3', name: '王强', email: 'wangqiang@example.com', role: 'staff' },
 assignee: { id: 'user-4', name: '陈丽', email: 'chenli@example.com', role: 'manager' },
 createdAt: new Date(Date.now() - 86400000 * 3),
 updatedAt: new Date(Date.now() - 86400000 * 1),
 attachments: [
 { id: 'att-3', name: 'requirement_doc.docx', size: 51200, type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', uploadedAt: new Date(Date.now() - 86400000 * 3) }
 ],
 replies: [
 {
 id: 'rep-3',
 author: { id: 'user-4', name: '陈丽', email: 'chenli@example.com', role: 'manager' },
 content: '需求已确认，将安排开发团队进行评估和开发计划制定。',
 type: 'COMMENT',
 createdAt: new Date(Date.now() - 86400000 * 1)
 }
 ]
 },
 {
 id: 'TKT-003',
 title: '数据库连接池频繁耗尽',
 description: '生产环境数据库连接池经常达到上限，导致部分请求失败。需要进行容量规划和优化。',
 type: 'PROBLEM',
 priority: 'CRITICAL',
 status: 'COMPLETED',
 submitter: { id: 'user-5', name: '赵刚', email: 'zhaogang@example.com', role: 'admin' },
 assignee: { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 createdAt: new Date(Date.now() - 86400000 * 7),
 updatedAt: new Date(Date.now() - 86400000 * 2),
 attachments: [
 { id: 'att-4', name: 'connection_pool_analysis.xlsx', size: 153600, type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', uploadedAt: new Date(Date.now() - 86400000 * 7) }
 ],
 replies: [
 {
 id: 'rep-4',
 author: { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 content: '已完成数据库连接池的分析，发现是某些长时间运行的查询占用了连接。已优化查询逻辑并增加了连接池大小。',
 type: 'RESOLUTION',
 createdAt: new Date(Date.now() - 86400000 * 2)
 }
 ]
 },
 {
 id: 'TKT-004',
 title: '部署新版本支付服务',
 description: '需要部署支付服务的新版本，包含多项安全修复和性能改进。计划在周末进行。',
 type: 'CHANGE',
 priority: 'HIGH',
 status: 'ASSIGNED',
 submitter: { id: 'user-2', name: '李娜', email: 'lina@example.com', role: 'staff' },
 assignee: { id: 'user-3', name: '王强', email: 'wangqiang@example.com', role: 'staff' },
 createdAt: new Date(Date.now() - 86400000 * 2),
 updatedAt: new Date(Date.now() - 86400000 * 1),
 attachments: [
 { id: 'att-5', name: 'deployment_plan.pdf', size: 81920, type: 'application/pdf', uploadedAt: new Date(Date.now() - 86400000 * 2) }
 ],
 replies: []
 },
 {
 id: 'TKT-005',
 title: '用户登录失败率异常升高',
 description: '近两天用户登录失败率显著上升，可能存在安全攻击或配置问题。需要紧急排查。',
 type: 'INCIDENT',
 priority: 'CRITICAL',
 status: 'IN_PROGRESS',
 submitter: { id: 'user-4', name: '陈丽', email: 'chenli@example.com', role: 'manager' },
 assignee: { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 createdAt: new Date(Date.now() - 3600000 * 6),
 updatedAt: new Date(Date.now() - 3600000 * 1),
 attachments: [
 { id: 'att-6', name: 'login_attempts.csv', size: 256000, type: 'text/csv', uploadedAt: new Date(Date.now() - 3600000 * 6) }
 ],
 replies: [
 {
 id: 'rep-5',
 author: { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 content: '初步分析发现大量来自特定IP段的登录尝试，疑似暴力破解攻击。已临时封禁相关IP并加强了登录验证。',
 type: 'UPDATE',
 createdAt: new Date(Date.now() - 3600000 * 1)
 }
 ]
 }
]);
// 计算属性：筛选后的工单
const filteredTickets = computed(() => {
 return tickets.value.filter(ticket => {
 const matchesSearch = !searchQuery.value ||
 ticket.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
 ticket.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
 ticket.id.toLowerCase().includes(searchQuery.value.toLowerCase());
 const matchesStatus = !filterStatus.value || ticket.status === filterStatus.value;
 const matchesPriority = !filterPriority.value || ticket.priority === filterPriority.value;
 const matchesAssignee = !filterAssignee.value || ticket.assignee.name === filterAssignee.value;
 const matchesType = !filterType.value || ticket.type === filterType.value;
 return matchesSearch && matchesStatus && matchesPriority && matchesAssignee && matchesType;
 });
});
// 获取状态文本
const getStatusText = (status: string) => {
 switch (status) {
 case 'ASSIGNED':
 return '已派单';
 case 'IN_PROGRESS':
 return '处理中';
 case 'COMPLETED':
 return '完成';
 case 'VECTOR_STORED':
 return '向量存储';
 default:
 return status;
 }
};
// 获取状态标签类型
const getStatusTagType = (status: string) => {
 switch (status) {
 case 'ASSIGNED':
 return 'primary';
 case 'IN_PROGRESS':
 return 'warning';
 case 'COMPLETED':
 return 'success';
 case 'VECTOR_STORED':
 return 'info';
 default:
 return 'info';
 }
};
// 获取优先级文本
const getPriorityText = (priority: string) => {
 switch (priority) {
 case 'LOW':
 return '低';
 case 'MEDIUM':
 return '中';
 case 'HIGH':
 return '高';
 case 'CRITICAL':
 return '紧急';
 default:
 return priority;
 }
};
// 获取优先级标签类型
const getPriorityTagType = (priority: string) => {
 switch (priority) {
 case 'LOW':
 return 'info';
 case 'MEDIUM':
 return 'primary';
 case 'HIGH':
 return 'warning';
 case 'CRITICAL':
 return 'danger';
 default:
 return 'info';
 }
};
// 获取类型文本
const getTypeText = (type: string) => {
 switch (type) {
 case 'INCIDENT':
 return '故障报告';
 case 'REQUEST':
 return '服务请求';
 case 'PROBLEM':
 return '问题分析';
 case 'CHANGE':
 return '变更请求';
 default:
 return type;
 }
};
// 获取类型标签类型
const getTypeTagType = (type: string) => {
 switch (type) {
 case 'INCIDENT':
 return 'danger';
 case 'REQUEST':
 return 'success';
 case 'PROBLEM':
 return 'warning';
 case 'CHANGE':
 return 'primary';
 default:
 return 'info';
 }
};
// 格式化日期
const formatDate = (date: Date) => {
 return new Date(date).toLocaleString('zh-CN', {
 year: 'numeric',
 month: '2-digit',
 day: '2-digit',
 hour: '2-digit',
 minute: '2-digit',
 second: '2-digit'
 });
};
// 格式化文件大小
const formatFileSize = (size: number) => {
 if (size < 1024)
 return size + ' B';
 if (size < 1024 * 1024)
 return (size / 1024).toFixed(1) + ' KB';
 return (size / (1024 * 1024)).toFixed(1) + ' MB';
};
// 权限检查函数
const canEditTicket = (ticket: Ticket | null) => {
 if (!ticket)
 return false;
 // 简化权限逻辑：负责人可以编辑
 return ticket.assignee.id === 'user-1';
};
const canAssignTicket = (ticket: Ticket | null) => {
 if (!ticket)
 return false;
 // 简化权限逻辑：管理员或经理可以分配
 return ['user-4', 'user-5'].includes(ticket.submitter.id);
};
const canUpdateStatus = (ticket: Ticket | null) => {
 if (!ticket)
 return false;
 // 简化权限逻辑：负责人可以更新状态
 return ticket.assignee.id === 'user-1';
};
const canAddReply = (ticket: Ticket | null) => {
 if (!ticket)
 return false;
 // 简化权限逻辑：负责人可以回复
 return ticket.assignee.id === 'user-1';
};
const canCloseTicket = (ticket: Ticket | null) => {
 if (!ticket)
 return false;
 // 简化权限逻辑：负责人或管理员可以关闭
 return ticket.assignee.id === 'user-1' || ticket.submitter.role === 'admin';
};
const canStoreVector = (ticket: Ticket | null) => {
 if (!ticket)
 return false;
 // 简化权限逻辑：只有完成状态的工单可以存储向量
 return ticket.status === 'COMPLETED';
};
// 模态框操作函数
const viewTicket = (ticket: Ticket) => {
 currentTicket.value = ticket;
 showTicketDetailModal.value = true;
};
const editTicket = (ticket: Ticket | null) => {
 if (!ticket)
 return;
 editedTicket.value = { ...ticket };
 showEditTicketModal.value = true;
};
const assignTicket = (ticket: Ticket | null) => {
 if (!ticket)
 return;
 currentTicket.value = ticket;
 selectedAssignee.value = '';
 showAssignTicketModal.value = true;
};
const updateTicketStatus = (ticket: Ticket | null) => {
 if (!ticket)
 return;
 currentTicket.value = ticket;
 selectedStatus.value = '';
 statusComment.value = '';
 showStatusUpdateModal.value = true;
};
const closeTicketDetailModal = () => {
 showTicketDetailModal.value = false;
 currentTicket.value = null;
 newReplyContent.value = '';
};
const closeEditTicketModal = () => {
 showEditTicketModal.value = false;
 editedTicket.value = null;
};
const closeAssignTicketModal = () => {
 showAssignTicketModal.value = false;
 selectedAssignee.value = '';
};
const closeStatusUpdateModal = () => {
 showStatusUpdateModal.value = false;
 selectedStatus.value = '';
 statusComment.value = '';
};
const createNewTicket = () => {
 ElMessage.info('新建工单功能开发中');
};
const saveEditedTicket = () => {
 if (!editedTicket.value)
 return;
 const index = tickets.value.findIndex(t => t.id === editedTicket.value!.id);
 if (index !== -1) {
 tickets.value[index] = { ...editedTicket.value };
 ElMessage.success('工单已更新');
 }
 closeEditTicketModal();
};
const assignSelectedUser = () => {
 if (!currentTicket.value || !selectedAssignee.value)
 return;
 const user = users.find(u => u.id === selectedAssignee.value);
 if (user) {
 const index = tickets.value.findIndex(t => t.id === currentTicket.value!.id);
 if (index !== -1) {
 tickets.value[index].assignee = user;
 ElMessage.success('工单已分配');
 }
 }
 closeAssignTicketModal();
};
const updateSelectedStatus = () => {
 if (!currentTicket.value || !selectedStatus.value)
 return;
 const index = tickets.value.findIndex(t => t.id === currentTicket.value!.id);
 if (index !== -1) {
 tickets.value[index].status = selectedStatus.value as Ticket['status'];
 ElMessage.success('状态已更新');
 }
 closeStatusUpdateModal();
};
const addReply = () => {
 if (!currentTicket.value || !newReplyContent.value.trim())
 return;
 const newReply: Reply = {
 id: 'rep-' + Date.now(),
 author: { id: 'user-1', name: '张伟', email: 'zhangwei@example.com', role: 'staff' },
 content: newReplyContent.value,
 type: 'COMMENT',
 createdAt: new Date()
 };
 const index = tickets.value.findIndex(t => t.id === currentTicket.value!.id);
 if (index !== -1) {
 tickets.value[index].replies.push(newReply);
 ElMessage.success('回复已添加');
 }
 newReplyContent.value = '';
};
const closeTicket = () => {
 if (!currentTicket.value)
 return;
 ElMessageBox.confirm('确定要关闭此工单吗？', '提示', {
 confirmButtonText: '确定',
 cancelButtonText: '取消',
 type: 'warning'
 }).then(() => {
 const index = tickets.value.findIndex(t => t.id === currentTicket.value!.id);
 if (index !== -1) {
 tickets.value[index].status = 'COMPLETED';
 ElMessage.success('工单已关闭');
 }
 closeTicketDetailModal();
 }).catch(() => {
 ElMessage.info('已取消关闭');
 });
};
const storeVector = () => {
 if (!currentTicket.value)
 return;
 const index = tickets.value.findIndex(t => t.id === currentTicket.value!.id);
 if (index !== -1) {
 tickets.value[index].status = 'VECTOR_STORED';
 ElMessage.success('向量存储成功');
 }
 closeTicketDetailModal();
};
const toggleNotifications = () => {
 ElMessage.info('通知中心功能开发中');
};
</script>

<style scoped>
.ticket-container {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.ticket-main {
  flex: 1;
  margin-left: 250px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left .breadcrumb {
  display: flex;
  align-items: center;
}

.header-left .breadcrumb ol {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.header-left .breadcrumb li {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.header-left .breadcrumb li:first-child {
  color: #999;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  position: relative;
}

.search-input {
  width: 280px;
}

.notification-btn {
  position: relative;
  padding: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #666;
  font-size: 18px;
}

.notification-btn:hover {
  color: #409eff;
}

.notification-btn .badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ticket-content {
  flex: 1;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.page-subtitle {
  color: #999;
  font-size: 14px;
  margin: 8px 0 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-panel {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-row {
  display: flex;
  gap: 24px;
}

.filter-item {
  flex: 1;
}

.filter-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.ticket-list-panel {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.panel-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.ticket-id {
  color: #409eff;
  font-weight: 500;
}

.ticket-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.ticket-desc {
  font-size: 13px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 详情页样式 */
.detail-content {
  padding: 8px 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-item label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.detail-item .value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section label {
  display: block;
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.title-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.desc-box {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  color: #666;
  white-space: pre-wrap;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
}

.avatar.submitter {
  background: #e6f7ff;
  color: #1890ff;
}

.avatar.assignee {
  background: #f6ffed;
  color: #52c41a;
}

.avatar.reply {
  background: #e6f7ff;
  color: #1890ff;
}

.user-detail p {
  margin: 0;
}

.user-detail .email {
  font-size: 13px;
  color: #999;
}

.attachments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.attachment-item .size {
  font-size: 12px;
  color: #999;
}

.download-btn {
  margin-left: auto;
  color: #409eff;
}

.empty-text {
  color: #999;
  font-size: 14px;
}

.replies-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reply-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.reply-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-detail p {
  margin: 0;
}

.author-detail .time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  padding: 16px;
  color: #666;
  background: #f5f5f5;
  border-left: 4px solid #409eff;
}

.reply-form {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

.reply-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

/* 表单样式 */
.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
</style>