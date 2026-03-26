package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class OrderTrackingController implements DashboardChild {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> branchFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker datePicker;
    @FXML private GridPane trackingGrid;
    @FXML private Label pendingCountLabel;
    @FXML private Label inTransitCountLabel;
    @FXML private Label receivedCountLabel;
    @FXML private Label cancelledCountLabel;

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
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    public void initialize() {
        branchFilter.getItems().addAll(
                "All Branches", "Nairobi HQ",
                "Mombasa Branch", "Kisumu Branch", "Nakuru Branch");
        branchFilter.setValue("All Branches");

        statusFilter.getItems().addAll(
                "All Statuses", "Pending", "Dispatched",
                "In Transit", "Confirmed", "Received", "Cancelled");
        statusFilter.setValue("All Statuses");
    }

    @FXML
    private void handleApplyFilter() {
        String search = searchField.getText().trim();
        String branch = branchFilter.getValue();
        String status = statusFilter.getValue();
        System.out.println("Filter: " + search + " | " + branch + " | " + status);
        // TODO: Filter tracking cards dynamically from database
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleClearFilter() {
        searchField.clear();
        branchFilter.setValue("All Branches");
        statusFilter.setValue("All Statuses");
        datePicker.setValue(null);
    }

    @FXML
    private void handleDispatchNew() {
        System.out.println("Dispatch new order clicked.");
        // TODO: Open dispatch dialog or navigate to inventory dispatch
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleApproveDispatch() {
        System.out.println("Order ORD-8831 approved and dispatched.");
        // TODO: Update order status in database and refresh cards
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleRejectOrder() {
        System.out.println("Order ORD-8831 rejected.");
        // TODO: Update order status in database and refresh cards
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
