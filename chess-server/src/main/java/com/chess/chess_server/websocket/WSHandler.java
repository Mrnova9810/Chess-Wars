package com.chess.chess_server.websocket;

import com.chess.chess_server.Room;
import engine.GameState;
import engine.Move;
import engine.Piece;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class WSHandler extends TextWebSocketHandler {

    //list of  room and there ws_session/ browser --> (together Max 2)
    private static final Map<String, Room> rooms  =  new ConcurrentHashMap<>();


    // each session belongs to which room or not  that's store here
     private static final Map <WebSocketSession,String> S_room = new ConcurrentHashMap<>();
     static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    // check for connections
    //--> String gives browser web socket server


@Override
    public void afterConnectionEstablished( WebSocketSession session ) throws Exception{
    System.out.println("Connected:" + session.getId());
    session.sendMessage(new TextMessage("CONNECTED:"+ session.getId()));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws  Exception{
             String msg = message.getPayload();
             System.out.println("clint: -->" + msg);

             // JOIN room
             if(msg.startsWith("JOIN:")){
                 String roomId = msg.substring(5);
                 joinRoom(session,roomId);
                 return;
             }

             //  if session is not in any room
             if(!S_room.containsKey(session)){
                  session.sendMessage(new TextMessage("Error: Not_In_Room"));
                  return;
            }





             // String passing here!
            relayMessage(session,message);








    }

    public synchronized void joinRoom(WebSocketSession session, String roomId) throws Exception {

        // already in a room
        if (S_room.containsKey(session)) {
            session.sendMessage(new TextMessage("ALREADY_IN_ROOM"));
            return;
        }

        // create room if not exists
        rooms.computeIfAbsent(roomId, k -> new Room());

        Room room =  rooms.get(roomId);
        List<WebSocketSession> players = room.getPlayer();


        // In case of reconnection
        if(room.isPlayerDisconnected && room.isGameActive() && room.playerDisconnected != null){
            room.isPlayerReconnected =true;
            room.disconnectTask.cancel(false);    //false if not running  --> then only task will stop
            // true if running     --> then  if task is already started then interrupt it and stop it
            // half updates are possible so --> miss Behaviour
            // notify opponent that player reconnected

            System.out.println( "White:" + room.white);
            System.out.println( "Black:" + room.black);
            System.out.println("playerDisconnected:" + room.playerDisconnected);



           if(room.playerDisconnected == room.white){
               if(room.black != null && room.black.isOpen()){
                   room.black.sendMessage(new TextMessage("OPPONENT_RECONNECTED"));
               }
           }else{
               if(room.white !=null && room.white.isOpen()){
                   room.white.sendMessage(new TextMessage("OPPONENT_RECONNECTED"));
               }
           }




            session.sendMessage(new TextMessage("JOINED_BACK"));

            // send game state FEN
            // turn                     --> done
            // player color             --> done
            // opponent name            --> done
            // remove old session with new session id  --> done
            String OpponentName = (room.playerDisconnected == room.white) ? room.playersName.get(room.black) : room.playersName.get(room.white);
            String playerColor = (room.playerDisconnected == room.white) ? "WHITE" : "BLACK";
            if(playerColor.equals("WHITE") ){
                room.white = session;
            } else {
                room.black = session;
            }
            Piece.Color turn = room.getState().turn;
            players.remove(room.playerDisconnected);
            players.add(session);
            S_room.remove(room.playerDisconnected);
            S_room.put(session,roomId);


            session.sendMessage(new TextMessage("BOARD_POSITION:"+room.getState().createFEN()));
            session.sendMessage(new TextMessage("TURN:" + turn));
            session.sendMessage(new TextMessage("YourSide:"+playerColor));
            session.sendMessage(new TextMessage("OPPONENT_NAME:" + OpponentName));



            // update flags
            room.playerDisconnected = null;
            room.isPlayerDisconnected = false;


            return;
        }

        // room full
        if (players.size() >= 2) {
            session.sendMessage(new TextMessage("ROOM_FULL"));
            return;
        }



        // join room
        players.add(session);
        S_room.put(session, roomId);
        session.sendMessage(new TextMessage("JOINED"));


        if(players.size() == 2){// Room with 2 player
            for (WebSocketSession player : players){
                if (player.isOpen()) {
                    try {
                        player.sendMessage(new TextMessage("READY_TO_GO"));
                        room.white = null;
                        room.black = null;

                    } catch (Exception e){
                        System.out.println("send failed : " + player.getId());
                        e.printStackTrace();
                    }
                }else {
                    System.out.println(" server --> player not ready to receive");
                }
            }

        }
    }

    public void relayMessage(WebSocketSession sender , TextMessage message) throws  Exception{
         // 1. check from which room it's belong   || what if player don't belong to any room

        String msg = message.getPayload();

        String roomID = S_room.get(sender);
        if(roomID == null){
            sender.sendMessage(new TextMessage("JOIN The SESSION FIRST"));
            return;
        }

        // 2. is room have one player / 2 ??

        Room room =  rooms.get(roomID);
        if(room == null ) return;
        List<WebSocketSession> players =  rooms.get(roomID).getPlayer();
        if(players == null) return;
        GameState state = room.getState();

        // if 1 --> inform the sender about this.

        if(msg.startsWith("LEAVE_ROOM")  || msg.startsWith("EXIT_ROOM")){
            room.white = null;
            room.black = null;
            room.startRequest = 0;
            room.RematchRequests = 0;

            //remove that session from room and session room(S-room) mapping
            players.remove(sender);
            S_room.remove(sender);

            if(msg.startsWith("EXIT_ROOM")){
                if(players.size() == 1){
                    WebSocketSession otherPlayer;
                    if(players.get(0) != sender){
                        otherPlayer = players.get(0);
                        otherPlayer.sendMessage(new TextMessage("EXIT_ROOM"));
                    }
                }

            }



            // delete room if empty after session remover
            if(players.isEmpty()) {
                rooms.remove(roomID);
            }

            // tell other player and update control panel of other. if any!
            if(players.size() == 1){
                WebSocketSession otherPlayer;
               if(players.get(0) != sender){
                   otherPlayer = players.get(0);
                   otherPlayer.sendMessage(new TextMessage("OPPONENT_LEFT_FROM_THIS_ROOM"));
               }
            }
            return;
        }

        if(msg.startsWith("NAME:")){
            room.playersName.put(sender,msg.substring(5));

            if(players.size() == 2){
                WebSocketSession receiver = (sender == players.get(0))?  players.get(1) : players.get(0);
                sender.sendMessage(new TextMessage("OPPONENT_NAME:" + room.playersName.get(receiver)));
                receiver.sendMessage(new TextMessage("OPPONENT_NAME:" + room.playersName.get(sender)));
            }

            return;
        }


        if(players.size() == 1){
             sender.sendMessage(new TextMessage("YOU ARE ALONE IN ROOM"));
             return;
         }
         // if 2 --> send message to other player
         if( players.size() == 2){


             if(msg.startsWith("MyColor:")){
                 sideChanger(room,sender,msg);
                 return;
             }

             if(msg.equals("REMATCH")){
                 room.RematchRequests++;
                 System.out.println("RematchReq : "+ room.RematchRequests);
                 if(room.RematchRequests == 2){
                     room.resetGame();
                     for (WebSocketSession player : players){
                         if(player.isOpen()) {
                             try {

                                 player.sendMessage(new TextMessage("REMATCH"));
                                 // assigne
                                 // both players color
                             } catch (Exception e) {
                                 System.out.println("send failed : " + player.getId());
                                 e.printStackTrace();
                             }
                         }else{
                             System.out.println("player not ready to receive");
                         }
                     }
                 }

             }




             if(msg.equals("READY")){
                 room.startRequest++;
                 room.resetGame();
                 System.out.println("startReq: " +  room.startRequest);
                 if(room.startRequest == 2){

                         if(players.get(0) == room.white  && players.get(1) == room.black){
                             players.get(0).sendMessage(new TextMessage("YourSide:WHITE"));
                             players.get(1).sendMessage(new TextMessage("YourSide:BLACK"));
                         }else if(players.get(0) == room.black  && players.get(1) == room.white){
                             players.get(1).sendMessage(new TextMessage("YourSide:WHITE"));
                             players.get(0).sendMessage(new TextMessage("YourSide:BLACK"));
                         }else{
                             Random random = new Random();
                             int p1 = random.nextInt(2);
                             int p2 = (p1 == 1) ? 0 : 1;
                             players.get(p1).sendMessage(new TextMessage("YourSide:WHITE"));
                             players.get(p2).sendMessage(new TextMessage("YourSide:BLACK"));
                             room.white = players.get(p1);
                             room.black = players.get(p2);


                         }

                         for (WebSocketSession player : players) {
                             if (player.isOpen()) {
                                 try {
                                     player.sendMessage(new TextMessage("START_GAME"));

                                     // assigne
                                     // both players color


                                 } catch (Exception e) {
                                     System.out.println("send failed : " + player.getId());
                                     e.printStackTrace();
                                 }
                             } else {
                                 System.out.println("player not ready to receive ");
                             }
                         }
                 }
                 return;
             }


             // when msg comes in Room with Player 2 then check msg.
             // Type of messages
             // --> MOVE

             // Extract move here
             String moveStr = message.getPayload();
             if(!moveStr.startsWith("MOVE:"))return;

             // Convert that move into, MOVE String in TO  from ROW, COL --> To Row , Col


             Move move = state.strToMove(moveStr);
             System.out.println(moveStr);
             if(state.moveGenerator.isMoveLegal(state,move)){   // if move is legal
                 // now broadcast to both boards
                 state.ApplyMove(move);
                 state.currentStatues = state.GameStatus(state.turn);
                 if(state.currentStatues != GameState.states.CONTINUE){
                     room.endGame();
                 }

                 for(WebSocketSession player : players){
                         try {
                             if(player.isOpen()) {
                                 player.sendMessage(new TextMessage(moveStr));
                             }
                         }catch (Exception e){
                             System.out.println("send failed : " +player.getId());
                             e.printStackTrace();
                         }
                 }
             }
             else{
                 System.out.println("INVALID_MOVE");
                 sender.sendMessage(new TextMessage("INVALID_MOVE"));
                 // later we will pass game state means FEN so it's can match it too.
             }

             // check it  here
             // messages we can send
             //  JOINED     -> done
             //  START
             //  MOVE:e2e4  -> done
             //  INVALID_MOVE
             //  OPPONENT_LEFT


         }
    }


    // check when it's disconnected
    // closed tab/ lost internet
         //-->remove it from room

    @Override
    public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomID = S_room.get(session);
        System.out.println("Disconnected:" + session.getId());

        if(roomID == null) return;
        Room room = rooms.get(roomID);


        if(room.isGameActive()){
            // notify other player.
            msgToOpponent(session, room,"OPPONENT_DISCONNECTED");

            room.isPlayerDisconnected =true;
            room.playerDisconnected = session;
            room.isPlayerReconnected = false;
            handleDisconnection(session);
            return;
        }

        // if player playing and disconnected in middle then... -->  start on so we need to check if player is playing a game or not.
        removeDisconnected(session);
    }


    public void removeDisconnected(WebSocketSession session){
        String roomID = S_room.get(session);
        System.out.println("Disconnected:" + session.getId());


        // I have here session
        // find the room where I have it this session
        // if( null / no room )  the do nothing

        // remove session from room list
        // remove session --> room mapping


        //  2 playes in room case
        // if other player is also in room then
        // inform other

        // if room is empty
        // delete room

        //  also S_room  from delete session  mapping with room.

        if(roomID == null) return;
        Room room = rooms.get(roomID);
        List<WebSocketSession>  players = room.getPlayer();

        if(players== null){
            S_room.remove(session);
            return;
        }

        //remove that session from room and session room(S-room) mapping
        players.remove(session);
        S_room.remove(session);


        // needs to notify other player and remove that session

        if(players.size() == 1){
            WebSocketSession otherPlayer = players.get(0);

            //if both players disconnected then this will not send non message by
            if(otherPlayer.isOpen()){
                try {
                    otherPlayer.sendMessage(new TextMessage("OPPONENT_DISCONNECTED_FROM_SERVER"));
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Error in disconnection");
                }
            }
        }

        // delete room if empty after session remover
        if(players.isEmpty()){
            rooms.remove(roomID);
        }
    }


    public void sideChanger(Room room,WebSocketSession sender, String msg){
    WebSocketSession receiver = null;
    for (WebSocketSession player : room.getPlayer()){
        if(player != sender) {
            receiver = player;
            break;}
    }


        String color  = msg.substring(8);
        try {
            if(receiver== null){     // just for safty it's never gonna run any way.
                 sender.sendMessage(new TextMessage("Failed to change color"));
                System.out.println(receiver == null);
                 return;
            }


            switch (color){
                case "WHITE" ->{
                    room.white = sender;
                    if(sender.isOpen()) sender.sendMessage(new TextMessage("YourSide:WHITE"));
                    if(receiver.isOpen()) receiver.sendMessage(new TextMessage("YourSide:BLACK"));
                    room.white = sender;
                    room.black = receiver;
                }
                case "BLACK"->{
                     if(sender.isOpen()) sender.sendMessage( new TextMessage("YourSide:BLACK"));
                     if(receiver.isOpen()) receiver.sendMessage(new TextMessage("YourSide:WHITE"));
                     room.white = receiver;
                     room.black = sender;

                }
                case "FATE_DECIDE" -> {
                    if(sender.isOpen()) sender.sendMessage( new TextMessage("YourSide:FATE_DECIDE"));
                    if(receiver.isOpen()) receiver.sendMessage(new TextMessage("YourSide:FATE_DECIDE"));
                    room.white= null;
                    room.black = null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error ->In ColorChange : " + e);
        }


    }



    public void handleDisconnection( WebSocketSession session){

        String roomID = S_room.get(session);
        Room room = rooms.get(roomID);

        if(room == null) return;
        // if match going on...

        room.disconnectTask = scheduler.schedule(() ->{
            if(!room.isPlayerReconnected){
                declareOpponentWinner(session,room);
                // then remove the player from room
                System.out.println("scheduler time RUN OUT!");
                room.isPlayerDisconnected =false;
                room.playerDisconnected =null;

                removeDisconnected(session);
            }
        },60, TimeUnit.SECONDS);
    }

    public void  declareOpponentWinner(WebSocketSession session,Room room){
         WebSocketSession opponent = (room.getPlayer().get(0) == session) ? room.getPlayer().get(1): room.getPlayer().get(0);
         try {
             if(opponent.isOpen()) {
                 room.endGame();
                 room.getState().reset();
                 room.startRequest =  0;
                 room.RematchRequests = 0;
                 opponent.sendMessage(new TextMessage("OPPONENT_LEFT_YOU_WON"));
                 System.out.println("OTHER PLAYER WINS");
             }
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    public void msgToOpponent(WebSocketSession sender, Room room, String opponentToMsg) {

            List<WebSocketSession> players = room.getPlayer();
            if (players.size() <2 ) return;
            WebSocketSession opponent = (players.get(0) == sender) ? players.get(1) : players.get(0);
            try {  if(opponent.isOpen()) {
                opponent.sendMessage(new TextMessage(opponentToMsg));}
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("failed to sent mst to opponent!");
            }
        }
}



