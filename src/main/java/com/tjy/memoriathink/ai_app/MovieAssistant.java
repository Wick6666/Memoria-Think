package com.tjy.memoriathink.ai_app;

import com.tjy.memoriathink.advisor.MyLoggerAdvisor;
import com.tjy.memoriathink.chatmemory.FileBasedChatMemory;
import com.tjy.memoriathink.prompt.SystemPromptConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 光影智感影视智能助手
 * 融合情绪分析、个性化推荐与AI内容生成
 * 基于 Spring AI + RAG + ReAct + Tool Calling
 * 
 * @author tjy
 * @since 2025-10
 */
@Component
@Slf4j
public class MovieAssistant {
    
    @Resource
    private VectorStore pgVectorVectorStore;
    
    @Resource
    private ToolCallback[] allTools;
    
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    
    private final ChatClient chatClient;
    
    /**
     * 使用光影智感影视助手提示词
     */
    private static final String SYSTEM_PROMPT = SystemPromptConfig.getMovieAssistantPrompt();
    
    /**
     * 初始化 ChatClient
     * @param dashscopeChatModel AI 聊天模型
     */
    public MovieAssistant(ChatModel dashscopeChatModel) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
                )
                .build();
    }
    
    /**
     * 基础对话（支持多轮对话记忆）
     * @param message 用户消息
     * @param chatId 对话ID
     * @return AI回复
     */
    public String chat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("MovieAssistant response: {}", content);
        return content;
    }
    
    /**
     * 流式对话（支持 SSE 实时输出）
     * @param message 用户消息
     * @param chatId 对话ID
     * @return 流式响应
     */
    public Flux<String> chatStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }
    
    /**
     * RAG 增强检索对话（基于向量数据库）
     * @param message 用户消息
     * @param chatId 对话ID
     * @return AI回复
     */
    public String chatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("MovieAssistant RAG response: {}", content);
        return content;
    }
    
    /**
     * Tool Calling 对话（支持工具调用）
     * 可调用文件读写、网页抓取、图片搜索、PDF生成等工具
     * 
     * @param message 用户消息
     * @param chatId 对话ID
     * @return AI回复
     */
    public String chatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("MovieAssistant with tools response: {}", content);
        return content;
    }
    
    /**
     * MCP 服务调用（集成外部 MCP 服务）
     * 如图片搜索、高德地图等
     * 
     * @param message 用户消息
     * @param chatId 对话ID
     * @return AI回复
     */
    public String chatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("MovieAssistant with MCP response: {}", content);
        return content;
    }
    
    /**
     * 完整能力调用（RAG + Tools + MCP）
     * 融合所有能力，提供最强大的智能体体验
     * 
     * @param message 用户消息
     * @param chatId 对话ID
     * @return AI回复
     */
    public String chatWithFullCapability(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .advisors(new org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor(pgVectorVectorStore))
                .tools(allTools)
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("MovieAssistant full capability response: {}", content);
        return content;
    }
    
    /**
     * 流式完整能力调用（支持 SSE 实时输出）
     * @param message 用户消息
     * @param chatId 对话ID
     * @return 流式响应
     */
    public Flux<String> chatWithFullCapabilityStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .advisors(new org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor(pgVectorVectorStore))
                .tools(allTools)
                .tools(toolCallbackProvider)
                .stream()
                .content();
    }
}

