// 对话历史记录
let conversationHistory = [];
// 当前选择的模型
let selectedModel = "qwen-plus";

// 更新温度值显示
function updateTempValue(value) {
    document.getElementById("tempValue").textContent = value;
}

// 更新模型选择
function updateModelValue(value) {
    selectedModel = value;
    console.log("选择的模型：", value);
}

// 清除聊天记录
function clearChat() {
    if (confirm('确定要清除所有聊天记录吗？')) {
        const chatbox = document.getElementById("chatbox");
        chatbox.innerHTML = '';
        // 清空对话历史
        conversationHistory = [];
    }
}

// 自动调整文本框高度
function autoResize() {
    const textarea = document.getElementById("input");
    if (!textarea) return;
    
    // 先重置高度，以便正确计算 scrollHeight
    textarea.style.height = 'auto';
    
    // 计算新的高度，限制在 min-height 和 max-height 之间
    const newHeight = Math.max(50, Math.min(textarea.scrollHeight, 150));
    textarea.style.height = newHeight + 'px';
}

// 监听输入事件，自动调整高度
document.addEventListener('DOMContentLoaded', function() {
    const textarea = document.getElementById("input");
    if (textarea) {
        // 页面加载时先调整一次
        autoResize();
        
        textarea.addEventListener('input', autoResize);
        
        // 监听键盘事件
        textarea.addEventListener('keydown', function(e) {
            // Enter 发送，Shift+Enter 换行
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                send();
            }
        });
    }
});

async function send() {
    const msg = document.getElementById("input").value;
    
    // 如果输入为空，不发送
    if (!msg || !msg.trim()) {
        return;
    }
    
    const temp = document.getElementById("temp").value;
    const chatbox = document.getElementById("chatbox");
    const inputField = document.getElementById("input");

    chatbox.innerHTML += `<p class="user-message"><b>你:</b>${msg}</p>`;

    // 添加用户消息到历史记录
    conversationHistory.push({
        role: "user",
        content: msg
    });
    
    // 立即清空输入框并重置高度
    inputField.value = "";
    inputField.style.height = 'auto';

    try {
        const res = await fetch("/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                messages: conversationHistory,
                temperature: parseFloat(temp),
                model: selectedModel
            })
        });

        const data = await res.json();

        // 检查是否有回复内容
        if (data && data.reply) {
            chatbox.innerHTML += `<p class="ai-message"><b>AI:</b>${data.reply}</p>`;
            
            // 添加 AI 回复到历史记录
            conversationHistory.push({
                role: "assistant",
                content: data.reply
            });
            
            // 滚动到底部
            setTimeout(() => {
                chatbox.scrollTop = chatbox.scrollHeight;
            }, 50);
        } else if (data.error) {
            chatbox.innerHTML += `<p class="ai-message" style="color: red;"><b>AI:</b>错误:${data.error}</p>`;
        } else {
            chatbox.innerHTML += `<p class="ai-message" style="color: orange;"><b>AI:</b>未收到回复，请检查 API Key 是否已设置</p>`;
        }
    } catch (error) {
        chatbox.innerHTML += `<p class="ai-message" style="color: red;"><b>AI:</b>网络错误:${error.message}</p>`;
    }
}