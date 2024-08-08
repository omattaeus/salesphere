package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get WebSocket status", description = "Returns the status of the WebSocket",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "text/plain")),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal Error", responseCode = "500", content = @io.swagger.v3.oas.annotations.media.Content)
            }
    )
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("WebSocket está ativo");
    }
}