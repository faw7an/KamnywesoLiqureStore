package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class OrderHistoryController implements DashboardChild, BranchChild {

    @FXML private Label subTitleLabel;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;
    @FXML private VBox ordersBody;

    private DashboardController hqDashboard;
    private BranchDashboardController branchDashboard;
    private String staffName, role, branch;

    // DashboardChild (HQ)
    @Override public void setDashboardController(DashboardController d) { this.hqDashboard = d; }

    // BranchChild (branch)
    @Override public void setBranchDashboardController(BranchDashboardController d) { this.branchDashboard = d; }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;
        if (subTitleLabel != null)
            subTitleLabel.setText("Orders placed at " + branch);
        resetTimer();
    }

    @FXML
    public void initialize() {
        statusFilter.getItems().addAll(
                "All Statuses", "Completed", "Pending", "Cancelled");
        statusFilter.setValue("All Statuses");
    }

    @FXML
    private void handleApplyFilter() {
        System.out.println("Filtering orders...");
        // TODO: Query database with filters
        resetTimer();
    }

    @FXML
    private void handleViewReceipt() {
        System.out.println("View receipt clicked.");
        // TODO: Open receipt modal with order details
        resetTimer();
    }

    private void resetTimer() {
        if (hqDashboard    != null) hqDashboard.resetSessionTimer();
        if (branchDashboard != null) branchDashboard.resetSessionTimer();
    }
}
