package com.tjy.memoriathink.prompt;

/**
 * 系统提示词配置类
 * 统一管理所有AI智能体的系统提示词，避免硬编码
 * 
 * @author tjy
 * @since 2025-10
 */
public class SystemPromptConfig {
    
    /**
     * 获取梦境解析师提示词
     * 用于梦境分析、情绪解读场景
     * 
     * @return 梦境解析师系统提示词
     */
    public static String getDreamDirectorPrompt() {
        return DreamDirectorPrompt.SYSTEM_PROMPT;
    }
    
    /**
     * 获取光影智感影视助手提示词
     * 用于影视推荐、观影分析、内容生成场景
     * 
     * @return 影视智能助手系统提示词
     */
    public static String getMovieAssistantPrompt() {
        return MovieAssistantPrompt.SYSTEM_PROMPT;
    }
    
    /**
     * 根据场景类型获取对应提示词
     * 
     * @param promptType 提示词类型枚举
     * @return 对应的系统提示词
     */
    public static String getPromptByType(PromptType promptType) {
        return switch (promptType) {
            case DREAM_DIRECTOR -> getDreamDirectorPrompt();
            case MOVIE_ASSISTANT -> getMovieAssistantPrompt();
        };
    }
    
    /**
     * 提示词类型枚举
     */
    public enum PromptType {
        /** 梦境解析师 */
        DREAM_DIRECTOR,
        /** 影视智能助手 */
        MOVIE_ASSISTANT
    }
}

