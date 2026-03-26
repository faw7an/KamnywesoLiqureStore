package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CustomerController implements DashboardChild {

    // ── Stat labels ────────────────────────────────────────────
    @FXML private Label totalCustomersLabel;
    @FXML private Label registeredCountLabel;
    @FXML private Label walkInCountLabel;
    @FXML private Label newThisWeekLabel;

    // ── Search/filter ──────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private ComboBox<String> branchFilter;
    @FXML private ComboBox<String> typeFilter;

    // ── Table ──────────────────────────────────────────────────
    @FXML private VBox customerTableBody;

    // ── Detail panel ───────────────────────────────────────────
    @FXML private Label detailAvatarLabel;
    @FXML private Label detailNameLabel;
    @FXML private Label detailPhoneLabel;
    @FXML private Label detailBranchLabel;
    @FXML private Label detailOrdersLabel;
    @FXML private Label detailSpentLabel;
    @FXML private Label detailLoyaltyLabel;
    @FXML private Label detailSinceLabel;

    // ── Session ────────────────────────────────────────────────
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

        typeFilter.getItems().addAll(
                "All Types", "Registered", "Walk-in");
        typeFilter.setValue("All Types");
    }

    @FXML
    private void handleSearch() {
        String query  = searchField.getText().trim();
        String branch = branchFilter.getValue();
        String type   = typeFilter.getValue();
        System.out.println("Searching: " + query +
                " | Branch: " + branch + " | Type: " + type);
        // TODO: Filter customer table from database
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleAddCustomer() {
        System.out.println("Add new customer clicked.");
        // TODO: Open add customer dialog
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleViewCustomer() {
        // TODO: Load selected customer details into right panel
        System.out.println("View customer clicked.");
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handlePlaceOrderForCustomer() {
        if (dashboard != null) {
            dashboard.showOrderProcessing();
        }
    }

    @FXML
    private void handleViewFullHistory() {
        System.out.println("View full order history for customer.");
        // TODO: Open full order history dialog/page
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
