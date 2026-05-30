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
        <div class="color-legend">
          <h4>颜色图例</h4>
          <div class="legend-items">
            <div class="legend-item">
              <span class="legend-color bg-blue-100"></span>
              <span class="legend-text">分片1</span>
            </div>
            <div class="legend-item">
              <span class="legend-color bg-green-100"></span>
              <span class="legend-text">分片2</span>
            </div>
            <div class="legend-item">
              <span class="legend-color bg-purple-100"></span>
              <span class="legend-text">分片3</span>
            </div>
            <div class="legend-item">
              <span class="legend-color bg-pink-100"></span>
              <span class="legend-text">分片4</span>
            </div>
            <div class="legend-item overlap">
              <span class="legend-color bg-yellow-300"></span>
              <span class="legend-text">重叠部分</span>
            </div>
          </div>
        </div>
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
import { ref, computed, reactive, onMounted } from 'vue'
import Sidebar from '../components/Sidebar.vue'
import TopHeader from '../components/TopHeader.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Document, Edit, Delete, DocumentCopy, Share, Plus, Upload, Refresh, Download, List, View, Scissor, FolderOpened } from '@element-plus/icons-vue'
import { knowledgeApi, fileApi } from '../services/api'

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
const loading = ref(false)

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

const knowledgeBases = ref([])
const files = ref([])

const fetchKnowledgeBases = async () => {
  try {
    const response = await knowledgeApi.list({})
    const data = response.data || response || []
    console.log('API返回的原始数据:', data)
    console.log('数据类型:', typeof data, Array.isArray(data))
    knowledgeBases.value = (data || []).map((kb) => ({
      id: kb.knowledgeId,
      name: kb.name,
      description: kb.description,
      type: kb.type,
      fileCount: kb.fileCount || 0,
      chunkCount: kb.chunkCount || 0,
      status: 'ACTIVE',
      createdAt: kb.createdTime ? new Date(kb.createdTime) : new Date(),
      updatedAt: kb.updatedTime ? new Date(kb.updatedTime) : new Date()
    }))
    console.log('处理后的knowledgeBases:', knowledgeBases.value)
    console.log('fileCount值:', knowledgeBases.value.map(kb => ({ name: kb.name, fileCount: kb.fileCount, chunkCount: kb.chunkCount })))
  } catch (error) {
    console.error('Failed to fetch knowledge bases:', error)
  }
}

const fetchFiles = async () => {
  if (!currentKBId.value) return
  loading.value = true
  try {
    const response = await fileApi.list(currentKBId.value, {})
    const data = response.data || response || []
    files.value = (data || []).map((f) => ({
      id: f.fileId,
      knowledgeBaseId: f.knowledgeId || currentKBId.value,
      title: f.fileTitle,
      originalName: f.fileName,
      softwareVersion: f.version || '',
      feature: f.feature || '',
      microservice: f.microservice || '',
      status: f.status || 'UPLOADED',
      size: f.size || 0,
      uploadTime: f.createTime ? new Date(f.createTime) : new Date(),
      chunks: []
    }))
  } catch (error) {
    console.error('Failed to fetch files:', error)
  } finally {
    loading.value = false
  }
}

const switchKB = async (id) => {
  currentKBId.value = id
  await fetchFiles()
}

onMounted(() => {
  fetchKnowledgeBases()
})

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

  const chunks = selectedFileForChunk.value.chunks
  const colors = ['bg-blue-100', 'bg-green-100', 'bg-purple-100', 'bg-pink-100', 'bg-indigo-100', 'bg-red-100', 'bg-teal-100']

  let mergedText = ''
  const chunkAssignments = []
  const overlapFlags = []

  for (let i = 0; i < chunks.length; i++) {
    const content = chunks[i].content
    
    let maxOverlap = 0
    for (let len = Math.min(content.length, mergedText.length); len > 0; len--) {
      const suffix = content.substring(0, len)
      if (mergedText.endsWith(suffix)) {
        maxOverlap = len
        break
      }
    }

    const newContent = content.substring(maxOverlap)
    
    if (maxOverlap > 0) {
      const startIdx = mergedText.length - maxOverlap
      for (let j = startIdx; j < mergedText.length; j++) {
        overlapFlags[j] = true
      }
    }

    for (let j = 0; j < newContent.length; j++) {
      chunkAssignments.push(i)
      overlapFlags.push(false)
    }

    mergedText += newContent
  }

  let result = ''
  let currentChunkIndex = -1
  let inOverlap = false

  for (let i = 0; i < mergedText.length; i++) {
    const char = mergedText[i]
    const isOverlap = overlapFlags[i]
    const chunkIdx = chunkAssignments[i]

    if (isOverlap && !inOverlap) {
      if (currentChunkIndex !== -1) {
        result += '</span>'
      }
      result += `<span class="bg-yellow-300">${char}`
      inOverlap = true
      currentChunkIndex = -1
    } else if (isOverlap && inOverlap) {
      result += char
    } else if (!isOverlap && chunkIdx !== currentChunkIndex) {
      if (inOverlap) {
        result += '</span>'
        inOverlap = false
      }
      if (currentChunkIndex !== -1) {
        result += '</span>'
      }
      const color = colors[chunkIdx % colors.length]
      result += `<span class="${color}">${char}`
      currentChunkIndex = chunkIdx
    } else {
      result += char
    }
  }

  if (currentChunkIndex !== -1) {
    result += '</span>'
  }
  if (inOverlap) {
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
  if (!file || !file.status) return false
  const status = file.status.toUpperCase()
  return status === 'CHUNKED' || status === 'VECTOR_STORED'
}

