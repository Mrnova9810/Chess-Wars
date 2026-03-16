package UI;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {


    enum Style{BASIC_STYLE , CUTE_STYLE}

    Style currentStyle = Style.CUTE_STYLE;

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

        for(String color : colors){
            for (String ty: type) {
                String key  = color + "_" + ty;
                PieceImage.put(key ,new Image(getClass().getResourceAsStream("/basic_pieces/"+key+ ".png")));
            }
        }







    }


}
