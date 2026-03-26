package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class StockReturnsController implements DashboardChild {

    @FXML private ComboBox<String> returnItemCombo;
    @FXML private TextField returnBranchField;
    @FXML private TextField returnQtyField;
    @FXML private TextArea returnReasonField;
    @FXML private Label returnErrorLabel;

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        returnBranchField.setText(b);
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    public void initialize() {
        returnItemCombo.getItems().addAll(
                "Alvaro 500ml", "Fanta Orange 500ml",
                "Coca Cola 500ml", "Sprite 500ml",
                "Dasani Water 500ml", "Shiraz Red Wine 750ml");
    }

    @FXML
    private void handleSubmitReturn() {
        String item   = returnItemCombo.getValue();
        String qty    = returnQtyField.getText().trim();
        String reason = returnReasonField.getText().trim();

        if (item == null) {
            showError("Please select an item to return.");
            return;
        }
        if (qty.isEmpty() || !qty.matches("\\d+")) {
            showError("Please enter a valid quantity.");
            return;
        }
        if (reason.isEmpty()) {
            showError("Please provide a reason for the return.");
            return;
        }

        String returnId = "RET-" + String.format("%03d", (int)(Math.random() * 900 + 100));
        System.out.println("Return request submitted:");
        System.out.println("  ID: " + returnId);
        System.out.println("  Item: " + item);
        System.out.println("  Qty: " + qty);
        System.out.println("  Branch: " + branch);
        System.out.println("  Reason: " + reason);

        // TODO: Save to database and refresh table
        returnItemCombo.setValue(null);
        returnQtyField.clear();
        returnReasonField.clear();
        returnErrorLabel.setVisible(false);
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    private void showError(String msg) {
        returnErrorLabel.setText(msg);
        returnErrorLabel.setVisible(true);
    }
}
