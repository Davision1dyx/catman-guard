<template>
  <div class="chat-container">
    <Sidebar />
    
    <!-- 主内容区 -->
    <div class="chat-main">
      <!-- 顶部状态栏 -->
      <TopHeader title="智能问答">
        <template #search>
          <div class="search-box">
            <el-icon><Search /></el-icon>
            <input type="text" placeholder="搜索工单、文档或对话..." />
          </div>
        </template>
        <template #actions>
          <button class="notification-btn">
            <el-icon><Bell /></el-icon>
            <span class="notification-badge">3</span>
          </button>
          <div class="knowledge-select">
            <span>全部知识库</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
        </template>
      </TopHeader>

      <!-- 主工作区 -->
      <main class="chat-content">
        <!-- 欢迎态 -->
        <div v-if="messages.length === 0" class="welcome-section">
          <div class="welcome-content">
            <h2>您好，我是猫猫侠，您的智能运维助手</h2>
            <p>我可以帮您解答故障排查、配置咨询、操作指导等问题，也可以为您创建工单</p>
            <div class="quick-buttons">
              <button class="quick-btn quick-btn-blue">
                <i class="el-icon-wrench"></i>故障排查
              </button>
              <button class="quick-btn quick-btn-green">
                <i class="el-icon-setting"></i>配置咨询
              </button>
              <button class="quick-btn quick-btn-yellow">
                <i class="el-icon-document"></i>操作指导
              </button>
              <button class="quick-btn quick-btn-purple">
                <i class="el-icon-plus"></i>新建工单
              </button>
            </div>
          </div>
        </div>

        <!-- 对话区域 -->
        <div class="conversation-section" v-show="messages.length > 0">
          <div class="conversation-list" v-if="messages.length > 0">
            <div
              v-for="(msg, index) in messages"
              :key="index"
              :class="['message-item', msg.type]"
            >
              <div class="message-avatar">
                <div v-if="msg.type === 'user'" class="avatar-user">
                  <i class="el-icon-user"></i>
                </div>
                <div v-else class="avatar-ai">
                  <img src="/catman.png" alt="CATMAN" @error="handleImgError">
                </div>
              </div>
              <div class="message-body">
                <div :class="['message-bubble', msg.type]">
                  <div v-html="renderMarkdown(msg.content)"></div>
                </div>
                <div class="message-time">刚刚</div>
                
                <!-- 参考文档 -->
                <div v-if="msg.type === 'ai' && showReferences" class="references-box">
                  <details>
                    <summary>参考文档 (2)</summary>
                    <div class="reference-list">
                      <div class="reference-item">
                        <a href="#" class="reference-link">Tomcat 端口配置指南.pdf</a>
                        <p class="reference-snippet">相关片段：...Connection refused 通常表示端口已被其他进程占用...</p>
                      </div>
                      <div class="reference-item">
                        <a href="#" class="reference-link">服务器故障排查手册.docx</a>
                        <p class="reference-snippet">相关片段：...常见 Tomcat 启动问题及解决方案...</p>
                      </div>
                    </div>
                  </details>
                </div>
              </div>
            </div>

            <!-- 意图识别卡片 -->
            <div v-if="showIntentCard" class="intent-card">
              <div class="intent-header">
                <h3>检测到故障报告意图</h3>
                <button class="intent-close" @click="showIntentCard = false">
                  <i class="el-icon-close"></i>
                </button>
              </div>
              <div class="intent-body">
                <div class="form-row">
                  <div class="form-group">
                    <label>问题类型</label>
                    <select>
                      <option>故障</option>
                      <option>咨询</option>
                      <option>需求</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>项目名称</label>
                    <input type="text" placeholder="选择或输入项目" />
                  </div>
                </div>
                <div class="form-group">
                  <label>问题详情</label>
                  <textarea rows="4" placeholder="详细描述问题现象、影响范围等"></textarea>
                </div>
                <div class="form-group">
                  <label>环境信息</label>
                  <div class="form-row">
                    <input type="text" placeholder="版本号" value="8.5.91" />
                    <input type="text" placeholder="操作系统" value="CentOS 7.9" />
                  </div>
                </div>
                <div class="form-group">
                  <label>附件上传</label>
                  <div class="upload-area">
                    <i class="el-icon-upload"></i>
                    <p>拖拽日志文件到此处或点击上传</p>
                    <p class="upload-hint">支持 JPG, PNG, PDF, LOG 等格式</p>
                  </div>
                </div>
                <div class="form-hint">系统将自动派单给：李明 (今日值班)</div>
                <div class="form-actions">
                  <button class="btn-submit">确认提交</button>
                  <button class="btn-cancel">修改信息</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 思考过程 -->
          <div v-for="(thinking, index) in thinkingMessages" :key="'thinking-' + index" class="thinking-block">
            <div class="thinking-header" @click="toggleThinking(index)">
              <div class="thinking-header-left">
                <span class="thinking-icon">⚡</span>
                <span>思考过程</span>
              </div>
              <span :class="['thinking-toggle', { expanded: thinking.expanded }]">▼</span>
            </div>
            <div :class="['thinking-content', { expanded: thinking.expanded }]">
              {{ thinking.content }}
            </div>
          </div>

          <!-- 推荐问题 -->
          <div v-if="recommendQuestions.length > 0" class="recommend-box">
            <h4>你可能还想问</h4>
            <div class="recommend-list">
              <div
                v-for="(question, index) in recommendQuestions"
                :key="index"
                class="recommend-item"
                @click="askRecommendation(question)"
              >{{ question }}</div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="isLoading" class="loading-message">
            <div class="loading-dots">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
            <span>正在思考中...</span>
          </div>
        </div>

        <!-- 底部输入区 -->
        <div class="input-section">
          <div class="input-context">
            <span>已记住上文关于对话的讨论</span>
          </div>
          <div class="input-area">
            <div class="input-wrapper">
              <textarea
                v-model="inputMessage"
                @input="autoResize"
                @keydown="handleKeyDown"
                placeholder="输入您的问题或描述..."
                rows="3"
              ></textarea>
              <div class="input-actions">
                <button class="action-btn"><i class="el-icon-paperclip"></i></button>
                <button class="action-btn"><i class="el-icon-lightbulb"></i></button>
              </div>
              <button
                class="send-btn"
                :disabled="!inputMessage.trim() || isLoading"
                @click="sendMessage"
              >
                <i class="el-icon-send"></i>发送
              </button>
            </div>
            <div class="input-footer">
              <label class="checkbox-label">
                <input type="checkbox" checked />
                <span>搜索内部知识库</span>
              </label>
              <span class="input-tip">按 Enter 发送，Shift+Enter 换行</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import Sidebar from '../components/Sidebar.vue'
