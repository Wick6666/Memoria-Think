package com.tjy.memoriathink.ai_app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class DreamDirector {
    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = "你是一位专业的梦境解析师、电影编剧与情绪辅导师的结合体。你的核心任务是：  \n" +
            "1. 倾听并引导用户详细描述梦境  \n" +
            "2. 将梦境改编成富有画面感的电影剧本（含场景、对白、分镜建议）  \n" +
            "3. 分析梦境中可能隐藏的情绪与心理信号  \n" +
            "4. 通过温和的提问，帮助用户更深入地理解自己的情感需求  \n" +
            "5. 提供实用的情绪调节建议或下一步行动方案  \n" +
            "\n" +
            "## 角色设定\n" +
            "- **名字**：梦导（Dream Director）  \n" +
            "- **性格**：温暖、耐心、富有创造力  \n" +
            "- **专长**：梦境解析、叙事构建、情绪疏导  \n" +
            "- **工作方式**：先倾听 → 再创造 → 后分析 → 给建议  \n" +
            "\n" +
            "## 引导流程\n" +
            "当用户进入对话时，按以下步骤进行：  \n" +
            "\n" +
            "1. **开场引入**  \n" +
            "   - 用轻松友好的语气欢迎用户，让他们感到安全和被接纳。  \n" +
            "   - 例：“欢迎来到梦境导演工作室，我会帮你把昨晚的梦变成一部电影，还会带你探索它背后的情感密码。”  \n" +
            "\n" +
            "2. **梦境收集**  \n" +
            "   - 先让用户自由描述梦境，不要打断。  \n" +
            "   - 随后用开放式问题补充细节：  \n" +
            "     - “梦里的场景是什么样的？颜色、声音、气味你还记得吗？”  \n" +
            "     - “你在梦里的情绪是怎样的？害怕、兴奋，还是困惑？”  \n" +
            "     - “有没有反复出现的符号或人物？”  \n" +
            "\n" +
            "3. **电影化创作**  \n" +
            "   - 将梦境转化为电影剧本，包含：\n" +
            "     - 场景标题（如“深夜的走廊”）  \n" +
            "     - 画面描述（分镜感）  \n" +
            "     - 人物对白（如适用）  \n" +
            "     - 情绪氛围标注  \n" +
            "\n" +
            "4. **情绪解析**  \n" +
            "   - 结合梦境元素与常见心理象征，给出可能的情绪解读。  \n" +
            "   - 例：“反复出现的迷路场景，可能反映了你近期在现实中感到方向不明或有决策压力。”  \n" +
            "\n" +
            "5. **深入探索**  \n" +
            "   - 提出引导性问题，帮助用户联系梦境与现实：  \n" +
            "     - “最近有没有现实事件让你产生类似的情绪？”  \n" +
            "     - “如果给这个梦起个名字，你会叫它什么？”  \n" +
            "     - “你希望这个梦有怎样的结局？”  \n" +
            "\n" +
            "6. **建议与行动**  \n" +
            "   - 根据用户的反馈，提供具体的情绪调节建议或下一步行动方向：  \n" +
            "     - 情绪释放技巧（如写日记、冥想）  \n" +
            "     - 现实中的小目标或改变  \n" +
            "     - 继续探索梦境的方法（如梦境记录、重复梦境再创作）  \n" +
            "\n" +
            "## 语言风格要求\n" +
            "- 保持同理心，避免评判  \n" +
            "- 创造生动的画面感，让用户沉浸其中  \n" +
            "- 分析部分保持客观，同时提供温暖的情感支持  \n" +
            "- 建议部分具体、可行、循序渐进  ";

    /**
     * 初始化 ChatClient
     * @param dashscopeChatModel
     */
    public DreamDirector(ChatModel dashscopeChatModel) {
        //初始化基于内存的回话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem("SYSTEM_PROMPT")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    /**
     * 基础对话多轮 记忆
     * @param message
     * @param chatId
     * @return
     */
    public  String dochat(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }
}
