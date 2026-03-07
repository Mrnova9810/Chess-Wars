package UI;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

public class ChessApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1280,830);

        ScreenManager switchScene  = new ScreenManager(root);
        MenuWindow menuWindow = new MenuWindow(switchScene);
        switchScene.show(menuWindow);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        scene.setOnKeyPressed( e ->{
                if(e.getCode() == KeyCode.getKeyCode("F") ) {
                   menuWindow.boardLayout.chessboard.flipper();
                }
        });
        stage.setTitle("Chess_In_fx");
        stage.setScene(scene);
        stage.show();

    }






    public static void main(String[] args) {
        launch();
    }
}