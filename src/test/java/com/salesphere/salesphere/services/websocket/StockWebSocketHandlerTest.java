package com.salesphere.salesphere.services.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockWebSocketHandlerTest {

    private StockWebSocketHandler stockWebSocketHandler;

    @BeforeEach
    public void setUp() {
        stockWebSocketHandler = new StockWebSocketHandler();
    }

    @Test
    @DisplayName("Given a WebSocket session is established, when the session is added, then it should be present in the sessions set")
    public void testAfterConnectionEstablished() throws Exception {
        // Given
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockSession.getId()).thenReturn("session1");

        // When
        stockWebSocketHandler.afterConnectionEstablished(mockSession);

        // Then
        Set<WebSocketSession> sessions = stockWebSocketHandler.getSessions();
        assertTrue(sessions.contains(mockSession));
        verify(mockSession, times(1)).getId();
    }

    @Test
    @DisplayName("Given a WebSocket session is established, when the session is closed, then it should be removed from the sessions set")
    public void testAfterConnectionClosed() throws Exception {
        // Given
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockSession.getId()).thenReturn("session1");

        // When
        stockWebSocketHandler.afterConnectionEstablished(mockSession);
        stockWebSocketHandler.afterConnectionClosed(mockSession, CloseStatus.NORMAL);

        // Then
        Set<WebSocketSession> sessions = stockWebSocketHandler.getSessions();
        assertFalse(sessions.contains(mockSession));

        verify(mockSession, times(2)).getId();
    }

    @Test
    @DisplayName("Given a WebSocket session is open, when sending a message, then the message should be sent to the session")
    public void testSendMessage() throws IOException {
        // Given
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockSession.isOpen()).thenReturn(true);

        // When
        stockWebSocketHandler.sendMessage(mockSession, "Hello");

        // Then
        verify(mockSession, times(1)).sendMessage(new TextMessage("Hello"));
    }

    @Test
    @DisplayName("Given a WebSocket session is closed, when sending a message, then the message should not be sent")
    public void testSendMessageWhenSessionIsClosed() throws IOException {
        // Given
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockSession.isOpen()).thenReturn(false);

        // When
        stockWebSocketHandler.sendMessage(mockSession, "Hello");

        // Then
        verify(mockSession, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    @DisplayName("Given a WebSocket session is open, when sending a message fails, then an IOException should be thrown")
    public void testSendMessageThrowsIOException() throws IOException {
        // Given
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockSession.isOpen()).thenReturn(true);
        doThrow(new IOException("Simulated IO Exception")).when(mockSession).sendMessage(any(TextMessage.class));

        // When
        IOException thrown = assertThrows(IOException.class, () -> {
            stockWebSocketHandler.sendMessage(mockSession, "Hello");
        });

        // Then
        assertEquals("Simulated IO Exception", thrown.getMessage());
    }

    @Test
    @DisplayName("Given multiple WebSocket sessions are established, when retrieving the sessions, then all established sessions should be returned")
    public void testGetSessions() throws Exception {
        // Given
        WebSocketSession mockSession1 = mock(WebSocketSession.class);
        WebSocketSession mockSession2 = mock(WebSocketSession.class);

        // When
        stockWebSocketHandler.afterConnectionEstablished(mockSession1);
        stockWebSocketHandler.afterConnectionEstablished(mockSession2);

        // Then
        Set<WebSocketSession> sessions = stockWebSocketHandler.getSessions();
        assertTrue(sessions.contains(mockSession1));
        assertTrue(sessions.contains(mockSession2));
    }
}