package UI;



import engine.Piece;


import javafx.geometry.Pos;
import javafx.scene.control.Label;

import javafx.scene.layout.*;



public class BoardLayout extends BorderPane {

    BoardView chessboard;
    StackPane centerWrapper;
    StackPane centerContainer;

    // #52221f

    JoinRoomLayer joinRoomLayer;
    ScreenManager screenManager;
    Label topLabel;
    Label bottomLabel;

    Region topSpace;
    Region bottomSpace;





    BoardLayout(ScreenManager screenManager, JoinRoomLayer joinRoomLayer){
        this.screenManager = screenManager;
        this.joinRoomLayer =  joinRoomLayer;
       chessboard = new BoardView(this);
       chessboard.setAlignment(Pos.CENTER);
       centerWrapper = new StackPane(chessboard);
       centerWrapper.setStyle("""
               -fx-effect : dropshadow(gaussian,black,20,0.5,0,0);
               -fx-background-color:  #2b2b2b;
               -fx-background-radius: 20;
               -fx-padding: 10;
               """);

       centerWrapper.setAlignment(Pos.CENTER);

       centerContainer = new StackPane((centerWrapper));
       centerContainer.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);


       setCenter(centerContainer);


       borders();
    }

    public void borders() {



        //  ____________________________________
        // |  player 1                          |
        // |       |____________________|       |
        // |       |                    |       |
        // |       |                    |       |
        // |       |                    |       |
        // |       |                    |       |
        // |       |                    |       |
        // |       |                    |       |
        // |       |____________________|       |
        // |  player 2                           |
        // |____________________________________|


        HBox topUI ;
        HBox bottomUI ;
        VBox leftUI;
        VBox rightUI;


        topSpace = new Region();
        bottomSpace = new Region();


        // Top
        topLabel = new Label();
        topLabel.setStyle("""
                -fx-text-fill: white;
                """);
        topLabel.setTranslateX(80);

          topUI = new HBox( 10 , topSpace, topLabel);
          topUI.setAlignment(Pos.CENTER_LEFT);
          topUI.getStyleClass().add("boardBackGround");

        // bottom
        bottomLabel = new Label();
        bottomLabel.setStyle("""
                 -fx-text-fill :  white;
                 """);

        bottomUI = new HBox( 10 ,bottomSpace, bottomLabel);
        bottomUI.setAlignment(Pos.TOP_LEFT);

        bottomUI.getStyleClass().add("boardBackGround");

        topSpace.prefWidthProperty().bind(topUI.widthProperty().multiply(0.02));
        bottomSpace.prefWidthProperty().bind(bottomUI.widthProperty().multiply(0.02));





        topUI.prefHeightProperty().bind(heightProperty().subtract(centerWrapper.heightProperty()).divide(2));
        bottomUI.prefHeightProperty().bind(heightProperty().subtract(centerWrapper.heightProperty()).divide(2));


        // left
        leftUI = new VBox();
        leftUI.getStyleClass().add("boardBackGround");



        // right
        rightUI = new VBox();
        rightUI.getStyleClass().add("boardBackGround");

        showTurn(Piece.Color.WHITE);



         leftUI.prefWidthProperty().bind(widthProperty().subtract(centerWrapper.widthProperty()).divide(2));
         rightUI.prefWidthProperty().bind(widthProperty().subtract(centerWrapper.widthProperty()).divide(2));


        this.setTop(topUI);
        this.setBottom(bottomUI);
        this.setLeft(leftUI);
        this.setRight(rightUI);
    }

    public void updateLabel(){
        // based on POV and player color
        // first name -> your
        // second name -> opponent
        if(chessboard.controller.multiplayerMode){


            if(chessboard.controller.opponentConnection == UIController.Connection.CONNECTED) {
                if (chessboard.controller.playerColor == UIController.Color.WHITE) {
                    playerSideLabel().setText("White player[  " + chessboard.controller.firstPerson + "  ]");
                    opponentSideLabel().setText("Black player[  " + chessboard.controller.secondPerson + "  ]");
                } else {
                    playerSideLabel().setText("Black player[  " + chessboard.controller.firstPerson + "  ]");
                    opponentSideLabel().setText("White player[  " + chessboard.controller.secondPerson + "  ]");
                }
            } else if (chessboard.controller.opponentConnection == UIController.Connection.DISCONNECTED) {
                if (chessboard.controller.playerColor == UIController.Color.WHITE) {
                    playerSideLabel().setText("White player[  " + chessboard.controller.firstPerson + "  ]");
                    opponentSideLabel().setText("Black player[  " + chessboard.controller.secondPerson + "  ] [ Disconnected  ❌]");
                    opponentSideLabel().setStyle("""
                             -fx-text-fill: ;
                            """);
                } else {
                    playerSideLabel().setText("Black player[  " + chessboard.controller.firstPerson + "  ]");
                    opponentSideLabel().setText("White player[  " + chessboard.controller.secondPerson + "  ] [ Disconnected  ❌]");
                }
            } else if (chessboard.controller.opponentConnection == UIController.Connection.RECONNECTED) {
                if (chessboard.controller.playerColor == UIController.Color.WHITE){
                    playerSideLabel().setText("White player[  " + chessboard.controller.firstPerson + "  ]");
                    opponentSideLabel().setText("Black player[  " + chessboard.controller.secondPerson + "  ] [ Reconnected \uD83D\uDCF6]");
                }else{
                    playerSideLabel().setText("Black player[  " + chessboard.controller.firstPerson + "  ]");
                    opponentSideLabel().setText("White player[  " + chessboard.controller.secondPerson + "  ] [ Reconnected \uD83D\uDCF6]");
                }
            }
        }else{


            if(chessboard.controller.CurrentPOV == UIController.POV.WHITE){
                 topLabel.setText("Black player");
                 bottomLabel.setText("White player");


            }else{
                System.out.println("POV : BLACK ha");
                    topLabel.setText("White player");
                    bottomLabel.setText("Black player");
            }
        }
    }


    public void showTurn(Piece.Color turn){
        if(chessboard.controller.multiplayerMode && chessboard.controller.opponentConnection == UIController.Connection.DISCONNECTED) {
            opponentSideLabel().setStyle("""
                    -fx-text-fill: #8d3518;
                    -fx-effect : null;
                    """);
            return;
        }
        if(turn == Piece.Color.WHITE){
            if(chessboard.controller.CurrentPOV == UIController.POV.WHITE){
                topLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : null;
                        """);
                bottomLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : dropshadow(gaussian,cyan,20,0.2,2,2);
                        """);

            }else{
                topLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : dropshadow(gaussian,cyan,20,0.2,2,2);
                        """);
                bottomLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : null;
                        """);


            }
        }else{
            if(chessboard.controller.CurrentPOV == UIController.POV.WHITE){
                topLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : dropshadow(gaussian,cyan,20,0.2,2,2);
                        """);
                bottomLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : null;
                        """);
            }else{
                topLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : null;
                        """);
                bottomLabel.setStyle("""
                        -fx-text-fill: white;
                        -fx-effect : dropshadow(gaussian,cyan,20,0.2,2,2);
                        """);
            }
        }



    }

    public Label playerSideLabel(){
        if(chessboard.controller.CurrentPOV == UIController.POV.WHITE){
            if(chessboard.controller.playerColor == UIController.Color.WHITE) return bottomLabel;
            else return topLabel;

        }else{
            if(chessboard.controller.playerColor == UIController.Color.WHITE) return topLabel;
            else return bottomLabel;
        }
    }
    public Label opponentSideLabel(){
        if(chessboard.controller.CurrentPOV == UIController.POV.WHITE){
            if(chessboard.controller.playerColor == UIController.Color.WHITE) return topLabel;
            else return bottomLabel;

        }else{
            if(chessboard.controller.playerColor == UIController.Color.WHITE) return bottomLabel;
            else return topLabel;
        }
    }

    }
