package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DashboardHomeController implements DashboardChild {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label netProfitLabel;
    @FXML private Label ordersTodayLabel;
    @FXML private Label stockAlertsLabel;
    @FXML private VBox ordersTableBody;

    private DashboardController dashboard;
    private String staffName;
    private String role;
    private String branch;

    @Override
    public void setDashboardController(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;

        // Personalised greeting
        String firstName = staffName.split(" ")[0];
        welcomeLabel.setText("Good morning, " + firstName);

        // Live date + branch
        LocalDate today = LocalDate.now();
        String dayName = today.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String formatted = today.format(
                DateTimeFormatter.ofPattern("d MMMM yyyy"));
        dateLabel.setText(dayName + ", " + formatted + " · " + branch);
    }

    @FXML
    public void initialize() {
        // Static data — replace with DB calls later
    }

    @FXML
    private void handleRefresh() {
        if (dashboard != null) dashboard.resetSessionTimer();
        System.out.println("Dashboard refreshed.");
    }

    @FXML
    private void handleViewAllOrders() {
        if (dashboard != null) dashboard.showOrderProcessing();
    }

    @FXML
    private void handleViewAlerts() {
        if (dashboard != null) dashboard.showAlerts();
    }

    @FXML
    private void handleNewOrder() {
        if (dashboard != null) dashboard.showOrderProcessing();
    }

    @FXML
    private void handleViewInventory() {
        if (dashboard != null) dashboard.showInventory();
    }

    @FXML
    private void handleGenerateReport() {
        if (dashboard != null) dashboard.showReports();
    }

    @FXML
    private void handleManageStaff() {
        if (dashboard != null) dashboard.showStaff();
    }
}
