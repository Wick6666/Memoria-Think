# MemoriaThink Frontend

基于 Vue3 的智能对话应用前端项目

## 功能特性

- 🏠 **主页**: 应用选择界面
- 🎭 **幻梦剧场**: AI 对话应用，支持实时流式响应
- 🧠 **流动智心**: 智能体助手应用，支持实时流式响应

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vue Router** - 官方路由管理器
- **Axios** - HTTP 客户端
- **Vite** - 下一代前端构建工具

## 项目结构

```
MemoriaThink-frontend/
├── src/
│   ├── views/           # 页面组件
│   │   ├── Home.vue     # 主页
│   │   ├── DreamDirector.vue  # 幻梦剧场
│   │   └── Manus.vue    # 流动智心
│   ├── router/          # 路由配置
│   ├── utils/           # 工具函数
│   ├── App.vue          # 根组件
│   ├── main.js          # 入口文件
│   └── style.css        # 全局样式
├── index.html
├── vite.config.js       # Vite 配置
└── package.json
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

项目将在 `http://localhost:3000` 启动

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 后端接口配置

后端接口地址: `http://localhost:8123/api`

已在 `vite.config.js` 中配置了代理，开发环境下会自动转发 `/api` 请求到后端服务器。

### API 端点

1. **幻梦剧场**
   - 接口: `GET /api/ai/dreamDirector/chat/sse`
   - 参数: `message`, `chatId`
   - 类型: Server-Sent Events (SSE)

2. **流动智心**
   - 接口: `GET /api/ai/CustomManus/chat`
   - 参数: `message`
   - 类型: Server-Sent Events (SSE)

## 功能说明

### 主页
- 展示两个智能应用的入口卡片
- 点击卡片即可进入对应的聊天界面

### 幻梦剧场
- 自动生成唯一的会话 ID
- 支持实时流式对话
- 用户消息显示在右侧，AI 消息显示在左侧
- 支持 Shift+Enter 换行，Enter 发送

### 流动智心
- 智能体助手功能
- 实时流式响应
- 与幻梦剧场类似的聊天界面，但使用不同的配色方案

## 开发说明

- 项目使用 Vue 3 的 Composition API
- 所有组件使用 `<script setup>` 语法
- 样式采用 Scoped CSS，每个组件独立样式
- SSE 通信使用原生 EventSource API

## 注意事项

1. 确保后端服务运行在 `http://localhost:8123`
2. 如需修改后端地址，请编辑 `vite.config.js` 中的 proxy 配置
3. 生产环境部署时，需要配置 Nginx 或其他反向代理来转发 API 请求

## License

MIT

