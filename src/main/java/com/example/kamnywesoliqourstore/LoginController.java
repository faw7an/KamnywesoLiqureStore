package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private ComboBox<String> branchComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Label orLabel;

    // Called automatically when the FXML is loaded
    @FXML
    public void initialize() {
        // Populate branch dropdown
        branchComboBox.getItems().addAll(
                "Nairobi HQ",
                "Mombasa Branch",
                "Kisumu Branch",
                "Nakuru Branch"
        );
    }

    // Handles branch selection from dropdown
    @FXML
    private void handleBranchSelection() {
        String selectedBranch = branchComboBox.getValue();
        if (selectedBranch != null) {
            System.out.println("Branch selected: " + selectedBranch);
        }
    }

    // Handles LOGIN button click
    @FXML
    private void handleLogin() {
        String branch = branchComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate branch selection
        if (branch == null || branch.isEmpty()) {
            errorLabel.setText("Please select a branch.");
            errorLabel.setVisible(true);
            return;
        }

        // Validate username
        if (username.isEmpty()) {
            errorLabel.setText("Please enter your Staff ID or Email.");
            errorLabel.setVisible(true);
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            errorLabel.setText("Please enter your password.");
            errorLabel.setVisible(true);
            return;
        }

        // TODO: Replace this with real authentication logic
        if (username.equals("admin") && password.equals("admin123")) {
            errorLabel.setVisible(false);
            System.out.println("Login successful!");
            System.out.println("Branch: " + branch);
            System.out.println("Remember Me: " + rememberMeCheckBox.isSelected());
            // TODO: Navigate to the main dashboard screen
        } else {
            errorLabel.setText("Invalid username or password!");
            errorLabel.setVisible(true);
        }
    }

    // Handles Forgot Password? hyperlink click
    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("forgotPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles Sign Up hyperlink click
    @FXML
    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
