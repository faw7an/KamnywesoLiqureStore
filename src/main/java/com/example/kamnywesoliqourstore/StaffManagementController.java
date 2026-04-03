package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class StaffManagementController implements DashboardChild {

    @FXML private VBox staffTab;
    @FXML private VBox pendingTab;
    @FXML private Button staffTabBtn;
    @FXML private Button pendingTabBtn;
    @FXML private TextField staffSearchField;

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML public void initialize() {}

    @FXML
    private void showStaffTab() {
        staffTab.setVisible(true); staffTab.setManaged(true);
        pendingTab.setVisible(false); pendingTab.setManaged(false);
        staffTabBtn.setStyle("-fx-background-color: #0D1B2A; -fx-background-radius: 8 0 0 8; -fx-text-fill: white; -fx-font-size: 13; -fx-cursor: hand; -fx-border-color: transparent;");
        pendingTabBtn.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; -fx-border-radius: 0 8 8 0; -fx-text-fill: #444444; -fx-font-size: 13; -fx-cursor: hand;");
    }

    @FXML
    private void showPendingTab() {
        staffTab.setVisible(false); staffTab.setManaged(false);
        pendingTab.setVisible(true); pendingTab.setManaged(true);
        pendingTabBtn.setStyle("-fx-background-color: #0D1B2A; -fx-background-radius: 0 8 8 0; -fx-text-fill: white; -fx-font-size: 13; -fx-cursor: hand; -fx-border-color: transparent;");
        staffTabBtn.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; -fx-border-radius: 8 0 0 8; -fx-text-fill: #444444; -fx-font-size: 13; -fx-cursor: hand;");
    }

    @FXML private void handleViewStaff()        { System.out.println("View staff profile."); if (dashboard != null) dashboard.resetSessionTimer(); }
    @FXML private void handleSuspendStaff()     { System.out.println("Staff suspended."); if (dashboard != null) dashboard.resetSessionTimer(); }
    @FXML private void handleActivateStaff()    { System.out.println("Staff activated."); if (dashboard != null) dashboard.resetSessionTimer(); }
    @FXML private void handleResetPassword()    { System.out.println("Password reset sent."); if (dashboard != null) dashboard.resetSessionTimer(); }
    @FXML private void handleApproveStaff()     { System.out.println("Staff registration approved."); if (dashboard != null) dashboard.resetSessionTimer(); }
    @FXML private void handleRejectStaff()      { System.out.println("Staff registration rejected."); if (dashboard != null) dashboard.resetSessionTimer(); }
    @FXML private void handleTerminateSession() { System.out.println("Session terminated."); if (dashboard != null) dashboard.resetSessionTimer(); }
}
