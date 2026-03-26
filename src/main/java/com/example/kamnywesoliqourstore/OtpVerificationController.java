package com.example.kamnywesoliqourstore;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OtpVerificationController {

    // ── OTP boxes ──────────────────────────────────────────────
    @FXML private TextField otp1;
    @FXML private TextField otp2;
    @FXML private TextField otp3;
    @FXML private TextField otp4;
    @FXML private TextField otp5;
    @FXML private TextField otp6;

    // ── Labels ─────────────────────────────────────────────────
    @FXML private Label otpInfoLabel;
    @FXML private Label branchInfoLabel;
    @FXML private Label timerLabel;
    @FXML private Label errorLabel;
    @FXML private Label attemptsLabel;

    // ── Buttons ────────────────────────────────────────────────
    @FXML private Button resendBtn;
    @FXML private Button verifyBtn;

    // ── Internal state ─────────────────────────────────────────
    private Timeline countdownTimer;
    private int secondsRemaining = 299;
    private int failedAttempts   = 0;
    private static final int MAX_ATTEMPTS = 3;

    // Simulated OTP — replace with real OTP service later
    private String generatedOtp = "123456";

    // ── Session data passed from LoginController ───────────────
    private String staffPhone  = "+254 7XX XXX XXX";
    private String branchName  = "Nairobi HQ";
    private String staffRole   = "Manager";
    private String staffName   = "John Doe";

    // ───────────────────────────────────────────────────────────
    // Called by LoginController to pass session data
    // ───────────────────────────────────────────────────────────
    public void initData(String phone, String branch,
                         String role, String name) {
        this.staffPhone = phone;
        this.branchName = branch;
        this.staffRole  = role;
        this.staffName  = name;

        // Mask phone: +254 7XX XXX 678 → show last 3 digits only
        String masked = maskPhone(phone);
        otpInfoLabel.setText("OTP sent to " + masked);
        branchInfoLabel.setText("Branch: " + branch);

        // TODO: Trigger real OTP send here
        System.out.println("2FA OTP sent. Simulated OTP: " + generatedOtp);
    }

    // ───────────────────────────────────────────────────────────
    // INITIALIZE
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        setupOtpField(otp1, null, otp2);
        setupOtpField(otp2, otp1, otp3);
        setupOtpField(otp3, otp2, otp4);
        setupOtpField(otp4, otp3, otp5);
        setupOtpField(otp5, otp4, otp6);
        setupOtpField(otp6, otp5, null);

        startCountdown();
    }

    // ───────────────────────────────────────────────────────────
    // VERIFY BUTTON
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleVerify() {
        if (failedAttempts >= MAX_ATTEMPTS) {
            showError("Account locked. Too many failed attempts.");
            verifyBtn.setDisable(true);
            return;
        }

        String entered = otp1.getText() + otp2.getText()
                + otp3.getText() + otp4.getText()
                + otp5.getText() + otp6.getText();

        if (entered.length() < 6) {
            showError("Please enter the full 6-digit OTP.");
            return;
        }

        if (!entered.equals(generatedOtp)) {
            failedAttempts++;
            int remaining = MAX_ATTEMPTS - failedAttempts;
            if (remaining > 0) {
                showError("Invalid OTP. " + remaining
                        + " attempt(s) remaining.");
            } else {
                showError("Account locked for 15 minutes"
                        + " due to too many failed attempts.");
                verifyBtn.setDisable(true);
                lockAllOtpFields();
            }
            clearOtpFields();
            return;
        }

        // OTP correct — navigate to main dashboard
        stopCountdown();
        errorLabel.setVisible(false);
        navigateToDashboard();
    }

    // ───────────────────────────────────────────────────────────
    // RESEND OTP
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleResendOtp() {
        clearOtpFields();
        resendBtn.setVisible(false);
        timerLabel.setVisible(true);
        failedAttempts = 0;
        errorLabel.setVisible(false);
        verifyBtn.setDisable(false);
        unlockAllOtpFields();
        startCountdown();
        // TODO: Trigger real OTP resend here
        System.out.println("OTP resent. Simulated OTP: " + generatedOtp);
    }

    // ───────────────────────────────────────────────────────────
    // BACK TO LOGIN
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleBackToLogin() {
        stopCountdown();
        navigateToLogin();
    }

    // ───────────────────────────────────────────────────────────
    // HELPERS
    // ───────────────────────────────────────────────────────────
    private void setupOtpField(TextField current,
                               TextField prev, TextField next) {
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
                    && current.getText().isEmpty()
                    && prev != null) {
                prev.requestFocus();
            }
        });
    }

    private void startCountdown() {
        stopCountdown();
        secondsRemaining = 299;
        timerLabel.setVisible(true);
        resendBtn.setVisible(false);

        countdownTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsRemaining--;
                    int min = secondsRemaining / 60;
                    int sec = secondsRemaining % 60;
                    timerLabel.setText(String.format(
                            "Resend code in %02d:%02d", min, sec));
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
        if (countdownTimer != null) countdownTimer.stop();
    }

    private void clearOtpFields() {
        otp1.clear(); otp2.clear(); otp3.clear();
        otp4.clear(); otp5.clear(); otp6.clear();
        otp1.requestFocus();
    }

    private void lockAllOtpFields() {
        otp1.setDisable(true); otp2.setDisable(true);
        otp3.setDisable(true); otp4.setDisable(true);
        otp5.setDisable(true); otp6.setDisable(true);
    }

    private void unlockAllOtpFields() {
        otp1.setDisable(false); otp2.setDisable(false);
        otp3.setDisable(false); otp4.setDisable(false);
        otp5.setDisable(false); otp6.setDisable(false);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) return phone;
        // Show +254 7XX XXX + last 3 digits
        String last3 = phone.substring(phone.length() - 3);
        return "+254 7XX XXX " + last3;
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            // Pass session data to dashboard controller
            DashboardController dc = loader.getController();
            dc.initSession(staffName, staffRole, branchName);

            Stage stage = (Stage) verifyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load dashboard. Please try again.");
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) verifyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);

            // re-center after scene change
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

