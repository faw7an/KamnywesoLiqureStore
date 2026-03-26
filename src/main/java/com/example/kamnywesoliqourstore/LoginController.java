package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private ComboBox<String> branchComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink signUpLink;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Label orLabel;

    @FXML
    public void initialize() {
        branchComboBox.getItems().addAll(
                "Nairobi HQ",
                "Mombasa Branch",
                "Kisumu Branch",
                "Nakuru Branch"
        );
    }

    @FXML
    private void handleBranchSelection() {
        String selectedBranch = branchComboBox.getValue();
        if (selectedBranch != null) {
            System.out.println("Branch selected: " + selectedBranch);
        }
    }

    @FXML
    private void handleLogin() {
        String branch   = branchComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (branch == null || branch.isEmpty()) {
            showError("Please select a branch.");
            return;
        }
        if (username.isEmpty()) {
            showError("Please enter your Staff ID or Email.");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        // TODO: Replace with real authentication (database check)
        // Simulated credentials for testing
        // username: admin, password: Admin@123
        if (username.equals("admin") && password.equals("Admin@123")) {
            errorLabel.setVisible(false);
            navigateToOtp(username, branch, "Manager", "John Doe",
                    "+254712345678");
        } else {
            showError("Invalid Staff ID or password.");
        }
    }

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

    // ── Helpers ────────────────────────────────────────────────
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void navigateToOtp(String username, String branch,
                               String role, String name, String phone) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("otpVerification.fxml"));
            Parent root = loader.load();

            // Pass login data to OTP controller
            OtpVerificationController otpCtrl = loader.getController();
            otpCtrl.initData(phone, branch, role, name);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));

            // re-center after scene change
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error. Please try again.");
        }
    }
}
