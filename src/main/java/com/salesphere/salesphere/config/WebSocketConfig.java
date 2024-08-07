package com.salesphere.salesphere.config;

import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final StockWebSocketHandler stockWebSocketHandler;

    public WebSocketConfig(StockWebSocketHandler stockWebSocketHandler) {
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(stockWebSocketHandler, "/stock-updates").setAllowedOrigins("*");
    }
}