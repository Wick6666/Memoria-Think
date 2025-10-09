<template>
  <div class="home-container">
    <div class="header">
      <h1>MemoriaThink</h1>
      <p class="subtitle">选择您的智能应用</p>
    </div>
    
    <div class="apps-container">
      <div class="app-card" @click="navigateTo('/dream-director')">
        <div class="app-icon">🎭</div>
        <h2>幻梦剧场</h2>
        <p>与AI展开梦幻般的对话体验</p>
      </div>
      
      <div class="app-card" @click="navigateTo('/manus')">
        <div class="app-icon">🧠</div>
        <h2>流动智心</h2>
        <p>智能体助手，随时为您服务</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.header {
  text-align: center;
  color: white;
  margin-bottom: 60px;
}

.header h1 {
  font-size: 56px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  font-size: 20px;
  opacity: 0.95;
  font-weight: 300;
}

.apps-container {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 1200px;
}

.app-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  width: 320px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.app-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
}

.app-icon {
  font-size: 64px;
  text-align: center;
  margin-bottom: 20px;
}

.app-card h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 12px;
  text-align: center;
}

.app-card p {
  font-size: 16px;
  color: #666;
  text-align: center;
  line-height: 1.6;
}
</style>

