<template>
  <div class="chat-container">
    <div class="chat-header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h1>🎭 幻梦剧场</h1>
      <div class="chat-id">会话ID: {{ chatId }}</div>
    </div>
    
    <div class="messages-container" ref="messagesContainer">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']"
      >
        <div class="message-avatar">
          {{ msg.role === 'user' ? '👤' : '🎭' }}
        </div>
        <div class="message-content">
          <div class="message-text">{{ msg.content }}</div>
        </div>
      </div>
      
      <div v-if="isLoading" class="message ai-message">
        <div class="message-avatar">🎭</div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-container">
      <textarea
        v-model="inputMessage"
        @keydown.enter.prevent="handleEnter"
        placeholder="输入您的消息..."
        rows="1"
        ref="textarea"
      ></textarea>
      <button @click="sendMessage" :disabled="!inputMessage.trim() || isLoading">
        发送
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const messagesContainer = ref(null)
const textarea = ref(null)
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatId = ref('')

// 生成聊天ID
const generateChatId = () => {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  const userMessage = inputMessage.value.trim()
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage
  })
  
  inputMessage.value = ''
  scrollToBottom()
  
  isLoading.value = true
  
  try {
    // 创建AI消息占位
    const aiMessageIndex = messages.value.length
    messages.value.push({
      role: 'assistant',
      content: ''
    })
    
    // 使用EventSource接收SSE流
    const eventSource = new EventSource(
      `/api/ai/dreamDirector/chat/sse?message=${encodeURIComponent(userMessage)}&chatId=${chatId.value}`
    )
    
    eventSource.onmessage = (event) => {
      const data = event.data
      if (data && data !== '[DONE]') {
        messages.value[aiMessageIndex].content += data
        scrollToBottom()
      }
    }
    
    eventSource.onerror = (error) => {
      console.error('SSE错误:', error)
      eventSource.close()
      isLoading.value = false
      if (!messages.value[aiMessageIndex].content) {
        messages.value[aiMessageIndex].content = '抱歉，发生了错误，请重试。'
      }
    }
    
    eventSource.addEventListener('end', () => {
      eventSource.close()
      isLoading.value = false
    })
    
  } catch (error) {
    console.error('发送消息失败:', error)
    isLoading.value = false
    messages.value.push({
      role: 'assistant',
      content: '抱歉，发生了错误，请重试。'
    })
    scrollToBottom()
  }
}

// 处理回车键
const handleEnter = (e) => {
  if (e.shiftKey) {
    return
  }
  sendMessage()
}

// 返回主页
const goBack = () => {
  router.push('/')
}

onMounted(() => {
  chatId.value = generateChatId()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-header h1 {
  font-size: 24px;
  margin: 0;
}

.chat-id {
  font-size: 12px;
  opacity: 0.8;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 70%;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.ai-message {
  align-self: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.message-content {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-text {
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.input-container {
  padding: 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 12px;
}

.input-container textarea {
  flex: 1;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  resize: none;
  font-family: inherit;
  max-height: 120px;
  min-height: 44px;
}

.input-container textarea:focus {
  outline: none;
  border-color: #667eea;
}

.input-container button {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
}

.input-container button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.input-container button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

