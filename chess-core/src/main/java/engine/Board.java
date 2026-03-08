package engine;

public class Board {
    private final  Piece[][] board = new Piece[8][8];

    public Piece get (int x, int y){
      return board[x][y];
    }

    public void set (int x, int y, Piece piece ){
        board[x][y] = piece;
    }

    public  boolean isEmpty(int x, int y ){
        return board[x][y] == null;
    }

    public void initialSetup(){

        Piece.Type[]  pieces = { Piece.Type.ROOK, Piece.Type.KNIGHT, Piece.Type.BISHOP, Piece.Type.QUEEN, Piece.Type.KING , Piece.Type.BISHOP, Piece.Type.KNIGHT , Piece.Type.ROOK};

        for(int row= 0;row <=7; row++){
            if(row == 2 || row == 3|| row == 4 || row == 5 ) continue;

            for (int col =0; col <= 7; col++){
                    if(row == 0) { // black
                        board[row][col] = new Piece(pieces[col], Piece.Color.BLACK);
                    } else if (row == 1){
                        board[row][col] = new Piece(Piece.Type.PAWN, Piece.Color.BLACK);
                    } else if (row == 6) {
                        board[row][col] = new Piece(Piece.Type.PAWN, Piece.Color.WHITE);
                    } else{
                        board[row][col] = new Piece(pieces[col], Piece.Color.WHITE);
                    }
            }
        }
    }


}
