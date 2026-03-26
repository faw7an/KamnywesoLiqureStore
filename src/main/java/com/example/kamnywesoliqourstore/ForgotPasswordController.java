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
    private int secondsRemaining = 299;

    // Simulated OTP — replace with real OTP service later
    private String generatedOtp = "123456";

    // ───────────────────────────────────────────────────────────
    // INITIALIZE
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Update info label when radio selection changes
        phoneRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                sendToInfoLabel.setText(
                        "OTP will be sent to the phone number linked to your account.");
            }
        });
        emailRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                sendToInfoLabel.setText(
                        "OTP will be sent to the email address linked to your account.");
            }
        });

        // Live password rule validation on step 3
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordRules(newVal);
        });

        // Auto-advance OTP boxes
        setupOtpField(otp1, null, otp2);
        setupOtpField(otp2, otp1, otp3);
        setupOtpField(otp3, otp2, otp4);
        setupOtpField(otp4, otp3, otp5);
        setupOtpField(otp5, otp4, otp6);
        setupOtpField(otp6, otp5, null);

        showStep(1);
    }

    // ───────────────────────────────────────────────────────────
    // STEP NAVIGATION
    // ───────────────────────────────────────────────────────────
    private void showStep(int step) {
        step1Pane.setVisible(step == 1);
        step1Pane.setManaged(step == 1);
        step2Pane.setVisible(step == 2);
        step2Pane.setManaged(step == 2);
        step3Pane.setVisible(step == 3);
        step3Pane.setManaged(step == 3);
        updateDots(step);
    }

    private void updateDots(int step) {
        String active   = "-fx-text-fill: #E04A2A; -fx-font-size: 18;";
        String done     = "-fx-text-fill: #4CAF50; -fx-font-size: 18;";
        String inactive = "-fx-text-fill: #CCCCCC; -fx-font-size: 18;";
        dot1.setStyle(step == 1 ? active : (step > 1 ? done : inactive));
        dot2.setStyle(step == 2 ? active : (step > 2 ? done : inactive));
        dot3.setStyle(step == 3 ? active : inactive);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 1 — Enter Details
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep1Continue() {
        String identifier = identifierField.getText().trim();

        if (identifier.isEmpty()) {
            showError(step1ErrorLabel, "Please enter your Staff ID or Email.");
            return;
        }

        step1ErrorLabel.setVisible(false);

        // Set OTP sent label based on method chosen
        boolean usingPhone = phoneRadio.isSelected();
        if (usingPhone) {
            otpSentLabel.setText("OTP sent to the phone number linked to: " + identifier);
        } else {
            otpSentLabel.setText("OTP sent to the email linked to: " + identifier);
        }

        // TODO: Trigger real OTP send here
        System.out.println("Sending OTP via "
                + (usingPhone ? "phone" : "email")
                + " for account: " + identifier);

        startCountdown();
        showStep(2);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 2 — Verify OTP
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep2Back() {
        stopCountdown();
        clearOtpFields();
        step2ErrorLabel.setVisible(false);
        showStep(1);
    }

    @FXML
    private void handleStep2Continue() {
        String enteredOtp = otp1.getText() + otp2.getText() + otp3.getText()
                + otp4.getText() + otp5.getText() + otp6.getText();

        if (enteredOtp.length() < 6) {
            showError(step2ErrorLabel, "Please enter the full 6-digit OTP.");
            return;
        }

        if (!enteredOtp.equals(generatedOtp)) {
            showError(step2ErrorLabel, "Invalid OTP. Please try again.");
            return;
        }

        stopCountdown();
        step2ErrorLabel.setVisible(false);
        showStep(3);
    }

    @FXML
    private void handleResendOtp() {
        secondsRemaining = 299;
        resendBtn.setVisible(false);
        timerLabel.setVisible(true);
        clearOtpFields();
        startCountdown();
        // TODO: Trigger real OTP resend here
        System.out.println("OTP resent. Simulated OTP: " + generatedOtp);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 3 — Reset Password
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep3Back() {
        step3ErrorLabel.setVisible(false);
        successLabel.setVisible(false);
        showStep(2);
    }

    @FXML
    private void handleResetPassword() {
        String newPassword     = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        if (!isPasswordValid(newPassword)) {
            showError(step3ErrorLabel, "Password does not meet all requirements.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError(step3ErrorLabel, "Passwords do not match.");
            return;
        }

        step3ErrorLabel.setVisible(false);
        successLabel.setVisible(true);

        // TODO: Save new password to database here
        System.out.println("Password reset successfully for: "
                + identifierField.getText());

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
    // HELPERS
    // ───────────────────────────────────────────────────────────
    private void updatePasswordRules(String password) {
        setRule(rule8Chars,   password.length() >= 8,            "At least 8 characters");
        setRule(ruleUppercase, password.matches(".*[A-Z].*"),    "One uppercase letter");
        setRule(ruleNumber,   password.matches(".*[0-9].*"),     "One number");
        setRule(ruleSpecial,  password.matches(
                ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"), "One special symbol");
    }

    private void setRule(Label label, boolean passed, String ruleText) {
        if (passed) {
            label.setText("✓  " + ruleText);
            label.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12;");
        } else {
            label.setText("X  " + ruleText);
            label.setStyle("-fx-text-fill: #999999; -fx-font-size: 12;");
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    private void setupOtpField(TextField current, TextField prev, TextField next) {
        current.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1) {
                current.setText(newVal.substring(0, 1));
            }
            if (newVal.length() == 1 && next != null) {
                next.requestFocus();
            }
        });
        current.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("BACK_SPACE")
                    && current.getText().isEmpty() && prev != null) {
                prev.requestFocus();
            }
        });
    }

    private void startCountdown() {
        stopCountdown();
        secondsRemaining = 299;
        resendBtn.setVisible(false);
        timerLabel.setVisible(true);

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;
            timerLabel.setText(
                    String.format("Resend code in %02d:%02d", minutes, seconds));
            if (secondsRemaining <= 0) {
                stopCountdown();
                timerLabel.setVisible(false);
                resendBtn.setVisible(true);
            }
        }));
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }

    private void clearOtpFields() {
        otp1.clear(); otp2.clear(); otp3.clear();
        otp4.clear(); otp5.clear(); otp6.clear();
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dot1.getScene().getWindow();
            stage.setScene(new Scene(root));

            // re-center after scene change
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

