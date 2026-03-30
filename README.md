# AI 聊天助手 🤖

基于阿里云百炼平台（通义千问）的智能聊天应用。

## 👨‍💻 项目信息

- **作者**: 张浩新
- **学号**: 202535720128
- **技术栈**: Java Spring Boot + 阿里云 Qwen-plus API

---

## ✨ 功能特性

- 🎨 现代化 UI 界面，渐变色彩设计
- 🌡️ 可调节的 Temperature 参数，控制 AI 创造性
- 💬 实时对话式交互
- 🔒 API Key 安全配置（环境变量）
- ⚡ 快速响应，流畅体验

---

## 📖 Temperature 参数说明

**Temperature（温度）** 是控制 AI 回复创造性的重要参数：

| 值范围 | 效果 | 适用场景 |
|--------|------|----------|
| **0.0 - 0.3** | 非常严谨、确定性强 | 数学计算、事实验答、代码生成 |
| **0.4 - 0.6** | 平衡、适中 | 日常对话、一般问答 |
| **0.7 - 0.9** | 富有创造力、多样化 | 创意写作、头脑风暴、故事创作 |
| **1.0** | 最具创造性、随机性最大 | 诗歌创作、艺术构思 |

**推荐值**: `0.7`（适合大多数场景）

### 示例对比

- **Temperature = 0.2**  
  "北京是中国的首都。"（事实性回答，简洁准确）

- **Temperature = 0.9**  
  "北京，这座古老而现代的城市，作为中国的首都，承载着三千多年的历史底蕴..."（更丰富、更有文采）

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- 阿里云百炼 API Key（[获取地址](https://dashscope.console.aliyun.com/apiKey)）

### 安装步骤

#### 1. 克隆/下载项目

```bash
cd "D:\java project\AIChat"
```

#### 2. 配置 API Key

##### Windows (PowerShell)
```powershell
$env:ALIYUN_API_KEY="sk-your-api-key-here"
```

##### Windows (CMD)
```cmd
set ALIYUN_API_KEY=sk-your-api-key-here
```

##### Linux/Mac
```bash
export ALIYUN_API_KEY="sk-your-api-key-here"
```

> 💡 **提示**: 可以将环境变量添加到系统设置中永久生效

#### 3. 编译项目

```bash
mvn clean install
```

#### 4. 启动应用

##### Windows PowerShell
```powershell
$env:ALIYUN_API_KEY="sk-your-api-key-here"; mvn spring-boot:run
```

##### Windows CMD
```cmd
set ALIYUN_API_KEY=sk-your-api-key-here && mvn spring-boot:run
```

##### Linux/Mac
```bash
export ALIYUN_API_KEY="sk-your-api-key-here" && mvn spring-boot:run
```

#### 5. 访问应用

打开浏览器访问：http://localhost:8080

---

## 📁 项目结构

```
AIChat/
├── src/main/
│   ├── java/hx/aichat/aichat/
│   │   ├── AiChatApplication.java      # 主启动类
│   │   ├── controller/
│   │   │   └── ChatController.java     # 聊天接口控制器
│   │   └── service/
│   │       └── AliyunService.java      # 阿里云 API 服务
│   └── resources/static/
│       ├── index.html                  # 前端页面
│       ├── style.css                   # 样式文件
│       └── app.js                      # 前端脚本
├── pom.xml                             # Maven 配置
└── .env.example                        # 环境变量示例
```

---

## 🔧 核心代码说明

### 1. 后端 API 调用 (`AliyunService.java`)

```java
// 请求阿里云 API
String jsonInput = String.format("""
{
  "model": "qwen-plus",
  "messages": [
    {
      "role": "user",
      "content": "%s"
    }
  ],
  "temperature": %f
}
""", message, temperature);
```

### 2. 前端交互 (`app.js`)

```javascript
// 发送消息到后端
const res = await fetch("/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
        message: msg,
        temperature: parseFloat(temp)
    })
});
```

---

## 🎨 界面预览

访问 http://localhost:8080 即可看到：

- 渐变色背景（紫色系）
- 白色卡片式聊天窗口
- 实时显示的 Temperature 滑块
- 学生信息展示区

---

## ❓ 常见问题

### Q1: 提示 "API Key 未设置"？
**A**: 请检查环境变量 `ALIYUN_API_KEY` 是否正确设置

### Q2: 遇到 HTTP 400 错误？
**A**: 检查 API Key 是否有效，或网络连接是否正常

### Q3: Temperature 设置多少合适？
**A**: 日常对话推荐 0.7，需要严谨回答时调低到 0.3，创意写作可调高到 0.9

### Q4: 如何修改默认端口 8080？
**A**: 在 `application.properties` 中添加 `server.port=8081`

---

## 📝 更新日志

### v1.0.0
- ✅ 初始版本发布
- ✅ 集成阿里云百炼平台
- ✅ 实现基础聊天功能
- ✅ 添加 Temperature 调节功能
- ✅ 优化 UI 界面

---

## 📄 许可证

本项目仅供学习交流使用。

---

## 🙏 致谢

- 阿里云百炼平台：https://dashscope.console.aliyun.com/
- 通义千问大模型
- Spring Boot 框架

---

**如有问题，欢迎联系！** 📧