import TopHeader from '../components/TopHeader.vue'
import { Search, Bell, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 历史对话存储
const HISTORY_KEY = 'catman_chat_histories'

const getChatHistories = () => {
  try {
    const data = localStorage.getItem(HISTORY_KEY)
    return data ? JSON.parse(data) : []
  } catch (e) {
    return []
  }
}

const saveChatHistory = (history) => {
  try {
    const histories = getChatHistories()
    const index = histories.findIndex(h => h.id === history.id)
    if (index >= 0) {
      histories[index] = history
    } else {
      histories.unshift(history)
    }
    if (histories.length > 50) histories.pop()
    localStorage.setItem(HISTORY_KEY, JSON.stringify(histories))
  } catch (e) {
    console.error('保存历史失败:', e)
  }
}

const deleteChatHistory = (id) => {
  try {
    const histories = getChatHistories().filter(h => h.id !== id)
    localStorage.setItem(HISTORY_KEY, JSON.stringify(histories))
  } catch (e) {
    console.error('删除历史失败:', e)
  }
}

// 状态
const messages = ref([])
const thinkingMessages = ref([])
const recommendQuestions = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const isSending = ref(false)
const isStopped = ref(false)
const showReferences = ref(true)
const showIntentCard = ref(false)

// 模式配置
const selectedModes = reactive({
  websearch: false,
  research: false,
  knowledge: false
})

// 当前对话ID
const currentConversationId = ref('conv-' + Date.now())
const currentTitle = ref('新对话')

// Markdown渲染
const renderMarkdown = (text) => {
  if (!text) return ''
  let html = text
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/```(\w*)\n([\s\S]*?)```/g, '<pre><code class="$1">$2</code></pre>')
    .replace(/\n/g, '<br>')
    .replace(/- (.*)/g, '<li>$1</li>')
    .replace(/\d+\. (.*)/g, '<li>$1</li>')
    .replace(/<li>/g, '<ul><li>')
    .replace(/<\/li>(?!<li>)/g, '</li></ul>')
  return html
}

