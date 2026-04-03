package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderTrackingController implements DashboardChild, BranchChild {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> branchFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker datePicker;
    @FXML private GridPane trackingGrid;
    @FXML private Label pendingCountLabel;
    @FXML private Label inTransitCountLabel;
    @FXML private Label receivedCountLabel;
    @FXML private Label cancelledCountLabel;

    // HQ-only elements — hidden for branch users
    @FXML private Button dispatchNewBtn;

    // Branch context info label
    @FXML private Label branchFilterInfo;

    private DashboardController hqDashboard;
    private BranchDashboardController branchDashboard;
    private String staffName, role, branch;
    private boolean isBranchMode = false;

    // ═══════════════════════════════════════════════════════════════════════════════
    // NEW: ORDER DATA STRUCTURE
    // ═══════════════════════════════════════════════════════════════════════════════
    private static class StockOrder {
        String orderId;
        String destinationBranch;
        String items;
        String status;
        LocalDate createdDate;
        String createdTime;
        String dispatchTime;
        String estimatedArrival;
        String driver;
        String notes;

        StockOrder(String id, String branch, String items, String status,
                   LocalDate date, String time) {
            this.orderId = id;
            this.destinationBranch = branch;
            this.items = items;
            this.status = status;
            this.createdDate = date;
            this.createdTime = time;
        }

        String getStatusColor() {
            return switch (status) {
                case "Pending" -> "#FEF3C7";
                case "Dispatched" -> "#DBEAFE";
                case "In Transit" -> "#DBEAFE";
                case "Confirmed" -> "#DBEAFE";
                case "Received" -> "#D1FAE5";
                case "Cancelled" -> "#FEE2E2";
                default -> "#F3F4F6";
            };
        }

        String getStatusTextColor() {
            return switch (status) {
                case "Pending" -> "#92400E";
                case "Dispatched" -> "#1D4ED8";
                case "In Transit" -> "#1D4ED8";
                case "Confirmed" -> "#1D4ED8";
                case "Received" -> "#065F46";
                case "Cancelled" -> "#991B1B";
                default -> "#666666";
            };
        }
    }

    // NEW: ORDER DATA STORAGE
    private Map<String, List<StockOrder>> ordersByBranch = new HashMap<>();
    private List<StockOrder> allOrders = new ArrayList<>();

    @Override
    public void setDashboardController(DashboardController d) {
        this.hqDashboard  = d;
        this.isBranchMode = false;
    }

    @Override
    public void setBranchDashboardController(BranchDashboardController d) {
        this.branchDashboard = d;
        this.isBranchMode    = true;
    }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;

        if (isBranchMode) {
            // ── Branch mode ─────────────────────────────────────
            // 1. Lock branch filter to this branch only
            if (branchFilter != null) {
                branchFilter.getItems().clear();
                branchFilter.getItems().add(branch);
                branchFilter.setValue(branch);
                branchFilter.setDisable(true);
                branchFilter.setStyle(
                        "-fx-background-color: #F0F0F0; -fx-border-color: #CCCCCC; " +
                                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-opacity: 0.85;");
            }

            // 2. Hide Dispatch button — HQ only
            if (dispatchNewBtn != null) {
                dispatchNewBtn.setVisible(false);
                dispatchNewBtn.setManaged(false);
            }

            // 3. Show branch filter info label
            if (branchFilterInfo != null) {
                branchFilterInfo.setText(
                        "Showing stock orders for " + branch + " only. Use 'Confirm Receipt' to acknowledge received shipments.");
                branchFilterInfo.setStyle(
                        "-fx-text-fill: #2563EB; -fx-font-size: 12; " +
                                "-fx-background-color: #EFF6FF; -fx-border-color: #BFDBFE; " +
                                "-fx-border-radius: 6; -fx-background-radius: 6; " +
                                "-fx-padding: 6 12 6 12; -fx-border-width: 1;");
                branchFilterInfo.setVisible(true);
                branchFilterInfo.setManaged(true);
            }

            // 4. Hide Approve/Reject buttons in all tracking cards
            hideHQOnlyButtons();

            // 5. Update display for branch's orders
            updateOrdersForBranch(branch);

        } else {
            // HQ mode — show all branches
            if (branchFilter != null) branchFilter.setValue("All Branches");
            if (branchFilterInfo != null) branchFilterInfo.setVisible(false);

            // Update display for all orders
            updateOrdersForBranch("All Branches");
        }

        resetTimer();
    }

    /**
     * Hides any Approve/Reject/Dispatch buttons in the tracking cards.
     * These are HQ-only actions.
     */
    private void hideHQOnlyButtons() {
        if (trackingGrid == null) return;
        trackingGrid.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                hideHQButtonsInVBox((VBox) node);
            }
        });
    }

    private void hideHQButtonsInVBox(VBox vbox) {
        vbox.getChildren().forEach(child -> {
            if (child instanceof HBox) {
                ((HBox) child).getChildren().forEach(btn -> {
                    if (btn instanceof Button) {
                        String text = ((Button) btn).getText();
                        if (text != null && (
                                text.contains("Approve") ||
                                        text.contains("Reject") ||
                                        text.contains("Dispatch"))) {
                            btn.setVisible(false);
                            btn.setManaged(false);
                        }
                    }
                });
            }
            if (child instanceof VBox) {
                hideHQButtonsInVBox((VBox) child);
            }
        });
    }

    @FXML
    public void initialize() {
        // Initialize sample data
        initializeSampleOrders();

        branchFilter.getItems().addAll(
                "All Branches", "Nairobi HQ",
                "Mombasa Branch", "Kisumu Branch", "Nakuru Branch");
        branchFilter.setValue("All Branches");

        // NEW: Add listener for branch filter changes (HQ only)
        if (!isBranchMode) {
            branchFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateOrdersForBranch(newVal);
                }
            });
        }

        statusFilter.getItems().addAll(
                "All Statuses", "Pending", "Dispatched",
                "In Transit", "Confirmed", "Received", "Cancelled");
        statusFilter.setValue("All Statuses");
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // NEW: INITIALIZE SAMPLE ORDER DATA
    // ═══════════════════════════════════════════════════════════════════════════════
    private void initializeSampleOrders() {
        // Nairobi HQ
        List<StockOrder> nairobiOrders = new ArrayList<>();
        nairobiOrders.add(new StockOrder("#ORD-8829", "Nairobi HQ", "Alvaro x20, Fanta x50",
                "Received", LocalDate.of(2026, 3, 25), "08:14 AM"));
        nairobiOrders.add(new StockOrder("#ORD-8830", "Nairobi HQ", "Coca Cola x30, Water x100",
                "In Transit", LocalDate.of(2026, 3, 25), "09:00 AM"));
        nairobiOrders.add(new StockOrder("#ORD-8831", "Nairobi HQ", "Sprite x45, Fanta x30",
                "Pending", LocalDate.of(2026, 3, 25), "11:30 AM"));
        ordersByBranch.put("Nairobi HQ", nairobiOrders);
        allOrders.addAll(nairobiOrders);

        // Mombasa Branch
        List<StockOrder> mombasaOrders = new ArrayList<>();
        mombasaOrders.add(new StockOrder("#ORD-8832", "Mombasa Branch", "Alvaro x50, Water x80",
                "Received", LocalDate.of(2026, 3, 24), "10:00 AM"));
        mombasaOrders.add(new StockOrder("#ORD-8833", "Mombasa Branch", "Coca Cola x40, Sprite x60",
                "In Transit", LocalDate.of(2026, 3, 25), "08:45 AM"));
        mombasaOrders.add(new StockOrder("#ORD-8834", "Mombasa Branch", "Fanta x70, Water x100",
                "Dispatched", LocalDate.of(2026, 3, 25), "12:00 PM"));
        ordersByBranch.put("Mombasa Branch", mombasaOrders);
        allOrders.addAll(mombasaOrders);

        // Kisumu Branch
        List<StockOrder> kisumuOrders = new ArrayList<>();
        kisumuOrders.add(new StockOrder("#ORD-8835", "Kisumu Branch", "Sprite x35, Coca Cola x55",
                "Received", LocalDate.of(2026, 3, 23), "02:30 PM"));
        kisumuOrders.add(new StockOrder("#ORD-8836", "Kisumu Branch", "Alvaro x25, Fanta x45",
                "In Transit", LocalDate.of(2026, 3, 25), "07:15 AM"));
        kisumuOrders.add(new StockOrder("#ORD-8837", "Kisumu Branch", "Water x150, Sprite x50",
                "Pending", LocalDate.of(2026, 3, 25), "10:00 AM"));
        ordersByBranch.put("Kisumu Branch", kisumuOrders);
        allOrders.addAll(kisumuOrders);

        // Nakuru Branch
        List<StockOrder> nakuruOrders = new ArrayList<>();
        nakuruOrders.add(new StockOrder("#ORD-8838", "Nakuru Branch", "Coca Cola x45, Water x90",
                "Received", LocalDate.of(2026, 3, 22), "11:20 AM"));
        nakuruOrders.add(new StockOrder("#ORD-8839", "Nakuru Branch", "Fanta x60, Alvaro x30",
                "In Transit", LocalDate.of(2026, 3, 25), "09:30 AM"));
        nakuruOrders.add(new StockOrder("#ORD-8840", "Nakuru Branch", "Sprite x40, Water x75",
                "Cancelled", LocalDate.of(2026, 3, 25), "01:00 PM"));
        ordersByBranch.put("Nakuru Branch", nakuruOrders);
        allOrders.addAll(nakuruOrders);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // NEW: UPDATE ORDERS FOR SELECTED BRANCH
    // ═══════════════════════════════════════════════════════════════════════════════
    private void updateOrdersForBranch(String selectedBranch) {
        List<StockOrder> ordersToDisplay = new ArrayList<>();

        if ("All Branches".equals(selectedBranch)) {
            ordersToDisplay.addAll(allOrders);
        } else {
            List<StockOrder> branchOrders = ordersByBranch.getOrDefault(selectedBranch, new ArrayList<>());
            ordersToDisplay.addAll(branchOrders);
        }

        // Update stats based on displayed orders
        long pending = ordersToDisplay.stream().filter(o -> "Pending".equals(o.status)).count();
        long inTransit = ordersToDisplay.stream()
                .filter(o -> "In Transit".equals(o.status) || "Dispatched".equals(o.status))
                .count();
        long received = ordersToDisplay.stream().filter(o -> "Received".equals(o.status)).count();
        long cancelled = ordersToDisplay.stream().filter(o -> "Cancelled".equals(o.status)).count();

        // Update labels
        if (pendingCountLabel != null) {
            pendingCountLabel.setText(String.valueOf(pending));
        }
        if (inTransitCountLabel != null) {
            inTransitCountLabel.setText(String.valueOf(inTransit));
        }
        if (receivedCountLabel != null) {
            receivedCountLabel.setText(String.valueOf(received));
        }
        if (cancelledCountLabel != null) {
            cancelledCountLabel.setText(String.valueOf(cancelled));
        }

        System.out.println("Updated orders for " + selectedBranch + ": " + ordersToDisplay.size() + " total");
        System.out.println("  Pending: " + pending + " | In Transit: " + inTransit +
                " | Received: " + received + " | Cancelled: " + cancelled);

        // TODO: Dynamically update tracking cards grid with ordersToDisplay
        // This would involve:
        // 1. Clear trackingGrid children
        // 2. Create new VBox cards for each order in ordersToDisplay
        // 3. Add to trackingGrid
    }

    @FXML
    private void handleApplyFilter() {
        String selectedBranch = branchFilter.getValue();
        String selectedStatus = statusFilter.getValue();
        System.out.println("Filter: branch=" + selectedBranch
                + " | status=" + selectedStatus);
        // TODO: Apply status filter to current displayed orders
        // If status != "All Statuses", filter ordersToDisplay further
        resetTimer();
    }

    @FXML
    private void handleClearFilter() {
        if (!isBranchMode) branchFilter.setValue("All Branches");
        statusFilter.setValue("All Statuses");
        if (datePicker != null) datePicker.setValue(null);
        if (searchField != null) searchField.clear();
    }

    // HQ-only — guarded
    @FXML private void handleDispatchNew() {
        if (isBranchMode) return;
        String selectedBranch = branchFilter != null ? branchFilter.getValue() : "Unknown";
        System.out.println("HQ: Create new dispatch order for " + selectedBranch);
        resetTimer();
    }

    @FXML private void handleApproveDispatch() {
        if (isBranchMode) return;
        String selectedBranch = branchFilter != null ? branchFilter.getValue() : "Unknown";
        System.out.println("HQ: Order approved and dispatched for " + selectedBranch);
        // TODO: Update order status to "Dispatched" in database
        // Then call updateOrdersForBranch(selectedBranch) to refresh
        resetTimer();
    }

    @FXML private void handleRejectOrder() {
        if (isBranchMode) return;
        String selectedBranch = branchFilter != null ? branchFilter.getValue() : "Unknown";
        System.out.println("HQ: Order rejected for " + selectedBranch);
        // TODO: Update order status to "Cancelled" in database
        // Then call updateOrdersForBranch(selectedBranch) to refresh
        resetTimer();
    }

    // Branch-only — confirm receipt of stock
    @FXML private void handleConfirmReceipt() {
        System.out.println("Branch " + branch + " confirmed receipt of stock order.");
        // TODO: Update order status to "Received" in database
        resetTimer();
    }

    private void resetTimer() {
        if (hqDashboard     != null) hqDashboard.resetSessionTimer();
        if (branchDashboard != null) branchDashboard.resetSessionTimer();
    }
}
