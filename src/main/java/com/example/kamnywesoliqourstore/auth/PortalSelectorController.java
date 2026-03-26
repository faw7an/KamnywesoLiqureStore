package com.example.kamnywesoliqourstore.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortalSelectorController {

    private static final Logger LOGGER = Logger.getLogger(PortalSelectorController.class.getName());

    @FXML
    private void handleAdminSelect(MouseEvent event) {
        navigateTo(event, "/com/example/kamnywesoliqourstore/auth/login-view.fxml");
    }

    @FXML
    private void handleBranchSelect(MouseEvent event) {
        // You can point this to login or a specialized branch portal entry
        navigateTo(event, "/com/example/kamnywesoliqourstore/auth/login-view.fxml");
    }

    private void navigateTo(MouseEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to: " + fxmlFile, e);
        }
    }
}