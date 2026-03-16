package UI;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



import java.util.Objects;

public class ChessApp extends Application {
    @Override
    public void start(Stage stage) {

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1280,830);

        ScreenManager switchScene  = new ScreenManager(root);
        MenuWindow menuWindow = new MenuWindow(switchScene);
        switchScene.show(menuWindow);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        scene.setOnKeyPressed( e ->{
            if(e.getCode() == KeyCode.getKeyCode("F") ) {
                   menuWindow.boardLayout.chessboard.flipper();
                   menuWindow.boardLayout.updateLabel();
                   menuWindow.boardLayout.showTurn(menuWindow.boardLayout.chessboard.controller.state.turn);
            }
            if(e.getCode() == KeyCode.getKeyCode("P")){
                String fen1 = menuWindow.boardLayout.chessboard.controller.state.createFEN();
                System.out.println("fen1: " + fen1);
            }
            if(e.getCode() == KeyCode.S){
                // style and color change
               menuWindow.boardLayout.chessboard.imageLoader.currentStyle = (  menuWindow.boardLayout.chessboard.imageLoader.currentStyle == ImageLoader.Style.BASIC_STYLE) ? ImageLoader.Style.CUTE_STYLE : ImageLoader.Style.BASIC_STYLE;
               if(menuWindow.boardLayout.chessboard.imageLoader.currentStyle == ImageLoader.Style.CUTE_STYLE){
                   menuWindow.boardLayout.chessboard.squareColorChanger(ImageLoader.Style.CUTE_STYLE);
               }else{
                   menuWindow.boardLayout.chessboard.squareColorChanger(ImageLoader.Style.BASIC_STYLE);
               }
               menuWindow.boardLayout.chessboard.drawPieces();
            }if(e.getCode() == KeyCode.C){
                menuWindow.boardLayout.chessboard.squareColorChanger(null);
            }
        });
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();

    }




    public static void main(String[] args) {
        launch();
    }
}