package com.tjy.memoriathink.tools;

import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 网页抓取工具
 */
public class WebScrapingTool {

    // 2025-10-11 修改：智能提取网页内容，格式化输出，限制长度
    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 智能提取网页核心内容
            String content = extractMainContent(document);
            
            // 格式化输出
            StringBuilder result = new StringBuilder();
            result.append("📄 网页内容提取成功\n");
            result.append("=" .repeat(60)).append("\n");
            result.append("🔗 URL: ").append(url).append("\n");
            result.append("📌 标题: ").append(document.title()).append("\n\n");
            
            // 限制内容长度
            int maxLength = 2500;
            if (content.length() > maxLength) {
                content = content.substring(0, maxLength);
                int lastPeriod = content.lastIndexOf("。");
                if (lastPeriod > maxLength - 200) {
                    content = content.substring(0, lastPeriod + 1);
                }
                result.append("📝 内容摘要（已智能截取）:\n");
            } else {
                result.append("📝 完整内容:\n");
            }
            
            result.append("-".repeat(60)).append("\n");
            result.append(content);
            if (content.length() >= maxLength) {
                result.append("\n\n... (内容较长，已截取核心部分)");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "⚠️ 网页抓取失败: " + e.getMessage();
        }
    }

    // 2025-10-11 新增：智能提取网页主要内容，过滤掉导航、广告等噪音
    /**
     * 智能提取网页主要内容，过滤掉导航、广告等噪音
     */
    private String extractMainContent(Document document) {
        // 移除脚本、样式等非内容元素
        document.select("script, style, nav, header, footer, aside, .ad, .advertisement").remove();
        
        // 尝试查找主要内容区域（常见的内容容器）
        Elements mainContent = document.select("article, main, .content, .post, .article, #content, #main");
        
        String text;
        if (!mainContent.isEmpty()) {
            // 找到主要内容区域
            text = mainContent.first().text();
        } else {
            // 降级方案：提取 body 内容
            Element body = document.body();
            if (body != null) {
                text = body.text();
            } else {
                text = document.text();
            }
        }
        
        // 清理文本：移除多余空白，规范化
        text = text.replaceAll("\\s+", " ").trim();
        
        return text;
    }
}
