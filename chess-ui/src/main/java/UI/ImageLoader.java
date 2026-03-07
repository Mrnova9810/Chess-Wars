package UI;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {




    public  final Map<String , Image> PieceImage = new HashMap<>();

    public ImageLoader(){

        String[] colors = {"white", "black"};
        String[] type = {"pawn", "rook", "knight", "bishop", "king", "queen" };
        String[] sides = {"front","back"};


        for(String color : colors){
            for (String ty: type) {
                 for(String side : sides){
                     String key  =  color + "_" + ty + "_" + side;
                    PieceImage.put(key ,new Image(getClass().getResourceAsStream("/Pieces/"+key+ ".png")));
                  }
            }
        }



    }


}
