package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class InventoryController implements DashboardChild {

    @FXML private Label totalSkusLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label outOfStockLabel;
    @FXML private Label pendingReturnsLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> branchFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private VBox inventoryTableBody;

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    public void initialize() {
        branchFilter.getItems().addAll("All Branches","Nairobi HQ","Mombasa Branch","Kisumu Branch","Nakuru Branch");
        branchFilter.setValue("All Branches");
        statusFilter.getItems().addAll("All Status","OK","Low","Critical","Out of Stock");
        statusFilter.setValue("All Status");
    }

    @FXML private void handleAddStock() {
        System.out.println("Add stock item clicked.");
        // TODO: Open add stock dialog
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleRestock() {
        System.out.println("Restock item clicked.");
        // TODO: Open restock slide-over modal with quantity input
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleApproveReturn() {
        System.out.println("Return approved.");
        // TODO: Update return status and dispatch replacement
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML private void handleRejectReturn() {
        System.out.println("Return rejected.");
        // TODO: Update return status
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
