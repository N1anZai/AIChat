package hx.aichat.aichat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class AliyunService {

    @Value("${aliyun.api.key}")
    private String apiKey;

    @Value("${aliyun.api.url}")
    private String apiUrl;

    /**
     * JSON 字符串转义方法
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    public String getResponse(String message, double temperature) {
        // 兼容旧方法，构造单条消息的列表
        java.util.List<java.util.Map<String, Object>> messages = new java.util.ArrayList<>();
        messages.add(java.util.Map.of("role", "user", "content", message));
        return getResponseWithConversation(messages, temperature, "qwen-plus");
    }
    
    public String getResponse(String message, double temperature, String model) {
        // 兼容旧方法，构造单条消息的列表
        java.util.List<java.util.Map<String, Object>> messages = new java.util.ArrayList<>();
        messages.add(java.util.Map.of("role", "user", "content", message));
        return getResponseWithConversation(messages, temperature, model);
    }

    @SuppressWarnings("unchecked")
    public String getResponseWithConversation(java.util.List<?> messages, double temperature) {
        return getResponseWithConversation(messages, temperature, "qwen-plus");
    }
    
    @SuppressWarnings("unchecked")
    public String getResponseWithConversation(java.util.List<?> messages, double temperature, String model) {
        HttpURLConnection conn = null;
        try {
            // 检查 API Key 是否为空
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return "Error: API Key 未设置，请检查环境变量 ALIYUN_API_KEY";
            }
            
            // 构建包含对话历史的消息数组
            StringBuilder messagesJson = new StringBuilder();
            messagesJson.append("[");
            for (int i = 0; i < messages.size(); i++) {
                if (i > 0) messagesJson.append(",");
                java.util.Map<String, Object> msg = (java.util.Map<String, Object>) messages.get(i);
                String role = (String) msg.get("role");
                String content = (String) msg.get("content");
                messagesJson.append(String.format(
                    "{\"role\":\"%s\",\"content\":\"%s\"}", 
                    role, 
                    escapeJson(content)
                ));
            }
            messagesJson.append("]");
            
            // 使用标准 OpenAI 兼容格式 (支持对话历史)
            String jsonInput = String.format("""
            {
              "model": "%s",
              "messages": %s,
              "temperature": %f
            }
            """, model, messagesJson.toString(), temperature);
                
            URL url = new URL(apiUrl);
                
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(60000);
    
            try (OutputStream os = conn.getOutputStream()) {
                byte[] inputBytes = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes);
                os.flush();
            }
    
            // 检查 HTTP 响应码
            int statusCode = conn.getResponseCode();
                
            if (statusCode != 200) {
                // 读取错误响应体
                BufferedReader errorBr = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)
                );
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorBr.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                return "Error: HTTP " + statusCode + " - " + errorResponse.toString();
            }
    
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
    
            StringBuilder response = new StringBuilder();
            String line;
    
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
    
            String result = response.toString();
            
            // 解析标准 OpenAI 兼容格式响应
            // 返回格式：{"choices":[{"message":{"content":"回复内容"}}]}
            String searchPattern = "\"content\":\"";
            int start = result.indexOf(searchPattern);
            if (start == -1) {
                return "Error: 无法解析 API 响应";
            }
            start += searchPattern.length();
            
            // 处理转义字符，找到结束引号
            int end = start;
            while (end < result.length()) {
                char c = result.charAt(end);
                if (c == '\\' && end + 1 < result.length()) {
                    end += 2; // 跳过转义字符
                    continue;
                }
                if (c == '"') {
                    break;
                }
                end++;
            }
    
            if (end <= start) {
                return "Error: 无法解析 API 响应";
            }
    
            String reply = result.substring(start, end);
            // 处理转义字符
            reply = reply.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
            return reply;

        } catch (Exception e) {
            // 返回详细的错误信息
            return "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
