package com.tjy.memoriathink.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.tjy.memoriathink.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;

    // 保存工具调用信息的响应结果（要调用那些工具）
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        // 1、校验提示词，拼接用户提示词
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        // 2、调用 AI 大模型，获取工具调用结果
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, this.chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于等下 Act
            this.toolCallChatResponse = chatResponse;
            // 3、解析工具调用结果，获取要调用的工具
            // 助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            
            // 2025-10-11 修改：优化日志输出格式，使用专业的分层展示
            // 输出提示信息 - 使用专业的格式化风格
            String result = assistantMessage.getText();
            if (StrUtil.isNotBlank(result)) {
                log.info("\n💭 {} 的思考过程:\n{}\n{}", getName(), "-".repeat(50), result);
            }
            
            if (!toolCallList.isEmpty()) {
                log.info("\n🔧 准备调用 {} 个工具:", toolCallList.size());
                for (int i = 0; i < toolCallList.size(); i++) {
                    AssistantMessage.ToolCall toolCall = toolCallList.get(i);
                    log.info("   [{}] 工具: {} | 参数: {}", i + 1, toolCall.name(), formatToolArgs(toolCall.arguments()));
                }
            }
            // 如果不需要调用工具，返回 false
            if (toolCallList.isEmpty()) {
                // 只有不调用工具时，才需要手动记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题：" + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }
    }

    // 2025-10-11 新增：格式化工具参数，避免过长
    /**
     * 格式化工具参数，避免过长
     */
    private String formatToolArgs(String args) {
        if (args == null) {
            return "{}";
        }
        if (args.length() > 100) {
            return args.substring(0, 100) + "...";
        }
        return args;
    }

    // 2025-10-11 新增：格式化工具返回结果的摘要信息，只显示关键内容
    /**
     * 格式化工具返回结果的摘要信息
     */
    private String formatToolResultSummary(String toolName, String result) {
        if (result == null || result.isEmpty()) {
            return "   ⚠️ 工具返回空结果";
        }
        
        // 提取第一行作为摘要（通常包含关键信息）
        String[] lines = result.split("\n", 3);
        if (lines.length > 0 && lines[0].length() > 0) {
            String summary = lines[0];
            if (summary.length() > 80) {
                summary = summary.substring(0, 80) + "...";
            }
            return String.format("   ✅ %s | %s", toolName, summary);
        }
        
        // 降级方案：截取前 80 个字符
        String summary = result.substring(0, Math.min(80, result.length()));
        if (result.length() > 80) {
            summary += "...";
        }
        return String.format("   ✅ %s | %s", toolName, summary);
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具需要调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if (terminateToolCalled) {
            // 任务结束，更改状态
            setState(AgentState.FINISHED);
        }
        
        // 2025-10-11 修改：格式化输出工具执行结果，只显示摘要信息
        // 格式化输出工具执行结果
        log.info("\n📊 工具执行结果:");
        for (ToolResponseMessage.ToolResponse response : toolResponseMessage.getResponses()) {
            log.info(formatToolResultSummary(response.name(), response.responseData()));
        }
        
        // 返回详细结果供 AI 分析（完整数据）
        String results = toolResponseMessage.getResponses().stream()
                .map(ToolResponseMessage.ToolResponse::responseData)
                .collect(Collectors.joining("\n\n"));
        
        return results;
    }
}
