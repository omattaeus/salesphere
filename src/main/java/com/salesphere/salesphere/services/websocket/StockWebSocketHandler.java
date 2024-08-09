package com.salesphere.salesphere.services.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class StockWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(StockWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("Sessão WebSocket estabelecida: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Sessão WebSocket fechada: {}. Status: {}", session.getId(), status);
    }

    public void sendMessage(WebSocketSession session, String message) throws IOException {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            logger.error("Erro ao enviar mensagem via WebSocket: {}", e.getMessage());
            throw e;
        }
    }

    public void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                logger.error("Erro ao enviar mensagem via WebSocket para a sessão {}: {}", session.getId(), e.getMessage());
            }
        }
    }

    public Set<WebSocketSession> getSessions() {
        return new HashSet<>(sessions);
    }
}