const canChunk = (file) => {
  if (!file || !file.status) return false
  const status = file.status.toUpperCase()
  return status === 'CONVERTED' || status === 'UPLOADED'
}

const editKB = (kb) => {
  editingKB.value = kb
  kbForm.name = kb.name
  kbForm.description = kb.description
  kbForm.type = kb.type
  showCreateKBDialog.value = true
}

const deleteKB = async (kb) => {
  try {
    await ElMessageBox.confirm(`确定要删除知识库 "${kb.name}" 吗？此操作将删除该知识库下的所有文件，此操作不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await knowledgeApi.delete(kb.id)
    await fetchKnowledgeBases()
    if (currentKBId.value === kb.id) {
      currentKBId.value = knowledgeBases.value.length > 0 ? knowledgeBases.value[0].id : null
    }
    if (currentKBId.value) await fetchFiles()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveKB = async () => {
  try {
    if (editingKB.value) {
      await knowledgeApi.update(editingKB.value.id, {
        name: kbForm.name,
        description: kbForm.description,
        type: kbForm.type
      })
    } else {
      await knowledgeApi.create({
        name: kbForm.name,
        description: kbForm.description,
        type: kbForm.type
      })
    }
    await fetchKnowledgeBases()
    ElMessage.success(editingKB.value ? '修改成功' : '创建成功')
  } catch (error) {
    ElMessage.error(editingKB.value ? '修改失败' : '创建失败')
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

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('knowledgeId', currentKBId.value)
    formData.append('title', uploadForm.title)
    formData.append('softwareVersion', uploadForm.softwareVersion)
    formData.append('feature', uploadForm.feature)
    formData.append('microservice', uploadForm.microservice)

    await fileApi.upload(formData)
    await fetchFiles()
    await fetchKnowledgeBases()

    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }

  showUploadDialog.value = false
  uploadForm.title = ''
  uploadForm.softwareVersion = ''
  uploadForm.feature = ''
  uploadForm.microservice = ''
  selectedFile.value = null
  if (fileInputRef.value) fileInputRef.value.value = ''
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

const viewChunks = async (file) => {
  if (!canViewChunks(file)) return

  selectedFileForChunk.value = file

  try {
    const response = await fileApi.listChunks(file.id)
    const data = response.data || response
    if (data.chunks && data.chunks.length > 0) {
      chunkDetails.chunks = data.chunks.map(c => ({
        id: c.chunkId,
        content: c.content,
        size: c.size
      }))
      chunkDetails.maxChunkSize = data.maxChunkSize
      chunkDetails.minChunkSize = data.minChunkSize
      chunkDetails.avgChunkSize = data.avgChunkSize
    } else {
      chunkDetails.chunks = []
      chunkDetails.maxChunkSize = 0
      chunkDetails.minChunkSize = 0
      chunkDetails.avgChunkSize = 0
    }
  } catch (error) {
    ElMessage.error('获取分片信息失败')
    chunkDetails.chunks = []
  }

  showChunkDetailDialog.value = true
}

const openVisualization = async (file) => {
  if (!canViewChunks(file)) return

  selectedFileForChunk.value = file

  try {
    const response = await fileApi.listChunks(file.id)
    const data = response.data || response
    if (data && data.chunks && data.chunks.length > 0) {
      const mappedChunks = data.chunks.map(c => ({
        id: c.chunkId,
        content: c.content,
        size: c.size
      }))
      chunkDetails.chunks = mappedChunks
      selectedFileForChunk.value.chunks = mappedChunks
      chunkDetails.maxChunkSize = data.maxChunkSize || 0
      chunkDetails.minChunkSize = data.minChunkSize || 0
      chunkDetails.avgChunkSize = data.avgChunkSize || 0
    } else {
      chunkDetails.chunks = []
      selectedFileForChunk.value.chunks = []
      chunkDetails.maxChunkSize = 0
      chunkDetails.minChunkSize = 0
      chunkDetails.avgChunkSize = 0
    }
  } catch (error) {
    console.error('获取分片信息失败:', error)
    ElMessage.error('获取分片信息失败')
    chunkDetails.chunks = []
    selectedFileForChunk.value.chunks = []
  }

  showVisualizationDialog.value = true
}

const openChunkModal = (file) => {
  if (!canChunk(file)) return
  
  selectedFileForChunk.value = file
  showChunkDialog.value = true
}

const performChunking = async () => {
  if (!selectedFileForChunk.value) return

  try {
    await fileApi.chunk(selectedFileForChunk.value.id, {
      chunkType: chunkForm.mode,
      chunkSize: chunkForm.length,
      chunkOverlap: chunkForm.childLength
    })
    await fetchFiles()
    await fetchKnowledgeBases()
    ElMessage.success('分片任务已提交')
  } catch (error) {
    ElMessage.error('分片失败')
  }

  showChunkDialog.value = false
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

const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm(`确定要删除文件 "${file.title}" 吗？此操作不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await fileApi.delete(file.id)
    await fetchFiles()
    await fetchKnowledgeBases()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel' && !(error && error.type === 'cancel')) {
      ElMessage.error('删除失败')
    }
  }
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

.color-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.color-legend h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.legend-items {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-text {
  font-size: 13px;
  color: #666;
}

.legend-item.overlap .legend-text {
  font-weight: 600;
  color: #d97706;
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