package UI;

import javafx.scene.layout.StackPane;

public class Square extends StackPane {

    final  int uiRow;
    final  int uiCol;

    Square(int uiRow ,int uiCol){
        this.uiRow = uiRow;
        this.uiCol = uiCol;
    }
}
