package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Pemanggilan FXML dari direktori resources/com/example/view/ yang benar
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 480, 750);
        stage.setTitle("RasaSumatera - Aplikasi Kuliner Wisata");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}