// 获取API端点
const getApiEndpoint = () => {
  if (selectedModes.websearch) {
    return '/processing/catman/conversation/chat/websearch'
  }
  return '/processing/catman/conversation/chat/chat'
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  const container = document.querySelector('.conversation-list')
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  isLoading.value = true
  isSending.value = true
  isStopped.value = false

  messages.value.push({
    type: 'user',
    content: message,
    timestamp: Date.now()
  })

  thinkingMessages.value = [{ content: '正在处理...', expanded: true }]
  recommendQuestions.value = []
  showIntentCard.value = false
  currentAiContent = ''
  currentAiThinking = ''
  currentReferences = []

  inputMessage.value = message
  await scrollToBottom()

  try {
    const apiEndpoint = getApiEndpoint()
    const url = `${apiEndpoint}?message=${encodeURIComponent(message)}&conversationId=${encodeURIComponent(currentConversationId.value)}`

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'text/event-stream'
      }
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      if (isStopped.value) {
        reader.cancel()
        break
      }

      buffer += decoder.decode(value, { stream: true })

      let lines = []
      while (buffer.includes('\n')) {
        const newlineIndex = buffer.indexOf('\n')
        let line = buffer.substring(0, newlineIndex).trim()
        buffer = buffer.substring(newlineIndex + 1)
        if (line) {
          lines.push(line)
        }
      }

      for (const rawLine of lines) {
        let line = rawLine
        if (line.startsWith('data:')) {
          line = line.substring(5).trim()
        }
        if (!line) continue

        try {
          const data = JSON.parse(line)
          handleResponseData(data)
        } catch (e) {
          if (line.includes('{') && !line.includes('}')) {
            buffer = line + '\n' + buffer
          }
        }
      }
    }

    thinkingMessages.value.forEach(m => { m.expanded = false })
    await scrollToBottom()
    saveCurrentConversation()

    isLoading.value = false
    isSending.value = false
    isStopped.value = false
    await nextTick()

  } catch (error) {
    console.error('发送失败:', error)
    ElMessage.error('发送消息失败: ' + error.message)
  } finally {
    isLoading.value = false
    isSending.value = false
    isStopped.value = false
  }
}

// 当前AI消息内容
let currentAiContent = ''
let currentAiThinking = ''
let currentReferences = []

// 处理响应数据
const handleResponseData = (data) => {
  switch (data.type) {
    case 'text':
      let lastMsg = messages.value[messages.value.length - 1]
      if (!lastMsg || lastMsg.type !== 'ai') {
        currentAiContent = ''
        messages.value.push({ type: 'ai', content: '', references: [], thinking: '' })
        lastMsg = messages.value[messages.value.length - 1]
      }
      currentAiContent += data.content
      lastMsg.content = currentAiContent
      break

    case 'thinking':
      currentAiThinking = data.content
      if (thinkingMessages.value.length > 0) {
        thinkingMessages.value[0].content = data.content
      } else {
        thinkingMessages.value = [{ content: data.content, expanded: false }]
      }
      const aiMsg = messages.value[messages.value.length - 1]
      if (aiMsg && aiMsg.type === 'ai') {
        aiMsg.thinking = data.content
      }
      break

    case 'reference':
      try {
        let refs = typeof data.content === 'string' ? JSON.parse(data.content) : data.content
        if (Array.isArray(refs)) {
          currentReferences = [...currentReferences, ...refs]
        } else if (typeof refs === 'object') {
          currentReferences.push(refs)
        }
        const aiMsgRef = messages.value[messages.value.length - 1]
        if (aiMsgRef && aiMsgRef.type === 'ai') {
          aiMsgRef.references = currentReferences
        }
      } catch (e) {
        console.error('解析参考信息失败:', e)
      }
      break

    case 'recommend':
      try {
        let questions = typeof data.content === 'string' ? JSON.parse(data.content) : data.content
        if (Array.isArray(questions)) {
          recommendQuestions.value = questions
        }
        currentAiContent = ''
      } catch (e) {
        console.error('解析推荐问题失败:', e)
      }
      break

    case 'error':
      ElMessage.error(data.content || '发生错误')
      break
  }
  scrollToBottom()
}

