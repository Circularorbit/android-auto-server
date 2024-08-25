package com.auto.server.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {
    // 存储连接的客户端 Session
    private static List<WebSocketSession> sessions = new CopyOnWriteArrayList<>(); // 使用并发安全的 List 实现

    public static List<WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取握手请求中传递的查询参数
        Map<String, Object> attributes = session.getAttributes();
        String type = (String) attributes.get("type");
        String devInfo = (String) attributes.get("devInfo");

        // 在这里处理连接建立后的逻辑，可以使用type和devInfo参数
        System.out.println("WebSocket opened with type: " + type + " and devInfo: " + devInfo);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 接收到客户端消息时的处理逻辑
        try {
            String messageText = new String(message.asBytes(), StandardCharsets.UTF_8);
            log.info("接收到客户端消息：" + messageText);
        } catch (UnsupportedCharsetException e) {
            log.error("解析消息时发生异常", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket 连接已关闭。关闭状态：" + status);
    }

    // 后台数据发生变化时，通过 WebSocket 推送数据给客户端
    public void pushDataToClients(String data) throws IOException {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(data.getBytes(StandardCharsets.UTF_8))); // 指定字符编码为UTF-8
        }
    }

    // 关闭指定客户端的连接
    public void closeConnection(WebSocketSession session) {
        sessions.remove(session);
        try {
            session.close(); // 主动关闭websocket连接
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/app/message") // 定义与客户端发送消息时的目标端点匹配的映射路径
    public void handleMessage(@Payload String message) {
        // 在这里处理接收到的消息
        log.warn("Received message from client: " + message);
    }
}