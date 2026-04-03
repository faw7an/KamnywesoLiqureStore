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

    // ── Simulated OTP (replace with real OTP logic from HQ) ────
    private String generatedOtp = "123456";

    // Store user data for registration
    private String registeredFullName;
    private String registeredPhone;
    private String registeredEmail;
    private String registeredBranch;
    private String registeredRole;
    private String registeredPassword;

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
        // Active dot color: orange (#E04A2A)
        // Complete dot color: green (#4CAF50)
        // Inactive dot color: gray (#CCCCCC)

        String activeStyle = "-fx-text-fill: #E04A2A; -fx-font-size: 18;";
        String completeStyle = "-fx-text-fill: #22C55E; -fx-font-size: 18;";
        String inactiveStyle = "-fx-text-fill: #D1D5DB; -fx-font-size: 18;";

        dot1.setStyle(step == 1 ? activeStyle : (step > 1 ? completeStyle : inactiveStyle));
        dot2.setStyle(step == 2 ? activeStyle : (step > 2 ? completeStyle : inactiveStyle));
        dot3.setStyle(step == 3 ? activeStyle : inactiveStyle);
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

        // Validation
        if (fullName.isEmpty()) {
            showError(step1ErrorLabel, "Please enter your full name.");
            return;
        }
        if (fullName.length() < 3) {
            showError(step1ErrorLabel, "Full name must be at least 3 characters.");
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
        if (email.isEmpty()) {
            showError(step1ErrorLabel, "Please enter your email address.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(step1ErrorLabel, "Please enter a valid email address.");
            return;
        }
        if (branch == null || branch.isEmpty()) {
            showError(step1ErrorLabel, "Please select a branch.");
            return;
        }
        if (role == null || role.isEmpty()) {
            showError(step1ErrorLabel, "Please select a role.");
            return;
        }

        // Store validated data
        registeredFullName = fullName;
        registeredPhone = phone;
        registeredEmail = email;
        registeredBranch = branch;
        registeredRole = role;

        step1ErrorLabel.setVisible(false);
        showStep(2);
    }

    // ───────────────────────────────────────────────────────────
    // STEP 2 HANDLERS
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleStep2Back() {
        step2ErrorLabel.setVisible(false);
        regPasswordField.clear();
        confirmPasswordField.clear();
        showStep(1);
    }

    @FXML
    private void handleStep2Continue() {
        String password = regPasswordField.getText();
        String confirm  = confirmPasswordField.getText();

        if (password.isEmpty()) {
            showError(step2ErrorLabel, "Please enter a password.");
            return;
        }

        if (!isPasswordValid(password)) {
            showError(step2ErrorLabel, "Password does not meet all requirements.");
            return;
        }

        if (confirm.isEmpty()) {
            showError(step2ErrorLabel, "Please confirm your password.");
            return;
        }

        if (!password.equals(confirm)) {
            showError(step2ErrorLabel, "Passwords do not match.");
            return;
        }

        // Store password
        registeredPassword = password;
        step2ErrorLabel.setVisible(false);

        // Update OTP sent label with actual phone number
        otpSentLabel.setText("OTP sent to " + registeredPhone);

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

        // TODO: Call HQ server to resend OTP
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

        // TODO: Send registration data to HQ server
        System.out.println("═══ REGISTRATION COMPLETE ═══");
        System.out.println("Full Name:   " + registeredFullName);
        System.out.println("Phone:       " + registeredPhone);
        System.out.println("Email:       " + registeredEmail);
        System.out.println("Branch:      " + registeredBranch);
        System.out.println("Role:        " + registeredRole);
        System.out.println("Password:    " + (registeredPassword.length() + " chars"));
        System.out.println("═══════════════════════════════");

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
    // PASSWORD VALIDATION HELPERS
    // ───────────────────────────────────────────────────────────

    private void updatePasswordRules(String password) {
        boolean has8Chars   = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber   = password.matches(".*[0-9].*");
        boolean hasSpecial  = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        updateRuleDisplay(rule8Chars, has8Chars);
        updateRuleDisplay(ruleUppercase, hasUppercase);
        updateRuleDisplay(ruleNumber, hasNumber);
        updateRuleDisplay(ruleSpecial, hasSpecial);
    }

    private void updateRuleDisplay(Label ruleLabel, boolean isMet) {
        String labelText = ruleLabel.getText();

        if (isMet) {
            // Replace X with ✓ and mark as complete
            if (labelText.contains("X  ")) {
                ruleLabel.setText(labelText.replace("X  ", "✓  "));
            }
            ruleLabel.setStyle("-fx-text-fill: #22C55E; -fx-font-weight: bold; -fx-font-size: 12;");
        } else {
            // Ensure X is displayed
            if (labelText.contains("✓  ")) {
                ruleLabel.setText(labelText.replace("✓  ", "X  "));
            }
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
    // OTP FIELD SETUP
    // ───────────────────────────────────────────────────────────

    private void setupOtpField(TextField current, TextField prev, TextField next) {
        // Allow only single digits
        current.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1) {
                current.setText(newVal.substring(0, 1));
                return;
            }
            // Auto-advance to next field when digit entered
            if (newVal.length() == 1 && newVal.matches("[0-9]") && next != null) {
                next.requestFocus();
            }
        });

        // Handle backspace to go to previous field
        current.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("BACK_SPACE")
                    && current.getText().isEmpty() && prev != null) {
                prev.requestFocus();
            }
        });
    }

    // ───────────────────────────────────────────────────────────
    // COUNTDOWN TIMER
    // ───────────────────────────────────────────────────────────

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
            countdownTimer = null;
        }
    }

    private void clearOtpFields() {
        otp1.clear(); otp2.clear(); otp3.clear();
        otp4.clear(); otp5.clear(); otp6.clear();
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

            // Re-center after scene change
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            System.err.println("Error navigating to login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