// 保存当前对话
const saveCurrentConversation = () => {
  if (messages.value.length === 0) return

  const userMessages = messages.value.filter(m => m.type === 'user')
  const title = userMessages.length > 0
    ? userMessages[0].content.slice(0, 30) + (userMessages[0].content.length > 30 ? '...' : '')
    : '新对话'

  const history = {
    id: currentConversationId.value,
    title,
    messages: messages.value,
    createdAt: messages.value[0]?.timestamp || Date.now(),
    updatedAt: Date.now()
  }

  saveChatHistory(history)
  currentTitle.value = title
}

// 点击推荐问题
const askRecommendation = (question) => {
  inputMessage.value = question
  sendMessage()
}

// 切换思考过程展开状态
const toggleThinking = (index) => {
  if (thinkingMessages.value[index]) {
    thinkingMessages.value[index].expanded = !thinkingMessages.value[index].expanded
  }
}

// 自动调整输入框高度
const autoResize = () => {
  const textarea = document.querySelector('.input-area textarea')
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px'
  }
}

// 处理键盘事件
const handleKeyDown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// 图片加载失败处理
const handleImgError = (e) => {
  e.target.style.display = 'none'
  e.target.parentElement.innerHTML = '<i class="el-icon-robot"></i>'
}

// 初始化
onMounted(() => {})
</script>

<style scoped>
.chat-container {
  width: 100%;
  min-height: 100vh;
  background: #f9fafb;
  display: flex;
}

.chat-main {
  flex: 1;
  margin-left: 256px;
  display: flex;
  flex-direction: column;
}

/* 顶部状态栏 */
.chat-header {
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
  width: 280px;
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

.notification-btn {
  position: relative;
  padding: 8px;
  border: none;
  background: none;
  cursor: pointer;
  color: #6b7280;
  font-size: 18px;
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 16px;
  height: 16px;
  background: #ef4444;
  color: #fff;
  border-radius: 50%;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.knowledge-select {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
}

.knowledge-select:hover {
  background: #f9fafb;
}

/* 主工作区 */
.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 欢迎态 */
.welcome-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
}

.welcome-content h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 12px;
}

.welcome-content p {
  font-size: 16px;
  color: #6b7280;
  margin-bottom: 32px;
}

.quick-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
}

.quick-btn-blue {
  background: #dbeafe;
  color: #1d4ed8;
}

.quick-btn-blue:hover {
  background: #bfdbfe;
}

.quick-btn-green {
  background: #dcfce7;
  color: #16a34a;
}

.quick-btn-green:hover {
  background: #bbf7d0;
}

.quick-btn-yellow {
  background: #fef3c7;
  color: #d97706;
}

.quick-btn-yellow:hover {
  background: #fde68a;
}

.quick-btn-purple {
  background: #ede9fe;
  color: #7c3aed;
}

.quick-btn-purple:hover {
  background: #ddd6fe;
}

/* 对话区域 */
.conversation-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.avatar-user {
  width: 32px;
  height: 32px;
  background: #e5e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 12px;
}

.avatar-ai {
  width: 32px;
  height: 32px;
  background: #dbeafe;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-ai img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-body {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.6;
  font-size: 14px;
}

.message-bubble.user {
  background: #2563eb;
  color: #fff;
  border-radius: 16px 4px 16px 16px;
}

.message-bubble.ai {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 4px 16px 16px 16px;
}

