package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private ComboBox<String> branchComboBox;
    @FXML private TextField staffIdField;              // FIXED: was usernameField
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;         // FIXED: added fx:id to FXML
    @FXML private Label forgotPasswordLabel;           // FIXED: changed from Hyperlink to Label
    @FXML private Label signUpLabel;                   // FIXED: changed from Hyperlink to Label
    @FXML private Label errorLabel;
    @FXML private Button signInButton;                 // FIXED: was loginButton

    @FXML
    public void initialize() {
        branchComboBox.getItems().addAll(
                "Nairobi HQ",
                "Mombasa Branch",
                "Kisumu Branch",
                "Nakuru Branch"
        );

        // Clear error label on startup
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void handleBranchSelection() {
        String selectedBranch = branchComboBox.getValue();
        if (selectedBranch != null) {
            System.out.println("Branch selected: " + selectedBranch);
            // Clear any previous errors when branch changes
            if (errorLabel != null) {
                errorLabel.setVisible(false);
            }
        }
    }

    @FXML
    private void handleLogin() {
        String branch   = branchComboBox.getValue();
        String staffId  = staffIdField.getText().trim();    // FIXED: was usernameField
        String password = passwordField.getText().trim();

        // Validation
        if (branch == null || branch.isEmpty()) {
            showError("Please select a branch.");
            return;
        }
        if (staffId.isEmpty()) {
            showError("Please enter your Staff ID or Email.");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        // TODO: Replace with real authentication (connect to server/database)
        // For now: simulated credentials for testing
        // Test credentials: staffId=admin, password=Admin@123

        if (authenticateUser(staffId, password, branch)) {
            errorLabel.setVisible(false);
            navigateToOtp(staffId, branch, "Manager", "John Doe", "+254712345678");
        } else {
            showError("Invalid Staff ID or password.");
        }
    }

    /**
     * Authenticates user against the server/database
     * TODO: Replace with actual backend call (REST API, RMI, Sockets, etc.)
     */
    private boolean authenticateUser(String staffId, String password, String branch) {
        // Hardcoded for testing - REPLACE WITH SERVER CALL
        if (staffId.equals("admin") && password.equals("Admin@123")) {
            return true;
        }
        return false;
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("forgotPassword.fxml"));

            if (loader.getLocation() == null) {
                showError("Forgot Password page not found.");
                return;
            }

            Parent root = loader.load();
            Stage stage = (Stage) signInButton.getScene().getWindow();  // FIXED: was loginButton
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading Forgot Password: " + e.getMessage());
            e.printStackTrace();
            showError("Could not load Forgot Password page. Please try again.");
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("register-view.fxml"));

            if (loader.getLocation() == null) {
                showError("Registration page not found.");
                return;
            }

            Parent root = loader.load();
            Stage stage = (Stage) signInButton.getScene().getWindow();  // FIXED: was loginButton
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading Registration: " + e.getMessage());
            e.printStackTrace();
            showError("Could not load Registration page. Please try again.");
        }
    }

    // ── Helper Methods ────────────────────────────────────────────────
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

    private void navigateToOtp(String staffId, String branch,
                               String role, String name, String phone) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("otp-view.fxml"));

            if (loader.getLocation() == null) {
                showError("OTP Verification page not found.");
                return;
            }

            Parent root = loader.load();

            // Pass login data to OTP controller
            OtpVerificationController otpCtrl = loader.getController();
            otpCtrl.initData(phone, branch, role, name);

            Stage stage = (Stage) signInButton.getScene().getWindow();  // FIXED: was loginButton
            stage.setScene(new Scene(root));


            // Re-center window after scene change
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            System.err.println("Error navigating to OTP: " + e.getMessage());
            e.printStackTrace();
            showError("Navigation error. Please try again.");
        }
    }
}
