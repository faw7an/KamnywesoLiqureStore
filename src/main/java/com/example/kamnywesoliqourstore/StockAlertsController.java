package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockAlertsController implements DashboardChild, BranchChild {

    @FXML private Label pageSubtitle;
    @FXML private Label branchContextLabel;
    @FXML private Label alertBannerTitle;
    @FXML private Label alertBannerSub;
    @FXML private Button acknowledgeAllBtn;
    @FXML private ComboBox<String> branchSelectorCombo;  // ← NEW: Branch selector for HQ
    @FXML private VBox alertsTableBody;

    // Branch name labels on each row
    @FXML private Label alertRow1Branch;
    @FXML private Label alertRow2Branch;
    @FXML private Label alertRow3Branch;
    @FXML private Label branchColHeader;

    // HQ-only Approve buttons
    @FXML private Button approveBtn1;
    @FXML private Button approveBtn2;
    @FXML private Button approveBtn3;

    // Branch-only Request Restock buttons
    @FXML private Button requestBtn1;
    @FXML private Button requestBtn2;
    @FXML private Button requestBtn3;

    // Stat labels
    @FXML private Label outOfStockCount;
    @FXML private Label criticalCount;
    @FXML private Label lowStockCount;
    @FXML private Label requestsSentCount;

    // Alert row containers for easy access
    @FXML private HBox alertRowContainer1;
    @FXML private HBox alertRowContainer2;
    @FXML private HBox alertRowContainer3;

    // Product name labels
    @FXML private Label productLabel1;
    @FXML private Label productLabel2;
    @FXML private Label productLabel3;

    // Stock level labels
    @FXML private Label currentStockLabel1;
    @FXML private Label currentStockLabel2;
    @FXML private Label currentStockLabel3;

    // Min stock labels
    @FXML private Label minStockLabel1;
    @FXML private Label minStockLabel2;
    @FXML private Label minStockLabel3;

    // Progress bars
    @FXML private ProgressBar progressBar1;
    @FXML private ProgressBar progressBar2;
    @FXML private ProgressBar progressBar3;

    // Status labels
    @FXML private Label statusLabel1;
    @FXML private Label statusLabel2;
    @FXML private Label statusLabel3;

    private DashboardController hqDashboard;
    private BranchDashboardController branchDashboard;
    private String staffName, role, branch;
    private boolean isBranchMode = false;

    // ═══════════════════════════════════════════════════════════════════════════════
    // MOCK DATA STRUCTURE - Replace with database calls later
    // ═══════════════════════════════════════════════════════════════════════════════
    private static class StockAlert {
        String product;
        String branch;
        int currentStock;
        int minStock;
        String status;  // "Out of Stock", "Critical", "Low"
        String requestStatus;  // "Sent OK", "Pending", etc.

        StockAlert(String p, String b, int current, int min, String s, String rs) {
            product = p;
            branch = b;
            currentStock = current;
            minStock = min;
            status = s;
            requestStatus = rs;
        }

        double getProgress() {
            if (minStock == 0) return 0;
            return Math.min(1.0, (double) currentStock / minStock);
        }

        String getStatusColor() {
            return switch (status) {
                case "Out of Stock" -> "#FEE2E2";  // Red
                case "Critical" -> "#FFF5F5";       // Light red
                case "Low" -> "#FFFBEB";            // Yellow
                default -> "#F3F4F6";
            };
        }

        String getStatusTextColor() {
            return switch (status) {
                case "Out of Stock" -> "#991B1B";  // Dark red
                case "Critical" -> "#991B1B";       // Dark red
                case "Low" -> "#92400E";            // Dark orange
                default -> "#444444";
            };
        }
    }

    // Sample data for each branch - Replace with database queries
    private Map<String, List<StockAlert>> alertsByBranch = new HashMap<>();

    @Override public void setDashboardController(DashboardController d) {
        this.hqDashboard  = d;
        this.isBranchMode = false;
    }

    @Override public void setBranchDashboardController(BranchDashboardController d) {
        this.branchDashboard = d;
        this.isBranchMode    = true;
    }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;

        if (isBranchMode) {
            // ── Branch mode ──────────────────────────────────────
            pageSubtitle.setText("Stock alerts for " + branch + " only");

            branchContextLabel.setText(
                    "Showing stock alerts for " + branch + " only. " +
                            "Use 'Request Restock' to send a restock request to Nairobi HQ.");
            branchContextLabel.setVisible(true);
            branchContextLabel.setManaged(true);

            if (alertRow1Branch != null) alertRow1Branch.setText(branch);
            if (alertRow2Branch != null) alertRow2Branch.setText(branch);
            if (alertRow3Branch != null) alertRow3Branch.setText(branch);

            if (branchColHeader != null) {
                branchColHeader.setText("");
            }

            // Hide HQ Approve buttons
            setVisible(approveBtn1, false);
            setVisible(approveBtn2, false);
            setVisible(approveBtn3, false);

            // Show Request Restock buttons
            setVisible(requestBtn1, true);
            setVisible(requestBtn2, true);
            setVisible(requestBtn3, true);

            // Hide branch selector (not needed in branch mode)
            setVisible(branchSelectorCombo, false);

            alertBannerTitle.setText("Stock alerts detected at " + branch);
            alertBannerSub.setText(
                    "Use 'Request Restock' to notify Nairobi HQ. " +
                            "Requests are tracked in the Stock Returns module.");

        } else {
            // ── HQ mode ──────────────────────────────────────────
            pageSubtitle.setText("Items below minimum threshold across all branches");
            branchContextLabel.setVisible(false);
            branchContextLabel.setManaged(false);

            // Show Approve, hide Request
            setVisible(approveBtn1, true);
            setVisible(approveBtn2, true);
            setVisible(approveBtn3, true);
            setVisible(requestBtn1, false);
            setVisible(requestBtn2, false);
            setVisible(requestBtn3, false);

            // SHOW BRANCH SELECTOR IN HQ MODE
            setVisible(branchSelectorCombo, true);
        }

        resetTimer();
    }

    @FXML public void initialize() {
        // Initialize sample data for all branches
        initializeSampleData();

        // Setup branch selector only if we're in HQ mode
        if (!isBranchMode && branchSelectorCombo != null) {
            branchSelectorCombo.getItems().addAll(
                    "Nairobi HQ",
                    "Mombasa Branch",
                    "Kisumu Branch",
                    "Nakuru Branch"
            );
            branchSelectorCombo.setValue("Nairobi HQ");  // Default to HQ

            // Listen for branch selection changes
            branchSelectorCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateAlertsForBranch(newVal);
                }
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // INITIALIZE SAMPLE DATA - Replace with database queries
    // ═══════════════════════════════════════════════════════════════════════════════
    private void initializeSampleData() {
        // Nairobi HQ alerts
        List<StockAlert> nairobi = new ArrayList<>();
        nairobi.add(new StockAlert("Sprite 500ml", "Nairobi HQ", 0, 20, "Out of Stock", "Sent OK"));
        nairobi.add(new StockAlert("Coca Cola 500ml", "Nairobi HQ", 3, 20, "Critical", "Sent OK"));
        nairobi.add(new StockAlert("Fanta Orange 500ml", "Nairobi HQ", 8, 15, "Low", "Sent OK"));
        alertsByBranch.put("Nairobi HQ", nairobi);

        // Mombasa Branch alerts
        List<StockAlert> mombasa = new ArrayList<>();
        mombasa.add(new StockAlert("Alvaro 500ml", "Mombasa Branch", 0, 45, "Out of Stock", "Sent OK"));
        mombasa.add(new StockAlert("Dasani Water 500ml", "Mombasa Branch", 5, 50, "Critical", "Sent OK"));
        mombasa.add(new StockAlert("Sprite 500ml", "Mombasa Branch", 10, 25, "Low", "Sent OK"));
        alertsByBranch.put("Mombasa Branch", mombasa);

        // Kisumu Branch alerts
        List<StockAlert> kisumu = new ArrayList<>();
        kisumu.add(new StockAlert("Fanta Orange 500ml", "Kisumu Branch", 0, 30, "Out of Stock", "Sent OK"));
        kisumu.add(new StockAlert("Coca Cola 500ml", "Kisumu Branch", 2, 20, "Critical", "Sent OK"));
        kisumu.add(new StockAlert("Alvaro 500ml", "Kisumu Branch", 12, 45, "Low", "Sent OK"));
        alertsByBranch.put("Kisumu Branch", kisumu);

        // Nakuru Branch alerts
        List<StockAlert> nakuru = new ArrayList<>();
        nakuru.add(new StockAlert("Dasani Water 500ml", "Nakuru Branch", 1, 50, "Out of Stock", "Sent OK"));
        nakuru.add(new StockAlert("Sprite 500ml", "Nakuru Branch", 4, 25, "Critical", "Sent OK"));
        nakuru.add(new StockAlert("Coca Cola 500ml", "Nakuru Branch", 9, 20, "Low", "Sent OK"));
        alertsByBranch.put("Nakuru Branch", nakuru);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // UPDATE ALERTS FOR SELECTED BRANCH
    // ═══════════════════════════════════════════════════════════════════════════════
    private void updateAlertsForBranch(String selectedBranch) {
        List<StockAlert> alerts = alertsByBranch.getOrDefault(selectedBranch, new ArrayList<>());

        // Update alert banner
        alertBannerTitle.setText("Stock alerts detected at " + selectedBranch);
        alertBannerSub.setText("Review stock levels and approve restock requests below.");

        // Display up to 3 alerts
        updateAlertRow(1, alerts.size() > 0 ? alerts.get(0) : null);
        updateAlertRow(2, alerts.size() > 1 ? alerts.get(1) : null);
        updateAlertRow(3, alerts.size() > 2 ? alerts.get(2) : null);

        // Update stats
        long outOfStock = alerts.stream().filter(a -> "Out of Stock".equals(a.status)).count();
        long critical = alerts.stream().filter(a -> "Critical".equals(a.status)).count();
        long low = alerts.stream().filter(a -> "Low".equals(a.status)).count();

        if (outOfStockCount != null) outOfStockCount.setText(String.valueOf(outOfStock));
        if (criticalCount != null) criticalCount.setText(String.valueOf(critical));
        if (lowStockCount != null) lowStockCount.setText(String.valueOf(low));
        if (requestsSentCount != null) requestsSentCount.setText(String.valueOf(alerts.size()));
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // UPDATE INDIVIDUAL ALERT ROW
    // ═══════════════════════════════════════════════════════════════════════════════
    private void updateAlertRow(int rowNumber, StockAlert alert) {
        if (alert == null) {
            // Hide row if no alert
            setRowVisible(rowNumber, false);
            return;
        }

        setRowVisible(rowNumber, true);

        // Get row components by row number
        Label productLabel = switch (rowNumber) {
            case 1 -> productLabel1;
            case 2 -> productLabel2;
            case 3 -> productLabel3;
            default -> null;
        };

        Label branchLabel = switch (rowNumber) {
            case 1 -> alertRow1Branch;
            case 2 -> alertRow2Branch;
            case 3 -> alertRow3Branch;
            default -> null;
        };

        Label currentStockLabel = switch (rowNumber) {
            case 1 -> currentStockLabel1;
            case 2 -> currentStockLabel2;
            case 3 -> currentStockLabel3;
            default -> null;
        };

        Label minStockLabel = switch (rowNumber) {
            case 1 -> minStockLabel1;
            case 2 -> minStockLabel2;
            case 3 -> minStockLabel3;
            default -> null;
        };

        ProgressBar progressBar = switch (rowNumber) {
            case 1 -> progressBar1;
            case 2 -> progressBar2;
            case 3 -> progressBar3;
            default -> null;
        };

        Label statusLabel = switch (rowNumber) {
            case 1 -> statusLabel1;
            case 2 -> statusLabel2;
            case 3 -> statusLabel3;
            default -> null;
        };

        HBox rowContainer = switch (rowNumber) {
            case 1 -> alertRowContainer1;
            case 2 -> alertRowContainer2;
            case 3 -> alertRowContainer3;
            default -> null;
        };

        // Update all labels
        if (productLabel != null) productLabel.setText(alert.product);
        if (branchLabel != null) branchLabel.setText(alert.branch);
        if (currentStockLabel != null) currentStockLabel.setText(alert.currentStock + " units");
        if (minStockLabel != null) minStockLabel.setText(alert.minStock + " units");

        if (progressBar != null) {
            progressBar.setProgress(alert.getProgress());
        }

        if (statusLabel != null) {
            statusLabel.setText(alert.status);
            statusLabel.setStyle("-fx-font-size: 11; -fx-text-fill: " + alert.getStatusTextColor() +
                    "; -fx-background-color: " + alert.getStatusColor() +
                    "; -fx-background-radius: 10; -fx-padding: 2 8 2 8;");
        }

        if (rowContainer != null) {
            rowContainer.setStyle("-fx-padding: 10 0 10 0; -fx-border-color: transparent transparent #F3F4F6 transparent; " +
                    "-fx-border-width: 1; -fx-background-color: " + alert.getStatusColor() + ";");
        }
    }

    private void setRowVisible(int rowNumber, boolean visible) {
        HBox rowContainer = switch (rowNumber) {
            case 1 -> alertRowContainer1;
            case 2 -> alertRowContainer2;
            case 3 -> alertRowContainer3;
            default -> null;
        };

        if (rowContainer != null) {
            rowContainer.setVisible(visible);
            rowContainer.setManaged(visible);
        }
    }

    // ───────────────────────────────────────────────────────────────────────────────
    // ACTION HANDLERS
    // ───────────────────────────────────────────────────────────────────────────────

    @FXML
    private void handleApproveRestock() {
        if (isBranchMode) return;
        String selectedBranch = branchSelectorCombo != null ? branchSelectorCombo.getValue() : "Unknown";
        System.out.println("HQ: Restock request approved for " + selectedBranch + " — dispatching stock.");
        // TODO: Update database with approval
        resetTimer();
    }

    @FXML
    private void handleRequestRestock() {
        System.out.println(branch + ": Restock request sent to Nairobi HQ.");
        // TODO: Create restock request record in database
        resetTimer();
    }

    @FXML
    private void handleDismissAlert() {
        System.out.println("Alert dismissed.");
        // TODO: Mark alert as acknowledged in database
        resetTimer();
    }

    @FXML
    private void handleAcknowledgeAll() {
        String selectedBranch = branchSelectorCombo != null ? branchSelectorCombo.getValue() : branch;
        System.out.println("All alerts acknowledged for " + selectedBranch + ".");
        // TODO: Mark all alerts as acknowledged in database
        resetTimer();
    }

    private void setVisible(Object obj, boolean visible) {
        if (obj instanceof Button btn) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        } else if (obj instanceof ComboBox<?> combo) {
            combo.setVisible(visible);
            combo.setManaged(visible);
        }
    }

    private void resetTimer() {
        if (hqDashboard     != null) hqDashboard.resetSessionTimer();
        if (branchDashboard != null) branchDashboard.resetSessionTimer();
    }
}
