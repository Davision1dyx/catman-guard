<template>
  <div class="file-container">
    <Sidebar />
    
    <!-- 主内容区 -->
    <main class="file-main">
      <!-- 顶部状态栏 -->
      <header class="file-header">
        <div class="header-left">
          <nav class="breadcrumb">
            <ol>
              <li><i class="el-icon-home"></i><span>首页</span></li>
              <li><i class="el-icon-arrow-right"></i><span>文件管理</span></li>
            </ol>
          </nav>
        </div>
        <div class="header-right">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input type="text" v-model="searchQuery" placeholder="搜索文件..." />
          </div>
          <button class="upload-btn" @click="uploadFile">
            <i class="el-icon-upload"></i>上传文件
          </button>
        </div>
      </header>

      <!-- 主工作区 -->
      <div class="file-content">
        <!-- 统计卡片 -->
        <div class="stats-row">
          <div class="stat-card">
            <div class="stat-icon bg-blue">
              <i class="el-icon-files"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ fileList.length }}</p>
              <p class="stat-label">文件总数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon bg-green">
              <i class="el-icon-check-circle"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ processedCount }}</p>
              <p class="stat-label">已处理</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon bg-orange">
              <i class="el-icon-clock"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ processingCount }}</p>
              <p class="stat-label">处理中</p>
            </div>
          </div>
        </div>

        <!-- 搜索和筛选 -->
        <div class="filter-bar">
          <el-select 
            v-model="fileTypeFilter"
            placeholder="文件类型"
            class="type-select"
          >
            <el-option label="全部" value="" />
            <el-option label="PDF" value="pdf" />
            <el-option label="Word" value="doc" />
            <el-option label="Excel" value="xlsx" />
            <el-option label="Markdown" value="md" />
            <el-option label="文本" value="txt" />
          </el-select>
          <el-select 
            v-model="statusFilter"
            placeholder="状态"
            class="status-select"
          >
            <el-option label="全部" value="" />
            <el-option label="已处理" value="已处理" />
            <el-option label="处理中" value="处理中" />
          </el-select>
        </div>

        <!-- 文件列表 -->
        <div class="file-list-container">
          <div v-if="fileList.length === 0" class="empty-state">
            <div class="empty-icon">
              <i class="el-icon-folder-opened"></i>
            </div>
            <p>暂无文件，请上传文档</p>
          </div>
          
          <div v-else class="file-grid">
            <div 
              v-for="file in filteredFiles" 
              :key="file.id" 
              class="file-card"
            >
              <div class="file-card-header">
                <span class="file-icon-large">{{ getFileIcon(file.type) }}</span>
                <div class="file-card-actions">
                  <button class="action-btn" @click="viewFile(file)">
                    <i class="el-icon-eye"></i>
                  </button>
                  <button class="action-btn" @click="viewSplit(file)">
                    <i class="el-icon-document"></i>
                  </button>
                  <button class="action-btn delete" @click="deleteFile(file)">
                    <i class="el-icon-delete"></i>
                  </button>
                </div>
              </div>
              <div class="file-card-body">
                <h4 class="file-name">{{ file.name }}</h4>
                <div class="file-meta">
                  <span class="meta-item">{{ file.size }}</span>
                  <span class="meta-divider">·</span>
                  <span class="meta-item">{{ file.type }}</span>
                </div>
                <div class="file-footer">
                  <el-tag :type="getStatusType(file.status)" size="small">
                    {{ file.status }}
                  </el-tag>
                  <span class="upload-time">{{ file.uploadTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 上传对话框 -->
    <el-dialog 
      title="上传文件" 
      :visible.sync="uploadDialogVisible"
      width="500px"
    >
      <div class="upload-form">
        <el-form :model="uploadForm" label-width="80px">
          <el-form-item label="文件">
            <div class="upload-area" @click="triggerFileInput">
              <i class="el-icon-cloud-upload"></i>
              <p>点击或拖拽文件到此处上传</p>
              <p class="upload-hint">支持 PDF, Word, Excel, Markdown, TXT 格式</p>
              <input 
                type="file" 
                ref="fileInput" 
                class="file-input"
                @change="handleFileSelect"
                accept=".pdf,.doc,.docx,.xlsx,.xls,.md,.txt"
              />
            </div>
            <div v-if="selectedFile" class="selected-file">
              <i class="el-icon-file"></i>
              <span>{{ selectedFile.name }}</span>
            </div>
          </el-form-item>
          <el-form-item label="标题">
            <el-input v-model="uploadForm.title" placeholder="请输入文件标题" />
          </el-form-item>
          <el-form-item label="描述">
            <el-textarea v-model="uploadForm.description" placeholder="请输入文件描述" rows="3" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button 
          @click="submitUpload" 
          type="primary"
          :disabled="!selectedFile"
        >
          确认上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 分片详情对话框 -->
    <el-dialog 
      title="分片详情" 
      :visible.sync="splitDialogVisible"
      width="800px"
    >
      <div v-if="currentFile" class="split-detail">
        <div class="split-header">
          <h3>{{ currentFile.name }}</h3>
          <p>总分片数: {{ splits.length }}</p>
        </div>
        <div class="split-list">
          <div 
            v-for="(split, index) in splits" 
            :key="index" 
            class="split-item"
          >
            <div class="split-header-row">
              <span class="split-index">分片 {{ index + 1 }}</span>
              <span class="split-tokens">{{ split.tokens }} tokens</span>
            </div>
            <div class="split-content">
              <p>{{ split.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import Sidebar from '../components/Sidebar.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fileApi } from '../services/api'

const searchQuery = ref('')
const fileTypeFilter = ref('')
const statusFilter = ref('')
const uploadDialogVisible = ref(false)
const splitDialogVisible = ref(false)
const currentFile = ref(null)
const selectedFile = ref(null)
const fileInput = ref(null)
const loading = ref(false)

const uploadForm = reactive({
  title: '',
  description: ''
})

const fileList = ref([])

const splits = ref([])

const fetchFiles = async () => {
  loading.value = true
  try {
    const data = await fileApi.listAll({})
    fileList.value = (data || []).map((f: any) => ({
      id: f.id,
      name: f.title || f.originalName || '未知文件',
      type: f.type || getFileType(f.originalName || ''),
      size: f.size ? formatSize(f.size) : '0 KB',
      sizeBytes: f.size || 0,
      uploadTime: f.uploadTime ? new Date(f.uploadTime).toLocaleString('zh-CN') : new Date().toLocaleString('zh-CN'),
      status: getStatusText(f.status),
      statusRaw: f.status
    }))
  } catch (error) {
    console.error('Failed to fetch files:', error)
  } finally {
    loading.value = false
  }
}

const getFileType = (filename) => {
  const ext = filename.split('.').pop()?.toUpperCase() || ''
  const typeMap: Record<string, string> = {
    'PDF': 'PDF',
    'DOC': 'Word', 'DOCX': 'Word',
    'XLS': 'Excel', 'XLSX': 'Excel',
    'MD': 'Markdown', 'MARKDOWN': 'Markdown',
    'TXT': '文本'
  }
  return typeMap[ext] || '文本'
}

const getStatusText = (status) => {
  const statusMap: Record<string, string> = {
    'INIT': '待处理',
    'UPLOADED': '待处理',
    'CONVERTING': '处理中',
    'CONVERTED': '已处理',
    'CHUNKED': '已处理',
    'VECTOR_STORED': '已处理',
    'FAILED': '失败'
  }
  return statusMap[status] || '待处理'
}

onMounted(() => {
  fetchFiles()
})

const processedCount = computed(() =>
  fileList.value.filter(f => f.statusRaw === 'CONVERTED' || f.statusRaw === 'CHUNKED' || f.statusRaw === 'VECTOR_STORED').length
)

const processingCount = computed(() =>
  fileList.value.filter(f => f.statusRaw === 'CONVERTING' || f.statusRaw === 'UPLOADED').length
)

const filteredFiles = computed(() => {
  return fileList.value.filter(file => {
    const matchSearch = !searchQuery.value ||
      file.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchType = !fileTypeFilter.value ||
      file.type.toLowerCase().includes(fileTypeFilter.value.toLowerCase())
    const matchStatus = !statusFilter.value || file.status === statusFilter.value
    return matchSearch && matchType && matchStatus
  })
})

const getFileIcon = (type) => {
  const icons: Record<string, string> = {
    'PDF': '📕',
    'Word': '📘',
    'Excel': '📗',
    'Markdown': '📝',
    '文本': '📄'
  }
  return icons[type] || '📄'
}

const getStatusType = (status) => {
  const types: Record<string, string> = {
    '已处理': 'success',
    '处理中': 'warning',
    '待处理': 'info',
    '失败': 'danger'
  }
  return types[status] || 'info'
}

const uploadFile = () => {
  uploadDialogVisible.value = true
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    selectedFile.value = file
    uploadForm.title = file.name.replace(/\.[^/.]+$/, '')
  }
}

const submitUpload = async () => {
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('title', uploadForm.title)
    formData.append('description', uploadForm.description)

    await fileApi.upload(formData)
    await fetchFiles()

    uploadDialogVisible.value = false
    uploadForm.title = ''
    uploadForm.description = ''
    selectedFile.value = null
    if (fileInput.value) {
      fileInput.value.value = ''
    }

    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const formatSize = (bytes) => {
  if (!bytes) return '0 KB'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const viewFile = (file) => {
  ElMessage.info(`查看文件: ${file.name}`)
}

const viewSplit = async (file) => {
  currentFile.value = file
  try {
    const data = await fileApi.getChunks(file.id)
    splits.value = (data || []).map((s: any) => ({
      content: s.content || '',
      tokens: s.tokens || s.size || 0
    }))
  } catch (error) {
    console.error('Failed to fetch splits:', error)
  }
  splitDialogVisible.value = true
}

const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该文件吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await fileApi.delete(file.id)
    await fetchFiles()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.file-container {
  display: flex;
  min-height: 100vh;
  background: #f9fafb;
}

.file-main {
  flex: 1;
  margin-left: 256px;
  display: flex;
  flex-direction: column;
}

/* 顶部状态栏 */
.file-header {
  background: #fff;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-left {
  flex: 1;
}

.breadcrumb ol {
  display: flex;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
  gap: 8px;
}

.breadcrumb li {
  display: flex;
  align-items: center;
  color: #6b7280;
  font-size: 14px;
}

.breadcrumb li:first-child i {
  margin-right: 4px;
}

.breadcrumb li:last-child {
  color: #1f2937;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  position: relative;
}

.search-box i {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.search-box input {
  width: 200px;
  padding: 8px 12px 8px 36px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.search-box input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-btn:hover {
  background: #1d4ed8;
}

/* 主工作区 */
.file-content {
  flex: 1;
  padding: 24px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.bg-blue {
  background: #dbeafe;
  color: #2563eb;
}

.stat-icon.bg-green {
  background: #dcfce7;
  color: #16a34a;
}

.stat-icon.bg-orange {
  background: #fef3c7;
  color: #d97706;
}

.stat-info {
  flex: 1;
}

.stat-value {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.type-select,
.status-select {
  width: 150px;
}

/* 文件列表 */
.file-list-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 24px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #9ca3af;
}

.empty-icon {
  width: 64px;
  height: 64px;
  background: #f3f4f6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
}

/* 文件卡片网格 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.file-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.file-card:hover {
  border-color: #2563eb;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1);
}

.file-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
}

.file-icon-large {
  font-size: 48px;
}

.file-card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #2563eb;
  border-color: #2563eb;
}

.action-btn.delete:hover {
  color: #ef4444;
  border-color: #ef4444;
}

.file-card-body {
  padding: 0 20px 20px;
}

.file-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.meta-item {
  font-size: 12px;
  color: #6b7280;
}

.meta-divider {
  color: #d1d5db;
}

.file-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-time {
  font-size: 12px;
  color: #9ca3af;
}

/* 上传表单 */
.upload-form {
  padding: 20px 0;
}

.upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.upload-area:hover {
  border-color: #2563eb;
  background: #f9fafb;
}

.upload-area i {
  font-size: 48px;
  color: #9ca3af;
  margin-bottom: 12px;
}

.upload-area p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.upload-hint {
  font-size: 12px !important;
  color: #9ca3af !important;
  margin-top: 4px !important;
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.selected-file {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 12px;
  background: #f0fdf4;
  border: 1px solid #86efac;
  border-radius: 8px;
  color: #16a34a;
}

.selected-file i {
  font-size: 18px;
}

/* 分片详情 */
.split-detail {
  max-height: 500px;
  overflow-y: auto;
}

.split-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e5e5;
  margin-bottom: 16px;
}

.split-header h3 {
  margin: 0;
  font-size: 16px;
}

.split-header p {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #6b7280;
}

.split-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.split-item {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
}

.split-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.split-index {
  font-weight: 600;
  color: #1f2937;
}

.split-tokens {
  font-size: 13px;
  color: #6b7280;
  padding: 4px 10px;
  background: #f3f4f6;
  border-radius: 20px;
}

.split-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #4b5563;
}

/* 响应式 */
@media (max-width: 768px) {
  .file-main {
    margin-left: 0;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
  }
  
  .file-grid {
    grid-template-columns: 1fr;
  }
}
</style>
