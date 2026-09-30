<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Chat-bot AI Food Store</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 0;
      }

      .chat-button {
        position: fixed;
        bottom: 20px;
        right: 20px;
        width: 60px;
        height: 60px;
        background-color: #0078ff;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        color: white;
        box-shadow: 0 4px 15px rgba(0, 120, 255, 0.4);
        z-index: 1000;
        transition: transform 0.2s ease;
      }

      .chat-button:hover {
        transform: scale(1.08);
      }

      .chat-icon {
        font-size: 26px;
      }

      .chat-container {
        position: fixed;
        bottom: 20px;
        right: 20px;
        width: 380px;
        height: 550px;
        border-radius: 16px;
        background-color: white;
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.25);
        display: flex;
        flex-direction: column;
        overflow: hidden;
        z-index: 1000;
        display: none;
      }

      .chat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 14px 16px;
        background: linear-gradient(135deg, #0078ff, #0056b3);
        color: white;
      }

      .chat-title {
        display: flex;
        align-items: center;
        font-weight: bold;
        font-size: 16px;
      }

      .chat-title img {
        width: 28px;
        height: 28px;
        border-radius: 50%;
        margin-right: 10px;
        background: white;
        padding: 2px;
      }

      .close-chat {
        cursor: pointer;
        font-size: 24px;
        color: white;
        line-height: 1;
      }

      .chat-box {
        flex: 1;
        overflow-x: hidden;
        overflow-y: auto;
        padding: 15px;
        background-color: #f7f9fa;
      }

      .message {
        margin-bottom: 15px;
        max-width: 82%;
        padding: 10px 14px;
        border-radius: 16px;
        position: relative;
        clear: both;
        line-height: 1.5;
        font-size: 13.5px;
        word-break: break-word;
      }

      .user-message {
        font-weight: 500;
        background-color: #e3f2fd;
        color: #0b3d91;
        border: 1px solid #bbdefb;
        float: right;
        border-bottom-right-radius: 4px;
        white-space: pre-wrap;
      }

      .bot-message {
        background-color: #0078ff;
        color: white;
        float: left;
        border-bottom-left-radius: 4px;
        white-space: pre-wrap;
        box-shadow: 0 2px 6px rgba(0, 120, 255, 0.2);
      }

      .bot-message b, .bot-message strong {
        color: #fff9a6;
      }

      .user-avatar {
        width: 28px;
        height: 28px;
        border-radius: 50%;
        position: absolute;
        right: -36px;
        bottom: 0;
        background-size: cover;
      }

      .bot-avatar {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background-color: #0056b3;
        color: white;
        display: flex;
        justify-content: center;
        align-items: center;
        position: absolute;
        left: -42px;
        bottom: 0;
        font-size: 12px;
        font-weight: bold;
      }

      .answered-by {
        padding-bottom: 12px;
        text-align: center;
        font-size: 11px;
        color: #888;
        margin-top: 4px;
        clear: both;
      }

      .chat-quick-suggestions {
        display: flex;
        gap: 6px;
        padding: 8px 12px;
        overflow-x: auto;
        white-space: nowrap;
        background-color: #ffffff;
        border-top: 1px solid #eee;
        scrollbar-width: thin;
      }

      .chat-quick-suggestions::-webkit-scrollbar {
        height: 4px;
      }

      .chat-quick-suggestions::-webkit-scrollbar-thumb {
        background: #ddd;
        border-radius: 4px;
      }

      .suggestion-chip {
        display: inline-flex;
        align-items: center;
        padding: 5px 11px;
        background: #f0f7ff;
        color: #0066cc;
        border: 1px solid #cce3ff;
        border-radius: 14px;
        font-size: 12px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
        flex-shrink: 0;
      }

      .suggestion-chip:hover {
        background: #0078ff;
        color: #ffffff;
        border-color: #0078ff;
        transform: translateY(-1px);
        box-shadow: 0 2px 6px rgba(0, 120, 255, 0.25);
      }

      .chat-input-container {
        display: flex;
        align-items: center;
        padding: 10px 12px;
        background-color: white;
        border-top: 1px solid #eaeaea;
      }

      .chat-input {
        flex: 1;
        padding: 9px 14px;
        border: 1px solid #ddd;
        border-radius: 20px;
        outline: none;
        font-size: 13.5px;
      }

      .chat-input:focus {
        border-color: #0078ff;
      }

      .chat-send {
        margin-left: 8px;
        padding: 9px 16px;
        background-color: #0078ff;
        color: white;
        border: none;
        border-radius: 20px;
        cursor: pointer;
        font-size: 13.5px;
        font-weight: 500;
        transition: background-color 0.2s;
      }

      .chat-send:hover {
        background-color: #0056b3;
      }

      .chat-collapsed {
        position: fixed;
        bottom: 0;
        right: 0;
        width: 125px;
        height: 100vh;
        background-color: #0078ff;
        color: white;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;
        padding: 15px 0;
        box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
        z-index: 999;
        display: none;
      }

      .chat-collapsed-header {
        width: 100%;
        text-align: center;
        padding: 10px 0;
        font-weight: bold;
        display: flex;
        flex-direction: column;
        align-items: center;
      }

      .chat-collapsed-header span {
        margin-top: 5px;
      }

      .close-collapsed {
        font-size: 20px;
        cursor: pointer;
      }

      @media (max-width: 600px) {
        .chat-container {
          width: 100%;
          height: 100%;
          bottom: 0;
          right: 0;
          border-radius: 0;
        }
      }
    </style>
  </head>
  <body>
    <div class="chat-button" id="chatButton" onclick="openChat()">
      <div class="chat-icon">💬</div>
    </div>

    <div class="chat-container" id="chatContainer">
      <div class="chat-header">
        <div class="chat-title">
          <img
            src="https://img.freepik.com/free-vector/chatbot-chat-message-vectorart_78370-4104.jpg"
            alt="AI Icon"
          />
          Food Store AI Assistant
        </div>
        <div class="close-chat" onclick="toggleChatView('button')">&times;</div>
      </div>

      <div class="chat-box" id="chatBox">
        <!-- Messages -->
      </div>

      <div class="chat-quick-suggestions" id="quickSuggestions">
        <button type="button" class="suggestion-chip" onclick="askSuggestion('Gợi ý thực đơn giảm cân với thực phẩm của quán')">🥗 Thực đơn giảm cân</button>
        <button type="button" class="suggestion-chip" onclick="askSuggestion('Gợi ý món tăng cơ giàu đạm cho gymer')">💪 Món tăng cơ gymer</button>
        <button type="button" class="suggestion-chip" onclick="askSuggestion('Hôm nay ăn món gì ngon bổ?')">🍲 Hôm nay ăn gì?</button>
        <button type="button" class="suggestion-chip" onclick="askSuggestion('Quán có những loại trái cây tươi ngon nào?')">🍎 Trái cây tươi</button>
        <button type="button" class="suggestion-chip" onclick="askSuggestion('Gợi ý thực phẩm nấu đồ ăn dặm cho bé')">👶 Đồ ăn dặm cho bé</button>
        <button type="button" class="suggestion-chip" onclick="askSuggestion('Gợi ý món ăn Eat Clean healthy')">🥑 Eat Clean</button>
      </div>

      <div class="chat-input-container">
        <input
          type="text"
          class="chat-input"
          id="userInput"
          placeholder="Nhập câu hỏi hoặc chọn gợi ý..."
          onkeypress="handleKeyPress(event)"
        />
        <button class="chat-send" onclick="sendMessage()">Gửi</button>
      </div>
    </div>

    <div class="chat-collapsed" id="chatCollapsed">
      <div class="chat-collapsed-header">
        Chat-<br />bot AI
        <span class="close-collapsed" onclick="toggleChatView('none')">×</span>
      </div>

      <div class="chat-input-container" style="width: 90%; padding: 5px">
        <input
          type="text"
          class="chat-input"
          id="collapsedInput"
          placeholder="Nhập câu hỏi..."
          style="padding: 8px; width: 100%"
        />
        <button
          class="chat-send"
          onclick="sendCollapsedMessage()"
          style="margin-top: 5px; width: 100%"
        >
          Gửi
        </button>
      </div>
    </div>

    <script>
      function toggleChatView(view) {
        const chatButton = document.getElementById("chatButton");
        const chatContainer = document.getElementById("chatContainer");
        const chatCollapsed = document.getElementById("chatCollapsed");

        chatButton.style.display = "none";
        chatContainer.style.display = "none";
        chatCollapsed.style.display = "none";

        if (view === "button") {
          chatButton.style.display = "flex";
        } else if (view === "expanded") {
          chatContainer.style.display = "flex";
        } else if (view === "collapsed") {
          chatCollapsed.style.display = "flex";
        }
      }

      function openChat() {
        toggleChatView("expanded");
        if (document.getElementById("chatBox").innerHTML.trim() === "") {
          addBotMessage("👋 **Xin chào bạn!** Tôi là trợ lý AI của **Food Store** 🥗.\n\nTôi có thể giúp bạn:\n• Gợi ý món ăn ngon, bổ dưỡng mỗi ngày\n• Tư vấn thực đơn giảm cân, tăng cơ, Eat Clean\n• Giới thiệu các nguyên liệu tươi sạch đang có sẵn tại cửa hàng kèm giá bán\n\nBạn có thể nhấn vào các **gợi ý nhanh bên dưới** hoặc nhập câu hỏi nhé!");
        }
      }

      function formatBotResponse(text) {
        if (!text) return "";
        let formatted = text
          .replace(/&/g, "&amp;")
          .replace(/</g, "&lt;")
          .replace(/>/g, "&gt;");
        formatted = formatted.replace(/\*\*(.*?)\*\*/g, "<b>$1</b>");
        return formatted;
      }

      function addUserMessage(message) {
        const chatBox = document.getElementById("chatBox");
        const messageDiv = document.createElement("div");
        messageDiv.className = "message user-message";
        messageDiv.textContent = message;

        const avatar = document.createElement("div");
        avatar.className = "user-avatar";
        avatar.style.backgroundImage = "url('https://cdn-icons-png.flaticon.com/512/6596/6596121.png')";
        messageDiv.appendChild(avatar);

        chatBox.appendChild(messageDiv);
        chatBox.scrollTop = chatBox.scrollHeight;
      }

      function addBotMessage(message) {
        const chatBox = document.getElementById("chatBox");
        const messageDiv = document.createElement("div");
        messageDiv.className = "message bot-message";
        messageDiv.innerHTML = formatBotResponse(message);

        const avatar = document.createElement("div");
        avatar.className = "bot-avatar";
        avatar.innerHTML = "AI";
        messageDiv.appendChild(avatar);

        chatBox.appendChild(messageDiv);

        const answeredBy = document.createElement("div");
        answeredBy.className = "answered-by";
        answeredBy.innerHTML = "FoodStore AI Assistant";
        chatBox.appendChild(answeredBy);

        chatBox.scrollTop = chatBox.scrollHeight;
      }

      function askSuggestion(text) {
        document.getElementById("userInput").value = text;
        sendMessage();
      }

      function sendMessage() {
        const userInput = document.getElementById("userInput");
        const message = userInput.value.trim();
        if (message === "") return;

        addUserMessage(message);
        userInput.value = "";

        const typingIndicator = document.createElement("div");
        typingIndicator.className = "message bot-message";
        typingIndicator.id = "typingIndicator";
        typingIndicator.innerHTML = "Đang soạn gợi ý món cho bạn...";
        document.getElementById("chatBox").appendChild(typingIndicator);
        document.getElementById("chatBox").scrollTop = document.getElementById("chatBox").scrollHeight;

        fetchGeminiResponse(message);
      }

      function sendCollapsedMessage() {
        const collapsedInput = document.getElementById("collapsedInput");
        const message = collapsedInput.value.trim();

        if (message === "") return;

        collapsedInput.value = "";
        toggleChatView("expanded");

        document.getElementById("userInput").value = message;
        sendMessage();
      }

      function handleKeyPress(event) {
        if (event.key === "Enter") {
          sendMessage();
        }
      }

      async function fetchGeminiResponse(userText) {
        try {
          const requestBody = {
            contents: [{ parts: [{ text: userText }] }],
          };

          const response = await fetch("/gemini-proxy", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestBody),
          });

          const typingIndicator = document.getElementById("typingIndicator");
          if (typingIndicator) {
            typingIndicator.remove();
          }

          if (!response.ok) throw new Error("Lỗi kết nối API");

          const data = await response.text();
          addBotMessage(data);
        } catch (error) {
          const typingIndicator = document.getElementById("typingIndicator");
          if (typingIndicator) {
            typingIndicator.remove();
          }

          addBotMessage("Lỗi kết nối: " + error.message);
        }
      }

      document.addEventListener("DOMContentLoaded", function () {
        toggleChatView("button");
      });
    </script>
  </body>
</html>
