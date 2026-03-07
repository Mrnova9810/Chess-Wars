package com.chess.chess_server;


import org.springframework.web.socket.WebSocketSession;
import engine.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class Room {
    private final List<WebSocketSession> player = new ArrayList<>();
    public final Map<WebSocketSession, String> playersName = new ConcurrentHashMap<>();

    private  final GameState state;
    private boolean gameActive = false;

    // Null, In case of not decided!
    public WebSocketSession white = null;
    public  WebSocketSession black = null;

    public WebSocketSession playerDiconnected;
    public boolean isPlayerDisconnected;
    public boolean isPlayerReconnected;
    public  ScheduledFuture<?> disconnectTask ;





    public int startRequest = 0;
    public int RematchRequests = 0;

    public Room(){
        state = new GameState();
    }

    public void resetGame(){
       state.reset();
       this.gameActive = true;
    }


    public List<WebSocketSession> getPlayer(){
        return player;
    }

    public GameState getState(){
        return state;
    }

    public boolean isGameActive(){
        return gameActive;
    }
    public void endGame(){
        state.reset();
        this.gameActive = false;
    }


}
