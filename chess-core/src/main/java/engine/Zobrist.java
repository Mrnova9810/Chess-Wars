package engine;

import java.util.Random;

public  final class Zobrist {

    public static long[][][] PIECES;
    //[color] [pieces] [square]


    public static long side_to_move;

    public static long[] castling; // 4 side of castling

    public static long[] enPassant;// 8 files/cols


    static{
      init64BitNum();
    }

    public static void init64BitNum(){
        Random random = new Random(1234242);

        // for pieces / squares
        PIECES = new long[2][6][64];

        for (int color =0 ; color < 2;color++){ // color
          for(int type= 0; type<6; type++){
              for (int square = 0; square < 64; square++){
                  PIECES[color][type][square] = random.nextLong();
              }
          }
        }

        // side to move  ->
        // only using one because when the turn -->
        // white then XOR  and black don't XOR that number.
        // this will  tell about both case by one  long number.
        side_to_move = random.nextLong();


        // castling
        castling = new long[4];

        for (int castIndex = 0; castIndex < 4; castIndex++){
            castling[castIndex] = random.nextLong();
        }


        // en-passant
        enPassant = new long[8];
        for(int file = 0; file< 8;file++){
            enPassant[file] = random.nextLong();
        }
    }

    static int colorIndex(Piece.Color c){
        return (c == Piece.Color.WHITE)? 0 : 1;
    }

    static int type(Piece.Type type){
      return   switch (type){
          case PAWN   -> 0;
          case ROOK   -> 1;
          case KNIGHT -> 2;
          case BISHOP -> 3;
          case KING   -> 4;
          case QUEEN  -> 5;
        };
    }

    static int squareIndex(int row, int col){
        return row *8 + col;
    }






}
