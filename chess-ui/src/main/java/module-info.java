module com.chess.chessfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jfr;
    requires java.desktop;
    requires com.chess.core;
    requires java.net.http;
    requires javafx.media;


    opens com.chess.chessfx to javafx.fxml;
    exports UI;
    opens UI to javafx.fxml;
}