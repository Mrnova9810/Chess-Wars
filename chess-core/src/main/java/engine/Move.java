package engine;

public class Move {
    public final int fromRow, fromCol;
    public int toRow, toCol;
    public boolean Castling = false;

    public boolean promotion= false;
    public Piece.Type promotedTO;
    public Piece.Color promotionOF;




    public boolean isEnPassant = false;
    public int PrevCastlingRight;
    public long prevZobristHash;
    public GameState.states pre_status;





    public Piece PromotedPawn;
    public int prevEnPassantRow;
    public int prevEnPassantCol;



    Piece CapturePiece;
    Piece.Color turnOf;




    public Move(int fromRow, int fromCol, int toRow, int toCol){
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }


    public Move(int fromRow, int fromCol, int toRow, int toCol,boolean Castling){
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.Castling = Castling;


    }

}
