package UI;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class BoardLayout extends BorderPane {

    BoardView chessboard;
    Glow glow = new Glow(0.4);
    StackPane centerWrapper;
    StackPane centerContainer;

    int sidesHeight;
    int sideWeight;

    JoinRoomLayer joinRoomLayer;
    ScreenManager screenManager;



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




        // Top
          Label Player1 = new Label("black player[_____]");
        Player1.setStyle("""
                -fx-text-fill: white
                """);
        Player1.setEffect(glow);

          topUI = new HBox( 10 , Player1);
          topUI.setAlignment(Pos.CENTER);
          topUI.setPadding(new Insets(10));
          topUI.setStyle("""
                  -fx-background-color:#52221f;
                  """);

        // bottom
        Label Player2 = new Label("white player[_____]");
        Player2.setStyle("""
             
                """);

        Player2.setEffect(glow);


        bottomUI = new HBox( 10 , Player2);
        bottomUI.setAlignment(Pos.CENTER);
        bottomUI.setPadding(new Insets(10));
        bottomUI.setStyle("""
                  -fx-background-color: #52221f;
                  """);

        topUI.prefHeightProperty().bind(heightProperty().subtract(centerWrapper.heightProperty()).divide(2));
        bottomUI.prefHeightProperty().bind(heightProperty().subtract(centerWrapper.heightProperty()).divide(2));






        // left
        leftUI = new VBox();

        leftUI.setStyle("-fx-background-color: #52221f;");



        // right
        rightUI = new VBox();

        rightUI.setStyle("-fx-background-color: #52221f;");



         leftUI.prefWidthProperty().bind(widthProperty().subtract(centerWrapper.widthProperty()).divide(2));
         rightUI.prefWidthProperty().bind(widthProperty().subtract(centerWrapper.widthProperty()).divide(2));


        this.setTop(topUI);
        this.setBottom(bottomUI);
        this.setLeft(leftUI);
        this.setRight(rightUI);
    }
}
