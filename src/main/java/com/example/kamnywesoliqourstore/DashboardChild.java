package com.example.kamnywesoliqourstore;

/**
 * Interface implemented by all child page controllers
 * (DashboardHomeController, OrderProcessingController, etc.)
 * Allows the DashboardController to pass session data and
 * a reference to itself to each loaded page.
 */
public interface DashboardChild {

    /**
     * Called by DashboardController after loading the FXML.
     * @param dashboard reference to the parent shell controller
     */
    void setDashboardController(DashboardController dashboard);

    /**
     * Called to pass the current session data to the child page.
     * @param staffName   logged-in staff full name
     * @param role        staff role (Manager, Cashier, etc.)
     * @param branch      current branch name
     */
    void setSessionData(String staffName, String role, String branch);
}
