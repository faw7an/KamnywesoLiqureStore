package com.example.kamnywesoliqourstore;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class BranchDashboardController {

    // ── Top bar ────────────────────────────────────────────────
    @FXML private Label branchPill;
    @FXML private Label staffNameLabel;
    @FXML private Label staffRoleLabel;
    @FXML private Label avatarLabel;
    @FXML private Label sessionTimerLabel;
    @FXML private Button alertBadge;
    @FXML private Button sidebarToggleBtn;

    // ── Sidebar ────────────────────────────────────────────────
    @FXML private VBox sidebar;
    @FXML private VBox sidebarStaffCard;
    @FXML private Label sidebarNameLabel;
    @FXML private Label sidebarRoleLabel;
    @FXML private Label sidebarBranchBadge;
    @FXML private Label dateLabel;
    @FXML private Label secOperations;
    @FXML private Label secCustomers;
    @FXML private Label secStock;

    // ── Nav buttons ────────────────────────────────────────────
    @FXML private Button navPlaceOrder;
    @FXML private Button navOrderHistory;
    @FXML private Button navTrackStock;
    @FXML private Button navLoyalty;
    @FXML private Button navReturns;
    @FXML private Button navAlerts;

    // ── Content area ───────────────────────────────────────────
    @FXML private StackPane contentArea;

    // ── Session ────────────────────────────────────────────────
    private Timeline sessionTimer;
    private int sessionSecondsLeft = 30 * 60;
    private boolean sidebarExpanded = true;

    // ── Branch data ────────────────────────────────────────────
    private String currentStaffName = "Staff Member";
    private String currentRole      = "Cashier";
    private String currentBranch    = "Mombasa Branch";

    // Nav button labels — stored so collapse/expand works correctly
    private static final String[] NAV_LABELS_FULL = {
            "  Place Order",
            "  Order History",
            "  Track Stock Orders",
            "  Loyalty Programme",
            "  Stock Returns",
            "  Stock Alerts"
    };

    // Use simple ASCII symbols for collapsed mode — HTML entities DON'T work in JavaFX
    private static final String[] NAV_LABELS_ICON = {
            "\uD83D\uDED2", // 🛒 Shopping Cart
            "\uD83D\uDCCB", // 📋 Order History
            "\uD83D\uDCE6", // 📦 Track Stock Orders
            "\u2B50",      // ⭐ Loyalty Programme
            "\u21A9",     // ↩ Stock Returns
            "\u26A0",      // ⚠ Stock Alerts
    };

    private static final String ACTIVE_STYLE =
            "-fx-background-color: rgba(224,74,42,0.18); " +
                    "-fx-border-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12; " +
                    "-fx-font-weight: bold; " +
                    "-fx-alignment: CENTER_LEFT; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0 0 0 14;";

    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; " +
                    "-fx-border-color: transparent; " +
                    "-fx-text-fill: #aaaaaa; " +
                    "-fx-font-size: 12; " +
                    "-fx-alignment: CENTER_LEFT; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0 0 0 17;";

    // ─────────────────────────────────────────────────────────
    // INIT SESSION
    // ─────────────────────────────────────────────────────────
    public void initSession(String name, String role, String branch) {
        this.currentStaffName = name;
        this.currentRole      = role;
        this.currentBranch    = branch;

        // Top bar
        staffNameLabel.setText(name);
        staffRoleLabel.setText(role);
        avatarLabel.setText(getInitials(name));
        branchPill.setText(branch);

        // Sidebar
        sidebarNameLabel.setText(name);
        sidebarRoleLabel.setText(role + " · " + branch);
        sidebarBranchBadge.setText(branch.toUpperCase());

        // Date
        LocalDate today    = LocalDate.now();
        String dayShort    = today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String formatted   = today.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        dateLabel.setText(dayShort + ", " + formatted);

        // Role-based visibility
        applyRolePermissions(role);

        // Start session timer
        startSessionTimer();

        // Default to Place Order
        showPlaceOrder();
    }

    @FXML
    public void initialize() {
        setActiveNav(navPlaceOrder);
    }

    // ─────────────────────────────────────────────────────────
    // ROLE PERMISSIONS
    // ─────────────────────────────────────────────────────────
    private void applyRolePermissions(String role) {
        switch (role) {
            case "Cashier":
                hide(navReturns);
                hide(navAlerts);
                hide(secStock);
                break;
            case "Stock Controller":
                hide(navPlaceOrder);
                hide(navLoyalty);
                hide(secCustomers);
                showTrackStock();
                break;
            case "Supervisor":
            case "Manager":
                // All branch modules visible
                break;
        }
    }

    private void hide(Node node) {
        if (node != null) { node.setVisible(false); node.setManaged(false); }
    }

    // ─────────────────────────────────────────────────────────
    // SIDEBAR TOGGLE — uses real text, NOT HTML entities
    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleSidebarToggle() {
        sidebarExpanded = !sidebarExpanded;
        Button[] navButtons = { navPlaceOrder, navOrderHistory, navTrackStock,
                navLoyalty, navReturns, navAlerts };

        if (sidebarExpanded) {
            // Expand
            sidebar.setPrefWidth(200.0);
            sidebar.setMinWidth(200.0);

            showNode(sidebarStaffCard);
            showNode(dateLabel);
            showNode(secOperations);
            showNode(secCustomers);
            showNode(secStock);

            for (int i = 0; i < navButtons.length; i++) {
                if (navButtons[i].isManaged()) {
                    navButtons[i].setText(NAV_LABELS_FULL[i]);
                    navButtons[i].setPrefWidth(200.0);
                }
            }
        } else {
            // Collapse
            sidebar.setPrefWidth(52.0);
            sidebar.setMinWidth(52.0);

            hideNode(sidebarStaffCard);
            hideNode(dateLabel);
            hideNode(secOperations);
            hideNode(secCustomers);
            hideNode(secStock);

            for (int i = 0; i < navButtons.length; i++) {
                if (navButtons[i].isManaged()) {
                    navButtons[i].setText(NAV_LABELS_ICON[i]);
                    navButtons[i].setPrefWidth(52.0);
                }
            }
        }
    }

    private void showNode(Node n) { if (n != null) { n.setVisible(true); n.setManaged(true); } }
    private void hideNode(Node n) { if (n != null) { n.setVisible(false); n.setManaged(false); } }

    // ─────────────────────────────────────────────────────────
    // NAV HANDLERS
    // ─────────────────────────────────────────────────────────
    @FXML public void showPlaceOrder()   { setActiveNav(navPlaceOrder);   loadContent("orderProcessing.fxml"); }
    @FXML public void showOrderHistory() { setActiveNav(navOrderHistory);  loadContent("orderHistory.fxml"); }
    @FXML public void showTrackStock()   { setActiveNav(navTrackStock);   loadContent("orderTracking.fxml"); }
    @FXML public void showLoyalty()      { setActiveNav(navLoyalty);      loadContent("loyalty.fxml"); }
    @FXML public void showReturns()      { setActiveNav(navReturns);      loadContent("stockReturns.fxml"); }
    @FXML public void showAlerts()       { setActiveNav(navAlerts);       loadContent("stockAlerts.fxml"); }

    @FXML private void handleAlertClick() { showAlerts(); }

    // ─────────────────────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleLogout() {
        stopSessionTimer();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setWidth(620);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────
    // CONTENT LOADER
    // ─────────────────────────────────────────────────────────
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();
            Object controller = loader.getController();

            if (controller instanceof BranchChild) {
                ((BranchChild) controller).setBranchDashboardController(this);
                ((BranchChild) controller).setSessionData(currentStaffName, currentRole, currentBranch);
            } else if (controller instanceof DashboardChild) {
                ((DashboardChild) controller).setSessionData(currentStaffName, currentRole, currentBranch);
            }

            contentArea.getChildren().setAll(content);
            resetSessionTimer();

        } catch (Exception e) {
            e.printStackTrace();
            Label placeholder = new Label(fxmlFile.replace(".fxml", "") + "\n\nModule loading error — check console.");
            placeholder.setStyle("-fx-text-fill: #888888; -fx-font-size: 14; -fx-text-alignment: center;");
            placeholder.setAlignment(Pos.CENTER);
            contentArea.getChildren().setAll(placeholder);
        }
    }

    // ─────────────────────────────────────────────────────────
    // ACTIVE NAV HIGHLIGHT
    // ─────────────────────────────────────────────────────────
    private void setActiveNav(Button active) {
        for (Button btn : new Button[]{ navPlaceOrder, navOrderHistory, navTrackStock,
                navLoyalty, navReturns, navAlerts }) {
            if (btn != null) btn.setStyle(INACTIVE_STYLE);
        }
        if (active != null) active.setStyle(ACTIVE_STYLE);
    }

    // ─────────────────────────────────────────────────────────
    // SESSION TIMER
    // ─────────────────────────────────────────────────────────
    private void startSessionTimer() {
        stopSessionTimer();
        sessionSecondsLeft = 30 * 60;
        sessionTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            sessionSecondsLeft--;
            int min = sessionSecondsLeft / 60;
            int sec = sessionSecondsLeft % 60;
            sessionTimerLabel.setText(String.format("%02d:%02d", min, sec));
            if (sessionSecondsLeft <= 300 && sessionSecondsLeft > 60)
                sessionTimerLabel.setStyle("-fx-text-fill: #D97706; -fx-font-size: 11; -fx-font-weight: bold;");
            if (sessionSecondsLeft <= 60)
                sessionTimerLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 11; -fx-font-weight: bold;");
            if (sessionSecondsLeft <= 0) { stopSessionTimer(); handleLogout(); }
        }));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        sessionTimer.play();
    }

    private void stopSessionTimer() { if (sessionTimer != null) sessionTimer.stop(); }

    public void resetSessionTimer() {
        sessionSecondsLeft = 30 * 60;
        sessionTimerLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 11; -fx-font-weight: bold;");
    }

    public void updateAlertBadge(int count) {
        if (count > 0) {
            alertBadge.setText(count + " Stock Alert" + (count > 1 ? "s" : ""));
            alertBadge.setVisible(true);
            alertBadge.setManaged(true);
        } else {
            alertBadge.setVisible(false);
            alertBadge.setManaged(false);
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    public String getCurrentRole()   { return currentRole; }
    public String getCurrentBranch() { return currentBranch; }
    public String getCurrentStaff()  { return currentStaffName; }
}
