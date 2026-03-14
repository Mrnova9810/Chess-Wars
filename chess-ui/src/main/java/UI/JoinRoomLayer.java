package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.Random;





public class JoinRoomLayer extends Pane {

MenuWindow menuWindow;
VBox middleLayer;




// Join ROOM
HBox joinRoomBox ;
Label joinRoomLabel;

HBox nameBox;
Label yourNameLabel;
TextField yourNameArea ;

Button CreateRoom;
Button JoinRoom;





TextField RoomIdTextArea;
HBox textBox;

boolean joinReqSended = false;






// labels for control panel --> in multiplayer.
    Label controlPanelLabel;
    HBox titleBox;
    Label statusLabel;




    Label roomCodeLabel;
    Label matchLabel;




    HBox ClockInputBOX;
    Label inputTimerLabel;
    TextField inputTime_in_mins;


    HBox buttonBox;
    Button leaveRoomBtn;
    Button startBtn;

    // player color chooser
    HBox ColorBox;
    Label yourColorLabel;
    Region space;
    Button colorFlipperBtn;
    //btn left + right







boolean showTextArea = false;

    JoinRoomLayer( MenuWindow menuWindow){
        this.menuWindow = menuWindow;
        this.setMouseTransparent(true);
        this.setVisible(false);
        this.setStyle("""
               -fx-background-color: rgba(0,0,0,0.6);
               """);
        // center part
        middleLayer  = new VBox(20);

        middleLayer.setPadding(new Insets(80,100,100,80));
        middleLayer.setPrefSize(550,400);
        middleLayer.setLayoutX(380);
        middleLayer.setLayoutY(80);

        middleLayer.setStyle("""
              -fx-background-color: #050505;
              -fx-border-radius : 20;
              -fx-border-width: 3;
              -fx-border-color: white;
              -fx-background-radius : 20;
              -fx-effect: dropshadow(gaussian,#148a99,20,0.4,0,0);
              """);



        // for joining room components
       joinRoomBox = new HBox();
       joinRoomLabel = new Label();
       nameBox = new HBox();
       yourNameLabel = new Label("Enter Alias: ");
       yourNameArea = new TextField();

       CreateRoom = new Button("CREATE ROOM ");
       JoinRoom = new Button("JOIN ROOM:  ");
       RoomIdTextArea  = new TextField();
       textBox = new HBox(50);






        controlPanelLabel = new Label();

        statusLabel = new Label();
        yourColorLabel = new Label();
        roomCodeLabel = new Label();
        matchLabel = new Label();
        inputTimerLabel = new Label();
        inputTime_in_mins = new TextField();
        ClockInputBOX = new HBox();
        startBtn = new Button("Ready");

        ColorBox = new HBox();
        space = new Region();
        HBox.setHgrow(space,Priority.ALWAYS);
        colorFlipperBtn = new Button();
        colorFlipperBtn.setDisable(true);




      








       CreateRoom.getStyleClass().add("neon-button");
       JoinRoom.getStyleClass().add("neon-button");
       textBox.getChildren().add(JoinRoom);





        this.getChildren().add(middleLayer);

    }

    // in 30 mins adding room layer, in top corner of main panel.




