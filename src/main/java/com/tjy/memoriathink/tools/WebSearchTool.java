package com.tjy.memoriathink.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网页搜索工具
 */
public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    // 2025-10-11 修改：优化返回结果，格式化输出，只保留关键信息
    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            JSONObject jsonObject = JSONUtil.parseObj(response);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");

            if (organicResults == null || organicResults.isEmpty()) {
                return "未找到相关搜索结果";
            }

            int limit = Math.min(5, organicResults.size());
            List<Object> objects = organicResults.subList(0, limit);

            // 格式化搜索结果，只保留关键信息
            StringBuilder formattedResult = new StringBuilder();
            formattedResult.append(String.format("🔍 搜索结果：找到 %d 条相关信息\n", limit));
            formattedResult.append("=" .repeat(60)).append("\n\n");

            for (int i = 0; i < objects.size(); i++) {
                JSONObject item = (JSONObject) objects.get(i);
                String title = item.getStr("title", "无标题");
                String link = item.getStr("link", "");
                String snippet = item.getStr("snippet", "");
                
                // 清理摘要：移除高亮标记等噪音
                snippet = cleanSnippet(snippet);
                
                // 限制摘要长度
                if (snippet.length() > 150) {
                    snippet = snippet.substring(0, 150) + "...";
                }
                
                formattedResult.append(String.format("[%d] %s\n", i + 1, title));
                formattedResult.append(String.format("    🔗 %s\n", link));
                if (!snippet.isEmpty()) {
                    formattedResult.append(String.format("    📄 %s\n", snippet));
                }
                formattedResult.append("\n");
            }

            return formattedResult.toString();
        } catch (Exception e) {
            return "搜索出错: " + e.getMessage();
        }
    }

    // 2025-10-11 新增：清理搜索结果摘要中的噪音
    /**
     * 清理搜索结果摘要中的噪音
     */
    private String cleanSnippet(String snippet) {
        if (snippet == null) {
            return "";
        }
        // 移除多余的空白字符
        return snippet.replaceAll("\\s+", " ").trim();
    }
}
