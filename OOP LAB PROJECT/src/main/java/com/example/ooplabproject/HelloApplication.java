package com.example.ooplabproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import javafx.scene.image.ImageView;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        AnchorPane root=fxmlLoader.load();
        Scene scene = new Scene(root, 1000, 700);scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        stage.setTitle("AUDIO VIDEO PLAYER");
        stage.getIcons().add(new Image("https://www.shareicon.net/data/128x128/2016/08/18/809278_multimedia_512x512.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do you Wish to close");
            alert.setHeaderText("Developed By 22K-8735");
            alert.setContentText("Close or Cancel");
            ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            DialogPane dialogPane = alert.getDialogPane();
            ImageView view=new ImageView(new Image("https://www.shutterstock.com/image-vector/cross-red-icon-amazing-vector-260nw-2284813447.jpg"));
            view.setFitWidth(30);view.setFitHeight(30);
            dialogPane.setGraphic(view);
            dialogPane.getStyleClass().add("my-dialog-pane");
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
            alert.getButtonTypes().setAll(closeButtonType, cancelButtonType);
            if(alert.showAndWait().orElse(cancelButtonType) == closeButtonType)
            {
                stage.close();
            }
            else {
                windowEvent.consume();
            }

        });
    }
    public static void main(String[] args) {
        launch();
    }
}