package engine;

public class Piece {
    public enum  Type{KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN}
    public enum Color{ WHITE, BLACK}

    public  final  Type type;
    public  final  Color color;
    public boolean hasMoved = false;

    public Piece(Type type, Color color){
        this.type = type;
        this.color = color;
    }

}
