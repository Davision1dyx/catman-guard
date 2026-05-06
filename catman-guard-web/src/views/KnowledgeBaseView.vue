<template>
  <div class="kb-container">
    <Sidebar />

    <main class="kb-main">
      <TopHeader title="知识库管理">
        <template #search>
          <div class="search-box">
            <el-icon><Search /></el-icon>
            <input type="text" v-model="searchQuery" placeholder="搜索知识库..." />
          </div>
        </template>
      </TopHeader>

      <div class="kb-content">
        <div class="page-header">
          <div class="header-info">
            <h1>知识库管理</h1>
            <p>管理和维护多个知识库，每个知识库包含相关文档集合</p>
          </div>
          <button class="btn-primary" @click="showCreateKBDialog = true">
            <el-icon><Plus /></el-icon>创建知识库
          </button>
        </div>

        <div class="kb-grid-section">
          <div class="section-header">
            <h2>知识库列表</h2>
          </div>
          <div class="kb-grid">
            <div
              v-for="kb in knowledgeBases"
              :key="kb.id"
              class="kb-card"
              :class="{ active: kb.id === currentKBId }"
              @click="switchKB(kb.id)"
            >
              <div class="kb-card-header">
                <div class="kb-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="kb-actions">
                  <button class="icon-btn" @click.stop="editKB(kb)" title="编辑">
                    <el-icon><Edit /></el-icon>
                  </button>
                  <button class="icon-btn danger" @click.stop="deleteKB(kb)" title="删除">
                    <el-icon><Delete /></el-icon>
                  </button>
                </div>
              </div>
              <div class="kb-card-body">
                <h3>{{ kb.name }}</h3>
                <p class="kb-desc">{{ kb.description }}</p>
                <div class="kb-stats">
                  <span class="stat-item">
                    <el-icon><DocumentCopy /></el-icon>
                    {{ kb.fileCount }} 个文件
                  </span>
                  <span class="stat-item">
                    <el-icon><Share /></el-icon>
                    {{ kb.chunkCount }} 个分片
                  </span>
                </div>
                <div class="kb-tags">
                  <el-tag :type="getKBType(kb.type)" size="small">{{ getKBTypeText(kb.type) }}</el-tag>
                  <el-tag :type="getStatusTagType(kb.status)" size="small">{{ getStatusText(kb.status) }}</el-tag>
                </div>
              </div>
            </div>

            <div class="kb-card add-card" @click="showCreateKBDialog = true">
              <div class="add-content">
                <el-icon><Plus /></el-icon>
                <p>创建新的知识库</p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="currentKBId" class="kb-detail-section">
          <div class="detail-header">
            <div class="detail-info">
              <h2>{{ currentKB?.name }}</h2>
              <p>{{ currentKB?.description }}</p>
            </div>
            <div class="detail-actions">
              <button class="btn-primary" @click="showUploadDialog = true">
                <el-icon><Upload /></el-icon>上传文件
              </button>
              <button class="btn-success" @click="syncVectorDB">
                <el-icon><Refresh /></el-icon>同步向量库
              </button>
            </div>
          </div>

          <div class="filter-bar">
            <el-select v-model="fileTypeFilter" placeholder="文件类型" class="filter-select">
              <el-option label="全部" value="" />
              <el-option label="PDF" value="pdf" />
              <el-option label="Word" value="doc" />
              <el-option label="Excel" value="xlsx" />
              <el-option label="Markdown" value="md" />
              <el-option label="文本" value="txt" />
            </el-select>
            <el-select v-model="statusFilter" placeholder="状态" class="filter-select">
              <el-option label="全部" value="" />
              <el-option label="已分片" value="CHUNKED" />
              <el-option label="向量存储" value="VECTOR_STORED" />
              <el-option label="已转换" value="CONVERTED" />
              <el-option label="已上传" value="UPLOADED" />
            </el-select>
          </div>

          <div class="file-table">
            <el-table :data="filteredFiles" style="width: 100%">
              <el-table-column prop="title" label="文件标题" min-width="200">
                <template #default="{ row }">
                  <div class="file-title-cell">
                    <el-icon><Document /></el-icon>
                    <div class="file-title-info">
                      <span class="file-title">{{ row.title }}</span>
                      <span class="file-original">{{ row.originalName }}</span>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="softwareVersion" label="软件版本" width="120" />
              <el-table-column prop="feature" label="特性" width="120" />
              <el-table-column prop="microservice" label="微服务" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getFileStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="size" label="大小" width="100" />
              <el-table-column prop="uploadTime" label="上传时间" width="160" />
              <el-table-column label="操作" width="220" fixed="right">
                <template #default="{ row }">
                    <div class="action-btns">
                      <el-button type="text" size="small" @click="downloadFile(row)">
                        <el-icon><Download /></el-icon>
                      </el-button>
                      <el-button type="text" size="small" @click="viewChunks(row)" :disabled="!canViewChunks(row)">
                        <el-icon><List /></el-icon>
                      </el-button>
                      <el-button type="text" size="small" @click="openVisualization(row)" :disabled="!canViewChunks(row)">
                        <el-icon><View /></el-icon>
                      </el-button>
                      <el-button type="text" size="small" @click="openChunkModal(row)" :disabled="!canChunk(row)">
                        <el-icon><Scissor /></el-icon>
                      </el-button>
                      <el-button type="text" size="small" class="danger" @click="deleteFile(row)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </template>
              </el-table-column>
            </el-table>

            <div v-if="filteredFiles.length === 0" class="empty-state">
              <el-icon><FolderOpened /></el-icon>
              <p>该知识库中暂无文件，请上传第一个知识库文档</p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <el-dialog v-model="showCreateKBDialog" :title="editingKB ? '编辑知识库' : '创建知识库'" width="500px">
      <el-form :model="kbForm" label-width="100px">
        <el-form-item label="知识库名称">
          <el-input v-model="kbForm.name" placeholder="请输入知识库名称" />
        </el-form-item>
        <el-form-item label="知识库描述">
          <el-input v-model="kbForm.description" type="textarea" :rows="3" placeholder="请输入知识库描述" />
        </el-form-item>
        <el-form-item label="知识库类型">
          <el-select v-model="kbForm.type" placeholder="请选择知识库类型">
            <el-option label="技术文档" value="technical" />
            <el-option label="产品文档" value="product" />
            <el-option label="运维文档" value="operation" />
            <el-option label="培训资料" value="training" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateKBDialog = false">取消</el-button>
        <el-button type="primary" @click="saveKB">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showUploadDialog" :title="`上传文件到 ${currentKB?.name}`" width="600px">
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="文件标题">
          <el-input v-model="uploadForm.title" placeholder="请输入文件标题" />
        </el-form-item>
        <el-form-item label="软件版本">
          <el-input v-model="uploadForm.softwareVersion" placeholder="请输入软件版本" />
        </el-form-item>
        <el-form-item label="所属特性">
          <el-input v-model="uploadForm.feature" placeholder="请输入所属特性" />
        </el-form-item>
        <el-form-item label="微服务">
          <el-input v-model="uploadForm.microservice" placeholder="请输入微服务" />
        </el-form-item>
        <el-form-item label="选择文件">
          <div class="upload-area" @click="triggerFileInput">
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <p>点击或拖拽文件到此处上传</p>
            <p class="upload-hint">支持 PDF, DOC, DOCX, TXT, MD 等格式</p>
            <input type="file" ref="fileInputRef" class="hidden" @change="handleFileSelect" accept=".pdf,.doc,.docx,.txt,.md" />
          </div>
          <div v-if="selectedFile" class="selected-file">
            <el-icon><Document /></el-icon>
            <span>{{ selectedFile.name }}</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :disabled="!selectedFile">上传</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showChunkDialog" title="分片操作" width="600px">
      <div class="chunk-config">
        <el-form :model="chunkForm" label-width="100px">
          <el-form-item label="分片模式">
            <el-select v-model="chunkForm.mode" placeholder="请选择分片模式">
              <el-option label="固定长度分片" value="fixed_length" />
              <el-option label="父子分片" value="parent_child" />
              <el-option label="语义分片" value="semantic" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="chunkForm.mode === 'fixed_length'" label="分片长度">
            <el-input-number v-model="chunkForm.length" :min="100" :max="5000" />
          </el-form-item>
          <el-form-item v-if="chunkForm.mode === 'parent_child'" label="子块长度">
            <el-input-number v-model="chunkForm.childLength" :min="50" :max="2000" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showChunkDialog = false">取消</el-button>
        <el-button type="primary" @click="performChunking">开始分片</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showChunkDetailDialog" :title="`分片结果 - ${selectedFileForChunk?.title}`" width="900px">
      <div class="chunk-stats">
        <div class="stat-card blue">
          <p class="stat-label">总分片数</p>
          <p class="stat-value">{{ chunkDetails.chunks.length }}</p>
        </div>
        <div class="stat-card green">
          <p class="stat-label">最大分片</p>
          <p class="stat-value">{{ chunkDetails.maxChunkSize }} 字符</p>
        </div>
        <div class="stat-card purple">
          <p class="stat-label">最小分片</p>
          <p class="stat-value">{{ chunkDetails.minChunkSize }} 字符</p>
        </div>
        <div class="stat-card yellow">
          <p class="stat-label">平均分片</p>
          <p class="stat-value">{{ chunkDetails.avgChunkSize }} 字符</p>
        </div>
      </div>
      <el-table :data="chunkDetails.chunks" style="width: 100%; margin-top: 20px" max-height="400">
        <el-table-column label="序号" width="80">
          <template #default="{ $index }">{{ $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="150" />
        <el-table-column prop="size" label="长度" width="100" />
        <el-table-column prop="content" label="内容预览" min-width="300">
          <template #default="{ row }">
            <span class="chunk-preview">{{ row.content.substring(0, 80) }}{{ row.content.length > 80 ? '...' : '' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewChunkDetail(row)">查看</el-button>
            <el-button type="text" size="small" @click="copyChunk(row)">复制</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="showVisualizationDialog" :title="`分片可视化 - ${selectedFileForChunk?.title}`" width="1000px">
      <div class="visualization-content">
        <div class="original-content">
          <h4>原文档内容</h4>
          <div class="content-box" v-html="highlightedContent"></div>
        </div>
        <div class="chunk-detail-list">
          <h4>分片详情</h4>
          <div v-for="(chunk, index) in chunkDetails.chunks" :key="index" class="chunk-item">
            <div class="chunk-header">
              <span class="chunk-title">分片 {{ index + 1 }}</span>
              <span class="chunk-size">长度: {{ chunk.size }}</span>
              <el-button type="text" size="small" @click="copyChunk(chunk)">复制</el-button>
            </div>
            <div class="chunk-body">{{ chunk.content }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import Sidebar from '../components/Sidebar.vue'
import TopHeader from '../components/TopHeader.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const searchQuery = ref('')
const showCreateKBDialog = ref(false)
const showUploadDialog = ref(false)
const showChunkDialog = ref(false)
const showChunkDetailDialog = ref(false)
const showVisualizationDialog = ref(false)
const editingKB = ref(null)
const currentKBId = ref(null)
const selectedFile = ref(null)
const selectedFileForChunk = ref(null)
const fileInputRef = ref(null)

const kbForm = reactive({
  name: '',
  description: '',
  type: 'technical'
})

const uploadForm = reactive({
  title: '',
  softwareVersion: '',
  feature: '',
  microservice: ''
})

const chunkForm = reactive({
  mode: 'fixed_length',
  length: 1000,
  childLength: 200
})

const knowledgeBases = ref([
  {
    id: 'kb-1',
    name: 'API 网关文档',
    description: '包含API网关的配置指南、使用说明和最佳实践',
    type: 'technical',
    fileCount: 12,
    chunkCount: 145,
    status: 'ACTIVE',
    createdAt: new Date(Date.now() - 86400000 * 30),
    updatedAt: new Date()
  },
  {
    id: 'kb-2',
    name: '数据库运维手册',
    description: '数据库性能优化、备份恢复、故障排查等相关文档',
    type: 'operation',
    fileCount: 8,
    chunkCount: 98,
    status: 'ACTIVE',
    createdAt: new Date(Date.now() - 86400000 * 45),
    updatedAt: new Date(Date.now() - 86400000 * 5)
  },
  {
    id: 'kb-3',
    name: '用户服务规范',
    description: '用户管理服务的开发规范和接口文档',
    type: 'technical',
    fileCount: 5,
    chunkCount: 67,
    status: 'MAINTENANCE',
    createdAt: new Date(Date.now() - 86400000 * 20),
    updatedAt: new Date(Date.now() - 86400000 * 10)
  },
  {
    id: 'kb-4',
    name: '产品功能说明',
    description: '产品各功能模块的详细说明和用户手册',
    type: 'product',
    fileCount: 15,
    chunkCount: 210,
    status: 'ACTIVE',
    createdAt: new Date(Date.now() - 86400000 * 60),
    updatedAt: new Date()
  }
])

const files = ref([
  {
    id: '1',
    knowledgeBaseId: 'kb-1',
    title: 'API Gateway 配置指南',
    originalName: 'api_gateway_config_guide.pdf',
    softwareVersion: 'v2.3.1',
    feature: '认证授权',
    microservice: 'auth-service',
    status: 'CHUNKED',
    size: 2048576,
    uploadTime: new Date(Date.now() - 86400000),
    chunks: [
      { id: 'chunk-1-1', content: 'API Gateway 是微服务架构中的关键组件，负责请求路由、认证授权、限流等功能。它作为系统的入口点，对外提供统一的API接口，内部则根据路由规则将请求转发到相应的微服务。', size: 1024 },
      { id: 'chunk-1-2', content: '配置 API Gateway 的第一步是设置路由规则，指定目标服务的地址和路径。路由规则通常包括路径匹配、方法限制、参数校验等配置项。此外，还需要配置负载均衡策略，以便在多个实例间分配请求流量。', size: 950 },
      { id: 'chunk-1-3', content: '认证授权功能通过 JWT Token 实现，客户端需要在请求头中携带有效的 token。API Gateway 会验证 token 的有效性，并解析其中的用户身份信息。根据用户的权限级别，决定是否允许访问特定的资源或执行特定的操作。', size: 1100 }
    ]
  },
  {
    id: '2',
    knowledgeBaseId: 'kb-2',
    title: '数据库性能优化手册',
    originalName: 'db_performance_optimization.docx',
    softwareVersion: 'v1.8.5',
    feature: '性能调优',
    microservice: 'data-service',
    status: 'CONVERTED',
    size: 3145728,
    uploadTime: new Date(Date.now() - 172800000),
    chunks: []
  },
  {
    id: '3',
    knowledgeBaseId: 'kb-3',
    title: '用户服务开发规范',
    originalName: 'user_service_dev_guidelines.md',
    softwareVersion: 'v3.1.2',
    feature: '用户管理',
    microservice: 'user-service',
    status: 'UPLOADED',
    size: 1048576,
    uploadTime: new Date(Date.now() - 259200000),
    chunks: []
  },
  {
    id: '4',
    knowledgeBaseId: 'kb-1',
    title: '支付模块安全说明',
    originalName: 'payment_module_security.pdf',
    softwareVersion: 'v4.0.0',
    feature: '支付功能',
    microservice: 'payment-service',
    status: 'VECTOR_STORED',
    size: 1572864,
    uploadTime: new Date(Date.now() - 345600000),
    chunks: [
      { id: 'chunk-4-1', content: '支付模块的安全性至关重要，需要实施多层次的安全防护措施。这包括数据传输加密、敏感信息脱敏、交易风险监控等多个方面。', size: 1200 },
      { id: 'chunk-4-2', content: '敏感信息加密是支付模块的基础安全要求，所有交易数据必须经过加密处理。采用行业标准的加密算法，如 AES-256，确保即使数据被截获也无法轻易解密。', size: 1150 }
    ]
  },
  {
    id: '5',
    knowledgeBaseId: 'kb-4',
    title: '产品功能介绍',
    originalName: 'product_features_overview.docx',
    softwareVersion: 'v5.2.0',
    feature: '核心功能',
    microservice: 'product-service',
    status: 'CHUNKED',
    size: 2621440,
    uploadTime: new Date(Date.now() - 432000000),
    chunks: [
      { id: 'chunk-5-1', content: '本产品提供全面的功能模块，包括用户管理、数据处理、报表分析等核心功能。通过高效的架构设计，确保系统的稳定性和可扩展性。', size: 980 },
      { id: 'chunk-5-2', content: '用户管理模块支持角色权限控制，可以根据不同用户的角色分配相应的功能权限。系统还提供了审计日志功能，记录关键操作，便于追踪和审查。', size: 1050 }
    ]
  }
])

const fileTypeFilter = ref('')
const statusFilter = ref('')

const chunkDetails = reactive({
  chunks: [],
  maxChunkSize: 0,
  minChunkSize: 0,
  avgChunkSize: 0
})

const currentKB = computed(() => {
  return knowledgeBases.value.find(kb => kb.id === currentKBId.value)
})

const filteredFiles = computed(() => {
  let result = files.value.filter(f => f.knowledgeBaseId === currentKBId.value)
  if (searchQuery.value) {
    result = result.filter(f =>
      f.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      f.originalName.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  if (fileTypeFilter.value) {
    result = result.filter(f => f.originalName.toLowerCase().includes(fileTypeFilter.value))
  }
  if (statusFilter.value) {
    result = result.filter(f => f.status === statusFilter.value)
  }
  return result
})

const highlightedContent = computed(() => {
  if (!selectedFileForChunk.value || !selectedFileForChunk.value.chunks || selectedFileForChunk.value.chunks.length === 0) {
    return ''
  }

  let originalContent = ''
  selectedFileForChunk.value.chunks.forEach(chunk => {
    originalContent += chunk.content + ' '
  })

  const charMap = new Array(originalContent.length).fill(-1)
  const overlapMap = new Array(originalContent.length).fill(false)

  let pos = 0
  selectedFileForChunk.value.chunks.forEach((chunk, chunkIndex) => {
    const chunkText = chunk.content
    const chunkStart = originalContent.indexOf(chunkText, pos)
    
    if (chunkStart !== -1) {
      for (let i = chunkStart; i < chunkStart + chunkText.length; i++) {
        if (charMap[i] !== -1) {
          overlapMap[i] = true
        }
        charMap[i] = chunkIndex
      }
      pos = chunkStart + chunkText.length
    }
  })

  let result = ''
  let currentChunkIndex = -1
  
  for (let i = 0; i < originalContent.length; i++) {
    const char = originalContent[i]
    
    if (charMap[i] !== currentChunkIndex) {
      if (currentChunkIndex !== -1) {
        result += '</span>'
      }
      
      if (charMap[i] !== -1) {
        if (overlapMap[i]) {
          result += `<span class="bg-yellow-300">${char}`
        } else {
          const colors = ['bg-blue-100', 'bg-green-100', 'bg-purple-100', 'bg-pink-100', 'bg-indigo-100', 'bg-red-100', 'bg-teal-100']
          const color = colors[charMap[i] % colors.length]
          result += `<span class="${color}">${char}`
        }
      } else {
        result += char
      }
      
      currentChunkIndex = charMap[i]
    } else {
      result += char
    }
  }
  
  if (currentChunkIndex !== -1) {
    result += '</span>'
  }
  
  return result
})

const getKBType = (type) => {
  const types = { technical: '', product: 'success', operation: 'warning', training: 'info' }
  return types[type] || ''
}

const getKBTypeText = (type) => {
  const texts = { technical: '技术文档', product: '产品文档', operation: '运维文档', training: '培训资料' }
  return texts[type] || type
}

const getStatusText = (status) => {
  switch (status) {
    case 'ACTIVE':
      return '激活'
    case 'INACTIVE':
      return '停用'
    case 'MAINTENANCE':
      return '维护中'
    case 'INIT':
      return '初始化'
    case 'UPLOADED':
      return '已上传'
    case 'CONVERTING':
      return '转换中'
    case 'CONVERTED':
      return '已转换'
    case 'CHUNKED':
      return '已分片'
    case 'VECTOR_STORED':
      return '向量存储'
    default:
      return status
  }
}

const getStatusTagType = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'success'
    case 'INACTIVE':
      return 'info'
    case 'MAINTENANCE':
      return 'warning'
    default:
      return 'info'
  }
}

const getFileStatusType = (status) => {
  switch (status) {
    case 'INIT':
      return 'info'
    case 'UPLOADED':
      return 'primary'
    case 'CONVERTING':
      return 'warning'
    case 'CONVERTED':
      return 'success'
    case 'CHUNKED':
      return 'purple'
    case 'VECTOR_STORED':
      return 'indigo'
    default:
      return 'info'
  }
}

const canViewChunks = (file) => {
  return file.status === 'CHUNKED' || file.status === 'VECTOR_STORED'
}

const canChunk = (file) => {
  return file.status === 'CONVERTED' || file.status === 'UPLOADED'
}

const switchKB = (id) => {
  currentKBId.value = id
}

const editKB = (kb) => {
  editingKB.value = kb
  kbForm.name = kb.name
  kbForm.description = kb.description
  kbForm.type = kb.type
  showCreateKBDialog.value = true
}

const deleteKB = (kb) => {
  ElMessageBox.confirm(`确定要删除知识库 "${kb.name}" 吗？此操作将删除该知识库下的所有文件，此操作不可恢复。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    knowledgeBases.value = knowledgeBases.value.filter(k => k.id !== kb.id)
    files.value = files.value.filter(f => f.knowledgeBaseId !== kb.id)
    if (currentKBId.value === kb.id) {
      currentKBId.value = knowledgeBases.value.length > 0 ? knowledgeBases.value[0].id : null
    }
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const saveKB = () => {
  if (editingKB.value) {
    const index = knowledgeBases.value.findIndex(k => k.id === editingKB.value.id)
    if (index !== -1) {
      knowledgeBases.value[index] = { 
        ...knowledgeBases.value[index], 
        ...kbForm,
        updatedAt: new Date()
      }
    }
    ElMessage.success('修改成功')
  } else {
    knowledgeBases.value.push({
      id: `kb-${Date.now()}`,
      ...kbForm,
      status: 'ACTIVE',
      fileCount: 0,
      chunkCount: 0,
      createdAt: new Date(),
      updatedAt: new Date()
    })
    ElMessage.success('创建成功')
  }
  showCreateKBDialog.value = false
  editingKB.value = null
  kbForm.name = ''
  kbForm.description = ''
  kbForm.type = 'technical'
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    selectedFile.value = file
    uploadForm.title = file.name.replace(/\.[^/.]+$/, '')
  }
}

const submitUpload = async () => {
  if (!selectedFile.value || !currentKBId.value) return

  const newFileItem = {
    id: `file-${Date.now()}`,
    knowledgeBaseId: currentKBId.value,
    title: uploadForm.title,
    originalName: selectedFile.value.name,
    softwareVersion: uploadForm.softwareVersion,
    feature: uploadForm.feature,
    microservice: uploadForm.microservice,
    status: 'UPLOADED',
    size: selectedFile.value.size,
    uploadTime: new Date(),
    chunks: []
  }

  files.value.unshift(newFileItem)

  const kb = knowledgeBases.value.find(k => k.id === currentKBId.value)
  if (kb) kb.fileCount++

  showUploadDialog.value = false
  uploadForm.title = ''
  uploadForm.softwareVersion = ''
  uploadForm.feature = ''
  uploadForm.microservice = ''
  selectedFile.value = null
  if (fileInputRef.value) fileInputRef.value.value = ''

  ElMessage.success('上传成功')
}

const formatSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const syncVectorDB = () => {
  if (!currentKBId.value) return
  const kb = knowledgeBases.value.find(k => k.id === currentKBId.value)
  if (kb) kb.updatedAt = new Date()
  ElMessage.success('向量库同步已触发')
}

const downloadFile = (file) => {
  const blob = new Blob(['Simulated file content for ' + file.title], { type: 'application/octet-stream' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = file.originalName
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  ElMessage.success('文件已下载')
}

const viewChunks = (file) => {
  if (!canViewChunks(file)) return
  
  selectedFileForChunk.value = file
  
  if (file.chunks && file.chunks.length > 0) {
    const sizes = file.chunks.map(c => c.size)
    const totalSize = sizes.reduce((sum, size) => sum + size, 0)
    
    chunkDetails.chunks = file.chunks
    chunkDetails.maxChunkSize = Math.max(...sizes)
    chunkDetails.minChunkSize = Math.min(...sizes)
    chunkDetails.avgChunkSize = Math.round(totalSize / sizes.length)
  } else {
    chunkDetails.chunks = []
    chunkDetails.maxChunkSize = 0
    chunkDetails.minChunkSize = 0
    chunkDetails.avgChunkSize = 0
  }
  
  showChunkDetailDialog.value = true
}

const openVisualization = (file) => {
  if (!canViewChunks(file)) return
  
  selectedFileForChunk.value = file
  
  if (file.chunks && file.chunks.length > 0) {
    const sizes = file.chunks.map(c => c.size)
    const totalSize = sizes.reduce((sum, size) => sum + size, 0)
    
    chunkDetails.chunks = file.chunks
    chunkDetails.maxChunkSize = Math.max(...sizes)
    chunkDetails.minChunkSize = Math.min(...sizes)
    chunkDetails.avgChunkSize = Math.round(totalSize / sizes.length)
  } else {
    chunkDetails.chunks = []
    chunkDetails.maxChunkSize = 0
    chunkDetails.minChunkSize = 0
    chunkDetails.avgChunkSize = 0
  }
  
  showVisualizationDialog.value = true
}

const openChunkModal = (file) => {
  if (!canChunk(file)) return
  
  selectedFileForChunk.value = file
  showChunkDialog.value = true
}

const performChunking = () => {
  if (!selectedFileForChunk.value) return
  
  setTimeout(() => {
    const fileIndex = files.value.findIndex(f => f.id === selectedFileForChunk.value?.id)
    if (fileIndex !== -1) {
      files.value[fileIndex].status = 'CHUNKED'
      
      const mockChunks = []
      const contentBase = '这是分片内容的示例，包含有关该文档主题的关键信息。分片处理有助于提高检索效率和准确性。'
      
      for (let i = 0; i < 5; i++) {
        mockChunks.push({
          id: `chunk-${selectedFileForChunk.value?.id}-${i + 1}`,
          content: `${contentBase} 第 ${i + 1} 部分，用于演示分片效果。`,
          size: Math.floor(Math.random() * 500) + 500
        })
      }
      
      files.value[fileIndex].chunks = mockChunks
      selectedFileForChunk.value = files.value[fileIndex]
      
      const kbIndex = knowledgeBases.value.findIndex(kb => kb.id === files.value[fileIndex].knowledgeBaseId)
      if (kbIndex !== -1) {
        knowledgeBases.value[kbIndex].chunkCount += mockChunks.length
      }
    }
    
    showChunkDialog.value = false
    ElMessage.success('分片完成')
  }, 1500)
}

const viewChunkDetail = (chunk) => {
  ElMessage.info(chunk.content)
}

const copyChunk = (chunk) => {
  navigator.clipboard.writeText(chunk.content).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const deleteFile = (file) => {
  ElMessageBox.confirm(`确定要删除文件 "${file.title}" 吗？此操作不可恢复。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const kbIndex = knowledgeBases.value.findIndex(kb => kb.id === file.knowledgeBaseId)
    if (kbIndex !== -1) {
      knowledgeBases.value[kbIndex].fileCount -= 1
      if (file.status === 'CHUNKED' || file.status === 'VECTOR_STORED') {
        knowledgeBases.value[kbIndex].chunkCount -= file.chunks?.length ?? 0
      }
    }
    
    files.value = files.value.filter(f => f.id !== file.id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>

<style scoped>
.kb-container {
  display: flex;
  min-height: 100vh;
}

.kb-main {
  flex: 1;
  margin-left: 250px;
  background: #f5f5f5;
}

.kb-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
}

.breadcrumb ol {
  display: flex;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
}

.breadcrumb li {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.breadcrumb li + li {
  margin-left: 8px;
}

.search-box {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 4px;
  padding: 4px 12px;
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  margin-left: 8px;
  width: 200px;
}

.kb-content {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-info h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.header-info p {
  color: #999;
  margin: 4px 0 0;
}

.btn-primary {
  display: flex;
  align-items: center;
  background: #1890ff;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-success {
  display: flex;
  align-items: center;
  background: #52c41a;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-success:hover {
  background: #73d13d;
}

.kb-grid-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.section-header {
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.kb-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.kb-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.kb-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.kb-card.active {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.kb-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.kb-icon {
  width: 40px;
  height: 40px;
  background: #f0f5ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kb-icon :deep(.el-icon) {
  font-size: 20px;
  color: #1890ff;
}

.kb-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  background: transparent;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #999;
}

.icon-btn:hover {
  color: #1890ff;
}

.icon-btn.danger:hover {
  color: #ff4d4f;
}

.kb-card-body h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #333;
}

.kb-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.kb-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.stat-item :deep(.el-icon) {
  margin-right: 4px;
}

.kb-tags {
  display: flex;
  gap: 8px;
}

.add-card {
  border: 2px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 140px;
}

.add-card:hover {
  border-color: #1890ff;
}

.add-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.add-content :deep(.el-icon) {
  font-size: 24px;
  margin-bottom: 8px;
}

.add-content p {
  margin: 0;
  font-size: 14px;
}

.kb-detail-section {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.detail-info h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.detail-info p {
  color: #999;
  margin: 4px 0 0;
}

.detail-actions {
  display: flex;
  gap: 12px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.filter-select {
  width: 120px;
}

.file-table {
  padding: 16px 20px;
}

.file-title-cell {
  display: flex;
  align-items: center;
}

.file-title-cell :deep(.el-icon) {
  margin-right: 12px;
  color: #1890ff;
}

.file-title-info {
  display: flex;
  flex-direction: column;
}

.file-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.file-original {
  font-size: 12px;
  color: #999;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.action-btns :deep(.el-button.danger) {
  color: #ff4d4f;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  color: #999;
}

.empty-state :deep(.el-icon) {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0;
}

.upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: #1890ff;
}

.upload-icon {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 16px;
}

.upload-area p {
  margin: 0 0 8px;
  color: #666;
}

.upload-hint {
  font-size: 12px !important;
  color: #999 !important;
}

.selected-file {
  display: flex;
  align-items: center;
  margin-top: 12px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 4px;
}

.selected-file :deep(.el-icon) {
  margin-right: 8px;
  color: #1890ff;
}

.selected-file span {
  font-size: 13px;
  color: #333;
}

.chunk-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.stat-card.blue {
  background: #e6f7ff;
}

.stat-card.green {
  background: #f6ffed;
}

.stat-card.purple {
  background: #f9f0ff;
}

.stat-card.yellow {
  background: #fff7e6;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.chunk-preview {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.visualization-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.original-content h4,
.chunk-detail-list h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 12px;
  color: #333;
}

.content-box {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
}

.chunk-detail-list {
  margin-top: 16px;
}

.chunk-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.chunk-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.chunk-title {
  font-weight: 600;
  color: #333;
}

.chunk-size {
  font-size: 12px;
  color: #999;
}

.chunk-body {
  padding: 16px;
  white-space: pre-wrap;
  color: #666;
}

/* 分片可视化高亮样式 */
:deep(.bg-blue-100) {
  background-color: #e6f7ff;
}

:deep(.bg-green-100) {
  background-color: #f6ffed;
}

:deep(.bg-purple-100) {
  background-color: #f9f0ff;
}

:deep(.bg-pink-100) {
  background-color: #fff0f6;
}

:deep(.bg-indigo-100) {
  background-color: #eef2ff;
}

:deep(.bg-red-100) {
  background-color: #fff2f0;
}

:deep(.bg-teal-100) {
  background-color: #e6fffb;
}

:deep(.bg-yellow-300) {
  background-color: #fff3b0;
}

.hidden {
  display: none;
}
</style>