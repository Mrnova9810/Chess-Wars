package UI;

public class Launcher {
    public static void main(String[] args) {
        ChessApp.main(args);  // delegates to your real app
    }

//    jpackage
//     --type app-image
//     --name ChessGame
//     --input target
//     --main-jar ChessApp.jar
//     --main-class UI.Launcher
//     --icon ..\icon\chess.ico
//     --win-shortcut --win-menu
}