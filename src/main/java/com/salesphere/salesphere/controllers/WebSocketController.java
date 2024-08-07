package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    private final StockWebSocketHandler stockWebSocketHandler;

    @Autowired
    public WebSocketController(StockWebSocketHandler stockWebSocketHandler) {
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("WebSocket está ativo");
    }
}