package com.example.kamnywesoliqourstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("portal-selector-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        // ✅ FULLSCREEN SETTINGS
        stage.setFullScreen(true);

// ✅ Show ESC hint (custom message)
        stage.setFullScreenExitHint("Press ESC to exit full screen");

// ✅ Allow ESC key to exit
        stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.valueOf("ESCAPE"));
        stage.show();
    }
}
