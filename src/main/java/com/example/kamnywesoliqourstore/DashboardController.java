package com.example.kamnywesoliqourstore;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {

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

    // ── Nav buttons ────────────────────────────────────────────
    @FXML private Button navDashboard;
    @FXML private Button navOrders;
    @FXML private Button navTracking;
    @FXML private Button navCustomers;
    @FXML private Button navInventory;
    @FXML private Button navBranches;
    @FXML private Button navStaff;
    @FXML private Button navReports;
    @FXML private Button navAlerts;
    @FXML private Button navLoyalty;
    @FXML private Button navReturns;

    // ── Section labels ─────────────────────────────────────────
    @FXML private Label secMainLabel;
    @FXML private Label secMgmtLabel;
    @FXML private Label secReportsLabel;

    // ── Content area ───────────────────────────────────────────
    @FXML private StackPane contentArea;

    // ── Session state ──────────────────────────────────────────
    private Timeline sessionTimer;
    private int sessionSecondsLeft = 30 * 60; // 30 minutes
    private boolean sidebarExpanded = true;

    // ── Current user data ──────────────────────────────────────
    private String currentStaffName = "John Doe";
    private String currentRole      = "Manager";
    private String currentBranch    = "Nairobi HQ";

    // Active nav button style
    private static final String ACTIVE_STYLE =
            "-fx-background-color: rgba(224,74,42,0.18); " +
                    "-fx-border-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 13; " +
                    "-fx-alignment: CENTER_LEFT; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0 0 0 14;";

    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; " +
                    "-fx-border-color: transparent; " +
                    "-fx-text-fill: #aaaaaa; " +
                    "-fx-font-size: 13; " +
                    "-fx-alignment: CENTER_LEFT; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0 0 0 17;";

    // ───────────────────────────────────────────────────────────
    // Called by OtpVerificationController with session data
    // ───────────────────────────────────────────────────────────
    public void initSession(String name, String role, String branch) {
        this.currentStaffName = name;
        this.currentRole      = role;
        this.currentBranch    = branch;

        // Top bar
        staffNameLabel.setText(name);
        staffRoleLabel.setText(role);
        branchPill.setText(branch);
        avatarLabel.setText(getInitials(name));

        // Sidebar
        sidebarNameLabel.setText(name);
        sidebarRoleLabel.setText(role + " · " + branch);

        // Apply role-based nav visibility
        applyRolePermissions(role);

        // Start session countdown
        startSessionTimer();

        // Load dashboard home as default content
        loadContent("dashboardHome.fxml");
    }

    @FXML
    public void initialize() {
        // Default state — will be properly set by initSession()
        setActiveNav(navDashboard);
    }

    // ───────────────────────────────────────────────────────────
    // ROLE-BASED SIDEBAR VISIBILITY
    // ───────────────────────────────────────────────────────────
    private void applyRolePermissions(String role) {
        switch (role) {
            case "Cashier":
                hide(navTracking);
                hide(navInventory);
                hide(navBranches);
                hide(navStaff);
                hide(navReports);
                hide(navAlerts);
                hide(navReturns);
                hide(secMgmtLabel);
                hide(secReportsLabel);
                break;

            case "Supervisor":
                hide(navBranches);
                hide(navStaff);
                break;

            case "Stock Controller":
                hide(navOrders);
                hide(navTracking);
                hide(navCustomers);
                hide(navBranches);
                hide(navStaff);
                hide(navReports);
                hide(navLoyalty);
                break;

            case "Manager":
            default:
                // Manager sees everything — no hiding needed
                break;
        }
    }

    private void hide(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }

    // ───────────────────────────────────────────────────────────
    // SIDEBAR TOGGLE (collapse / expand)
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleSidebarToggle() {
        sidebarExpanded = !sidebarExpanded;
        if (sidebarExpanded) {
            sidebar.setPrefWidth(200.0);
            sidebarStaffCard.setVisible(true);
            sidebarStaffCard.setManaged(true);
            secMainLabel.setVisible(true);
            secMainLabel.setManaged(true);
            secMgmtLabel.setVisible(true);
            secMgmtLabel.setManaged(true);
            secReportsLabel.setVisible(true);
            secReportsLabel.setManaged(true);
            // Show full button text
            setNavText(navDashboard,   "  Dashboard");
            setNavText(navOrders,      "  Order Processing");
            setNavText(navTracking,    "  Order Tracking");
            setNavText(navCustomers,   "  Customers");
            setNavText(navInventory,   "  Inventory");
            setNavText(navBranches,    "  Branch Management");
            setNavText(navStaff,       "  Staff Management");
            setNavText(navReports,     "  Sales Reports");
            setNavText(navAlerts,      "  Stock Alerts");
            setNavText(navLoyalty,     "  Loyalty Programme");
            setNavText(navReturns,     "  Stock Returns");
        } else {
            sidebar.setPrefWidth(56.0);
            sidebarStaffCard.setVisible(false);
            sidebarStaffCard.setManaged(false);
            secMainLabel.setVisible(false);
            secMainLabel.setManaged(false);
            secMgmtLabel.setVisible(false);
            secMgmtLabel.setManaged(false);
            secReportsLabel.setVisible(false);
            secReportsLabel.setManaged(false);
            // Icon-only mode using symbols
            setNavText(navDashboard,  "&#8962;");
            setNavText(navOrders,     "&#128722;");
            setNavText(navTracking,   "&#128666;");
            setNavText(navCustomers,  "&#128101;");
            setNavText(navInventory,  "&#128230;");
            setNavText(navBranches,   "&#127968;");
            setNavText(navStaff,      "&#128100;");
            setNavText(navReports,    "&#128202;");
            setNavText(navAlerts,     "&#9888;");
            setNavText(navLoyalty,    "&#11088;");
            setNavText(navReturns,    "&#8617;");
        }
    }

    private void setNavText(Button btn, String text) {
        if (btn.isManaged()) btn.setText(text);
    }

    // ───────────────────────────────────────────────────────────
    // NAV HANDLERS
    // ───────────────────────────────────────────────────────────
    @FXML public void showDashboardHome()   { setActiveNav(navDashboard);  loadContent("dashboardHome.fxml"); }
    @FXML public void showOrderProcessing() { setActiveNav(navOrders);     loadContent("orderProcessing.fxml"); }
    @FXML public void showOrderTracking()   { setActiveNav(navTracking);   loadContent("orderTracking.fxml"); }
    @FXML public void showCustomers()       { setActiveNav(navCustomers);  loadContent("customers.fxml"); }
    @FXML public void showInventory()       { setActiveNav(navInventory);  loadContent("inventory.fxml"); }
    @FXML public void showBranches()        { setActiveNav(navBranches);   loadContent("branches.fxml"); }
    @FXML public void showStaff()           { setActiveNav(navStaff);      loadContent("staffManagement.fxml"); }
    @FXML public void showReports()         { setActiveNav(navReports);    loadContent("salesReports.fxml"); }
    @FXML public void showAlerts()          { setActiveNav(navAlerts);     loadContent("stockAlerts.fxml"); }
    @FXML public void showLoyalty()         { setActiveNav(navLoyalty);    loadContent("loyalty.fxml"); }
    @FXML public void showReturns()         { setActiveNav(navReturns);    loadContent("stockReturns.fxml"); }

    @FXML
    private void handleAlertClick() {
        setActiveNav(navAlerts);
        loadContent("stockAlerts.fxml");
    }

    // ───────────────────────────────────────────────────────────
    // LOGOUT
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleLogout() {
        stopSessionTimer();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setWidth(620);
            stage.setHeight(600);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ───────────────────────────────────────────────────────────
    // CONTENT LOADER
    // ───────────────────────────────────────────────────────────
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlFile));
            Node content = loader.load();

            // Pass dashboard reference to child controllers
            Object controller = loader.getController();
            if (controller instanceof DashboardChild) {
                ((DashboardChild) controller).setDashboardController(this);
                ((DashboardChild) controller).setSessionData(
                        currentStaffName, currentRole, currentBranch);
            }

            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
            // Show placeholder if FXML not yet built
            Label placeholder = new Label(
                    fxmlFile.replace(".fxml", "")
                            + "\n\nThis module is being built...");
            placeholder.setStyle(
                    "-fx-text-fill: #888888; -fx-font-size: 16; " +
                            "-fx-alignment: center;");
            placeholder.setAlignment(javafx.geometry.Pos.CENTER);
            contentArea.getChildren().setAll(placeholder);
        }
    }

    // ───────────────────────────────────────────────────────────
    // ACTIVE NAV HIGHLIGHT
    // ───────────────────────────────────────────────────────────
    private void setActiveNav(Button active) {
        Button[] allNav = {
                navDashboard, navOrders, navTracking, navCustomers,
                navInventory, navBranches, navStaff, navReports,
                navAlerts, navLoyalty, navReturns
        };
        for (Button btn : allNav) {
            if (btn != null) btn.setStyle(INACTIVE_STYLE);
        }
        if (active != null) active.setStyle(ACTIVE_STYLE);
    }

    // ───────────────────────────────────────────────────────────
    // SESSION TIMER
    // ───────────────────────────────────────────────────────────
    private void startSessionTimer() {
        stopSessionTimer();
        sessionSecondsLeft = 30 * 60;

        sessionTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            sessionSecondsLeft--;
            int min = sessionSecondsLeft / 60;
            int sec = sessionSecondsLeft % 60;
            sessionTimerLabel.setText(
                    String.format("%02d:%02d", min, sec));

            // Turn amber when under 5 minutes
            if (sessionSecondsLeft <= 300 && sessionSecondsLeft > 60) {
                sessionTimerLabel.setStyle(
                        "-fx-text-fill: #D97706; -fx-font-size: 11; -fx-font-weight: bold;");
            }
            // Turn red when under 1 minute
            if (sessionSecondsLeft <= 60) {
                sessionTimerLabel.setStyle(
                        "-fx-text-fill: #DC2626; -fx-font-size: 11; -fx-font-weight: bold;");
            }
            // Session expired
            if (sessionSecondsLeft <= 0) {
                stopSessionTimer();
                handleLogout();
            }
        }));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        sessionTimer.play();
    }

    private void stopSessionTimer() {
        if (sessionTimer != null) sessionTimer.stop();
    }

    // Reset session timer on any activity (call from child controllers)
    public void resetSessionTimer() {
        sessionSecondsLeft = 30 * 60;
        sessionTimerLabel.setStyle(
                "-fx-text-fill: #4CAF50; -fx-font-size: 11; -fx-font-weight: bold;");
    }

    // Update alert badge count
    public void updateAlertBadge(int count) {
        if (count > 0) {
            alertBadge.setText("&#9888; " + count + " Stock Alert"
                    + (count > 1 ? "s" : ""));
            alertBadge.setVisible(true);
        } else {
            alertBadge.setVisible(false);
        }
    }

    // ───────────────────────────────────────────────────────────
    // HELPERS
    // ───────────────────────────────────────────────────────────
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1)
                + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    public String getCurrentRole()   { return currentRole; }
    public String getCurrentBranch() { return currentBranch; }
    public String getCurrentStaff()  { return currentStaffName; }
}

