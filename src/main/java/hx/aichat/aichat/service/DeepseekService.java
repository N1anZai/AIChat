package service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class DeepseekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    public String getResponse(String message, double temperature) {
        HttpURLConnection conn = null;
        try {
            // 检查 API Key 是否为空
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return "Error: API Key 未设置，请检查环境变量 DEEPSEEK_API_KEY";
            }
            
            System.out.println("API URL: " + apiUrl);
            System.out.println("API Key 前缀：" + (apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "太短"));
            System.out.println("请求消息：" + message);
            
            URL url = new URL(apiUrl);
    
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
    
            String jsonInput = String.format("""
            {
              "model": "deepseek-chat",
              "messages": [{"role": "user", "content": "%s"}],
              "temperature": %f
            }
            """, message, temperature);
            
            System.out.println("发送的请求 JSON: " + jsonInput);
    
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes());
            }
    
            // 检查 HTTP 响应码
            int statusCode = conn.getResponseCode();
            System.out.println("HTTP 状态码：" + statusCode);
                
            if (statusCode != 200) {
                // 读取错误响应体
                BufferedReader errorBr = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream())
                );
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorBr.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.out.println("错误响应：" + errorResponse.toString());
                return "Error: HTTP " + statusCode + " - " + errorResponse.toString();
            }
    
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
    
            StringBuilder response = new StringBuilder();
            String line;
    
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
    
            String result = response.toString();
            System.out.println("完整 API 响应：" + result);
    
            // 尝试解析 JSON 响应，查找 content 字段的值
            String searchPattern = "content\":\"";
            int start = result.indexOf(searchPattern) + searchPattern.length();
            int end = result.indexOf("\"", start);
    
            if (start < searchPattern.length() || end <= start) {
                System.out.println("解析失败，start=" + start + ", end=" + end);
                return "Error: 无法解析 API 响应";
            }
    
            String reply = result.substring(start, end);
            System.out.println("提取的回复：" + reply);
            return reply;

        } catch (Exception e) {
            System.out.println("异常信息：" + e.getMessage());
            e.printStackTrace();
            // 返回详细的错误信息
            return "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}