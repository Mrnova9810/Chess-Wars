package UI;

import javafx.scene.text.Text;

import javafx.geometry.Point2D;

public class LetterData {
    Text text;
    Point2D original;

    double currX;
    double currY;

    //initial position
    LetterData(Text t, Point2D point2D){
        this.text = t;
        this.original = point2D;
        currX = original.getX();
        currY = original.getY();

    }
}
