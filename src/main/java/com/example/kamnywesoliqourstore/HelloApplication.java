package com.example.kamnywesoliqourstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/kamnywesoliqourstore/auth/portal-selector-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 560, 600);
        stage.setTitle("Kamnyweso Liquor Store");
        stage.setScene(scene);
        stage.show();
    }
}
