package UI;

import engine.GameState;
import engine.Move;
import engine.MoveGenerator;
import engine.Piece;
import javafx.application.Platform;
import network.NetworkManager;

import java.util.List;

public class UIController {

    GameState state;

    MoveGenerator moveGenerator;
    Boolean multiplayerMode = false;
    NetworkManager networkManager;
    BoardLayout boardLayout;


    public enum RoomStatus{ IN_WAITING, READY_TO_GO}
    public enum Color{ WHITE, BLACK, FATE_DECIDE}

    public Color playerColor = Color.FATE_DECIDE;

    RoomStatus roomStatus = RoomStatus.IN_WAITING;
    public String firstPerson = "You";
    public String secondPerson = "[ ________ ]";





    public UIController(BoardLayout boardLayout){
        state = new GameState();
        this.boardLayout = boardLayout;
        moveGenerator = new MoveGenerator();
        networkManager = new NetworkManager(this);
        networkManager.connect();
    }

    public void  handleServerMessage(String moveStr){
        System.out.println("server --> " + moveStr);

        Platform.runLater(()->{
        if(moveStr.startsWith("MOVE:")){
          Move move = state.strToMove(moveStr);
          state.ApplyMove(move);
          boardLayout.chessboard.EndingSetUp();
        } else if (moveStr.equals("JOINED")) {
           networkManager.send("NAME:" + firstPerson);
        } else if (moveStr.startsWith("YourSide:")) {
            String color = moveStr.substring(9);
            switch (color){
                case "WHITE" -> playerColor = Color.WHITE;
                case "BLACK" -> playerColor = Color.BLACK;
                case "FATE_DECIDE" -> playerColor = Color.FATE_DECIDE;
            }
        } else if (moveStr.equals("READY_TO_GO")) {
            roomStatus = RoomStatus.READY_TO_GO;
            boardLayout.joinRoomLayer.startBtn.setDisable(false);
            boardLayout.joinRoomLayer.colorFlipperBtn.setDisable(false);
        } else if(moveStr.startsWith("OPPONENT_NAME:")){
            secondPerson = moveStr.substring(14);
        }
        else if (moveStr.equals("START_GAME") ){
            // start Game
            multiplayerMode = true;
            startFreshGame();

        } else if(moveStr.equals("REMATCH") ){
            boardLayout.chessboard.EndLayer.setVisible(false);
            boardLayout.chessboard.EndLayer.setMouseTransparent(true);
            state.reset();
            boardLayout.chessboard.drawPieces();

        }else if (moveStr.equals("OPPONENT_LEFT_FROM_THIS_ROOM")) {
            // show OpponentLeft...
            // start timer...
            roomStatus = RoomStatus.IN_WAITING;
            boardLayout.joinRoomLayer.joinReqSended = false;
            secondPerson = "[ ________ ]";
            playerColor = Color.FATE_DECIDE;
            boardLayout.joinRoomLayer.startBtn.setText("READY");
            boardLayout.joinRoomLayer.startBtn.setDisable(true);
            boardLayout.joinRoomLayer.colorFlipperBtn.setDisable(true);
        } else if (moveStr.equals("EXIT_ROOM")) {
            boardLayout.chessboard.EndLayer.newGame.setDisable(false);
            roomStatus = RoomStatus.IN_WAITING;
            secondPerson = "[ ________ ]";
            playerColor = Color.FATE_DECIDE;
            boardLayout.joinRoomLayer.startBtn.setText("READY");
            boardLayout.joinRoomLayer.startBtn.setDisable(true);
            boardLayout.joinRoomLayer.colorFlipperBtn.setDisable(true);

        }
            boardLayout.joinRoomLayer.updateUI();
            boardLayout.chessboard.ClearHighlights();
            boardLayout.chessboard.drawPieces();
        });



    }

    public void startFreshGame() {
        if(multiplayerMode){
            if(playerColor == Color.BLACK) boardLayout.chessboard.flipper();
        }
        state.reset();
        boardLayout.screenManager.show(boardLayout);
        System.out.println("ScreenManager...");
    }
    public void GoBackTOMenuWindow(){
        state.InRoom =false;
        state.RoomID ="";
        boardLayout.joinRoomLayer.joinReqSended = false;


        boardLayout.joinRoomLayer.menuWindow.TopRight.getChildren().clear();
        boardLayout.joinRoomLayer.menuWindow.TopRight.getChildren().add(boardLayout.joinRoomLayer.RightTopbar(boardLayout.joinRoomLayer.menuWindow.boardLayout.chessboard.controller.state.InRoom, boardLayout.joinRoomLayer.menuWindow.boardLayout.chessboard.controller.state.RoomID));
        boardLayout.joinRoomLayer.menuWindow.boardLayout.chessboard.controller.secondPerson = "[ __________ ]";
        boardLayout.joinRoomLayer.menuWindow.boardLayout.chessboard.controller.multiplayerMode =false;


        boardLayout.joinRoomLayer.showTextArea =false;


        boardLayout.joinRoomLayer.menuWindow.boardLayout.chessboard.controller.roomStatus = UIController.RoomStatus.IN_WAITING;
        boardLayout.joinRoomLayer.menuWindow.boardLayout.chessboard.controller.playerColor = UIController.Color.FATE_DECIDE;
        boardLayout.joinRoomLayer.startBtn.setText("READY");
        boardLayout.joinRoomLayer.startBtn.setDisable(true);
        boardLayout.joinRoomLayer.colorFlipperBtn.setDisable(true);
        boardLayout.joinRoomLayer.RoomIdTextArea.clear();

        boardLayout.joinRoomLayer.setVisible(false);
        boardLayout.joinRoomLayer.setMouseTransparent(true);

        boardLayout.joinRoomLayer.updateUI();

        boardLayout.screenManager.show(boardLayout.joinRoomLayer.menuWindow);
    }

    POV CurrentPOV = POV.WHITE;
    public  enum  POV { WHITE, BLACK}

    public  int uiRow(int engineRow){
        return (CurrentPOV == POV.WHITE) ? engineRow :7 -engineRow;
    }

    public int uiCol(int engineCol){
        return (CurrentPOV == POV.WHITE) ? engineCol : 7-engineCol;
    }


    public int engRow(int uiRow){
        return (CurrentPOV == POV.WHITE)?  uiRow : 7-uiRow;
    }
    public int engCol(int uiCol){
        return  (CurrentPOV == POV.WHITE)? uiCol : 7-uiCol;
    }


    public List<Move> getLegalMoves( int engRow , int engCol){
        return moveGenerator.LegalMoves( state,engRow,engCol);
    }

    public boolean isCorrectTurn(int row , int col){
        Piece p = state.board.get(row ,col);
        if(multiplayerMode){
            boolean playerTurn = false;
            if(p == null) return false;


            if(p.color == Piece.Color.WHITE && playerColor == Color.WHITE) playerTurn = true;
            if(p.color == Piece.Color.BLACK && playerColor ==Color.BLACK) playerTurn = true;




            return  p.color == state.turn && playerTurn;
        }


        return  p != null && p.color == state.turn;
    }

    public String getImageSide(Piece piece){
        if(CurrentPOV == POV.WHITE){
            return ( piece.color ==Piece.Color.WHITE)? "back" : "front";
        }else{
            return (piece.color == Piece.Color.WHITE)? "front" : "back";
        }
    }

}
