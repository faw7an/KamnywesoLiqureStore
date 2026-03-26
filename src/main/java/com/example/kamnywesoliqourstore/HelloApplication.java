package com.example.kamnywesoliqourstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Point to your login-view.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));

        // 2. Set the window size (matching your design)
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);

        stage.setTitle("DrinkShop - Distributed Management System");
        stage.setScene(scene);

        // 3. Optional: Center on screen
        stage.centerOnScreen();
        stage.show();
    }
}
