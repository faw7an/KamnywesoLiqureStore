package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockReturnsController implements DashboardChild, BranchChild {

    @FXML private ComboBox<String> returnItemCombo;
    @FXML private TextField returnBranchField;
    @FXML private TextField returnQtyField;
    @FXML private TextArea returnReasonField;
    @FXML private Label returnErrorLabel;

    // NEW: HQ Branch selector
    @FXML private ComboBox<String> hqBranchSelectorCombo;
    @FXML private Label hqBranchInfoLabel;

    // NEW: HQ approval actions
    @FXML private VBox returnsTableContainer;
    @FXML private Label returnsTableTitle;

    private DashboardController hqDashboard;
    private BranchDashboardController branchDashboard;
    private String staffName, role, branch;
    private boolean isBranchMode = false;

    // ═══════════════════════════════════════════════════════════════════════════════
    // NEW: STOCK RETURN DATA STRUCTURE
    // ═══════════════════════════════════════════════════════════════════════════════
    private static class StockReturn {
        String returnId;
        String fromBranch;
        String item;
        int quantity;
        String reason;
        LocalDate dateRaised;
        String status;  // "Pending Review", "Approved", "Rejected", "Replacement Sent"
        String hqResponse;  // HQ's action/notes

        StockReturn(String id, String branch, String item, int qty, String reason,
                    LocalDate date, String status, String response) {
            this.returnId = id;
            this.fromBranch = branch;
            this.item = item;
            this.quantity = qty;
            this.reason = reason;
            this.dateRaised = date;
            this.status = status;
            this.hqResponse = response;
        }

        String getStatusColor() {
            return switch (status) {
                case "Pending Review" -> "#FEF3C7";      // Yellow
                case "Approved" -> "#D1FAE5";            // Green
                case "Rejected" -> "#FEE2E2";            // Red
                case "Replacement Sent" -> "#DBEAFE";   // Blue
                default -> "#F3F4F6";
            };
        }

        String getStatusTextColor() {
            return switch (status) {
                case "Pending Review" -> "#92400E";      // Orange
                case "Approved" -> "#065F46";            // Green
                case "Rejected" -> "#991B1B";            // Red
                case "Replacement Sent" -> "#1D4ED8";   // Blue
                default -> "#666666";
            };
        }
    }

    // NEW: RETURNS DATA STORAGE
    private Map<String, List<StockReturn>> returnsByBranch = new HashMap<>();
    private List<StockReturn> allReturns = new ArrayList<>();

    @Override
    public void setDashboardController(DashboardController d) {
        this.hqDashboard = d;
        this.isBranchMode = false;
    }

    @Override
    public void setBranchDashboardController(BranchDashboardController d) {
        this.branchDashboard = d;
        this.isBranchMode = true;
    }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;

        if (isBranchMode) {
            // ── BRANCH MODE: Show form, hide selector ──────────────
            if (returnBranchField != null) {
                returnBranchField.setText(branch);
                returnBranchField.setEditable(false);
                returnBranchField.setStyle(
                        "-fx-background-color: #F0F0F0; " +
                                "-fx-border-color: #CCCCCC; " +
                                "-fx-border-radius: 6; -fx-background-radius: 6; " +
                                "-fx-padding: 0 8 0 8; -fx-text-fill: #555555;");
            }

            // Hide HQ selector
            if (hqBranchSelectorCombo != null) {
                hqBranchSelectorCombo.setVisible(false);
                hqBranchSelectorCombo.setManaged(false);
            }
            if (hqBranchInfoLabel != null) {
                hqBranchInfoLabel.setVisible(false);
                hqBranchInfoLabel.setManaged(false);
            }

            // Update table title
            if (returnsTableTitle != null) {
                returnsTableTitle.setText("My Return Requests (" + branch + ")");
            }

            // Display only this branch's returns
            updateReturnsForBranch(branch);

        } else {
            // ── HQ MODE: Show selector, hide form ──────────────────
            if (hqBranchSelectorCombo != null) {
                hqBranchSelectorCombo.setVisible(true);
                hqBranchSelectorCombo.setManaged(true);
            }
            if (hqBranchInfoLabel != null) {
                hqBranchInfoLabel.setVisible(true);
                hqBranchInfoLabel.setManaged(true);
            }

            // Update table title
            if (returnsTableTitle != null) {
                returnsTableTitle.setText("Return Requests - All Branches");
            }

            // Display all returns or selected branch
            updateReturnsForBranch("All Branches");
        }

        resetTimer();
    }

    @FXML
    public void initialize() {
        // Initialize sample return data
        initializeSampleReturns();

        returnItemCombo.getItems().addAll(
                "Alvaro 500ml",
                "Fanta Orange 500ml",
                "Coca Cola 500ml",
                "Sprite 500ml",
                "Dasani Water 500ml",
                "Shiraz Red Wine 750ml");

        // NEW: Setup HQ branch selector
        if (hqBranchSelectorCombo != null) {
            hqBranchSelectorCombo.getItems().addAll(
                    "All Branches",
                    "Nairobi HQ",
                    "Mombasa Branch",
                    "Kisumu Branch",
                    "Nakuru Branch");
            hqBranchSelectorCombo.setValue("All Branches");

            // Listen for branch selection changes (HQ only)
            hqBranchSelectorCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !isBranchMode) {
                    updateReturnsForBranch(newVal);
                }
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // NEW: INITIALIZE SAMPLE RETURN DATA
    // ═══════════════════════════════════════════════════════════════════════════════
    private void initializeSampleReturns() {
        // Nairobi HQ returns
        List<StockReturn> nairobiReturns = new ArrayList<>();
        nairobiReturns.add(new StockReturn("RET-001", "Nairobi HQ", "Alvaro 500ml", 5,
                "Broken bottles", LocalDate.of(2026, 3, 25), "Pending Review", "Awaiting HQ review"));
        nairobiReturns.add(new StockReturn("RET-002", "Nairobi HQ", "Fanta Orange", 3,
                "Expired stock", LocalDate.of(2026, 3, 20), "Approved", "Replacement: ORD-8840"));
        nairobiReturns.add(new StockReturn("RET-003", "Nairobi HQ", "Coca Cola 500ml", 10,
                "Wrong delivery", LocalDate.of(2026, 3, 15), "Rejected", "Insufficient evidence"));
        returnsByBranch.put("Nairobi HQ", nairobiReturns);
        allReturns.addAll(nairobiReturns);

        // Mombasa Branch returns
        List<StockReturn> mombasaReturns = new ArrayList<>();
        mombasaReturns.add(new StockReturn("RET-005", "Mombasa Branch", "Sprite 500ml", 8,
                "Damaged packaging", LocalDate.of(2026, 3, 10), "Replacement Sent", "ORD-8838 received"));
        mombasaReturns.add(new StockReturn("RET-006", "Mombasa Branch", "Water 500ml", 12,
                "Leaking bottles", LocalDate.of(2026, 3, 22), "Pending Review", "Awaiting HQ review"));
        mombasaReturns.add(new StockReturn("RET-007", "Mombasa Branch", "Alvaro 500ml", 4,
                "Expired stock", LocalDate.of(2026, 3, 18), "Approved", "Replacement: ORD-8841"));
        returnsByBranch.put("Mombasa Branch", mombasaReturns);
        allReturns.addAll(mombasaReturns);

        // Kisumu Branch returns
        List<StockReturn> kisumuReturns = new ArrayList<>();
        kisumuReturns.add(new StockReturn("RET-008", "Kisumu Branch", "Fanta Orange 500ml", 6,
                "Broken during transport", LocalDate.of(2026, 3, 23), "Pending Review", "Awaiting HQ review"));
        kisumuReturns.add(new StockReturn("RET-009", "Kisumu Branch", "Coca Cola 500ml", 7,
                "Defective caps", LocalDate.of(2026, 3, 19), "Approved", "Replacement: ORD-8842"));
        kisumuReturns.add(new StockReturn("RET-010", "Kisumu Branch", "Sprite 500ml", 5,
                "Wrong quantity received", LocalDate.of(2026, 3, 12), "Rejected", "Quantity verified - no issue"));
        returnsByBranch.put("Kisumu Branch", kisumuReturns);
        allReturns.addAll(kisumuReturns);

        // Nakuru Branch returns
        List<StockReturn> nakuruReturns = new ArrayList<>();
        nakuruReturns.add(new StockReturn("RET-011", "Nakuru Branch", "Water 500ml", 20,
                "Contaminated batch", LocalDate.of(2026, 3, 24), "Pending Review", "Awaiting HQ review"));
        nakuruReturns.add(new StockReturn("RET-012", "Nakuru Branch", "Alvaro 500ml", 3,
                "Cracked bottles", LocalDate.of(2026, 3, 21), "Approved", "Replacement: ORD-8843"));
        nakuruReturns.add(new StockReturn("RET-013", "Nakuru Branch", "Fanta Orange 500ml", 9,
                "Damaged labels", LocalDate.of(2026, 3, 16), "Replacement Sent", "ORD-8844 received"));
        returnsByBranch.put("Nakuru Branch", nakuruReturns);
        allReturns.addAll(nakuruReturns);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // NEW: UPDATE RETURNS FOR SELECTED BRANCH
    // ═══════════════════════════════════════════════════════════════════════════════
    private void updateReturnsForBranch(String selectedBranch) {
        List<StockReturn> returnsToDisplay = new ArrayList<>();

        if ("All Branches".equals(selectedBranch)) {
            returnsToDisplay.addAll(allReturns);
        } else {
            List<StockReturn> branchReturns = returnsByBranch.getOrDefault(selectedBranch, new ArrayList<>());
            returnsToDisplay.addAll(branchReturns);
        }

        System.out.println("Displaying " + returnsToDisplay.size() + " returns for " + selectedBranch);

        // TODO: Dynamically update returns table with returnsToDisplay
        // This would involve:
        // 1. Clear returnsTableContainer children
        // 2. Create new HBox rows for each return in returnsToDisplay
        // 3. For HQ: Add Approve/Reject buttons
        // 4. Add to returnsTableContainer
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

        String returnId = "RET-" + String.format("%03d",
                (int)(Math.random() * 900 + 100));
        System.out.println("Return request submitted:");
        System.out.println("  ID:     " + returnId);
        System.out.println("  Item:   " + item);
        System.out.println("  Qty:    " + qty);
        System.out.println("  Branch: " + branch);
        System.out.println("  Reason: " + reason);

        // TODO: Save to database with correct branch_id
        returnItemCombo.setValue(null);
        returnQtyField.clear();
        returnReasonField.clear();
        returnErrorLabel.setVisible(false);

        // Refresh returns list
        updateReturnsForBranch(isBranchMode ? branch : "All Branches");

        resetTimer();
    }

    // NEW: HQ Actions
    @FXML
    private void handleApproveReturn() {
        if (isBranchMode) return;
        String selectedBranch = hqBranchSelectorCombo != null ? hqBranchSelectorCombo.getValue() : "Unknown";
        System.out.println("HQ: Return approved for " + selectedBranch);
        // TODO: Update return status to "Approved" in database
        // TODO: Create replacement order
        // TODO: Refresh returns list
        resetTimer();
    }

    @FXML
    private void handleRejectReturn() {
        if (isBranchMode) return;
        String selectedBranch = hqBranchSelectorCombo != null ? hqBranchSelectorCombo.getValue() : "Unknown";
        System.out.println("HQ: Return rejected for " + selectedBranch);
        // TODO: Update return status to "Rejected" in database
        // TODO: Add rejection reason
        // TODO: Refresh returns list
        resetTimer();
    }

    private void showError(String msg) {
        returnErrorLabel.setText(msg);
        returnErrorLabel.setVisible(true);
    }

    private void resetTimer() {
        if (hqDashboard     != null) hqDashboard.resetSessionTimer();
        if (branchDashboard != null) branchDashboard.resetSessionTimer();
    }
}
