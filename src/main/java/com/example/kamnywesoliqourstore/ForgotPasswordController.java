package com.example.kamnywesoliqourstore;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ForgotPasswordController {

    // ── Step dots ──────────────────────────────────────────────
    @FXML private Label dot1;
    @FXML private Label dot2;
    @FXML private Label dot3;

    // ── Step panes ─────────────────────────────────────────────
    @FXML private VBox step1Pane;
    @FXML private VBox step2Pane;
    @FXML private VBox step3Pane;

    // ── Step 1 fields ──────────────────────────────────────────
    @FXML private TextField identifierField;
    @FXML private RadioButton phoneRadio;
    @FXML private RadioButton emailRadio;
    @FXML private ToggleGroup otpMethodGroup;
    @FXML private Label sendToInfoLabel;
    @FXML private Label step1ErrorLabel;

    // ── Step 2 fields ──────────────────────────────────────────
    @FXML private Label otpSentLabel;
    @FXML private TextField otp1;
    @FXML private TextField otp2;
    @FXML private TextField otp3;
    @FXML private TextField otp4;
    @FXML private TextField otp5;
    @FXML private TextField otp6;
    @FXML private Label timerLabel;
    @FXML private Button resendBtn;
    @FXML private Label step2ErrorLabel;

    // ── Step 3 fields ──────────────────────────────────────────
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Label rule8Chars;
    @FXML private Label ruleUppercase;
    @FXML private Label ruleNumber;
    @FXML private Label ruleSpecial;
    @FXML private Label step3ErrorLabel;
    @FXML private Label successLabel;

    // ── Internal state ─────────────────────────────────────────
    private Timeline countdownTimer;
    private int secondsRemaining = 299;  // 4:59

    // Simulated OTP — replace with real OTP service from HQ
    // TODO: Get this from server (sent via SMS/Email)
    private String generatedOtp = "123456";

    // Store user identifier for resetting password
    private String userIdentifier = "";

    // ───────────────────────────────────────────────────────────
    // INITIALIZE
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Update info label when radio selection changes
        phoneRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (sendToInfoLabel != null) {
                    sendToInfoLabel.setText("Reset code will be sent to the phone number linked to your account.");
                }
            }
        });

        emailRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (sendToInfoLabel != null) {
                    sendToInfoLabel.setText("Reset code will be sent to the email address linked to your account.");
                }
            }
        });

        // Live password rule validation on step 3
        if (newPasswordField != null) {
            newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                updatePasswordRules(newVal);
            });
        }

        // Auto-advance OTP boxes
        setupOtpField(otp1, null, otp2);
        setupOtpField(otp2, otp1, otp3);
        setupOtpField(otp3, otp2, otp4);
        setupOtpField(otp4, otp3, otp5);
        setupOtpField(otp5, otp4, otp6);
        setupOtpField(otp6, otp5, null);

        // Clear error labels
        if (step1ErrorLabel != null) step1ErrorLabel.setVisible(false);
        if (step2ErrorLabel != null) step2ErrorLabel.setVisible(false);
        if (step3ErrorLabel != null) step3ErrorLabel.setVisible(false);
        if (successLabel != null) successLabel.setVisible(false);

        showStep(1);
    }

    // ───────────────────────────────────────────────────────────
    // STEP NAVIGATION
    // ───────────────────────────────────────────────────────────
    private void showStep(int step) {
        if (step1Pane != null) {
            step1Pane.setVisible(step == 1);
            step1Pane.setManaged(step == 1);
        }
        if (step2Pane != null) {
            step2Pane.setVisible(step == 2);
            step2Pane.setManaged(step == 2);
        }
        if (step3Pane != null) {
            step3Pane.setVisible(step == 3);
            step3Pane.setManaged(step == 3);
        }
        updateDots(step);
    }

    private void updateDots(int step) {
        String activeStyle = "-fx-text-fill: #E04A2A; -fx-font-size: 18;";
        String completeStyle = "-fx-text-fill: #22C55E; -fx-font-size: 18;";
        String inactiveStyle = "-fx-text-fill: #D1D5DB; -fx-font-size: 18;";

        if (dot1 != null) dot1.setStyle(step == 1 ? activeStyle : (step > 1 ? completeStyle : inactiveStyle));
        if (dot2 != null) dot2.setStyle(step == 2 ? activeStyle : (step > 2 ? completeStyle : inactiveStyle));
        if (dot3 != null) dot3.setStyle(step == 3 ? activeStyle : inactiveStyle);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 1 — VERIFY ACCOUNT
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep1Continue() {
        String identifier = identifierField.getText().trim();

        // Validation
        if (identifier.isEmpty()) {
            showError(step1ErrorLabel, "Please enter your Staff ID or Email.");
            return;
        }

        // Store identifier for later use
        userIdentifier = identifier;

        // Clear error and proceed
        if (step1ErrorLabel != null) {
            step1ErrorLabel.setVisible(false);
        }

        // Set OTP sent label based on method chosen
        boolean usingPhone = phoneRadio.isSelected();
        if (otpSentLabel != null) {
            if (usingPhone) {
                otpSentLabel.setText("Reset code sent to phone linked to: " + identifier);
            } else {
                otpSentLabel.setText("Reset code sent to email linked to: " + identifier);
            }
        }

        // TODO: Call HQ server to send reset code via SMS/Email
        System.out.println("═══ PASSWORD RESET INITIATED ═══");
        System.out.println("Method:      " + (usingPhone ? "SMS" : "Email"));
        System.out.println("Identifier:  " + identifier);
        System.out.println("Simulated Code: " + generatedOtp);
        System.out.println("═════════════════════════════════");

        startCountdown();
        showStep(2);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 2 — VERIFY RESET CODE
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep2Back() {
        stopCountdown();
        clearOtpFields();
        if (step2ErrorLabel != null) {
            step2ErrorLabel.setVisible(false);
        }
        showStep(1);
    }

    @FXML
    private void handleStep2Continue() {
        String enteredOtp = getEnteredOtp();

        // Validate OTP length
        if (enteredOtp.length() < 6) {
            showError(step2ErrorLabel, "Please enter the full 6-digit code.");
            return;
        }

        // Validate OTP correctness
        if (!enteredOtp.equals(generatedOtp)) {
            showError(step2ErrorLabel, "Invalid code. Please try again.");
            clearOtpFields();
            return;
        }

        // Code correct — proceed to password reset
        System.out.println("✅ Reset code verified successfully");

        stopCountdown();
        if (step2ErrorLabel != null) {
            step2ErrorLabel.setVisible(false);
        }
        showStep(3);
    }

    @FXML
    private void handleResendOtp() {
        secondsRemaining = 299;
        if (resendBtn != null) {
            resendBtn.setVisible(false);
        }
        if (timerLabel != null) {
            timerLabel.setVisible(true);
        }
        clearOtpFields();
        startCountdown();

        // TODO: Call HQ server to resend reset code
        System.out.println("Reset code resent to " + userIdentifier);
        System.out.println("Simulated Code: " + generatedOtp);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 3 — RESET PASSWORD
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep3Back() {
        if (step3ErrorLabel != null) step3ErrorLabel.setVisible(false);
        if (successLabel != null) successLabel.setVisible(false);
        newPasswordField.clear();
        confirmNewPasswordField.clear();
        showStep(2);
    }

    @FXML
    private void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        // Validation
        if (newPassword.isEmpty()) {
            showError(step3ErrorLabel, "Please enter a new password.");
            return;
        }

        if (!isPasswordValid(newPassword)) {
            showError(step3ErrorLabel, "Password does not meet all requirements.");
            return;
        }

        if (confirmPassword.isEmpty()) {
            showError(step3ErrorLabel, "Please confirm your password.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError(step3ErrorLabel, "Passwords do not match.");
            return;
        }

        // Password reset successful
        System.out.println("✅ PASSWORD RESET SUCCESSFUL");
        System.out.println("Account: " + userIdentifier);
        System.out.println("═════════════════════════════════");

        if (step3ErrorLabel != null) step3ErrorLabel.setVisible(false);
        if (successLabel != null) successLabel.setVisible(true);

        // TODO: Send new password to HQ server to update database

        // Navigate back to login after 2 seconds
        Timeline delay = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> navigateToLogin()));
        delay.play();
    }

    @FXML
    private void handleBackToLogin() {
        stopCountdown();
        navigateToLogin();
    }

    // ───────────────────────────────────────────────────────────
    // PASSWORD VALIDATION HELPERS
    // ───────────────────────────────────────────────────────────

    private void updatePasswordRules(String password) {
        boolean has8Chars = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        updateRuleDisplay(rule8Chars, has8Chars, "At least 8 characters");
        updateRuleDisplay(ruleUppercase, hasUppercase, "One uppercase letter (A-Z)");
        updateRuleDisplay(ruleNumber, hasNumber, "One number (0-9)");
        updateRuleDisplay(ruleSpecial, hasSpecial, "One special character (!@#$%)");
    }

    private void updateRuleDisplay(Label ruleLabel, boolean isMet, String ruleText) {
        if (ruleLabel == null) return;

        if (isMet) {
            ruleLabel.setText("✓  " + ruleText);
            ruleLabel.setStyle("-fx-text-fill: #22C55E; -fx-font-weight: bold; -fx-font-size: 12;");
        } else {
            ruleLabel.setText("X  " + ruleText);
            ruleLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12;");
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    // ───────────────────────────────────────────────────────────
    // OTP FIELD HELPERS
    // ───────────────────────────────────────────────────────────

    private void setupOtpField(TextField current, TextField prev, TextField next) {
        if (current == null) return;

        // Only allow single digit input
        current.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1) {
                current.setText(newVal.substring(0, 1));
                return;
            }

            // Only allow digits
            if (newVal.length() == 1 && !newVal.matches("[0-9]")) {
                current.setText("");
                return;
            }

            // Auto-advance to next field
            if (newVal.length() == 1 && newVal.matches("[0-9]") && next != null) {
                next.requestFocus();
            }
        });

        // Handle backspace
        current.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("BACK_SPACE")
                    && current.getText().isEmpty() && prev != null) {
                prev.requestFocus();
            }
        });
    }

    private String getEnteredOtp() {
        StringBuilder otp = new StringBuilder();
        if (otp1 != null) otp.append(otp1.getText());
        if (otp2 != null) otp.append(otp2.getText());
        if (otp3 != null) otp.append(otp3.getText());
        if (otp4 != null) otp.append(otp4.getText());
        if (otp5 != null) otp.append(otp5.getText());
        if (otp6 != null) otp.append(otp6.getText());
        return otp.toString();
    }

    private void clearOtpFields() {
        if (otp1 != null) otp1.clear();
        if (otp2 != null) otp2.clear();
        if (otp3 != null) otp3.clear();
        if (otp4 != null) otp4.clear();
        if (otp5 != null) otp5.clear();
        if (otp6 != null) otp6.clear();
        if (otp1 != null) otp1.requestFocus();
    }

    // ───────────────────────────────────────────────────────────
    // COUNTDOWN TIMER
    // ───────────────────────────────────────────────────────────

    private void startCountdown() {
        stopCountdown();
        secondsRemaining = 299;

        if (timerLabel != null) timerLabel.setVisible(true);
        if (resendBtn != null) resendBtn.setVisible(false);

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            if (timerLabel != null) {
                timerLabel.setText(String.format("Resend code in %02d:%02d", minutes, seconds));
            }

            if (secondsRemaining <= 0) {
                stopCountdown();
                if (timerLabel != null) timerLabel.setVisible(false);
                if (resendBtn != null) resendBtn.setVisible(true);
            }
        }));

        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }

    // ───────────────────────────────────────────────────────────
    // UTILITY METHODS
    // ───────────────────────────────────────────────────────────

    private void showError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("login-view.fxml"));  // FIXED: was login.fxml

            if (loader.getLocation() == null) {
                System.err.println("Login page not found!");
                return;
            }

            Parent root = loader.load();
            Stage stage = (Stage) dot1.getScene().getWindow();
            stage.setScene(new Scene(root));

            // Re-center window
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            System.err.println("Error navigating to login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
