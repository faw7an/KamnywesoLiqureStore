package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class LoyaltyController implements DashboardChild {

    @FXML private TextField loyaltySearchField;
    @FXML private Label tierLabel, cardCustomerName, cardId, cardPhone;
    @FXML private Label pointsBalance, tierProgressLabel;
    @FXML private ProgressBar tierProgressBar;
    @FXML private TextField newMemberName, newMemberPhone;
    @FXML private ComboBox<String> startingTier;

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    public void initialize() {
        startingTier.getItems().addAll("Bronze", "Silver", "Gold");
        startingTier.setValue("Bronze");
    }

    @FXML
    private void handleSearchLoyalty() {
        String query = loyaltySearchField.getText().trim();
        System.out.println("Searching loyalty member: " + query);
        // TODO: Query database and populate loyalty card with customer data
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleRegisterMember() {
        String name  = newMemberName.getText().trim();
        String phone = newMemberPhone.getText().trim();
        String tier  = startingTier.getValue();

        if (name.isEmpty() || phone.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        // Generate card ID
        String cardID = "LC-" + (10000 + (int)(Math.random() * 89999));
        System.out.println("New loyalty member registered:");
        System.out.println("  Name: " + name);
        System.out.println("  Phone: " + phone);
        System.out.println("  Tier: " + tier);
        System.out.println("  Card ID: " + cardID);

        // TODO: Save to database and display new card
        newMemberName.clear();
        newMemberPhone.clear();
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
