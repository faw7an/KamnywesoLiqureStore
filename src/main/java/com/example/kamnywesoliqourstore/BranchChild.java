package com.example.kamnywesoliqourstore;

/**
 * Interface implemented by all child page controllers
 * used inside the Branch Dashboard (Mombasa, Kisumu, Nakuru).
 *
 * Note: The existing DashboardChild interface serves the HQ dashboard.
 * This interface serves the branch tab-based dashboard.
 * Controllers can implement BOTH interfaces if they are shared
 * between HQ and branch dashboards.
 */
public interface BranchChild {

    /**
     * Called by BranchDashboardController after loading the FXML.
     */
    void setBranchDashboardController(BranchDashboardController dashboard);

    /**
     * Called to pass the current session data to the child page.
     */
    void setSessionData(String staffName, String role, String branch);
}
