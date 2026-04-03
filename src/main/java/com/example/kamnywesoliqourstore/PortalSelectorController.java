//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.kamnywesoliqourstore;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PortalSelectorController {
    private static final Logger LOGGER = Logger.getLogger(PortalSelectorController.class.getName());

    @FXML
    private void handleAdminSelect(MouseEvent event) {

        this.navigateTo(event, "login-view.fxml");
    }

    @FXML
    private void handleBranchSelect(MouseEvent event) {
        this.navigateTo(event, "login-view.fxml");
    }

    private void navigateTo(MouseEvent event, String fxmlFile) {
        try {
            Parent root = (Parent)FXMLLoader.load(this.getClass().getResource(fxmlFile));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, (double)900.0F, (double)700.0F));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to: " + fxmlFile, e);
        }

    }
}
