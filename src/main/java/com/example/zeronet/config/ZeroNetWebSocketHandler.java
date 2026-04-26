package com.example.zeronet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
public class ZeroNetWebSocketHandler extends TextWebSocketHandler {

    // Map organizationId to a set of connected sessions
    private final Map<String, Set<WebSocketSession>> orgSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri() != null ? session.getUri().getQuery() : "";
        String orgId = extractQueryParam(query, "orgId");
        
        if (orgId != null && !orgId.isEmpty()) {
            orgSessions.computeIfAbsent(orgId, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("Session {} connected to org {}", session.getId(), orgId);
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: {}", payload);
        
        try {
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            String event = (String) data.get("event");
            
            if ("SUBSCRIBE_ORG".equals(event)) {
                // Handled via orgId in query params mainly, but can dynamically update
            } else if ("ACCEPT_INCIDENT".equals(event)) {
                // Logic to update incident
            } else if ("UPDATE_LOCATION".equals(event)) {
                // Logic to update responder location
            }
        } catch (Exception e) {
            log.error("Failed to process WebSocket message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        orgSessions.values().forEach(sessions -> sessions.remove(session));
        log.info("Session {} closed", session.getId());
    }

    private String extractQueryParam(String query, String param) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equals(param)) {
                return kv[1];
            }
        }
        return null;
    }
}
