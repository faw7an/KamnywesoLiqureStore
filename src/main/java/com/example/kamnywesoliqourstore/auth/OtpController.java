package com.example.kamnywesoliqourstore.auth;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OtpController {

    private static final Logger LOGGER = Logger.getLogger(OtpController.class.getName());

    @FXML private TextField otpSlot1;
    @FXML private TextField otpSlot2;
    @FXML private TextField otpSlot3;
    @FXML private TextField otpSlot4;
    @FXML private TextField otpSlot5;
    @FXML private TextField otpSlot6;

    private TextField[] otpFields;

    @FXML
    public void initialize() {
        otpFields = new TextField[]{otpSlot1, otpSlot2, otpSlot3, otpSlot4, otpSlot5, otpSlot6};

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            TextField currentField = otpFields[i];

            // Limit input to 1 digit and auto-focus next
            currentField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    currentField.setText(newValue.replaceAll("\\D", ""));
                }
                
                if (currentField.getText().length() > 1) {
                    currentField.setText(currentField.getText().substring(0, 1));
                }

                // If a number is typed, move to the next field
                if (!currentField.getText().isEmpty() && index < otpFields.length - 1) {
                    Platform.runLater(() -> otpFields[index + 1].requestFocus());
                }
            });

            // Handle Backspace to move to previous field
            currentField.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.BACK_SPACE) {
                    if (currentField.getText().isEmpty() && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }
            });
        }
    }

    @FXML
    private void handleVerify(ActionEvent event) {
        // Collect code
        StringBuilder code = new StringBuilder();
        for (TextField field : otpFields) {
            code.append(field.getText());
        }

        LOGGER.info("Verifying OTP: " + code);

        // For now, any 6-digit code works. Let's redirect to branch-portal.
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/kamnywesoliqourstore/admin/admin-dashboard-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Kamnyweso Liquor Store - Branch Portal Dashboard");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to Branch Portal: branch-portal-view.fxml", e);
        }
    }
}
