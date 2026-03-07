package UI;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Floating_Text extends Pane {

    List<LetterData> letters = new ArrayList<>();
    int fontSize = 40;
    int offset = 100;
    double width_of_word;




    double startX = 400;
    double startY = 60;
    double spacing = 40;
    double effective_radius = 100;
    double movingFactor = 10;


    Point2D mouse;

    public Floating_Text(String word){
        this.setStyle("-fx-background-color: transparent;");
        createLetters(word);
        animation();
    }

    public void createLetters(String word){
        Text text = new Text(word);
        text.setFont(Font.font("MV Boli", fontSize));
        width_of_word = text.getLayoutBounds().getWidth();


        // center start
        this.layoutBoundsProperty().addListener(((observableValue, oldVal, bounds) ->{

            startX = (bounds.getWidth()/2 - width_of_word/2)-offset;
            this.getChildren().clear();
            letters.clear();


            for (int i = 0; i < word.length(); i++) {
                String ch = String.valueOf(word.charAt(i));
                Text t = new Text(ch);
                t.setFont(Font.font("MV boli",fontSize));
                t.setFill(Color.WHITE);
                t.setSmooth(true);

                double x = startX + i*spacing;
                double y = startY;
                letters.add(new LetterData(t, new Point2D(x,y)));
                t.setLayoutX(x);
                t.setLayoutY(y);
                this.getChildren().add(t);
            }




        }));

    }












    public  void animation(){

        AnimationTimer timer =new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(mouse == null) return;
                updatePosition();
                for (LetterData l : letters){
                     l.text.setLayoutX(l.currX);
                     l.text.setLayoutY(l.currY);

                }
            }
        };

        timer.start();
    }

    public void updateMouse( double x , double y){
            mouse = new Point2D(x,y);
    }

    public void updatePosition(){
        for (LetterData l : letters){
            double dx = l.currX -mouse.getX();
            double dy = l.currY -mouse.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);


            // current position of letters



            if(dist < effective_radius  && dist > 0){ // mouse  will affect the position
                 double force = (effective_radius - dist)/ movingFactor;

                 l.currX += (dx/dist)*force;
                 l.currY += (dy/dist)*force;

            }else{
                l.currX += (l.original.getX()-l.currX) * 0.05;
                l.currY += (l.original.getY()-l.currY) * 0.05;
            }
        }
    }







}
