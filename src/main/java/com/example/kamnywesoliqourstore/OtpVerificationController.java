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
    @FXML private Label otpInfoLabel;          // FIXED: was missing in FXML
    @FXML private Label branchInfoLabel;       // FIXED: was missing in FXML
    @FXML private Label timerLabel;            // FIXED: was missing in FXML
    @FXML private Label errorLabel;            // FIXED: was missing in FXML
    @FXML private Label attemptsLabel;         // FIXED: was missing in FXML

    // ── Buttons ────────────────────────────────────────────────
    @FXML private Button resendBtn;            // FIXED: was missing fx:id in FXML
    @FXML private Button verifyBtn;            // FIXED: was missing fx:id in FXML

    // ── Internal state ─────────────────────────────────────────
    private Timeline countdownTimer;
    private int secondsRemaining = 299;        // 4:59
    private int failedAttempts   = 0;
    private static final int MAX_ATTEMPTS = 3;

    // Simulated OTP — replace with real OTP service later
    // TODO: Get this from server (sent via SMS)
    private String generatedOtp = "123456";

    // ── Session data passed from LoginController ───────────────
    private String staffPhone  = "+254712345678";
    private String branchName  = "Nairobi HQ";
    private String staffRole   = "Manager";
    private String staffName   = "John Doe";

    // ───────────────────────────────────────────────────────────
    // Called by LoginController to pass session data
    // ───────────────────────────────────────────────────────────
    public void initData(String phone, String branch,
                         String role, String name) {
        if (phone == null || branch == null || role == null || name == null) {
            System.err.println("Warning: Null data passed to OTP controller");
            return;
        }

        this.staffPhone = phone;
        this.branchName = branch;
        this.staffRole  = role;
        this.staffName  = name;

        // Update labels with actual data
        updateLabels();

        // TODO: Trigger real OTP send here via HQ server
        System.out.println("═══ 2FA OTP VERIFICATION ═══");
        System.out.println("Phone:       " + phone);
        System.out.println("Branch:      " + branch);
        System.out.println("Role:        " + role);
        System.out.println("Name:        " + name);
        System.out.println("Simulated OTP: " + generatedOtp);
        System.out.println("═══════════════════════════");
    }

    /**
     * Update labels with actual session data
     */
    private void updateLabels() {
        if (otpInfoLabel != null) {
            String masked = maskPhone(staffPhone);
            otpInfoLabel.setText("OTP sent to " + masked);
        }
        if (branchInfoLabel != null) {
            branchInfoLabel.setText(branchName);
        }
    }

    // ───────────────────────────────────────────────────────────
    // INITIALIZE
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Setup OTP field navigation (auto-advance)
        setupOtpField(otp1, null, otp2);
        setupOtpField(otp2, otp1, otp3);
        setupOtpField(otp3, otp2, otp4);
        setupOtpField(otp4, otp3, otp5);
        setupOtpField(otp5, otp4, otp6);
        setupOtpField(otp6, otp5, null);

        // Initialize UI state
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }

        // Start countdown timer
        startCountdown();

        // Request focus on first OTP field
        if (otp1 != null) {
            otp1.requestFocus();
        }
    }

    // ───────────────────────────────────────────────────────────
    // VERIFY OTP BUTTON
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleVerify() {
        // Check if account is locked
        if (failedAttempts >= MAX_ATTEMPTS) {
            showError("Account locked. Too many failed attempts. Please try again later.");
            if (verifyBtn != null) {
                verifyBtn.setDisable(true);
            }
            return;
        }

        // Collect OTP from all 6 fields
        String entered = getEnteredOtp();

        // Validate OTP length
        if (entered.length() < 6) {
            showError("Please enter the full 6-digit OTP.");
            return;
        }

        // Validate OTP digits only
        if (!entered.matches("[0-9]{6}")) {
            showError("OTP must contain only digits.");
            return;
        }

        // Check OTP correctness
        if (!entered.equals(generatedOtp)) {
            failedAttempts++;
            int remaining = MAX_ATTEMPTS - failedAttempts;

            if (remaining > 0) {
                showError("Invalid OTP. " + remaining + " attempt(s) remaining.");
            } else {
                showError("Account locked for 15 minutes due to too many failed attempts.");
                lockAllOtpFields();
                if (verifyBtn != null) {
                    verifyBtn.setDisable(true);
                }
            }
            clearOtpFields();
            return;
        }

        // ✅ OTP correct — proceed to dashboard
        System.out.println("✅ OTP verification successful!");
        System.out.println("User: " + staffName + " | Role: " + staffRole);

        stopCountdown();
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        // ══════════════════════════════════════════════════════
        // ROUTING LOGIC
        // Nairobi HQ  → full sidebar dashboard (dashboard.fxml)
        // Other branches → tab-based branch dashboard
        // ══════════════════════════════════════════════════════
        if (branchName.equalsIgnoreCase("Nairobi HQ")) {
            navigateToHQDashboard();
        } else {
            navigateToBranchDashboard();
        }
    }

    // ───────────────────────────────────────────────────────────
    // RESEND OTP
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleResendOtp() {
        System.out.println("OTP resend requested.");

        // Reset all states
        clearOtpFields();
        if (resendBtn != null) {
            resendBtn.setVisible(false);
        }
        if (timerLabel != null) {
            timerLabel.setVisible(true);
        }

        failedAttempts = 0;
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        if (verifyBtn != null) {
            verifyBtn.setDisable(false);
        }

        unlockAllOtpFields();
        startCountdown();

        // TODO: Call real OTP service to send new OTP via SMS
        System.out.println("OTP resent to " + staffPhone);
        System.out.println("Simulated OTP (for testing): " + generatedOtp);
    }

    // ───────────────────────────────────────────────────────────
    // BACK TO LOGIN
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleBackToLogin() {
        System.out.println("User returning to login screen.");
        stopCountdown();
        navigateToLogin();
    }

    // ── Nairobi HQ → full sidebar ──────────────────────────────
    private void navigateToHQDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();
            DashboardController dc = loader.getController();
            dc.initSession(staffName, staffRole, branchName);
            Stage stage = (Stage) verifyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load HQ dashboard. Check console.");
        }
    }

    // ── Branch → tab-based dashboard ──────────────────────────
    private void navigateToBranchDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("branchDashboard.fxml"));
            Parent root = loader.load();
            BranchDashboardController bc = loader.getController();
            bc.initSession(staffName, staffRole, branchName);
            Stage stage = (Stage) verifyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load branch dashboard. Check console.");
        }
    }

    // ───────────────────────────────────────────────────────────
    // HELPERS - OTP FIELD MANAGEMENT
    // ───────────────────────────────────────────────────────────

    /**
     * Setup each OTP input field with auto-advance and backspace navigation
     */
    private void setupOtpField(TextField current, TextField prev, TextField next) {
        if (current == null) return;

        // Only allow single digit input
        current.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1) {
                // Keep only first character
                current.setText(newVal.substring(0, 1));
                return;
            }

            // Only allow digits
            if (newVal.length() == 1 && !newVal.matches("[0-9]")) {
                current.setText("");
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

    /**
     * Get the complete OTP entered by user
     */
    private String getEnteredOtp() {
        StringBuilder otp = new StringBuilder();
        otp.append(otp1.getText());
        otp.append(otp2.getText());
        otp.append(otp3.getText());
        otp.append(otp4.getText());
        otp.append(otp5.getText());
        otp.append(otp6.getText());
        return otp.toString();
    }

    /**
     * Clear all OTP input fields and focus on first
     */
    private void clearOtpFields() {
        otp1.clear(); otp2.clear(); otp3.clear();
        otp4.clear(); otp5.clear(); otp6.clear();
        if (otp1 != null) {
            otp1.requestFocus();
        }
    }

    /**
     * Disable all OTP fields (when account is locked)
     */
    private void lockAllOtpFields() {
        otp1.setDisable(true); otp2.setDisable(true); otp3.setDisable(true);
        otp4.setDisable(true); otp5.setDisable(true); otp6.setDisable(true);
    }

    /**
     * Enable all OTP fields (when resending)
     */
    private void unlockAllOtpFields() {
        otp1.setDisable(false); otp2.setDisable(false); otp3.setDisable(false);
        otp4.setDisable(false); otp5.setDisable(false); otp6.setDisable(false);
    }

    /**
     * Display error message to user
     */
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

    /**
     * Mask phone number for display
     * Example: +254712345678 → +254 7XX XXX 5678
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 10) return phone;

        // Remove any spaces or dashes
        String cleaned = phone.replace(" ", "").replace("-", "");

        // Show country code, first digit, and last 4 digits
        // +254712345678 → +254 7XX XXX 5678
        if (cleaned.length() >= 13) {
            String countryCode = cleaned.substring(0, 4);  // +254
            String lastFour = cleaned.substring(cleaned.length() - 4); // 5678
            return countryCode + " 7XX XXX " + lastFour;
        }

        return cleaned;
    }

    // ───────────────────────────────────────────────────────────
    // COUNTDOWN TIMER
    // ───────────────────────────────────────────────────────────

    /**
     * Start countdown timer for OTP resend
     */
    private void startCountdown() {
        stopCountdown();
        secondsRemaining = 299;  // 4:59

        if (timerLabel != null) {
            timerLabel.setVisible(true);
        }
        if (resendBtn != null) {
            resendBtn.setVisible(false);
        }

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;

            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            if (timerLabel != null) {
                timerLabel.setText(String.format("Resend code in %02d:%02d", minutes, seconds));
            }

            // When timer expires
            if (secondsRemaining <= 0) {
                stopCountdown();
                if (timerLabel != null) {
                    timerLabel.setVisible(false);
                }
                if (resendBtn != null) {
                    resendBtn.setVisible(true);
                }
            }
        }));

        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    /**
     * Stop countdown timer
     */
    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }

    // ───────────────────────────────────────────────────────────
    // NAVIGATION
    // ───────────────────────────────────────────────────────────

    /**
     * Navigate to dashboard after successful OTP verification
     */
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("dashboard.fxml"));

            if (loader.getLocation() == null) {
                showError("Dashboard page not found.");
                return;
            }

            Parent root = loader.load();

            // Pass session data to dashboard controller
            try {
                DashboardController dc = loader.getController();
                dc.initSession(staffName, staffRole, branchName);
            } catch (Exception e) {
                System.err.println("Warning: Could not pass data to DashboardController");
            }

            Stage stage = (Stage) verifyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load dashboard. Please try again.");
        }
    }

    /**
     * Navigate back to login screen
     */
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("login-view.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("Login page not found.");
                return;
            }

            Parent root = loader.load();
            Stage stage = (Stage) verifyBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);

            // Re-center window
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            System.err.println("Error navigating to login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
