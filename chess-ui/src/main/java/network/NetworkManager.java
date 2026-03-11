package network;

import UI.UIController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class NetworkManager implements  WebSocket.Listener {

    // messages got from server
    //JOINED
    //START
    //MOVE:e2e4
    //INVALID_MOVE
    //OPPONENT_LEFT

    private WebSocket webSocket;
    private UIController uiController;

    public NetworkManager(UIController Controller){
        this.uiController = Controller;
    }

    public void connect(){
        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:8080/chess"),this)
                .thenAccept(ws-> this.webSocket = ws);
    }


    public void send(String message){
        if(webSocket != null){
            webSocket.sendText(message,true);
        }
    }


    @Override
    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last){
        uiController.handleServerMessage(data.toString());
        return WebSocket.Listener.super.onText(ws,data,last);
    }

}
