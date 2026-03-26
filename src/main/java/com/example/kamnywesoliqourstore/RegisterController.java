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

public class RegisterController {

    // ── Step dots ──────────────────────────────────────────────
    @FXML private Label dot1;
    @FXML private Label dot2;
    @FXML private Label dot3;

    // ── Step panes ─────────────────────────────────────────────
    @FXML private VBox step1Pane;
    @FXML private VBox step2Pane;
    @FXML private VBox step3Pane;

    // ── Step 1 fields ──────────────────────────────────────────
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> regBranchComboBox;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label step1ErrorLabel;

    // ── Step 2 fields ──────────────────────────────────────────
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label rule8Chars;
    @FXML private Label ruleUppercase;
    @FXML private Label ruleNumber;
    @FXML private Label ruleSpecial;
    @FXML private Label step2ErrorLabel;

    // ── Step 3 fields ──────────────────────────────────────────
    @FXML private Label otpSentLabel;
    @FXML private TextField otp1;
    @FXML private TextField otp2;
    @FXML private TextField otp3;
    @FXML private TextField otp4;
    @FXML private TextField otp5;
    @FXML private TextField otp6;
    @FXML private Label timerLabel;
    @FXML private Button resendBtn;
    @FXML private Label step3ErrorLabel;

    // ── Internal state ─────────────────────────────────────────
    private int currentStep = 1;
    private Timeline countdownTimer;
    private int secondsRemaining = 299; // 4:59

    // ── Simulated OTP (replace with real OTP logic later) ──────
    private String generatedOtp = "123456";

    // ───────────────────────────────────────────────────────────
    // INITIALIZE
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Populate branch dropdown
        regBranchComboBox.getItems().addAll(
                "Nairobi HQ",
                "Mombasa Branch",
                "Kisumu Branch",
                "Nakuru Branch"
        );

        // Populate role dropdown
        roleComboBox.getItems().addAll(
                "Cashier",
                "Manager",
                "Supervisor",
                "Stock Controller"
        );

        // Live password rule validation
        regPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordRules(newVal);
        });

        // Auto-advance OTP boxes on input
        setupOtpField(otp1, null, otp2);
        setupOtpField(otp2, otp1, otp3);
        setupOtpField(otp3, otp2, otp4);
        setupOtpField(otp4, otp3, otp5);
        setupOtpField(otp5, otp4, otp6);
        setupOtpField(otp6, otp5, null);

        // Show step 1 initially
        showStep(1);
    }

    // ───────────────────────────────────────────────────────────
    // STEP NAVIGATION
    // ───────────────────────────────────────────────────────────
    private void showStep(int step) {
        currentStep = step;

        step1Pane.setVisible(step == 1);
        step1Pane.setManaged(step == 1);
        step2Pane.setVisible(step == 2);
        step2Pane.setManaged(step == 2);
        step3Pane.setVisible(step == 3);
        step3Pane.setManaged(step == 3);

        updateDots(step);
    }

    private void updateDots(int step) {
        String activeColor   = "-fx-text-fill: #E04A2A; -fx-font-size: 18;";
        String doneColor     = "-fx-text-fill: #4CAF50; -fx-font-size: 18;";
        String inactiveColor = "-fx-text-fill: #CCCCCC; -fx-font-size: 18;";

        dot1.setStyle(step == 1 ? activeColor : (step > 1 ? doneColor : inactiveColor));
        dot2.setStyle(step == 2 ? activeColor : (step > 2 ? doneColor : inactiveColor));
        dot3.setStyle(step == 3 ? activeColor : inactiveColor);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 1 HANDLERS
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep1Continue() {
        String fullName = fullNameField.getText().trim();
        String phone    = phoneField.getText().trim();
        String email    = emailField.getText().trim();
        String branch   = regBranchComboBox.getValue();
        String role     = roleComboBox.getValue();

        if (fullName.isEmpty()) {
            showError(step1ErrorLabel, "Please enter your full name.");
            return;
        }
        if (phone.isEmpty()) {
            showError(step1ErrorLabel, "Please enter your phone number.");
            return;
        }
        if (!phone.matches("^\\+254[0-9]{9}$")) {
            showError(step1ErrorLabel, "Phone must be in format +254XXXXXXXXX.");
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            showError(step1ErrorLabel, "Please enter a valid email address.");
            return;
        }
        if (branch == null) {
            showError(step1ErrorLabel, "Please select a branch.");
            return;
        }
        if (role == null) {
            showError(step1ErrorLabel, "Please select a role.");
            return;
        }

        step1ErrorLabel.setVisible(false);
        showStep(2);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 2 HANDLERS
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep2Back() {
        step2ErrorLabel.setVisible(false);
        showStep(1);
    }

    @FXML
    private void handleStep2Continue() {
        String password = regPasswordField.getText();
        String confirm  = confirmPasswordField.getText();

        if (!isPasswordValid(password)) {
            showError(step2ErrorLabel, "Password does not meet all requirements.");
            return;
        }
        if (!password.equals(confirm)) {
            showError(step2ErrorLabel, "Passwords do not match.");
            return;
        }

        step2ErrorLabel.setVisible(false);

        // Update OTP sent label with actual phone number
        String phone = phoneField.getText().trim();
        otpSentLabel.setText("OTP sent to " + phone);

        // Start countdown timer and go to step 3
        startCountdown();
        showStep(3);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 3 HANDLERS
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep3Back() {
        stopCountdown();
        step3ErrorLabel.setVisible(false);
        clearOtpFields();
        showStep(2);
    }

    @FXML
    private void handleResendOtp() {
        // Reset and restart timer
        secondsRemaining = 299;
        resendBtn.setVisible(false);
        timerLabel.setVisible(true);
        clearOtpFields();
        startCountdown();
        System.out.println("OTP resent. New OTP: " + generatedOtp);
    }

    @FXML
    private void handleCompleteRegistration() {
        String enteredOtp = otp1.getText() + otp2.getText() + otp3.getText()
                + otp4.getText() + otp5.getText() + otp6.getText();

        if (enteredOtp.length() < 6) {
            showError(step3ErrorLabel, "Please enter the full 6-digit OTP.");
            return;
        }

        if (!enteredOtp.equals(generatedOtp)) {
            showError(step3ErrorLabel, "Invalid OTP. Please try again.");
            return;
        }

        // TODO: Save registration data to database here
        System.out.println("Registration complete!");
        System.out.println("Name:    " + fullNameField.getText());
        System.out.println("Phone:   " + phoneField.getText());
        System.out.println("Email:   " + emailField.getText());
        System.out.println("Branch:  " + regBranchComboBox.getValue());
        System.out.println("Role:    " + roleComboBox.getValue());

        stopCountdown();

        // Navigate back to login page
        navigateToLogin();
    }

    @FXML
    private void handleSignIn() {
        stopCountdown();
        navigateToLogin();
    }

    // ───────────────────────────────────────────────────────────
    // HELPERS
    // ───────────────────────────────────────────────────────────

    private void updatePasswordRules(String password) {
        setRule(rule8Chars,   password.length() >= 8);
        setRule(ruleUppercase, password.matches(".*[A-Z].*"));
        setRule(ruleNumber,   password.matches(".*[0-9].*"));
        setRule(ruleSpecial,  password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"));
    }

    private void setRule(Label label, boolean passed) {
        if (passed) {
            label.setText(label.getText().replace("X  ", "✓  "));
            label.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12;");
        } else {
            label.setText(label.getText().replace("✓  ", "X  "));
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
            timerLabel.setText(String.format("Resend code in %02d:%02d", minutes, seconds));

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

