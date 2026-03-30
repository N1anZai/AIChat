// 更新温度值显示
function updateTempValue(value) {
    document.getElementById("tempValue").textContent = value;
}

// 清除聊天记录
function clearChat() {
    if (confirm('确定要清除所有聊天记录吗？')) {
        const chatbox = document.getElementById("chatbox");
        chatbox.innerHTML = '';
    }
}

async function send() {
    const msg = document.getElementById("input").value;
    const temp = document.getElementById("temp").value;

    const chatbox = document.getElementById("chatbox");

    chatbox.innerHTML += `<p><b>你：</b>${msg}</p>`;

    try {
        const res = await fetch("/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                message: msg,
                temperature: parseFloat(temp)
            })
        });

        const data = await res.json();

        // 检查是否有回复内容
        if (data && data.reply) {
            chatbox.innerHTML += `<p><b>AI：</b>${data.reply}</p>`;
        } else if (data.error) {
            chatbox.innerHTML += `<p style="color: red;"><b>AI：</b>错误：${data.error}</p>`;
        } else {
            chatbox.innerHTML += `<p style="color: orange;"><b>AI：</b>未收到回复，请检查 API Key 是否已设置</p>`;
        }
        
        // 清空输入框
        document.getElementById("input").value = "";
    } catch (error) {
        chatbox.innerHTML += `<p style="color: red;"><b>AI：</b>网络错误：${error.message}</p>`;
        // 清空输入框
        document.getElementById("input").value = "";
    }
}