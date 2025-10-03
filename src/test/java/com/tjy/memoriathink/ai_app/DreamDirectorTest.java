package com.tjy.memoriathink.ai_app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
/**
 * 测试类：DreamDirectorTest
 * 使用Spring Boot的测试注解进行单元测试
 */
@SpringBootTest
class DreamDirectorTest {
    // 使用@Resource注解注入DreamDirector实例
    @Resource DreamDirector dreamDirector;
    /**
     * 测试doChat方法
     * 验证聊天功能是否正常工作
     */
    @Test
    void Testchat() {
        // 生成唯一的聊天ID
        String chatId = UUID.randomUUID().toString();

        // 第一条测试消息及断言
        String message = "我是张鑫";
        String answer = dreamDirector.dochat(message, chatId);
        Assertions.assertNotNull(answer);  // 验证回答不为空

        // 第二条测试消息及断言
        message = "我梦到了十年后的自己";
        answer = dreamDirector.dochat(message, chatId);
        Assertions.assertNotNull(answer);  // 验证回答不为空

        // 第三条测试消息及断言
        message = "我叫什么名字，回忆一下";
        answer = dreamDirector.dochat(message, chatId);  // 注意：这里使用了loveApp，可能是笔误
        Assertions.assertNotNull(answer);  // 验证回答不为空
    }


    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "昨晚梦到我在梦里很沮丧";
        String answer =  dreamDirector.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);

    }

    @Test
    void dochatWithReport() {
        String chatId = java.util.UUID.randomUUID().toString();
        String message = "我是张鑫，我喜欢做梦";
        DreamDirector.DreamReport dreamReport = dreamDirector.dochatWithReport(message, chatId);
        Assertions.assertNotNull(dreamReport);
    }
}