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

    @Test
    void doChatWithTools() {


        testMessage("帮我分析一下昨晚做的梦，梦里我在一条无限延伸的走廊里走，尽头有一扇光门。");

        testMessage("最近老是做关于考试的梦，我很焦虑，梦境助手能告诉我这可能反映了什么吗？");

        testMessage("给我生成一张符合‘海底星空’意境的梦境壁纸，直接保存为文件。");

        testMessage("保存我的梦境记录档案为文件。");

        testMessage("生成一份‘7天梦境探索计划’PDF，包含睡前冥想引导、梦境记录模板和解梦建议。");
        }

        private void testMessage(String message) {
            String chatId = UUID.randomUUID().toString();
            String answer = dreamDirector.doChatWithTools(message, chatId);
            Assertions.assertNotNull(answer);
        }

}