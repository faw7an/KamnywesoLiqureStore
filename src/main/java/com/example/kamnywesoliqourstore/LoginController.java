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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML private ComboBox<String> branchComboBox;
    @FXML private TextField staffIdField;
    @FXML private PasswordField passwordField;
    @FXML private Button signInButton;

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

    @FXML
    private void handleSignIn(ActionEvent event) {
        // 1. Get data from fields
        String staffId = staffIdField.getText();
        String password = passwordField.getText();

        // 2. Logic to move to OTP (based on your implementation plan)
        if (!staffId.isEmpty() && !password.isEmpty()) {
            LOGGER.info("Transitioning to 2FA Screen...");
            navigateToOtpScreen(event);
        } else {
            showAlert(Alert.AlertType.WARNING, "Form Error!", "Please enter your Staff ID and Password.");
        }
    }

    @FXML
    private void handleRegisterNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Kamnyweso Liquor - Create New Account");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load register-view.fxml", e);
        }
    }

    private void navigateToOtpScreen(ActionEvent event) {
        try {
            // Change from branch-portal-view.fxml to otp-view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("otp-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to OTP screen: otp-view.fxml", e);
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