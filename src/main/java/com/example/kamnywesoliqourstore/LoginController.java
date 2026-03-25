package com.example.kamnywesoliqourstore;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private ComboBox<String> branchComboBox;
    @FXML private TextField staffIdField;
    @FXML private PasswordField passwordField;

    /**
     * This method runs automatically when the FXML is loaded.
     * It populates the branch dropdown to match your design specs.
     */
    @FXML
    public void initialize() {
        branchComboBox.getItems().addAll("Nairobi HQ", "Nakuru", "Mombasa", "Kisumu");
        // Set a default selection to look professional on load
        branchComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Handles the "Sign In" button click.
     * Currently configured with the 'admin' bypass for testing.
     */
    @FXML
    private void handleSignIn(ActionEvent event) {
        String branch = branchComboBox.getValue();
        String staffId = staffIdField.getText();
        String password = passwordField.getText();

        // Simple validation logic
        if (staffId.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Form Error!", "Please enter your Staff ID and Password.");
            return;
        }

        // Test credentials: Staff ID 'admin' and Password '1234'
        if (staffId.equals("admin") && password.equals("1234")) {
            System.out.println("Login Successful for Branch: " + branch);
            navigateToBranchPortal(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Staff ID or Password.");
        }
    }

    private void navigateToBranchPortal(ActionEvent event) {
        try {
            // This loads the next screen in your UI implementation plan
            FXMLLoader loader = new FXMLLoader(getClass().getResource("branch-portal-view.fxml"));
            Parent root = loader.load();

            // Get the current window (Stage) and swap the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not find branch-portal-view.fxml");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}