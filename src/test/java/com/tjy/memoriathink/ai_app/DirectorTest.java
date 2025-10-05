package com.tjy.memoriathink.ai_app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DirectorTest {
    @Resource DreamDirector dreamDirector;

    @Test
    void dochatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String ms = "我是张鑫，我梦到我吃了一个饺子";
        DreamDirector.DreamReport dreamReport = dreamDirector.dochatWithReport(ms, chatId);
        Assertions.assertNotNull(dreamReport);

    }
}