package UI;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;

import javafx.geometry.Pos;


import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class MenuWindow  extends StackPane {


    BoardLayout boardLayout;


     Glow glow = new Glow();


     // background

     Pane backGround;
     BorderPane mainLayer;
     StackPane centerBlock;
     StackPane Animation;

     Media media;
     MediaPlayer mediaPlayer;
     MediaView mediaView;


     Floating_Text floatingText;


     BorderPane topBar;

    // Pane settingLayer ;
     JoinRoomLayer joiningRoomLayer;// when clicked on multiplayer this appears
     VBox TopRight;

     ScreenManager screenManager;




     // title ton top
     // animation in center
     //  2 Mode card at to bottom  player vs  player , multiplayer
     //  setting icon are right corner






    // root where I gonna have layer
    // 1. background layer ( filled with  xyz)
    // 2 center 2nd layer
    //     title text
    //     center for animation
    //     bottom 2 images pane modes


    //  meddle 3rd layer
    // 4. top-right settings icon  and after click settings appears


    // top 4th layer when clicked multiplayer
    // joining entre code for popup.


    // player vs player normal chessBoard  for now later it's will be bot vs bot.

     MenuWindow(ScreenManager screenManager){
        this.screenManager = screenManager;

        joiningRoomLayer = new JoinRoomLayer(this);
        boardLayout = new BoardLayout(screenManager,joiningRoomLayer);

        backGround = new Pane();
        mainLayer = new BorderPane();
        Animation = new StackPane();
        centerBlock = new StackPane();
        floatingText = new Floating_Text("CHESS WARS");
        this.setOnMouseMoved(e->{
            floatingText.updateMouse(e.getX(),e.getY());
        });
        floatingText.setMouseTransparent(true);
        topBar = new BorderPane();


        joiningRoomLayer.setMouseTransparent(true);

//        settingLayer = new Pane();
//        settingLayer.setMouseTransparent(true);
//        settingLayer.setVisible(false);

         // #fae4c3
         // #011008

        backGround.setStyle("""
                -fx-background-color: #0a0009;
                """);
        mainLayer.setVisible(true);
        CreateMainPane();
        this.getChildren().addAll(backGround, mainLayer, floatingText, joiningRoomLayer);

     }



     public void CreateMainPane(){
       Region space = new Region();
       space.setPrefHeight(100);

         topBar.setCenter(space);

         TopRight = joiningRoomLayer.RightTopbar( boardLayout.chessboard.controller.state.InRoom, boardLayout.chessboard.controller.state.RoomID);
         topBar.setRight(TopRight);  // for now
         mainLayer.setTop(topBar);

         // animation
         addAnimation();










          // bottomLayer
          HBox modesCard = new HBox(400);
          modesCard.setAlignment(Pos.CENTER);
          modesCard.setPadding(new Insets(50));

          ImageView passByPass = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Pieces/player_vs_player2.jpg"))));
          passByPass.setFitWidth(320);
          passByPass.setFitHeight(180);
          passByPass.setPreserveRatio(true);
          ImageView multiplayerImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Pieces/multiplayer.jpg"))));
          multiplayerImage.setFitHeight(180);
          multiplayerImage.setFitWidth(320);
          multiplayerImage.setPreserveRatio(true);

          VBox PlayerVsPlayer = CreateCardModes( passByPass, "Player vs Player ");

          PlayerVsPlayer.setOnMouseClicked(e->{
              animationStopper();
              boardLayout.chessboard.controller.startFreshGame(true);
          });
          VBox multiplayer   = CreateCardModes(multiplayerImage, "Multi player match");


          multiplayer.setOnMouseClicked(e->{
                 if(!boardLayout.chessboard.controller.state.InRoom){
                     animationStopper();
                     joiningRoomLayer.middleLayer.setTranslateY(-90);
                     joiningRoomLayer.ShowJoinRoomPop();
                 }else{
                     animationStopper();
                     joiningRoomLayer.middleLayer.setTranslateY(-80);
                     joiningRoomLayer.createControlPanel(boardLayout.chessboard.controller.state.RoomID);
                 }

          });





          modesCard.getChildren().addAll(PlayerVsPlayer,multiplayer);
          mainLayer.setBottom(modesCard);

     }
     public VBox CreateCardModes(ImageView image, String modeName){
          // pass
          VBox card = new VBox(10);


         StackPane modeImgSection = new StackPane();
         modeImgSection.setPrefSize(320, 180);
         modeImgSection.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
         card.setAlignment(Pos.CENTER);


         modeImgSection.setStyle("""
                 -fx-background-color: #0f1f1f;
                 -fx-background-radius : 15;
                 -fx-border-color: rgba(255,255,255,0.15);
                 -fx-border-radius: 15;
                 -fx-border-width:  2;
                 
                 """);


         Rectangle clip = new Rectangle(320,180);
         clip.setArcWidth(30);
         clip.setArcHeight(30);
         image.setClip(clip);



         //mouse handling
         ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
         scaleUp.setToX(1.03);
         scaleUp.setToY(1.03);


         ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150),card);
         scaleDown.setToX(1);
         scaleDown.setToY(1);


         ScaleTransition Shrink = new ScaleTransition(Duration.millis(150),card);
         Shrink.setToX(0.95);
         Shrink.setToY(0.95);

         ScaleTransition Expand= new ScaleTransition(Duration.millis(150), card);
         Expand.setToX(1);
         Expand.setToY(1);


         card.setOnMouseEntered(e->{
             scaleDown.stop();
             scaleUp.playFromStart();

         });

         card.setOnMouseExited(e->{
             scaleUp.stop();
             scaleDown.playFromStart();
         });


         card.setOnMouseClicked(e->{
             Shrink.playFromStart();
         });

         Shrink.setOnFinished(e->{
             Expand.playFromStart();
         });






         // text
         Label modeType = new Label(modeName);
         modeType.setFont(Font.font("MV boli",FontWeight.BOLD,18));
         modeType.setStyle("""
                 -fx-text-fill :  white;
                 -fx-effect : dropshadow(gaussian,cyan,20,0.4,2,2);
                 """);


         glow.setLevel(0.3);
         modeType.setEffect(glow);


         modeImgSection.getChildren().add(image);
         card.getChildren().addAll(modeImgSection,modeType);
         return card;
     }

     public void addAnimation(){
         // vedio resolution : width: 640  , height: 360
         media = new Media(getClass().getResource("/video/animation.mp4").toExternalForm());
         mediaPlayer = new MediaPlayer(media);
         mediaView = new MediaView(mediaPlayer);



         mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
         mediaPlayer.setMute(false);
         mediaPlayer.setVolume(0.1);
         mediaPlayer.play();


         mediaView.setFitWidth(640);
         mediaView.setFitHeight(360);
         mediaView.setPreserveRatio(true);

         // center animation
         Animation.setPrefSize(640,360);
         Animation.setMaxSize(640,360);
         Animation.setStyle("""
                 -fx-background-color: #0f1f1f;
                 -fx-background-radius : 15;
                 -fx-border-color: rgba(255,255,255,0.15);
                 -fx-border-radius: 0;
                 -fx-border-width:  2;
                 
                 """);
         Animation.getChildren().add(mediaView);
         centerBlock.setAlignment(Pos.CENTER);
         centerBlock.getChildren().add(Animation);
         mainLayer.setCenter(centerBlock);
     }


     public void animationStopper(){
         mediaPlayer.stop();
         centerBlock.getChildren().clear();
     }

     public void setAnimation(){
         mediaPlayer.play();
         centerBlock.getChildren().add(Animation);
     }
}
