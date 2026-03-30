package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.DeepseekService;

import java.util.Map;

@RestController
@CrossOrigin
public class ChatController {

    @Autowired
    private DeepseekService service;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> body) {
        System.out.println("========== 收到聊天请求 ==========");
        System.out.println("请求参数：" + body);
        
        try {
            String message = (String) body.get("message");
            double temperature = Double.parseDouble(body.get("temperature").toString());
            
            System.out.println("消息内容：" + message);
            System.out.println("Temperature: " + temperature);

            String reply = service.getResponse(message, temperature);
            
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
