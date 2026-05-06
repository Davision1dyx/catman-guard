<template>
  <div class="dashboard-container">
    <Sidebar />
    
    <main class="dashboard-main">
      <TopHeader title="数据看板">
        <template #actions>
          <div class="header-actions">
            <el-date-picker 
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
            <el-button type="primary" icon="Download">
              导出数据
            </el-button>
          </div>
        </template>
      </TopHeader>
      
      <div class="dashboard-title">
        <h1>运维数据看板</h1>
        <p>实时监控运维数据与统计分析</p>
      </div>
      
      <div class="dashboard-content">
        <!-- 核心统计卡片 -->
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-icon bg-blue">
              <el-icon><Message /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalConversations }}</div>
              <div class="stat-label">问答会话</div>
            </div>
            <div class="stat-trend positive">
              <el-icon><ArrowUp /></el-icon>
              <span>+12%</span>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon bg-green">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalTickets }}</div>
              <div class="stat-label">工单总数</div>
            </div>
            <div class="stat-trend positive">
              <el-icon><ArrowUp /></el-icon>
              <span>+8%</span>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon bg-purple">
              <el-icon><Files /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.documentCount }}</div>
              <div class="stat-label">知识库文档</div>
            </div>
            <div class="stat-trend positive">
              <el-icon><ArrowUp /></el-icon>
              <span>+15%</span>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon bg-orange">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.onlineStaff }}</div>
              <div class="stat-label">在线值班人员</div>
            </div>
            <div class="stat-trend neutral">
              <el-icon><Minus /></el-icon>
              <span>0%</span>
            </div>
          </div>
        </div>
        
        <!-- 问答解决率与工单转化率 -->
        <div class="charts-row">
          <div class="chart-card">
            <div class="chart-header">
              <h3>智能问答解决率</h3>
              <span class="chart-subtitle">用户问题在问答阶段直接解决的比例</span>
            </div>
            <div class="chart-content">
              <div class="gauge-chart">
                <svg viewBox="0 0 200 120" class="gauge-svg">
                  <path 
                    d="M 20 100 A 80 80 0 0 1 180 100" 
                    fill="none" 
                    stroke="#e5e7eb" 
                    stroke-width="16"
                    stroke-linecap="round"
                  />
                  <path 
                    d="M 20 100 A 80 80 0 0 1 180 100" 
                    fill="none" 
                    stroke="#7ed321" 
                    stroke-width="16"
                    stroke-linecap="round"
                    :stroke-dasharray="`${stats.qaResolveRate * 2.51} 251`"
                  />
                </svg>
                <div class="gauge-value">
                  <span class="gauge-number">{{ stats.qaResolveRate }}%</span>
                  <span class="gauge-label">问答解决率</span>
                </div>
              </div>
              <div class="gauge-stats">
                <div class="gauge-stat">
                  <span class="stat-num">{{ stats.solvedInQA }}</span>
                  <span class="stat-text">问答直接解决</span>
                </div>
                <div class="gauge-stat">
                  <span class="stat-num">{{ stats.convertedToTicket }}</span>
                  <span class="stat-text">转为工单</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="chart-card">
            <div class="chart-header">
              <h3>工单状态分布</h3>
            </div>
            <div class="chart-content">
              <div class="donut-chart">
                <svg viewBox="0 0 100 100" class="donut-svg">
                  <circle 
                    cx="50" cy="50" r="40" 
                    fill="none" 
                    stroke="#f3f4f6" 
                    stroke-width="12"
                  />
                  <circle 
                    cx="50" cy="50" r="40" 
                    fill="none" 
                    stroke="#f5a623" 
                    stroke-width="12"
                    :stroke-dasharray="`${ticketStatusDistribution.pending * 2.51} 251`"
                    stroke-dashoffset="0"
                    stroke-linecap="round"
                  />
                  <circle 
                    cx="50" cy="50" r="40" 
                    fill="none" 
                    stroke="#4a9eff" 
                    stroke-width="12"
                    :stroke-dasharray="`${ticketStatusDistribution.processing * 2.51} 251`"
                    :stroke-dashoffset="`-${ticketStatusDistribution.pending * 2.51}`"
                    stroke-linecap="round"
                  />
                  <circle 
                    cx="50" cy="50" r="40" 
                    fill="none" 
                    stroke="#7ed321" 
                    stroke-width="12"
                    :stroke-dasharray="`${ticketStatusDistribution.resolved * 2.51} 251`"
                    :stroke-dashoffset="`-${(ticketStatusDistribution.pending + ticketStatusDistribution.processing) * 2.51}`"
                    stroke-linecap="round"
                  />
                  <circle 
                    cx="50" cy="50" r="40" 
                    fill="none" 
                    stroke="#9ca3af" 
                    stroke-width="12"
                    :stroke-dasharray="`${ticketStatusDistribution.closed * 2.51} 251`"
                    :stroke-dashoffset="`-${(ticketStatusDistribution.pending + ticketStatusDistribution.processing + ticketStatusDistribution.resolved) * 2.51}`"
                    stroke-linecap="round"
                  />
                </svg>
                <div class="donut-center">
                  <span class="donut-total">{{ stats.totalTickets }}</span>
                  <span class="donut-label">工单总数</span>
                </div>
              </div>
              <div class="donut-legend">
                <div class="legend-item">
                  <span class="legend-color" style="background: #f5a623"></span>
                  <span>待处理 ({{ ticketStatusDistribution.pending }})</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background: #4a9eff"></span>
                  <span>处理中 ({{ ticketStatusDistribution.processing }})</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background: #7ed321"></span>
                  <span>已解决 ({{ ticketStatusDistribution.resolved }})</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background: #9ca3af"></span>
                  <span>已关闭 ({{ ticketStatusDistribution.closed }})</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 趋势图表 -->
        <div class="charts-row">
          <div class="chart-card full-width">
            <div class="chart-header">
              <h3>问答与工单趋势</h3>
              <div class="chart-tabs">
                <button 
                  v-for="tab in trendTabs" 
                  :key="tab.value"
                  :class="['tab-btn', { active: activeTrendTab === tab.value }]"
                  @click="activeTrendTab = tab.value"
                >
                  {{ tab.label }}
                </button>
              </div>
            </div>
            <div class="chart-content">
              <div class="line-chart">
                <svg viewBox="0 0 800 300" class="line-svg">
                  <defs>
                    <linearGradient id="qaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                      <stop offset="0%" style="stop-color:#4a9eff;stop-opacity:0.3" />
                      <stop offset="100%" style="stop-color:#4a9eff;stop-opacity:0" />
                    </linearGradient>
                    <linearGradient id="ticketGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                      <stop offset="0%" style="stop-color:#7ed321;stop-opacity:0.3" />
                      <stop offset="100%" style="stop-color:#7ed321;stop-opacity:0" />
                    </linearGradient>
                  </defs>
                  <!-- QA Area -->
                  <path 
                    :d="qaAreaPath" 
                    fill="url(#qaGradient)"
                  />
                  <!-- Ticket Area -->
                  <path 
                    :d="ticketAreaPath" 
                    fill="url(#ticketGradient)"
                  />
                  <!-- QA Line -->
                  <path 
                    :d="qaLinePath" 
                    fill="none" 
                    stroke="#4a9eff" 
                    stroke-width="3"
                  />
                  <!-- Ticket Line -->
                  <path 
                    :d="ticketLinePath" 
                    fill="none" 
                    stroke="#7ed321" 
                    stroke-width="3"
                  />
                  <!-- QA Points -->
                  <circle 
                    v-for="(point, index) in qaPoints" 
                    :key="'qa-' + index"
                    :cx="point.x" 
                    :cy="point.y" 
                    r="5" 
                    fill="#4a9eff"
                    class="line-point"
                  />
                  <!-- Ticket Points -->
                  <circle 
                    v-for="(point, index) in ticketPoints" 
                    :key="'ticket-' + index"
                    :cx="point.x" 
                    :cy="point.y" 
                    r="5" 
                    fill="#7ed321"
                    class="line-point"
                  />
                  <!-- Grid Lines -->
                  <line 
                    v-for="i in 7" 
                    :key="'grid-' + i"
                    x1="50" 
                    :y1="50 + (i - 1) * 50" 
                    x2="750" 
                    :y2="50 + (i - 1) * 50"
                    stroke="#e5e7eb"
                    stroke-dasharray="4"
                  />
                </svg>
                <div class="line-labels">
                  <span v-for="(label, index) in weekLabels" :key="index">{{ label }}</span>
                </div>
                <div class="line-legend">
                  <div class="legend-item">
                    <span class="legend-color" style="background: #4a9eff"></span>
                    <span>问答会话</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color" style="background: #7ed321"></span>
                    <span>工单数量</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 多维度统计 -->
        <div class="charts-row">
          <div class="chart-card">
            <div class="chart-header">
              <h3>知识库使用统计</h3>
            </div>
            <div class="chart-content">
              <div class="knowledge-stats">
                <div class="knowledge-item">
                  <div class="knowledge-icon bg-blue">
                    <el-icon><Collection /></el-icon>
                  </div>
                  <div class="knowledge-info">
                    <div class="knowledge-value">{{ knowledgeStats.total }}</div>
                    <div class="knowledge-label">文档总数</div>
                  </div>
                </div>
                <div class="knowledge-item">
                  <div class="knowledge-icon bg-green">
                    <el-icon><ZoomIn /></el-icon>
                  </div>
                  <div class="knowledge-info">
                    <div class="knowledge-value">{{ knowledgeStats.views }}</div>
                    <div class="knowledge-label">总访问量</div>
                  </div>
                </div>
                <div class="knowledge-item">
                  <div class="knowledge-icon bg-purple">
                    <el-icon><Search /></el-icon>
                  </div>
                  <div class="knowledge-info">
                    <div class="knowledge-value">{{ knowledgeStats.references }}</div>
                    <div class="knowledge-label">被引用次数</div>
                  </div>
                </div>
                <div class="knowledge-item">
                  <div class="knowledge-icon bg-orange">
                    <el-icon><ArrowUp /></el-icon>
                  </div>
                  <div class="knowledge-info">
                    <div class="knowledge-value">{{ knowledgeStats.avgRating }}.0</div>
                    <div class="knowledge-label">平均评分</div>
                  </div>
                </div>
              </div>
              <div class="knowledge-distribution">
                <h4>文档状态分布</h4>
                <div class="distribution-list">
                  <div class="distribution-item">
                    <div class="distribution-bar-track">
                      <div class="distribution-bar-fill bg-green" :style="{ width: (knowledgeStats.status.published / knowledgeStats.total * 100) + '%' }"></div>
                    </div>
                    <span class="distribution-label">已发布</span>
                    <span class="distribution-value">{{ knowledgeStats.status.published }}</span>
                  </div>
                  <div class="distribution-item">
                    <div class="distribution-bar-track">
                      <div class="distribution-bar-fill bg-blue" :style="{ width: (knowledgeStats.status.pending / knowledgeStats.total * 100) + '%' }"></div>
                    </div>
                    <span class="distribution-label">待审核</span>
                    <span class="distribution-value">{{ knowledgeStats.status.pending }}</span>
                  </div>
                  <div class="distribution-item">
                    <div class="distribution-bar-track">
                      <div class="distribution-bar-fill bg-orange" :style="{ width: (knowledgeStats.status.draft / knowledgeStats.total * 100) + '%' }"></div>
                    </div>
                    <span class="distribution-label">草稿</span>
                    <span class="distribution-value">{{ knowledgeStats.status.draft }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="chart-card">
            <div class="chart-header">
              <h3>值班人员状态</h3>
            </div>
            <div class="chart-content">
              <div class="staff-status">
                <div class="status-ring">
                  <svg viewBox="0 0 100 100" class="ring-svg">
                    <circle 
                      cx="50" cy="50" r="40" 
                      fill="none" 
                      stroke="#e5e7eb" 
                      stroke-width="12"
                    />
                    <circle 
                      cx="50" cy="50" r="40" 
                      fill="none" 
                      stroke="#7ed321" 
                      stroke-width="12"
                      :stroke-dasharray="`${staffStatus.activePercent * 2.51} 251`"
                      stroke-dashoffset="0"
                      stroke-linecap="round"
                    />
                    <circle 
                      cx="50" cy="50" r="40" 
                      fill="none" 
                      stroke="#f5a623" 
                      stroke-width="12"
                      :stroke-dasharray="`${staffStatus.leavePercent * 2.51} 251`"
                      :stroke-dashoffset="`-${staffStatus.activePercent * 2.51}`"
                      stroke-linecap="round"
                    />
                  </svg>
                  <div class="ring-center">
                    <span class="ring-total">{{ staffStatus.total }}</span>
                    <span class="ring-label">总人数</span>
                  </div>
                </div>
                <div class="status-list">
                  <div class="status-item">
                    <span class="status-dot bg-green"></span>
                    <span class="status-text">在职</span>
                    <span class="status-count">{{ staffStatus.active }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-dot bg-orange"></span>
                    <span class="status-text">请假</span>
                    <span class="status-count">{{ staffStatus.onLeave }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-dot bg-gray"></span>
                    <span class="status-text">离职</span>
                    <span class="status-count">{{ staffStatus.inactive }}</span>
                  </div>
                </div>
              </div>
              <div class="group-info">
                <h4>分组分布</h4>
                <div class="group-list">
                  <div 
                    v-for="group in groupDistribution" 
                    :key="group.id" 
                    class="group-item"
                  >
                    <span class="group-name">{{ group.name }}</span>
                    <span class="group-count">{{ group.count }}人</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 热门问题和人员绩效 -->
        <div class="charts-row">
          <div class="chart-card">
            <div class="chart-header">
              <h3>热门问题TOP10</h3>
            </div>
            <div class="chart-content">
              <div class="hot-questions">
                <div 
                  v-for="(question, index) in hotQuestions" 
                  :key="index" 
                  class="hot-item"
                >
                  <span class="hot-rank">{{ index + 1 }}</span>
                  <div class="hot-bar-track">
                    <div 
                      class="hot-bar-fill" 
                      :style="{ width: (question.count / hotQuestions[0].count * 100) + '%' }"
                    ></div>
                  </div>
                  <span class="hot-title">{{ question.title }}</span>
                  <span class="hot-count">{{ question.count }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="chart-card">
            <div class="chart-header">
              <h3>人员绩效排行</h3>
            </div>
            <div class="chart-content">
              <div class="ranking-list">
                <div 
                  v-for="(person, index) in topPerformers" 
                  :key="index" 
                  class="ranking-item"
                >
                  <div class="rank-badge" :class="'rank-' + (index + 1)">
                    {{ index + 1 }}
                  </div>
                  <div class="person-avatar">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="person-info">
                    <div class="person-name">{{ person.name }}</div>
                    <div class="person-metrics">
                      <span>{{ person.tickets }}工单</span>
                      <span>{{ person.resolved }}解决</span>
                    </div>
                  </div>
                  <div class="person-score">
                    <span class="score-value">{{ person.score }}</span>
                    <span class="score-label">分</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Sidebar from '../components/Sidebar.vue'
import TopHeader from '../components/TopHeader.vue'
import { 
  Message, List, Files, UserFilled, ArrowUp, Minus, 
  Collection, ZoomIn, Search, User 
} from '@element-plus/icons-vue'

const dateRange = ref([])
const activeTrendTab = ref('week')

const trendTabs = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本季度', value: 'quarter' }
]

const stats = ref({
  totalConversations: 856,
  totalTickets: 156,
  documentCount: 89,
  onlineStaff: 12,
  qaResolveRate: 72,
  solvedInQA: 616,
  convertedToTicket: 240
})

const ticketStatusDistribution = ref({
  pending: 12,
  processing: 28,
  resolved: 85,
  closed: 31
})

const weeklyQAData = ref([45, 52, 38, 56, 48, 62, 55])
const weeklyTicketData = ref([12, 18, 15, 22, 19, 25, 20])

const weekLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

const knowledgeStats = ref({
  total: 89,
  views: 3256,
  references: 876,
  avgRating: 4,
  status: {
    published: 65,
    pending: 18,
    draft: 6
  }
})

const staffStatus = ref({
  total: 18,
  active: 14,
  onLeave: 3,
  inactive: 1,
  activePercent: 78,
  leavePercent: 17
})

const groupDistribution = ref([
  { id: 'g1', name: '第一组', count: 6 },
  { id: 'g2', name: '第二组', count: 6 },
  { id: 'g3', name: '第三组', count: 6 }
])

const topPerformers = ref([
  { name: '张伟', tickets: 45, resolved: 42, score: 98 },
  { name: '李娜', tickets: 38, resolved: 35, score: 92 },
  { name: '王强', tickets: 32, resolved: 29, score: 88 },
  { name: '陈丽', tickets: 28, resolved: 25, score: 85 },
  { name: '赵刚', tickets: 23, resolved: 20, score: 80 }
])

const hotQuestions = ref([
  { title: '系统登录失败', count: 156 },
  { title: '数据库连接超时', count: 132 },
  { title: '服务启动异常', count: 98 },
  { title: '接口响应缓慢', count: 87 },
  { title: '日志报错分析', count: 76 },
  { title: '配置文件问题', count: 65 },
  { title: '权限不足', count: 54 },
  { title: '内存溢出', count: 43 },
  { title: '磁盘空间不足', count: 32 },
  { title: '网络不通', count: 28 }
])

// QA趋势线计算
const qaPoints = computed(() => {
  const maxVal = Math.max(...weeklyQAData.value)
  return weeklyQAData.value.map((val, index) => ({
    x: 50 + (index * 100),
    y: 250 - (val / maxVal * 200)
  }))
})

const qaLinePath = computed(() => {
  return qaPoints.value.map((p, i) => 
    `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`
  ).join(' ')
})

const qaAreaPath = computed(() => {
  const points = qaPoints.value
  if (!points.length) return ''
  const path = points.map((p, i) => 
    `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`
  ).join(' ')
  return `${path} L ${points[points.length - 1].x} 250 L 50 250 Z`
})

// Ticket趋势线计算
const ticketPoints = computed(() => {
  const maxVal = Math.max(...weeklyTicketData.value)
  return weeklyTicketData.value.map((val, index) => ({
    x: 50 + (index * 100),
    y: 250 - (val / maxVal * 200)
  }))
})

const ticketLinePath = computed(() => {
  return ticketPoints.value.map((p, i) => 
    `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`
  ).join(' ')
})

const ticketAreaPath = computed(() => {
  const points = ticketPoints.value
  if (!points.length) return ''
  const path = points.map((p, i) => 
    `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`
  ).join(' ')
  return `${path} L ${points[points.length - 1].x} 250 L 50 250 Z`
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
}

.dashboard-main {
  flex: 1;
  margin-left: 256px;
  display: flex;
  flex-direction: column;
  background: #f9fafb;
}

.dashboard-header {
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-title h1 {
  font-size: 20px;
  color: #1f2937;
  margin: 0;
}

.header-title p {
  font-size: 14px;
  color: #6b7280;
  margin: 4px 0 0 0;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.dashboard-content {
  flex: 1;
  padding: 24px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  position: relative;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
}

.stat-icon.bg-blue {
  background: linear-gradient(135deg, #4a9eff 0%, #2d7dd2 100%);
}

.stat-icon.bg-green {
  background: linear-gradient(135deg, #7ed321 0%, #5aac0e 100%);
}

.stat-icon.bg-orange {
  background: linear-gradient(135deg, #f5a623 0%, #d4891f 100%);
}

.stat-icon.bg-purple {
  background: linear-gradient(135deg, #9013fe 0%, #6c0fd6 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin: 4px 0 0 0;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
}

.stat-trend.positive {
  color: #10b981;
}

.stat-trend.negative {
  color: #ef4444;
}

.stat-trend.neutral {
  color: #6b7280;
}

/* 图表行 */
.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.chart-card.full-width {
  grid-column: span 2;
}

.chart-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  color: #1f2937;
}

.chart-subtitle {
  font-size: 12px;
  color: #9ca3af;
  margin-left: 12px;
}

.chart-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 6px 12px;
  font-size: 13px;
  border: none;
  border-radius: 6px;
  background: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  background: #e5e7eb;
}

.tab-btn.active {
  background: #4a9eff;
  color: #fff;
}

.chart-content {
  padding: 20px;
}

/* 仪表盘图表 */
.gauge-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.gauge-svg {
  width: 100%;
  height: 120px;
}

.gauge-value {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -20%);
  text-align: center;
}

.gauge-number {
  display: block;
  font-size: 36px;
  font-weight: 700;
  color: #10b981;
}

.gauge-label {
  font-size: 14px;
  color: #6b7280;
}

.gauge-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 20px;
}

.gauge-stat {
  text-align: center;
}

.gauge-stat .stat-num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.gauge-stat .stat-text {
  font-size: 13px;
  color: #6b7280;
}

/* 环形图 */
.donut-chart {
  display: flex;
  align-items: center;
  gap: 20px;
}

.donut-svg {
  transform: rotate(-90deg);
  width: 120px;
  height: 120px;
}

.donut-center {
  position: absolute;
  text-align: center;
}

.donut-total {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.donut-label {
  font-size: 12px;
  color: #6b7280;
}

.donut-legend {
  flex: 1;
}

.pie-legend, .donut-legend {
  flex: 1;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.legend-color {
  width: 14px;
  height: 14px;
  border-radius: 4px;
}

/* 趋势线图 */
.line-chart {
  position: relative;
}

.line-svg {
  width: 100%;
  height: 250px;
}

.line-point {
  cursor: pointer;
  transition: transform 0.2s;
}

.line-point:hover {
  transform: scale(1.5);
}

.line-labels {
  display: flex;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 10px;
  color: #6b7280;
  font-size: 12px;
}

.line-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
}

/* 知识库统计 */
.knowledge-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.knowledge-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.knowledge-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}

.knowledge-icon.bg-blue {
  background: #4a9eff;
}

.knowledge-icon.bg-green {
  background: #7ed321;
}

.knowledge-icon.bg-purple {
  background: #9013fe;
}

.knowledge-icon.bg-orange {
  background: #f5a623;
}

.knowledge-info {
  flex: 1;
}

.knowledge-value {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.knowledge-label {
  font-size: 12px;
  color: #6b7280;
  margin: 2px 0 0 0;
}

.knowledge-distribution {
  margin-top: 16px;
}

.knowledge-distribution h4 {
  font-size: 14px;
  color: #374151;
  margin: 0 0 12px 0;
}

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.distribution-bar-track {
  flex: 1;
  height: 8px;
  background: #f3f4f6;
  border-radius: 4px;
  overflow: hidden;
}

.distribution-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.distribution-bar-fill.bg-green {
  background: #7ed321;
}

.distribution-bar-fill.bg-blue {
  background: #4a9eff;
}

.distribution-bar-fill.bg-orange {
  background: #f5a623;
}

.distribution-label {
  width: 50px;
  font-size: 13px;
  color: #6b7280;
}

.distribution-value {
  width: 30px;
  text-align: right;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

/* 值班人员状态 */
.staff-status {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.status-ring {
  position: relative;
  width: 120px;
  height: 120px;
}

.ring-svg {
  transform: rotate(-90deg);
  width: 100%;
  height: 100%;
}

.ring-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.ring-total {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.ring-label {
  font-size: 12px;
  color: #6b7280;
}

.status-list {
  flex: 1;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.status-dot.bg-green {
  background: #7ed321;
}

.status-dot.bg-orange {
  background: #f5a623;
}

.status-dot.bg-gray {
  background: #9ca3af;
}

.status-text {
  flex: 1;
  font-size: 14px;
  color: #374151;
}

.status-count {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.group-info {
  margin-top: 16px;
}

.group-info h4 {
  font-size: 14px;
  color: #374151;
  margin: 0 0 12px 0;
}

.group-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.group-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f9fafb;
  border-radius: 6px;
}

.group-name {
  font-size: 14px;
  color: #374151;
}

.group-count {
  font-size: 14px;
  font-weight: 600;
  color: #4a9eff;
}

/* 热门问题 */
.hot-questions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #f9fafb;
  border-radius: 6px;
}

.hot-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
}

.hot-bar-track {
  flex: 1;
  height: 6px;
  background: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
}

.hot-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #4a9eff, #7ed321);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.hot-title {
  flex: 2;
  font-size: 13px;
  color: #374151;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-count {
  font-size: 13px;
  font-weight: 600;
  color: #4a9eff;
}

/* 人员绩效排行 */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.rank-badge {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #a0a0a0 100%);
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #b87333 100%);
}

.rank-4, .rank-5 {
  background: #9ca3af;
}

.person-avatar {
  width: 40px;
  height: 40px;
  background: #e6f7ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
}

.person-info {
  flex: 1;
}

.person-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.person-metrics {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

.person-metrics span {
  font-size: 12px;
  color: #6b7280;
}

.person-score {
  text-align: right;
}

.score-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #4a9eff;
}

.score-label {
  font-size: 11px;
  color: #9ca3af;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
  
  .chart-card.full-width {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .dashboard-main {
    margin-left: 0;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .staff-status {
    flex-direction: column;
  }
  
  .gauge-stats {
    gap: 20px;
  }
}
</style>