.message-time {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.message-item.user .message-time {
  text-align: right;
}

/* 参考文档 */
.references-box {
  margin-top: 12px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 8px;
  overflow: hidden;
}

.references-box details {
  padding: 12px;
}

.references-box summary {
  font-size: 13px;
  font-weight: 600;
  color: #1d4ed8;
  cursor: pointer;
}

.reference-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reference-item {
  padding: 10px;
  background: #fff;
  border-radius: 6px;
}

.reference-link {
  font-size: 13px;
  color: #2563eb;
  text-decoration: none;
}

.reference-link:hover {
  text-decoration: underline;
}

.reference-snippet {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

/* 意图识别卡片 */
.intent-card {
  margin-top: 24px;
  background: #fffbeb;
  border: 1px solid #fcd34d;
  border-radius: 12px;
  overflow: hidden;
}

.intent-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: rgba(251, 191, 36, 0.1);
}

.intent-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #92400e;
}

.intent-close {
  background: none;
  border: none;
  color: #92400e;
  cursor: pointer;
  padding: 4px;
}

.intent-body {
  padding: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-row .form-group {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-group select,
.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.form-group select:focus,
.form-group input:focus,
.form-group textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-group textarea {
  resize: none;
}

.form-row input {
  width: 100%;
}

.upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-area:hover {
  border-color: #2563eb;
  background: #f9fafb;
}

.upload-area i {
  font-size: 32px;
  color: #9ca3af;
  margin-bottom: 8px;
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

.form-hint {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 16px;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.btn-submit {
  padding: 10px 20px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-submit:hover {
  background: #1d4ed8;
}

.btn-cancel {
  padding: 10px 20px;
  background: #fff;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-cancel:hover {
  background: #f9fafb;
}

/* 思考过程 */
.thinking-block {
  margin: 16px 0;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.thinking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.thinking-header:hover {
  background: rgba(37, 99, 235, 0.05);
}

.thinking-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.thinking-icon {
  width: 20px;
  height: 20px;
  background: #2563eb;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 11px;
}

.thinking-toggle {
  font-size: 10px;
  color: #94a3b8;
  transition: transform 0.2s;
}

.thinking-toggle.expanded {
  transform: rotate(180deg);
}

.thinking-content {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
  padding: 0 16px;
}

.thinking-content.expanded {
  max-height: 300px;
  padding: 16px;
  border-top: 1px solid #e2e8f0;
  font-size: 13px;
  color: #64748b;
  font-family: monospace;
  white-space: pre-wrap;
  background: #fff;
}

/* 推荐问题 */
.recommend-box {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.recommend-box h4 {
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  margin: 0 0 12px 0;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recommend-item {
  padding: 10px 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}

.recommend-item:hover {
  background: #dbeafe;
  border-color: #2563eb;
  color: #1d4ed8;
}

/* 加载状态 */
.loading-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f3f4f6;
  border-radius: 4px 16px 16px 16px;
  color: #6b7280;
  font-size: 14px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #4a9eff;
  border-radius: 50%;
  animation: dotPulse 1.4s infinite ease-in-out;
}

.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotPulse {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 底部输入区 */
.input-section {
  background: #fff;
  border-top: 1px solid #e5e7eb;
}

.input-context {
  padding: 10px 24px;
  background: #f9fafb;
  text-align: center;
}

.input-context span {
  font-size: 12px;
  color: #9ca3af;
}

.input-area {
  padding: 16px 24px;
  max-width: 900px;
  margin: 0 auto;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-wrapper textarea {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  min-height: 60px;
  max-height: 150px;
  font-family: inherit;
  line-height: 1.6;
  outline: none;
  transition: all 0.15s ease;
}

.input-wrapper textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.input-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.15s ease;
}

.action-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.send-btn {
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
  transition: all 0.15s ease;
}

.send-btn:hover:not(:disabled) {
  background: #1d4ed8;
}

.send-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
}

.checkbox-label input {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.input-tip {
  font-size: 12px;
  color: #9ca3af;
}

/* 代码块样式 */
.message-bubble code {
  background: #f3f4f6;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9em;
}

.message-bubble.user code {
  background: rgba(255, 255, 255, 0.2);
}

.message-bubble pre {
  background: #1f2937;
  color: #e5e7eb;
  padding: 12px 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 8px 0;
}

.message-bubble pre code {
  background: transparent;
  padding: 0;
}

.message-bubble ul,
.message-bubble ol {
  margin: 8px 0;
  padding-left: 24px;
}

.message-bubble li {
  margin: 4px 0;
}
</style>
