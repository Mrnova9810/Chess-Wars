package engine;


import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    public boolean hasAnyLegalMove(GameState state , Piece.Color ColorX){   // for stalemate/ checkmate
        // searching for Piece of ColorX
        // if found any Piece then store there Pmoves
        // check for any legal move  --> if found  --->  return true
        // apply move
        // isInCheck -->  true  then not valid  --> next repeated
        //           --> false then valid
        // undo move
        // return
        for(int row = 0; row <=7;row ++){
            for(int col = 0; col <= 7; col++){
                Piece p = state.board.get(row,col);
                if(p != null && p.color == ColorX){
                    List<Move>   pseudoLegalMoves = PMoves(state,row,col);
                    for(Move m : pseudoLegalMoves){
                        state.ApplyMove(m);
                        if(!state.isInCheck(ColorX)){   // legal move found game continue  + color x have move
                            state.undoMove(m);
                            return  true;
                        }
                        state.undoMove(m);
                    }
                }
            }
        }
        return false;   // if no move found no legal move checkmate ya stalemate.
    }


    public List<Move> LegalMoves(GameState state, int row ,int col){
        List<Move> moves = PMoves(state,row,col);

        List<Move> LegalMoves= new ArrayList<>();

        if(state.board.get(row, col) == null || moves == null) return  LegalMoves;

        Piece.Color color = state.board.get(row,col).color;


        for (Move move : moves){
            state.ApplyMove(move);
            if(!state.isInCheck(color)){
                LegalMoves.add(move);
            }
            state.undoMove(move);
        }
        return LegalMoves;
    }

    public boolean isMoveLegal(GameState state, Move move){
        for(Move m : LegalMoves(state,move.fromRow, move.fromCol)){
            if(m.toRow == move.toRow && m.toCol == move.toCol) return true;
        }
        return false;
    }







    public List<Move> PMoves(GameState state, int fromRow, int fromCol){
        List<Move>  moves = new ArrayList<Move>();

        Piece piece = state.board.get(fromRow,fromCol);
        if (piece == null) return moves;

        switch (piece.type){
            case PAWN -> {return Pawn(state,fromRow,fromCol);}
            case KNIGHT -> {return Knight(state,fromRow,fromCol);}
            case BISHOP -> {return Bishop(state,fromRow,fromCol);}
            case ROOK -> {return Rook(state,fromRow,fromCol);}
            case QUEEN -> {return Queen(state,fromRow,fromCol);}
            case KING -> {return King(state,fromRow,fromCol);}

            default -> {
                return moves;        // null
            }
        }
    }

    public List<Move> Pawn(GameState state, int fromRow, int fromCol){
        List<Move> moves = new ArrayList<>();
        Piece pawn = state.board.get(fromRow,fromCol);
        int toRow ;
        int toCol ;


        int dir = (pawn.color == Piece.Color.BLACK) ? +1 : -1;   // black moves down words and white up words

        int StartRow  = (pawn.color == Piece.Color.BLACK ) ? 1 : 6;


        // case I 1 block froward
        toRow = fromRow + dir;
        toCol = fromCol;
        if(isInBoundary(toRow,toCol) &&  state.board.isEmpty(toRow,toCol)) {
            moves.add(new Move(fromRow, fromCol, toRow, toCol));

            // case II:  for forward move 2 block
            if (StartRow == fromRow) {
                toRow = fromRow + 2 * dir;
                if (isInBoundary(toRow, toCol) && state.board.isEmpty(fromRow + dir, toCol) && state.board.isEmpty(toRow, toCol)) {
                    moves.add(new Move(fromRow, fromCol, toRow, toCol));
                }
            }

        }

        // case III  for diagonal

        int[] dA = {-1,+1};   // digonal attack.

        for (int d :dA){
            toRow = fromRow + dir;
            toCol = fromCol +d;

            if(!isInBoundary(toRow,toCol)) continue;
            Piece target = state.board.get(toRow,toCol);
            if (target != null && target.color != pawn.color){

                      moves.add(new Move(fromRow,fromCol,toRow,toCol));
            }

        }
        // case IV En passant left for later.


        if(state.enPassantRow != -1){

            if(Math.abs(fromCol - state.enPassantCol)== 1   && fromRow + dir == state.enPassantRow) {   // checking adjacent piece == pawn
                toRow = fromRow + dir;
                toCol = state.enPassantCol;

                if(isInBoundary(toRow,toCol)){
                    moves.add(new Move(fromRow, fromCol,toRow,toCol));
                }

            }
        }
        return moves;
    }
    public List<Move> Knight(GameState state, int fromRow,int fromCol){
        List<Move> moves = new ArrayList<>();
        Piece knight = state.board.get(fromRow,fromCol);

        int toRow, dr;
        int toCol, dc;


        // places where knight can move

        // |       |(-2,-1)|      |(-2,+1)|       |
        // |(-1,-2)|       |      |       |(-1,+2)|
        // |       |       |Knight|       |       |
        // |(+1,-2)|       |      |       |(+1,+2)|
        // |       |(+2,-1)|      |(+2,+1)|       |

        int[][] moveOffset = {{-2,-1},
                              {-2,+1},
                              {-1,-2},
                              {-1,+2},
                              {+1,-2},
                              {+1,+2},
                              {+2,-1},
                              {+2,+1}};


        for(int[] offset : moveOffset){
             dr = offset[0];
             dc = offset[1];

             toRow = fromRow + dr;
             toCol =  fromCol + dc;

            if(!isInBoundary(toRow,toCol)) continue;
            Piece target = state.board.get(toRow,toCol);


            if(  target == null  ||  target.color != knight.color){
                moves.add(new Move(fromRow,fromCol,toRow,toCol));
            }
        }
        return moves;
    }
    public List<Move> Bishop(GameState state, int fromRow, int fromCol){
        List<Move> moves = new ArrayList<>();
        Piece bishop = state.board.get(fromRow,fromCol);

        int toRow,toCol,dr,dc;





        //   \       /      |
        //    \     /       |   (-1,-1)        (-1,+1)
        //     \   /        |
        //       #          |              #
        //     /   \        |
        //    /     \       |   (+1,-1)        (+1,+1)
        //   /       \      |

        int[][] offsetdirMove = {{-1,-1},        {-1,+1},
                                 {+1,-1},        {+1,+1}};


        // 1. check in !boundary  break
        // 2.  isEmpty --> add + continue
        // 3.  enemies --> add + break
        // 4.  friendly--> break


        for(int[] offest : offsetdirMove){
            dr = offest[0];
            dc = offest[1];
            toRow =fromRow;
            toCol =fromCol;

            while (true){
                toRow += dr;
                toCol += dc;
                if(!isInBoundary(toRow,toCol)) break;

                if(state.board.isEmpty(toRow,toCol)){
                    moves.add(new Move(fromRow,fromCol,toRow,toCol));
                    continue;
                }

                Piece target = state.board.get(toRow,toCol);
                // enemy
                if(target.color !=bishop.color){
                    moves.add(new Move(fromRow,fromCol,toRow,toCol));
                }
                // friendly piece
                break;

            }
        }
        return moves;
    }
    public List<Move> Rook(GameState state, int fromRow,int fromCol){
        List<Move> moves = new ArrayList<>();
        Piece rook = state.board.get(fromRow,fromCol);

        int toRow,toCol,dr,dc;


        //        |          |           (-1,0)
        //        |          |
        //  ------#------    |  (0,-1)     R      (0,+1)
        //        |          |
        //        |          |           (+1,0)

        int[][] offsetMove = {       {-1,0},
                               {0,-1},    {0,+1},
                                    {+1,0} };

        for (int[] offset : offsetMove){
            dr = offset[0];
            dc = offset[1];
            toRow = fromRow;
            toCol = fromCol;
            while (true){
                toRow += dr;
                toCol += dc;
                if(!isInBoundary(toRow,toCol)) break;

                if(state.board.isEmpty(toRow,toCol)){
                    moves.add(new Move(fromRow,fromCol,toRow,toCol));
                    continue;
                }

                Piece target = state.board.get(toRow,toCol);
                // enemy
                if(target.color !=rook.color){
                    moves.add(new Move(fromRow,fromCol,toRow,toCol));
                }
                // friendly piece
                break;
            }
        }












        return moves;
    }
    public List<Move> Queen(GameState state, int fromRow,int fromCol){
        List<Move> moves = new ArrayList<>();
        Piece queen = state.board.get(fromRow,fromCol);

        int toRow,toCol,dr,dc;

        // Queen  dir =  Rook + Bishop dir

        int[][] offsetMove = {  {-1,-1},{-1,0},{ -1,+1},
                                { 0,-1},         {0,+1},
                                {+1,-1},{+1,0} ,{+1,+1}};

        for (int[] offset : offsetMove){
            dr = offset[0];
            dc = offset[1];
            toRow = fromRow;
            toCol = fromCol;
            while (true){
                toRow += dr;
                toCol += dc;
                if(!isInBoundary(toRow,toCol)) break;

                if(state.board.isEmpty(toRow,toCol)){
                    moves.add(new Move(fromRow,fromCol,toRow,toCol));
                    continue;
                }

                Piece target = state.board.get(toRow,toCol);
                // enemy
                if(target.color !=queen.color){
                    moves.add(new Move(fromRow,fromCol,toRow,toCol));
                }
                // friendly piece
                break;
            }
        }
        return moves;

    }
    public List<Move> King(GameState state,int fromRow,int fromCol){
        List<Move> moves = new ArrayList<>();
        Piece king = state.board.get(fromRow,fromCol);

        int toRow,toCol,dr,dc;


        int[][] moveToOffset = {  {-1,-1},{-1,0},{ -1,+1},
                                  { 0,-1},         {0,+1},
                                  {+1,-1},{+1,0} ,{+1,+1}};

        for (int[] move : moveToOffset) {
            dr = move[0];
            dc = move[1];

                toRow  = fromRow + dr;
                toCol = fromCol + dc;

                //case I check not in boundary      --> skip + continue
                // case II friendly piece           --> skip + continue
                // case III  square is under attack --> skip+ continue
                // case IV Empty
                // case V enemy pieces




               if(!isInBoundary(toRow,toCol)) continue;
               Piece target = state.board.get(toRow,toCol);

               if(target != null && target.color == king.color) continue;

               // square in under attack skip + continue left.
              Piece.Color oppositeColor  = (king.color != Piece.Color.WHITE)? Piece.Color.WHITE : Piece.Color.BLACK;

               if(isSquareAttacked(state,oppositeColor,toRow,toCol)) continue;

               // empty + enemy
               moves.add(new Move(fromRow,fromCol,toRow,toCol));
        }

        // special case castling
        moves.addAll(castling(state, fromRow, fromCol));



        return moves;
    }

    public List<Move> castling(GameState state, int fromRow,int fromCol){
        List<Move> moves = new ArrayList<>();
          Piece king = state.board.get(fromRow,fromCol);

          if(fromCol != 4) return moves;
          if(state.castlingRights == 0) return moves;

          if(king.color == Piece.Color.WHITE  && fromRow !=7) return moves;
          if(king.color == Piece.Color.BLACK && fromRow !=0) return  moves;

          int toRow;
          int toCol;

          // check color for which we are checking castling

          // if white
               // check white king move -- > false


               // for o-o castling
               // check king side rook move --> false  + also check is rook available at that square??
                            // is btw squares empty ( K  __  __  R )--> true
                            // check king is under attack /check?  --> false
                            // passing square not in attack ?--> true
                            // final square not in attack ?--> true
                    //  then castle

             // for o-o-o castling
             // check Queen side rook move --> false
                     // is btw squares empty ( R  __ __ __  K ) -->  true
                     // check king is under attack              --> false
                     // passing square not in attack            --> true
                     // final square not in attack              --> true




                     //rooks can be under attack not matter.


          if(king.color == Piece.Color.WHITE){   // white
              if(!((state.castlingRights & 0b0011) == 0)){    // return null move
                  // not moved then
                  // for o-o
                  if (state.board.get(fromRow, fromCol + 3) != null) {
                      if (((state.castlingRights & 0b0001) != 0) && (state.board.get(fromRow, fromCol + 3).type == Piece.Type.ROOK) && (state.board.get(fromRow, fromCol + 3).color == Piece.Color.WHITE)) {   // rook not moved then king side
                          if (state.board.isEmpty(fromRow, fromCol + 1) && state.board.isEmpty(fromRow, fromCol + 2)) {  // both are empty
                              // checks king under attack  && passing square under attack  && final square under attack
                              if (!(isSquareAttacked(state, Piece.Color.BLACK, fromRow, fromCol)
                                      || isSquareAttacked(state, Piece.Color.BLACK, fromRow, fromCol + 1)
                                      || isSquareAttacked(state, Piece.Color.BLACK, fromRow, fromCol + 2))) {
                                  // after all rules satisfies then add  castling move
                                  toRow = fromRow;
                                  toCol = fromCol + 2;
                                  moves.add(new Move(fromRow, fromCol, toRow, toCol, true));


                              }
                          }

                      }
                  }

                  // for o-o-o
                  if (state.board.get(fromRow, fromCol - 4) != null) {
                      if (((state.castlingRights & 0b0010) != 0) && (state.board.get(fromRow, fromCol - 4).type == Piece.Type.ROOK) && (state.board.get(fromRow, fromCol - 4).color == Piece.Color.WHITE)) {  // Queen side rook not moved
                          if (state.board.isEmpty(fromRow, fromCol - 1) && state.board.isEmpty(fromRow, fromCol - 2) && state.board.isEmpty(fromRow, fromCol - 3)) {  // btw squares is empty
                              // checks king under attack  && passing square under attack  && final square under attack
                              if (!(isSquareAttacked(state, Piece.Color.BLACK, fromRow, fromCol)
                                      || isSquareAttacked(state, Piece.Color.BLACK, fromRow, fromCol - 1)
                                      || isSquareAttacked(state, Piece.Color.BLACK, fromRow, fromCol - 2))) {
                                  // after all rules satisfies then add  castling move
                                  toRow = fromRow;
                                  toCol = fromCol - 2;
                                  moves.add(new Move(fromRow, fromCol, toRow, toCol, true));

                              }
                          }
                      }
                  }
              }
          }else {  // black
              if (!((state.castlingRights & 0b0011) == 0)) {    // return null move

                  // not moved then


                  // for o-o
                  if ((state.board.get(fromRow, fromCol + 3) != null)) {
                      if (((state.castlingRights & 0b0100) != 0) && (state.board.get(fromRow, fromCol + 3).type == Piece.Type.ROOK) && (state.board.get(fromRow, fromCol + 3).color == Piece.Color.BLACK)) {   // rook not moved then king side
                          if (state.board.isEmpty(fromRow, fromCol + 1) && state.board.isEmpty(fromRow, fromCol + 2)) {  // both are empty
                              // checks king under attack  && passing square under attack  && final square under attack
                              if (!(isSquareAttacked(state, Piece.Color.WHITE, fromRow, fromCol)
                                      || isSquareAttacked(state, Piece.Color.WHITE, fromRow, fromCol + 1)
                                      || isSquareAttacked(state, Piece.Color.WHITE, fromRow, fromCol + 2))) {

                                  // after all rules satisfies then add  castling move
                                  toRow = fromRow;
                                  toCol = fromCol + 2;
                                  moves.add(new Move(fromRow, fromCol, toRow, toCol, true));


                              }
                          }

                      }
                  }

                  // for o-o-o
                  if ((state.board.get(fromRow, fromCol - 4) != null)) {
                      if (((state.castlingRights & 0b1000) != 0) && (state.board.get(fromRow, fromCol - 4).type == Piece.Type.ROOK) && (state.board.get(fromRow, fromCol - 4).color == Piece.Color.BLACK)) {  // Queen side rook not moved
                          if (state.board.isEmpty(fromRow, fromCol - 1) && state.board.isEmpty(fromRow, fromCol - 2) && state.board.isEmpty(fromRow, fromCol - 3)) {  // btw squares is empty
                              // checks king under attack  && passing square under attack  && final square under attack
                              if (!(isSquareAttacked(state, Piece.Color.WHITE, fromRow, fromCol)
                                      || isSquareAttacked(state, Piece.Color.WHITE, fromRow, fromCol - 1)
                                      || isSquareAttacked(state, Piece.Color.WHITE, fromRow, fromCol - 2))) {
                                  // after all rules satisfies then add  castling move
                                  toRow = fromRow;
                                  toCol = fromCol - 2;
                                  moves.add(new Move(fromRow, fromCol, toRow, toCol, true));
                              }
                          }
                      }
                  }
              }
          }
        return moves;
    }


    public boolean isSquareAttacked( GameState state,Piece.Color byColor, int row, int col){
        //1. pawn
        //2. knight
        //3. bishop/ queen  diagonal
        //4. rook / queen straight line
        //5. king

        // if we found even one attack then  don't check other and return true.

        // find the blocks from where it can attack.
        // check out of boundary condition for that block.
        // check is that block have x pieces of color byColor
        //-> not null
        // contain x
        // of color by color
        //  if have then skip + return ture

        // attacking block for used -->  fromRow, fromCol



        return isPawnAttack(state,byColor,row,col)
                || isKnightAttack(state,byColor,row,col)
                || isBishopAttack(state,byColor,row,col)
                ||  isRookAttack(state,byColor,row,col)
                || isKingAttack(state,byColor,row,col);
    }
    public boolean isPawnAttack(GameState state, Piece.Color byColor ,int row ,int col){
        // PAWN
        int PawnDir = (Piece.Color.WHITE == byColor)? -1:+1;
        int[] PawnCol = {-1,1};

        //
        //      (-1,-1)      (-1,+1)               (black side attacks)
        //               #                                                     (from the perspective of block)
        //      (+1,-1)      (+1,+1)               ( white side attacks)
        //



        int fromRow,fromCol;

        for (int d : PawnCol){  // -1 changed the perspective block from attack to that attacking square.
            fromRow = row -PawnDir;    // for reversing
            fromCol = col + d;

            if(!isInBoundary(fromRow,fromCol)) continue;

            Piece blockContain = state.board.get(fromRow,fromCol);
            if( blockContain != null && blockContain.color == byColor  && blockContain.type == Piece.Type.PAWN){
                return true;
            }
        }
        return false;
    }
    public boolean isKnightAttack(GameState state, Piece.Color byColor ,int row ,int col){
        int fromRow,fromCol,dc,dr;

        // places where knight can move

        // |       |(-2,-1)|      |(-2,+1)|       |
        // |(-1,-2)|       |      |       |(-1,+2)|
        // |       |       |Square|       |       |
        // |(+1,-2)|       |      |       |(+1,+2)|
        // |       |(+2,-1)|      |(+2,+1)|       |

        int[][] moveOffset = {     {-2,-1},          {-2,+1},
                            {-1,-2},                        {-1,+2},
                            {+1,-2},                         {+1,+2},
                                   {+2,-1},           {+2,+1}};


        for (int[] offset : moveOffset){
            dr = offset[0];
            dc = offset[1];

            fromRow = row + dr;
            fromCol = col + dc;

            if(!isInBoundary(fromRow,fromCol)) continue;

            Piece blockContain = state.board.get(fromRow,fromCol);
            if( blockContain != null && blockContain.color == byColor  && blockContain.type == Piece.Type.KNIGHT){
                return true;
            }
        }
        return false;
    }
    public boolean isBishopAttack(GameState state, Piece.Color byColor, int row, int col){
        int fromRow,fromCol,dr,dc;
        //   \       /      |
        //    \     /       |   (-1,-1)        (-1,+1)
        //     \   /        |
        //       #          |              #
        //     /   \        |
        //    /     \       |   (+1,-1)        (+1,+1)
        //   /       \      |

        int[][] offsetdirMove = {{-1,-1},        {-1,+1},
                {+1,-1},        {+1,+1}};

        for (int[] offset : offsetdirMove){
            dr = offset[0];
            dc = offset[1];
            fromRow = row;
            fromCol = col;

            while (true){
                fromRow +=dr;
                fromCol +=dc;

                // case 1. !boundary -> break
                // case 2. empty --> continue
                // case 3. contain != null && have piece  +  same color + bishop   --> return true
                // case 4. contain != null && have piece but not same color bishop have(any other piece) -->  break +  loop in that dir.


                //boundary
                if(!isInBoundary(fromRow,fromCol)) break;
                // Piece
                Piece BlockContain = state.board.get(fromRow,fromCol);
                // empty
                if(BlockContain == null) continue;

                // not empty + have piece + same color bishop / Queen
                if((BlockContain.type == Piece.Type.BISHOP || BlockContain.type == Piece.Type.QUEEN )  && BlockContain.color == byColor)return true;
                // not empty + have piece + but not same color bishop
                break;
            }
        }
        return false;
    }
    public boolean isRookAttack(GameState state,Piece.Color byColor , int row,int col){
        int fromRow,fromCol,dr,dc;
        //        |          |           (-1,0)
        //        |          |
        //  ------#------    |  (0,-1)     R      (0,+1)
        //        |          |
        //        |          |           (+1,0)

        int[][] offsetMove = {       {-1,0},
                {0,-1},    {0,+1},
                {+1,0} };
        for (int[] offset : offsetMove){
            dr = offset[0];
            dc = offset[1];
            fromRow = row;
            fromCol = col;

            while (true){
                fromRow +=dr;
                fromCol +=dc;

                // case 1. !boundary -> break
                // case 2. empty --> continue
                // case 3. contain != null && have piece  +  same color + Rook/Queen   --> return true
                // case 4. contain != null && have piece but not same color bishop have(any other piece) -->  break +  loop in that dir.


                //boundary
                if(!isInBoundary(fromRow,fromCol)) break;
                // Piece
                Piece BlockContain = state.board.get(fromRow,fromCol);
                // empty
                if(BlockContain == null) continue;

                // not empty + have piece + same color Rook / Queen
                if((BlockContain.type == Piece.Type.ROOK || BlockContain.type == Piece.Type.QUEEN )  && BlockContain.color == byColor)return true;
                // not empty + have piece + but not same color bishop
                break;
            }
        }
        return false;
    }
    public boolean isKingAttack(GameState state,Piece.Color byColor,int row,int col){
        int fromRow,fromCol,dr,dc;
        int[][] moveToOffset = {  {-1,-1},{-1,0},{ -1,+1},
                { 0,-1},         {0,+1},
                {+1,-1},{+1,0} ,{+1,+1}};

        for (int[] offset: moveToOffset){
            dr = offset[0];
            dc = offset[1];
            fromRow = row + dr;
            fromCol = col + dc;

            if(!isInBoundary(fromRow,fromCol))continue;
            Piece blockContain = state.board.get(fromRow,fromCol);
            // empty
            if(blockContain == null) continue;
            // have piece  + same color king
            if(blockContain.color == byColor  && blockContain.type == Piece.Type.KING)return true;
            // have + not same color king --> check next

        }

        return false;
    }
    public boolean isInBoundary(int row , int col){
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
}
