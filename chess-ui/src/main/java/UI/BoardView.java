package UI;

import engine.GameState;
import engine.Move;
import engine.Piece;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.util.List;


public class BoardView extends StackPane {



    public  static final  int SIZE = 8;
    public static final int TILE_SIZE = 90;



    private final Color Light = Color.BEIGE;
    private final   Color Dark = Color.SADDLEBROWN;
    private final Color FirstClickColor = Color.color(0.133, 0.161, 0.161, 1);
    private final Color optionsColor = Color.color(0.141, 0.949, 0.451, 1);
    private List<Move> highLightedMoves;

    private final GridPane gridBoard;
    private final Square[][] squares = new Square[SIZE][SIZE];


    // store first click.  --> if (-1) not selected any square
    private  int selectedRow = -1;
    private int selectedCol = -1;

    public StackPane promotionLayer;
    EndGame EndLayer;



    ImageLoader imageLoader;
    UIController controller;



    public BoardView(BoardLayout boardLayout){
        this.gridBoard = new GridPane();
        this.gridBoard.setPrefSize(TILE_SIZE*SIZE ,TILE_SIZE*SIZE);
        this.gridBoard.setMaxSize(TILE_SIZE *SIZE , TILE_SIZE*SIZE);
        this.gridBoard.setMinSize(TILE_SIZE*SIZE,TILE_SIZE*SIZE);

        createPromotionLayer();
        controller = new UIController(boardLayout);

        EndLayer= new EndGame(controller.state, this);
        imageLoader = new ImageLoader();

        createBoard();
        drawPieces();
        this.getChildren().addAll(gridBoard, promotionLayer,EndLayer);
    }



    public GridPane getGridBoard(){
        return gridBoard;
    }
    public void createBoard() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
               Square square = new Square(row,col);

                square.setOnMouseClicked(e -> {
                    onSquareClick(square);
                });

              square.setPrefSize(TILE_SIZE, TILE_SIZE);



