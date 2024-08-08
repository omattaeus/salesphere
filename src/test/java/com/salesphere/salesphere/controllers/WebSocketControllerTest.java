package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(WebSocketController.class)
public class WebSocketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockWebSocketHandler stockWebSocketHandler;

    @Test
    @DisplayName("Should return status 200 and message 'WebSocket is active' when accessing the /websocket/status endpoint")
    public void shouldReturnStatusAndMessageWhenStatusIsAccessed() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/websocket/status"))
                // Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("WebSocket está ativo"));
    }
}