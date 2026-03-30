package hx.aichat.aichat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import hx.aichat.aichat.service.AliyunService;

import java.util.Map;

@RestController
@CrossOrigin
public class ChatController {

    @Autowired
    private AliyunService service;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> body) {
        System.out.println("========== 收到聊天请求 ==========");
        System.out.println("请求参数：" + body);
        
        try {
            // 支持两种格式：messages(带上下文) 或 message(单条消息)
            Object messagesObj = body.get("messages");
            String singleMessage = (String) body.get("message");
            double temperature = Double.parseDouble(body.get("temperature").toString());
            // 获取模型参数，默认为 qwen-plus
            String model = body.containsKey("model") ? (String) body.get("model") : "qwen-plus";
            
            System.out.println("Temperature: " + temperature);
            System.out.println("选择的模型：" + model);

            String reply;
            if (messagesObj != null) {
                // 使用对话历史
                System.out.println("使用对话历史模式");
                reply = service.getResponseWithConversation((java.util.List<?>) messagesObj, temperature, model);
            } else if (singleMessage != null) {
                // 兼容旧的单条消息模式
                System.out.println("消息内容：" + singleMessage);
                reply = service.getResponse(singleMessage, temperature, model);
            } else {
                return Map.of("error", "缺少消息内容");
            }
            
            System.out.println("服务返回：" + reply);

            // 检查是否返回了错误
            if (reply.startsWith("Error:")) {
                System.out.println("检测到错误，返回 error 字段");
                return Map.of("error", reply.substring(6).trim());
            }

            System.out.println("返回正常回复");
            return Map.of("reply", reply);
        } catch (Exception e) {
            System.out.println("控制器异常：" + e.getMessage());
            e.printStackTrace();
            return Map.of("error", "请求处理失败：" + e.getMessage());
        }
    }
}
