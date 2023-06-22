module com.example.ooplabproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    opens com.example.ooplabproject to javafx.fxml;
    exports com.example.ooplabproject;
}