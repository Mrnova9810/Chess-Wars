package com.chess.chess_server.config;

import com.chess.chess_server.websocket.WSHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;



@Configuration
@EnableWebSocket

public class WSApplication implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
               registry.addHandler(new WSHandler(),"/chess").setAllowedOrigins("*");


    }
}