                boolean isLight = (row + col) % 2 == 0;
                Color color = isLight ? Light : Dark;
                square.setBackground(new Background(new BackgroundFill(color, null, Insets.EMPTY)));
                squares[row][col] = square;
                gridBoard.add(square, col, row);
            }
        }
    }

    void onSquareClick(Square square){
        int engRow = controller.engRow(square.uiRow);
        int engCol  = controller.engCol(square.uiCol);

        handleClick(engRow,engCol);
    }
    public void handleClick(int engR , int engC){

      if( selectedRow == -1){  // first click

          if(!controller.isCorrectTurn(engR,engC)) return;

          selectedRow = engR;
          selectedCol = engC;

         highLightSelectedSquare();
         highLightMoves(engR,engC); // highLight options

      }else {  // second click    here moves applying happens
          if (engR == selectedRow && engC == selectedCol) return;// if same click then do nothing.
          // case I selected move  ==  legal move then  clear + redraw  +  reset selected r,c
          if (highLightedMoves != null) {
              for (Move m : highLightedMoves) {
                  if (m.toRow == engR && m.toCol == engC) {


                      // promotion check and show layer
                      m.promotion = (controller.state.isPawnReachLastRow(m));
                      if (m.promotion){
                          promotionOverLay(m);
                          return ;  //stop here if promotion
                      }

                      ApplyMoveInUI(m);




                      selectedRow = -1;
                      selectedCol = -1;



                      return;
                  }
              }
          }

          // if selected move not legal move then select that one and make it selected move

          ClearHighlights();
          highLightedMoves = null;
          if (controller.isCorrectTurn(engR, engC)) {
              selectedCol = engC;
              selectedRow = engR;
              highLightSelectedSquare();
              highLightMoves(selectedRow, selectedCol);
              return;
          }
          selectedRow = -1;
          selectedCol = -1;

      }

    }

    public  void ApplyMoveInUI(Move m){

        if(controller.multiplayerMode) {
            // in multiplayer pass to network
            System.out.println("move req: "+controller.state.convertInString(m));
            controller.networkManager.send("MOVE:"+ controller.state.convertInString(m));

        }else{
            controller.state.ApplyMove(m);
            EndingSetUp(false);
            System.out.println("Move: " + controller.state.convertInString(m));
            String fen1 = controller.state.createFEN();
            System.out.println("fen1: " + fen1);

            ClearHighlights();
            drawPieces();
        }
    }


    public void EndingSetUp( boolean timeOutWin){

        controller.state.currentStatues = controller.state.GameStatus(controller.state.turn);
        if (controller.state.currentStatues != GameState.states.CONTINUE) {
            EndLayer.finalCenterBox(controller.state.currentStatues, controller.state.REASON);
        }

        if(timeOutWin){
            if(controller.playerColor == UIController.Color.WHITE){
                controller.state.currentStatues = GameState.states.WHITE_WINS;
            }else{
                controller.state.currentStatues = GameState.states.BLACK_WINS;
            }
            controller.state.REASON = "MATCH ABORTED";
            EndLayer.finalCenterBox(controller.state.currentStatues, controller.state.REASON);
        }
    }




    public void highLightMoves(int engRow , int engCol){
        if(!controller.isCorrectTurn(engRow,engCol)) return;
        highLightedMoves = controller.getLegalMoves(engRow,engCol);
        if(highLightedMoves == null) return;
        for (Move move : highLightedMoves){
            int Rx = controller.uiRow(move.toRow);      // engine
            int Cx = controller.uiCol(move.toCol);

            squares[Rx][Cx].setBorder(new Border(new BorderStroke(optionsColor,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
        }
    }
    public void highLightSelectedSquare(){

        if(!controller.isCorrectTurn(selectedRow,selectedCol)) return;
        int uiR = controller.uiRow(selectedRow);      // engine
        int uiC = controller.uiCol(selectedCol);

        squares[uiR][uiC].setBorder(new Border(new BorderStroke(FirstClickColor,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
    }

    public  void flipper(){
        selectedRow = -1;
        selectedCol = -1;
        highLightedMoves = null;
        ClearHighlights();
        controller.CurrentPOV = ( controller.CurrentPOV == UIController.POV.WHITE )? UIController.POV.BLACK : UIController.POV.WHITE;
        drawPieces();
    }

    public void ClearHighlights(){
        for(int row = 0 ; row < 8 ; row++){
            for(int col = 0;  col < 8 ; col++){
                squares[row][col].setBorder( null);
            }
        }
    }
    public void drawPieces(){
        int vr,vc;
         // clear
        for(int row = 0 ; row <= 7; row++){
            for(int col = 0 ; col <= 7; col++){
                squares[row][col].getChildren().clear();
            }
        }


        //
        for(int row = 0 ; row <= 7; row++){
            for(int col = 0 ; col <= 7; col++){
                Piece p = controller.state.board.get(row, col);
                if(p == null) continue;

                vr = controller.uiRow(row);
                vc = controller.uiCol(col);

                String color = (p.color == Piece.Color.WHITE)? "white" : "black";
                String type = p.type.name().toLowerCase();
                String side = controller.getImageSide(p);

                ImageView imageView = new ImageView(imageLoader.PieceImage.get(color + "_" + type + "_" + side ));

                imageView.setFitWidth(TILE_SIZE  * 0.85);
                imageView.setFitHeight(TILE_SIZE * 0.85);
                imageView.setPreserveRatio(true);

                squares[vr][vc].getChildren().add(imageView);
            }
        }







    }

    public void createPromotionLayer(){    // static
        promotionLayer = new StackPane();
        promotionLayer.setVisible(false);
        promotionLayer.setPickOnBounds(true);
        promotionLayer.setMouseTransparent(false);
        promotionLayer.setAlignment(Pos.CENTER);

        promotionLayer.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);

        promotionLayer.setStyle("""
                -fx-background-color: rgba(0,0,0,0.6);
                """);
    }
    public void promotionOverLay(Move m){  //dynamic
         promotionLayer.getChildren().clear();

        VBox promotionBox = createPromotionBox(m);

        promotionLayer.getChildren().add(promotionBox);
        promotionLayer.setVisible(true);
        drawPieces();

        gridBoard.setDisable(true);

    }
    public VBox createPromotionBox(Move m){
        VBox promotionBox = new VBox(10);
        promotionBox.setStyle("""
                -fx-background-color:rgba(0,0,0,0.6);
                -fx-padding: 15;
                -fx-border-color: black;
                -fx-border-width: 2;
                
               
                """);
        promotionBox.setAlignment(Pos.CENTER);
        promotionBox.setMaxSize(300,300);
        Piece p = controller.state.board.get(m.fromRow, m.fromCol);
        String basePath =p.color== Piece.Color.WHITE ? "/pieces/white_" : "/pieces/black_";

        ImageView queen  = new ImageView(new Image(getClass().getResourceAsStream(basePath + "queen_front.png")));
        ImageView rook   = new ImageView(new Image(getClass().getResourceAsStream(basePath + "rook_front.png")));
        ImageView bishop = new ImageView(new Image(getClass().getResourceAsStream(basePath + "bishop_front.png")));
        ImageView knight = new ImageView(new Image(getClass().getResourceAsStream(basePath + "knight_front.png")));

        queen.setFitHeight(70);
        queen.setFitWidth(70);
        queen.setPreserveRatio(true);

        rook.setFitHeight(70);
        rook.setFitWidth(70);
        rook.setPreserveRatio(true);


        bishop.setFitHeight(70);
        bishop.setFitWidth(70);
        bishop.setPreserveRatio(true);

        knight.setFitHeight(70);
        knight.setFitWidth(70);
        knight.setPreserveRatio(true);



        queen.setOnMouseClicked(mouseEvent -> {
            m.promotedTO = Piece.Type.QUEEN;
            ApplyMoveInUI(m);
            promotionLayer.setVisible(false);
            gridBoard.setDisable(false);
            drawPieces();
        });

        rook.setOnMouseClicked(mouseEvent -> {

            m.promotedTO = Piece.Type.ROOK;
            ApplyMoveInUI(m);
            promotionLayer.setVisible(false);
            gridBoard.setDisable(false);
            drawPieces();
        });
        bishop.setOnMouseClicked(mouseEvent -> {

            m.promotedTO = Piece.Type.BISHOP;
            ApplyMoveInUI(m);
            promotionLayer.setVisible(false);
            gridBoard.setDisable(false);
            drawPieces();
        });
        knight.setOnMouseClicked(mouseEvent -> {
            m.promotedTO = Piece.Type.KNIGHT;
            ApplyMoveInUI(m);
            promotionLayer.setVisible(false);
            gridBoard.setDisable(false);
            drawPieces();
        });

        promotionBox.getChildren().addAll(queen,rook,bishop,knight);

        return promotionBox;
    }
}
