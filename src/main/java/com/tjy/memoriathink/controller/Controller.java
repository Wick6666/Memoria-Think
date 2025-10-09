package com.tjy.memoriathink.controller;

import com.tjy.memoriathink.agent.CustomManus;
import com.tjy.memoriathink.ai_app.DreamDirector;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class Controller {
    @Resource
    private DreamDirector dreamDirector;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 梦境导演应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/dreamDirector/chat/sync")
    public String doChatWithDreamDirectorSync(String message, String chatId) {
        return dreamDirector.dochat(message, chatId);
    }


    /**
     * SSE 流式调用 梦境导演应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/dreamDirector/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithDreamDirectorSSE(String message, String chatId) {
        return dreamDirector.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 梦境导演应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/dreamDirector/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithDreamServerSentEvent(String message, String chatId) {
        return dreamDirector.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 梦境导演应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/dreamDirector/chat/sse_emitter")
    public SseEmitter doChatWithDreamDirectorServerSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        dreamDirector.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
    }
    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */

    @GetMapping("/CustomManus/chat")
    public SseEmitter doChatWithManus(String message) {
        CustomManus customManus = new CustomManus(allTools, dashscopeChatModel);
        return customManus.runStream(message);
    }




}