    public void ShowJoinRoomPop(){
        this.setVisible(true);
        this.setMouseTransparent(false);



        middleLayer.getChildren().clear();
        textBox.getChildren().remove(RoomIdTextArea);
        nameBox.getChildren().clear();
        joinRoomBox.getChildren().clear();


        // TOP center label
        joinRoomLabel.setText("⚔ ENTER THE ARENA ⚔");
        joinRoomLabel.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: white;
                -fx-font-size: 25px;
                -fx-effect : dropshadow(gaussian,#4c64b3,20,0.4,3,3);
                """);

        joinRoomBox.setAlignment(Pos.CENTER);
        joinRoomBox.setPadding(new Insets(0,0,15,0));
        joinRoomBox.getChildren().add(joinRoomLabel);

        // name
        yourNameLabel.getStyleClass().add("neon-label");
        nameBox.getChildren().addAll(yourNameLabel,yourNameArea);



        middleLayer.getChildren().addAll(joinRoomBox, nameBox, CreateRoom,textBox);

        CreateRoom.setOnAction(e->{
            if(!yourNameArea.getText().isEmpty()){
                menuWindow.setAnimation();
                menuWindow.boardLayout.chessboard.controller.firstPerson = yourNameArea.getText();
            }else return;

            //  we gonna passing  we generate room for random number--> room id
            // pass that number to sever that will create room and store it uses.
            // pop up disappear.
            // status mark will show up joined  + room id.

        // 1.room Id create
        // set room id

        // dummy id

        menuWindow.boardLayout.chessboard.controller.state.RoomID =  generateCode();
        menuWindow.boardLayout.chessboard.controller.networkManager.send("JOIN:" +menuWindow.boardLayout.chessboard.controller.state.RoomID);
        menuWindow.boardLayout.chessboard.controller.state.InRoom = true;



        menuWindow.TopRight.getChildren().clear();
        menuWindow.TopRight.getChildren().add(RightTopbar(menuWindow.boardLayout.chessboard.controller.state.InRoom, menuWindow.boardLayout.chessboard.controller.state.RoomID));

        this.setVisible(false);
        this.setMouseTransparent(true);
        });
        JoinRoom.setOnAction(e->{


            // in first show text area
            // user enter the code,
            // we pass to server.
            // server check's it and inform
            // failed / successful joining,
            // we update in status marker. in main manu + code
            if(!showTextArea){   // show text area
                textBox.getChildren().add(RoomIdTextArea);
                showTextArea = true;
            }else if(!RoomIdTextArea.getText().isEmpty()){  // exsit from pop up
                 // check from server room exist or not.
                 // if exist

                if(!yourNameArea.getText().isEmpty()){
                    menuWindow.boardLayout.chessboard.controller.firstPerson = yourNameArea.getText();
                    menuWindow.setAnimation();
                }else return;

                menuWindow.boardLayout.chessboard.controller.state.RoomID = RoomIdTextArea.getText();
                menuWindow.boardLayout.chessboard.controller.state.InRoom = true;
                menuWindow.boardLayout.chessboard.controller.networkManager.send("JOIN:" +menuWindow.boardLayout.chessboard.controller.state.RoomID);


                menuWindow.TopRight.getChildren().clear();
                menuWindow.TopRight.getChildren().add(RightTopbar(menuWindow.boardLayout.chessboard.controller.state.InRoom, menuWindow.boardLayout.chessboard.controller.state.RoomID));
                this.setVisible(false);
                this.setMouseTransparent(true);
            }

        });

    }






    public VBox RightTopbar(boolean InRoom, String RoomCode){
//         // top right corner setting
//         Button settingBtn = new Button();
//         ImageView settingIcon = new ImageView(new Image(getClass().getResourceAsStream("/Pieces/settings.png")));
//         settingIcon.setFitHeight(20);
//         settingIcon.setFitWidth(20);
//
//         settingIcon.setPreserveRatio(true);
//         settingBtn.setGraphic(settingIcon);
//
//         settingBtn.setStyle("""
//                  -fx-background-color : transparent;
//                  """);
//         settingBtn.setOnAction(e->{
//             // pop up setting layer
//         });
//         settingBtn.setOnMouseEntered(e-> {
//                     settingBtn.setStyle("""
//                               -fx-background-color: rgba(255,255,255,0.1); """);});
//         settingBtn.setOnMouseExited(e->{
//                     settingBtn.setStyle("""
//                          -fx-background-color: transparent;
//                          """);});

        // making this right as with VBox

        VBox rightTopsideBar = new VBox();
        VBox statusUI = new VBox();
        statusUI.setStyle("""
                 -fx-background-color: black;
                 -fx-border-radius : 10;
                 -fx-border-width : 3;
                 """);

        statusUI.setMaxSize(150,150);

        if(InRoom){
            rightTopsideBar.setVisible(true);
            // show join room status  + code

            Label statuesLabel = new Label("Status : In ROOM"  );

            Label roomCodeLabel = new Label( "Room ID: "  + RoomCode);

            String style = """
                -fx-text-fill:  white;
                -fx-padding:6 18;
                """;

            statuesLabel.setStyle(style);
            roomCodeLabel.setStyle(style);

            statusUI.getChildren().addAll(statuesLabel,roomCodeLabel);

            rightTopsideBar.getChildren().add(statusUI);

            // rightTopsideBar.getChildren().add(settingBtn);
        }
        return rightTopsideBar;
    }


    public void createControlPanel(String roomCode){
        middleLayer.getChildren().clear();
        ColorBox.getChildren().clear();

        this.setVisible(true);
        this.setMouseTransparent(false);
        // thing to add
        // 1. control panel  (top label)
        // 2. labels
              //--> show status   --> 1. In waiting... (1members) ,   ||2. ready to go .. (2 members)
              //--> match  :    [_____]     vs     [______]
              //--> timer :
              //--> leave btn
              // --> ready & start btn

        controlPanelLabel.setText("CONTROL PANEL");
        controlPanelLabel.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: white;
                -fx-font-size: 25px;
                -fx-effect : dropshadow(gaussian,#4c64b3,20,0.4,3,3);
                
                """);

        titleBox = new HBox(controlPanelLabel);
        titleBox.setAlignment(Pos.TOP_CENTER);     // in side there children will go to be aligned to center



        statusLabel.setText("Status : " + menuWindow.boardLayout.chessboard.controller.roomStatus);
        roomCodeLabel.setText("Room ID: " + roomCode );
        matchLabel.setText("Match B/W: "+   menuWindow.boardLayout.chessboard.controller.firstPerson + " Vs " + menuWindow.boardLayout.chessboard.controller.secondPerson);

        // color choosing btn flipper
        // left & right side
         ColorBox.setAlignment(Pos.BASELINE_LEFT);

         yourColorLabel .setText("YourColor : " + menuWindow.boardLayout.chessboard.controller.playerColor);
         colorFlipperBtn.setMinSize(60,20);


         ColorBox.getChildren().addAll(yourColorLabel,space,colorFlipperBtn);
         colorFlipperBtn.setOnAction(e->{
             UIController.Color playerColor;
              if(menuWindow.boardLayout.chessboard.controller.playerColor == UIController.Color.WHITE){
                  playerColor = UIController.Color.BLACK;
              }else if(menuWindow.boardLayout.chessboard.controller.playerColor == UIController.Color.FATE_DECIDE){
                  playerColor = UIController.Color.WHITE;
              }else{
                  playerColor= UIController.Color.FATE_DECIDE;
              }
              menuWindow.boardLayout.chessboard.controller.networkManager.send("MyColor:" +  playerColor);
             // send message to server...

         });







        inputTimerLabel.setText("Set Timer :");

        ClockInputBOX = new HBox();
        ClockInputBOX.getChildren().addAll(inputTimerLabel , inputTime_in_mins);



        // styling of Labels
        statusLabel.getStyleClass().add("neon-label");
        roomCodeLabel.getStyleClass().add("neon-label");
        matchLabel.getStyleClass().add("neon-label");
        yourColorLabel.getStyleClass().add("neon-label");
        inputTimerLabel.getStyleClass().add("neon-label");





        // start & leave btn
        startBtn = new Button("Ready");
        if(menuWindow.boardLayout.chessboard.controller.roomStatus == UIController.RoomStatus.IN_WAITING){startBtn.setDisable(true);}
        else {startBtn.setDisable(false);}


        startBtn.setOnAction(e->{
            // ask for match req from server --> player ready
            if(!joinReqSended) {
                menuWindow.boardLayout.chessboard.controller.networkManager.send("READY");
                startBtn.setText("Awaiting Opponent...");
                joinReqSended = true;
            }
        });
        leaveRoomBtn = new Button("Leave");
        leaveRoomBtn.setOnAction(e->{
            menuWindow.setAnimation();
            menuWindow.boardLayout.chessboard.controller.state.InRoom =false;
            menuWindow.boardLayout.chessboard.controller.state.RoomID ="";
            joinReqSended = false;

            this.setVisible(false);
            this.setMouseTransparent(true);


            menuWindow.TopRight.getChildren().clear();
            menuWindow.TopRight.getChildren().add(RightTopbar(menuWindow.boardLayout.chessboard.controller.state.InRoom, menuWindow.boardLayout.chessboard.controller.state.RoomID));
            menuWindow.boardLayout.chessboard.controller.secondPerson = "[ __________ ]";
            menuWindow.boardLayout.chessboard.controller.multiplayerMode =false;
            middleLayer.getChildren().clear();
            menuWindow.boardLayout.chessboard.controller.networkManager.send("LEAVE_ROOM");

            showTextArea =false;


            menuWindow.boardLayout.chessboard.controller.roomStatus = UIController.RoomStatus.IN_WAITING;
            menuWindow.boardLayout.chessboard.controller.playerColor = UIController.Color.FATE_DECIDE;
            startBtn.setText("READY");
            startBtn.setDisable(false);
            colorFlipperBtn.setDisable(true);
            RoomIdTextArea.clear();
        });

        startBtn.getStyleClass().add("neon-button");
        leaveRoomBtn.getStyleClass().add("neon-button");





        buttonBox = new HBox(100);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(leaveRoomBtn ,startBtn);

        middleLayer.setAlignment(Pos.BASELINE_LEFT);
        middleLayer.getChildren().addAll(titleBox,matchLabel,statusLabel,roomCodeLabel,ColorBox,ClockInputBOX, buttonBox);


    }


    public void updateUI() {
        matchLabel.setText("Match B/W: "+   menuWindow.boardLayout.chessboard.controller.firstPerson + " Vs " + menuWindow.boardLayout.chessboard.controller.secondPerson);
        statusLabel.setText("Status : " + menuWindow.boardLayout.chessboard.controller.roomStatus);
        roomCodeLabel.setText("Room ID: " + menuWindow.boardLayout.chessboard.controller.state.RoomID);

        yourColorLabel.setText("Selected Side:" +menuWindow.boardLayout.chessboard.controller.playerColor);
        switch ( menuWindow.boardLayout.chessboard.controller.playerColor) {
            case WHITE:
                //colorFlipperBtn.setText("WHITE");
                colorFlipperBtn.setStyle("""
                        -fx-background-color: white;
                        -fx-border-color: #00f5ff;
                       
                        """);
                break;

            case BLACK:
                //colorFlipperBtn.setText("BLACK");
                colorFlipperBtn.setStyle("""
                        -fx-background-color: black;
                        -fx-border-color: #00f5ff;
                        """);
                break;

            case FATE_DECIDE:
                //colorFlipperBtn.setText("RANDOM");
                colorFlipperBtn.setStyle("""
                         -fx-background-color: gray;
                         -fx-border-color: #00f5ff;
                        """);
                break;
        }
    }





    public static String generateCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }


}
