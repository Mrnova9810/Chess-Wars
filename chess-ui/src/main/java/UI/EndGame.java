package UI;

import engine.GameState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class EndGame extends StackPane {

    VBox centerBox;
    ImageView swordShield;

    GameState state;
    BoardView boardView;

    Button newGame ;
    Button exist ;





    EndGame(GameState state, BoardView boardView){

        this.state = state;
        this.boardView = boardView;
        this.setVisible(false);
        this.setMouseTransparent(true);

        this.setStyle("""
                -fx-background-color: rgba(0,0,0, 0.55);
                """);

        newGame = new Button("NEW GAME ");
        exist = new Button("EXIT");


      centerBox = new VBox(15);
      centerBox.setAlignment(Pos.CENTER);
      centerBox.setPrefSize(400,400);
      centerBox.setMaxSize(400,400);
      centerBox.setPadding(new Insets(0,0,0,0));
      centerBox.setStyle("""
              -fx-background-color: #050505;
              -fx-padding: 25;
              -fx-background-radius : 20;
              -fx-effect: dropshadow(gaussian,black,20,0.4,0,0);
              """);

        swordShield = new ImageView(new Image(getClass().getResourceAsStream("/Pieces/sword_shield.png")));
        swordShield.setFitWidth(200);
        swordShield.setFitHeight(200);
        swordShield.setPreserveRatio(true);

      this.getChildren().add(centerBox);


    }

    public void finalCenterBox(GameState.states currentStatus,String REASON){
        centerBox.getChildren().clear();

        this.setVisible(true);
        this.setMouseTransparent(false);

        Glow glow = new Glow();
        glow.setLevel(0.1);



        Label title = new Label();
        title.setEffect(glow);
        title.setStyle("""
                -fx-text-fill: white;
                -fx-font-size:22;
                -fx-font-weight: bold;
                """);


        Label result = new Label();
        result.setStyle("""
                -fx-text-fill: #f5c16c;
                -fx-font-size: 17;
                """);


        if(currentStatus != GameState.states.CONTINUE){
            switch(currentStatus){

                case CHECKMATE_WHITE_WINS ->{
                    result.setText("WHITE WINS");
                    title.setText("CHECKMATE");

                }
                case CHECKMATE_BLACK_WINS ->{
                    result.setText("BLACK WINS");
                    title.setText("CHECKMATE");

                }
                case DRAW ->{
                    title.setText("DRAW");
                    result.setText(REASON);
                }
            }
        }


        String btnStyle = """
                -fx-background-color:transparent;
                -fx-border-color : #f5c16c;
                -fx-border-radius: 10;
                -fx-text-fill:  #f5c16c;
                -fx-padding:6 18;
                """;

        newGame.setStyle(btnStyle);
        exist.setStyle(btnStyle);


        if(boardView.controller.multiplayerMode){
            newGame.setText("Rematch");
        }



        newGame.setOnAction(e->{
         state.reset();

         newGame.setText("Awaiting...");
         if(boardView.controller.multiplayerMode){
             boardView.controller.networkManager.send("REMATCH");
         }



        });
        exist.setOnAction(e->{

            //music / treads stop it here later
            state.reset();
            this.setVisible(false);
            this.setMouseTransparent(true);
            if(boardView.controller.multiplayerMode) {
                boardView.controller.networkManager.send("EXIT_ROOM");
                boardView.controller.multiplayerMode = false;
            }

            // take it back to manuWindow.

           boardView.controller.GoBackTOMenuWindow();

        });



        HBox buttons = new HBox(100, newGame,exist);
        buttons.setAlignment(Pos.CENTER);



        centerBox.getChildren().addAll(swordShield,title,result,buttons);

    }







}
