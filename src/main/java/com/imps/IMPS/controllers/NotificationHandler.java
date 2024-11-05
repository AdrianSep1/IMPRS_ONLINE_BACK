package com.imps.IMPS.controllers;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void sendNotification(String message) {
        TextMessage notification = new TextMessage(message);
        sessions.forEach(session -> {
            try {
                session.sendMessage(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
