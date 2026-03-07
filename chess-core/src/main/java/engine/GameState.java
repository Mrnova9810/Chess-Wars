package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    public enum  states{CHECKMATE_BLACK_WINS, CHECKMATE_WHITE_WINS,DRAW, CONTINUE }

    public Map<Long, Integer> repetitionCount = new HashMap<>();


    public long zobristHash;
    int castlingRights;
    public states currentStatues ;
    public String REASON =" ";

    public boolean InRoom = false;
    public String RoomID;






    public final Board board = new Board();
    public MoveGenerator moveGenerator = new MoveGenerator();
    public Piece.Color turn = Piece.Color.WHITE;
    public int enPassantRow = -1;
    public  int enPassantCol = -1;
    




    public void switchTurn(){
        turn = (turn == Piece.Color.WHITE)? Piece.Color.BLACK : Piece.Color.WHITE;
    }

     boolean white_king_move = false;
     boolean white_king_side_rook_move= false;
     boolean white_queen_side_rook_move=false;

     boolean black_king_move =  false;
     boolean black_king_side_rook_move = false;
     boolean black_queen_side_rook_move = false;


     public GameState(){
         board.initialSetup();
         castlingRights = 0b1111;
         computeInitialHash();
     }






    public boolean isInCheck(Piece.Color ofColor){
        // find position
        // is that square under attack?   --> true

        Piece isKing;
        Piece.Color enemyColor = (ofColor == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;


        for(int row = 0; row <=7;row ++){
            for(int col = 0; col <= 7; col++){
                isKing = board.get(row,col);
                if(isKing == null) continue;
                if(isKing.type == Piece.Type.KING   && isKing.color == ofColor){  // found at position
                   return moveGenerator.isSquareAttacked( this ,enemyColor, row,col);
                }
            }
        }
        return false;
    }

    public void  undoMove(Move move){

        // placing where it was.
        // place capture piece back
        // castling  --> rook move back
        // flag what was before


        // flag rest
        white_king_move = move.wk;
        white_king_side_rook_move = move.wks;
        white_queen_side_rook_move = move.wqs;

        black_king_move = move.bk;
        black_king_side_rook_move = move.bks;
        black_queen_side_rook_move = move.bqs;

        // turn revers
        turn = move.turnOf;
        enPassantRow =move.prevEnPassantRow;
        enPassantCol = move.prevEnPassantCol;



       // promotion
        if(move.promotion ){ // in this case p == Queen / promoted piece
            board.set(move.fromRow,move.fromCol, move.PromotedPawn);               // placing where it was.
            board.set(move.toRow, move.toCol, move.CapturePiece);  // capture piece
            return;
        }


        if(move.isEnPassant){
            Piece p = board.get(move.toRow,move.toCol);
            board.set(move.fromRow,move.fromCol, p);
            board.set(move.fromRow, move.toCol,move.CapturePiece);
            board.set(move.toRow,move.toCol, null);
            return;
        }




        Piece p = board.get(move.toRow,move.toCol);
        board.set(move.fromRow,move.fromCol, p);               // placing where it was.

        board.set(move.toRow, move.toCol, move.CapturePiece);  // capture piece


        if(move.Castling){
            if(move.toCol > move.fromCol){   // king side castling
                Piece rook =  board.get(move.fromRow, move.fromCol + 1);
                board.set(move.fromRow, move.fromCol + 1, null);
                board.set(move.fromRow, move.fromCol + 3, rook);
            } else{
                Piece rook =  board.get(move.fromRow, move.fromCol - 1);
                board.set(move.fromRow, move.fromCol - 1, null);
                board.set(move.fromRow, move.fromCol - 4, rook);
            }
        }




        Integer count = repetitionCount.get(zobristHash);
        if(count != null) {
            if (count == 1) {
                repetitionCount.remove(zobristHash);
            } else {
                repetitionCount.put(zobristHash, count - 1);
            }
        }



        zobristHash =move.prevZobristHash;

    }

    public void ApplyMove(Move move){
         move.PrevCastlingRight = castlingRights;
         move.prevZobristHash = zobristHash;
         move.pre_status = currentStatues;


        //normal moves apply
       //castling
       // flags update
       // promotion

        move.turnOf = turn;
        move.prevEnPassantRow= enPassantRow;
        move.prevEnPassantCol = enPassantCol;

        move.wk = white_king_move;
        move.wks = white_king_side_rook_move;
        move.wqs = white_queen_side_rook_move;

        move.bk = black_king_move;
        move.bks = black_king_side_rook_move;
        move.bqs = black_queen_side_rook_move;



       Piece p = board.get(move.fromRow, move.fromCol);
       if(p == null) return;   // no need but just for safety.






       // apply moves here

       // at ToRow, ToCol check for piece? if it's rook then update flags.
       move.CapturePiece =  board.get(move.toRow,move.toCol);

       if( move.CapturePiece != null &&  move.CapturePiece.type == Piece.Type.ROOK){
          if(move.CapturePiece.color == Piece.Color.WHITE){
              if( move.toRow == 7 && move.toCol == 0) white_queen_side_rook_move = true;
              if (move.toRow == 7 && move.toCol == 7) white_king_side_rook_move = true;
          }else {
              if( move.toRow == 0 && move.toCol == 0) black_queen_side_rook_move = true;
              if (move.toRow == 0 && move.toCol == 7) black_king_side_rook_move = true;
          }
       }

        // en-passant
        if(p.type == Piece.Type.PAWN) {
//            System.out.println("----------------");
//            System.out.println("move : ( " +move.fromRow  + ", " + move.fromCol + " )-->  (" + move.toRow +" , " +  move.toCol  + ")" );
//            System.out.println(enPassantRow + ",  " + enPassantCol );
//            System.out.println( move.toCol == enPassantCol && move.toRow == enPassantRow  );
//            System.out.println( Math.abs(move.toCol -move.fromCol));
            if (enPassantRow != -1) {
                if (move.toCol == enPassantCol && move.toRow == enPassantRow  &&   // moved to en passant square
                        Math.abs(move.toCol -move.fromCol) == 1 &&
                        Math.abs(move.fromRow - move.toRow )== 1) {                    // diagonal move

                    move.CapturePiece = board.get(move.fromRow, move.toCol);
                    move.isEnPassant = true;
                    board.set(move.fromRow, move.toCol, null);
                }
            }
        }


       board.set(move.toRow, move.toCol, p);
       board.set(move.fromRow, move.fromCol, null);




       // flags updates
       if (p.type == Piece.Type.KING) {   // King
           if (p.color == Piece.Color.WHITE && !white_king_move) {
               white_king_move = true;
           } else if (p.color == Piece.Color.BLACK && !black_king_move) {
               black_king_move = true;
           }
       }

       if (p.type == Piece.Type.ROOK) {  // Rook
           if (p.color == Piece.Color.WHITE) {
               // King side rook
               if (move.fromCol == 7 && move.fromRow == 7) {
                   white_king_side_rook_move = true;
               }
               // Queen side Rook
               if (move.fromCol == 0 && move.fromRow == 7) {
                   white_queen_side_rook_move = true;
               }
           } else {
               if (move.fromCol == 7 && move.fromRow == 0) {
                   black_king_side_rook_move = true;
               }
               // Queen side Rook
               if (move.fromCol == 0 && move.fromRow == 0) {
                   black_queen_side_rook_move = true;

               }
           }
       }



           // castling --> true
           // row tell --> black(0)/ white(7)
           // col tell --> type of castling
           // if( toCol > fromCol)    king side
           // else                    Queen side

           if (move.Castling) {// white side     || black
                   if (move.toCol > move.fromCol) {    // king side

                       if(move.fromRow == 7){
                           white_king_side_rook_move = true;
                       } else if (move.fromRow == 0){
                           black_king_side_rook_move = true;
                       }


                       //_K_  __ __ _R_ stating  --> __ __ _K_  _R_ in btw  --> __ _R_ _K_ __ final after castling
                    Piece Rook = board.get(move.fromRow, move.fromCol + 3);
                    board.set(move.fromRow,move.fromCol + 1, Rook);           // set rook
                    board.set(move.fromRow,move.fromCol + 3, null);     // clear rook from starting position




                   } else { // Queen Side castling
                       //  _R_  __  __  __ _K_   --> _R_  __ _K_  __  -->   __  __  _K_  _R_

                       if(move.fromRow == 7){
                           white_queen_side_rook_move = true;
                       } else if (move.fromRow == 0){
                           black_queen_side_rook_move = true;
                       }

                       Piece Rook = board.get(move.fromRow, move.fromCol - 4);
                       board.set(move.fromRow, move.fromCol - 1, Rook);
                       board.set(move.fromRow, move.fromCol - 4, null);
                   }

           }

       // promotion
        if(move.promotion) {
            if (p.type == Piece.Type.PAWN){
                int promotionRank = (p.color == Piece.Color.WHITE) ? 0 : 7;


                if (move.toRow == promotionRank) {
                    move.promotionOF = p.color;
                    move.PromotedPawn = p;

                    // now change pawn with chosen piece.
                    // Knight queen rook bishop
                    board.set(move.toRow, move.toCol, new Piece(move.promotedTO, p.color));


                }
            }
        }


        switchTurn();



           // en-passant square setup
           if(p.type == Piece.Type.PAWN && Math.abs( move.toRow - move.fromRow) == 2){
               enPassantCol = move.toCol;
               enPassantRow = (move.fromRow + move.toRow) / 2;
           }
               else{
               enPassantRow = -1;
               enPassantCol = -1;
               }







           updateStateInHash(move,p);
           repetitionCount.put(zobristHash, repetitionCount.getOrDefault(zobristHash,0) + 1);

   }

    public states GameStatus(Piece.Color color){
         // 3-fold repetition
        if(repetitionCount.get(zobristHash) >=3  ) {
            REASON = "3 FOLD REPETITION";
            return  states.DRAW;
        }


        if(InsufficientMaterial()){
            REASON ="INSUFFICIENT MATERIAL";
            return states.DRAW;
        }

        boolean haveMove = moveGenerator.hasAnyLegalMove(this,color);
        if(isInCheck(color) ){  // In check
            if(!haveMove) { // no legal moves
                return (color == Piece.Color.WHITE) ? states.CHECKMATE_BLACK_WINS : states.CHECKMATE_WHITE_WINS;
            }
        }
        else{  // not in check
            if(!haveMove){ //no legal moves
                REASON = "DUE TO STALEMATE";
                return states.DRAW;  //
            }
        }
        return states.CONTINUE;
    }

    public boolean InsufficientMaterial(){

         // storing position for only 2 Pieces
         int[] Row = new int[2];
         int[] Col = new int[2];
         //Array of minor pieces
        ArrayList<Piece> minor = new ArrayList<>();
         for(int row = 0; row <8; row++){
             for (int col =  0; col < 8; col++){
                 if(board.isEmpty(row,col) ) continue;
                 if(board.get(row,col).type == Piece.Type.KING) continue;
                 if(board.get(row, col).type == Piece.Type.ROOK ||
                         board.get(row, col).type == Piece.Type.QUEEN ||
                         board.get(row, col).type == Piece.Type.PAWN ){
                     return  false;
                 }

                 // Bishop & knight
                 minor.add(board.get(row,col));
                 if( minor.size() >2){
                     return false;
                 }else {
                     // position storing
                     Row[minor.size() - 1] = row;
                     Col[minor.size() - 1] = col;
                 }


             }
         }



        if(minor.isEmpty()){// only king vs King
            return  true;
        }else if (minor.size() == 1){  // any one knight && bishop
            // case  I  knight + K vs King
            // case II  bishop + K vs king
            return  true;

        }else{
            if(minor.get(0).type == Piece.Type.BISHOP   && minor.get(1).type == Piece.Type.BISHOP){
                Piece.Color oppositeColor = (minor.get(0).color == Piece.Color.WHITE )? Piece.Color.BLACK : Piece.Color.WHITE;
                if(minor.get(1).color== oppositeColor){// both pieces bishop with different color
                    // checking which color of bishop  if ( same diagonal color bishop) then --> true
                    if((Row[0] + Col[0])% 2 == (Row[1] + Col[1]) % 2) { // if true then same diagonal
                        return true;
                    }
                }
            }else{
                return false;
            }
        }




         return false;
    }


    public void computeInitialHash() {

        // Pieces/ squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board.get(row, col);
                if (p == null) continue;
                zobristHash ^= Zobrist.PIECES[Zobrist.colorIndex(p.color)][Zobrist.type(p.type)][Zobrist.squareIndex(row, col)];
            }
        }

        // side_of_turn
        if(turn == Piece.Color.BLACK){
            zobristHash ^= Zobrist.side_to_move;
        }

        // castling
        if((castlingRights & 0b0001) !=0 )   zobristHash ^= Zobrist.castling[0];    //WK
        if((castlingRights & 0b0010) !=0 )   zobristHash ^= Zobrist.castling[1];      // WQ
        if((castlingRights & 0b0100) !=0 )    zobristHash ^= Zobrist.castling[2];     //BK
        if((castlingRights & 0b1000) !=0 )    zobristHash ^= Zobrist.castling[3];     // BQ



        // en-passant /file
        if(enPassantCol != -1){
            zobristHash ^= Zobrist.enPassant[enPassantCol];
        }

        repetitionCount.put(zobristHash,1);
    }

    public void updateStateInHash(Move move ,Piece p){
        // for pieces move
        //from
        zobristHash ^= Zobrist.PIECES[Zobrist.colorIndex(p.color)][Zobrist.type(p.type)][Zobrist.squareIndex(move.fromRow,move.fromCol)];
        // to
        if(move.promotion){  //

            // adding promotion pieces
            zobristHash ^= Zobrist.PIECES[Zobrist.colorIndex(move.promotionOF)][Zobrist.type(move.promotedTO)][Zobrist.squareIndex(move.toRow, move.toCol)];

        }else {
            zobristHash ^= Zobrist.PIECES[Zobrist.colorIndex(p.color)][Zobrist.type(p.type)][Zobrist.squareIndex(move.toRow, move.toCol)];
        }


        // capture
        if(move.CapturePiece != null) {
            // en-passant Capture
            if(move.prevEnPassantCol != -1  && move.toCol== move.prevEnPassantCol && move.toRow == move.prevEnPassantRow){
                zobristHash ^= Zobrist.PIECES[Zobrist.colorIndex(move.CapturePiece.color)][Zobrist.type(move.CapturePiece.type)][Zobrist.squareIndex(move.fromRow, move.toCol)];
            }else {
                // normal  capture
                zobristHash ^= Zobrist.PIECES[Zobrist.colorIndex(move.CapturePiece.color)][Zobrist.type(move.CapturePiece.type)][Zobrist.squareIndex(move.toRow, move.toCol)];
            }
        }

        // castling
        // 04 bits  = 4 rights
        // 0001  = white king side
        // 0010  = white queen side
        // 0100  = black king side
        // 1000  = black queen side

        if(p.type == Piece.Type.KING){
            if(p.color == Piece.Color.WHITE){
                castlingRights &=  ~0b0011;   // white king side  & white queen side
            }else{
                castlingRights &= ~0b1100;
            }
        }

        if(p.type == Piece.Type.ROOK){
            if(p.color == Piece.Color.WHITE){
                if(move.fromRow == 7 && move.fromCol == 7)castlingRights &= ~0b0001; //WK
                if(move.fromRow ==7 && move.fromCol == 0) castlingRights &=~0b0010;  //WQ
            }else{
                if(move.fromRow== 0 && move.fromCol==7) castlingRights  &= ~0b0100;  //BK
                if(move.fromRow==0 && move.fromCol == 0) castlingRights &= ~0b1000;  //BQ
            }
        }

        if(move.CapturePiece !=null  && move.CapturePiece.type == Piece.Type.ROOK){
            if(move.CapturePiece.color == Piece.Color.WHITE){
                if(move.toRow == 7 && move.toCol == 7)castlingRights &= ~0b0001; //WK
                if(move.toRow ==7 && move.toCol == 0) castlingRights &=~0b0010;  //WQ
            }else {
                if (move.toRow == 0 && move.toCol == 7) castlingRights &= ~0b0100;  //BK
                if (move.toRow == 0 && move.toCol == 0) castlingRights &= ~0b1000;  //BQ
            }
        }

        int changed = move.PrevCastlingRight ^ castlingRights;

        //  update right
        if((changed & 0b0001) != 0 ) zobristHash ^= Zobrist.castling[0];    //WK
        if((changed & 0b0010) !=0)   zobristHash^=Zobrist.castling[1];      // WQ
        if((changed & 0b0100) !=0)   zobristHash^= Zobrist.castling[2];     //BK
        if((changed &0b1000) !=0)    zobristHash^=Zobrist.castling[3];      //BQ


        // en-passant
        if(move.prevEnPassantCol != -1){
            zobristHash^= Zobrist.enPassant[move.prevEnPassantCol]; // removed from en-passant from previous state
        }
        if (enPassantCol != -1) {
            zobristHash ^= Zobrist.enPassant[enPassantCol];     // adding en-passant to current state
        }


        zobristHash ^= Zobrist.side_to_move;
    }


    public void reset(){

        //set board to starting position
        bordRest();
        turn = Piece.Color.WHITE;


         // castling right
         castlingRights = 0b1111;

         // flags
         white_king_move = false;
         white_king_side_rook_move = false;
         white_queen_side_rook_move = false;
         black_king_move = false;
         black_king_side_rook_move = false;
         black_queen_side_rook_move = false;

         enPassantCol =-1;
         enPassantRow =-1;
         zobristHash =0;
        computeInitialHash();


         repetitionCount.clear();

         // starting position
         repetitionCount.put(zobristHash, 1);
         // hash set to zero --> initialize to starting state-> after
         currentStatues = states.CONTINUE;

    }

    public  void bordRest(){
         // clear board
         for (int row = 0; row < 8; row++){
             for(int col= 0; col <8;col++){
                 board.set(row,col,null);
             }
         }
        board.initialSetup();

    }



    public Move strToMove(String moveStr){



        int fromRow,fromCol,toRow,toCol;

        fromRow = 8 - Integer.parseInt(String.valueOf(moveStr.charAt(6)));
        toRow =   8 - Integer.parseInt(String.valueOf(moveStr.charAt(8)));

        switch (moveStr.charAt(5)){
            case 'a' ->fromCol = 0;

            case 'b' ->fromCol = 1;
            case 'c' ->fromCol = 2;
            case 'd' ->fromCol = 3;
            case 'e' ->fromCol = 4;
            case 'f' ->fromCol = 5;
            case 'g' ->fromCol = 6;
            case 'h' ->fromCol = 7;
            default -> fromCol = -1;
        }

        switch (moveStr.charAt(7)){
            case 'a' ->toCol = 0;
            case 'b' ->toCol = 1;
            case 'c' ->toCol = 2;
            case 'd' ->toCol = 3;
            case 'e' ->toCol = 4;
            case 'f' ->toCol = 5;
            case 'g' ->toCol = 6;
            case 'h' ->toCol = 7;
            default -> toCol =-1;
        }
        Move move = new Move(fromRow,fromCol,toRow,toCol);


        if(moveStr.length() == 10) {
            Piece.Type choice;
            System.out.println("length :" + moveStr.length());
            switch (moveStr.charAt(9)) {
                case 'q' -> choice = Piece.Type.QUEEN;
                case 'r' -> choice = Piece.Type.ROOK;
                case 'b' -> choice = Piece.Type.BISHOP;
                case 'k' -> choice = Piece.Type.KNIGHT;
                default-> choice = Piece.Type.PAWN;   // invalid never going to reach
            }
            if(moveStr.charAt(9) == 'c'){
                move.Castling = true;
            }else{
                move.promotion = true;
                move.promotedTO = choice;
            }
        }
         return  move;
    }



    public String convertInString(Move m){
        String move;
        int fromRow,toRow;
        char fromCol, toCol;

        fromRow = 8-m.fromRow;
        toRow = 8-m.toRow;

        switch (m.fromCol){
            case 0 -> fromCol = 'a';
            case 1 -> fromCol = 'b';
            case 2 -> fromCol = 'c';
            case 3 -> fromCol = 'd';
            case 4 -> fromCol = 'e';
            case 5 -> fromCol = 'f';
            case 6 -> fromCol = 'g';
            case 7 -> fromCol = 'h';
            default ->fromCol = ' ';

        }

        switch (m.toCol){
            case 0 -> toCol = 'a';
            case 1 -> toCol = 'b';
            case 2 -> toCol = 'c';
            case 3 -> toCol = 'd';
            case 4 -> toCol = 'e';
            case 5 -> toCol = 'f';
            case 6 -> toCol = 'g';
            case 7 -> toCol = 'h';
            default ->toCol = ' ';
        }

        move = "" + fromCol + fromRow + toCol + toRow;

        if(m.promotion){
            move += m.promotedTO.name().toLowerCase().charAt(0);  // add choice here
        }
        if(m.Castling){
            move += 'c';
        }

        return move;
    }

    public boolean isPawnReachLastRow(Move move){
         Piece p = board.get(move.fromRow, move.fromCol);
         if(p== null) return false;
         int promotionRank = (p.color == Piece.Color.WHITE) ? 0 : 7;
        return p.type == Piece.Type.PAWN && move.toRow == promotionRank;
    }


}
