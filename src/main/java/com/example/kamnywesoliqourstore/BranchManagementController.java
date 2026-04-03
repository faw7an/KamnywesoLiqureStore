package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;

public class BranchManagementController implements DashboardChild {

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML public void initialize() {}

    @FXML private void handleViewBranch() {
        System.out.println("View branch details clicked.");
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleSendStock() {
        System.out.println("Send stock to branch clicked.");
        // TODO: Open send stock dialog with quantity inputs per item
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleSendStockToAll() {
        System.out.println("Send stock to all branches clicked.");
        // TODO: Open bulk dispatch dialog
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
