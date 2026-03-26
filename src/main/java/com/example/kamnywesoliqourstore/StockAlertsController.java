package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;

public class StockAlertsController implements DashboardChild {

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML public void initialize() {}

    @FXML private void handleApproveRestock() {
        System.out.println("Restock request approved — dispatching from HQ.");
        // TODO: Create dispatch order and update stock request status
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleDismissAlert() {
        System.out.println("Alert dismissed.");
        // TODO: Mark alert as acknowledged in database
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleAcknowledgeAll() {
        System.out.println("All alerts acknowledged.");
        // TODO: Mark all alerts as acknowledged
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
