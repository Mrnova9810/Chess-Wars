package UI;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class ScreenManager {

    private final StackPane UIContainer;

    public ScreenManager(StackPane root){
        this.UIContainer = root;
    }

    public void show(Node node){
        UIContainer.getChildren().setAll(node);   // setALL removes other things which are inside it and add this!

    }




}
