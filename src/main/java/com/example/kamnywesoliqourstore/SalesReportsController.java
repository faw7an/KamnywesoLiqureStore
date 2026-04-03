package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SalesReportsController implements DashboardChild {

    @FXML private Button btnSales, btnPnL, btnOrders, btnStock, btnCustomer;
    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> branchSelector;
    @FXML private RadioButton pdfRadio, csvRadio;
    @FXML private Label reportTitleLabel, reportSubtitleLabel;

    private DashboardController dashboard;
    private String staffName, role, branch;

    @Override public void setDashboardController(DashboardController d) { this.dashboard = d; }
    @Override public void setSessionData(String n, String r, String b) {
        this.staffName = n; this.role = r; this.branch = b;
        branchSelector.setValue(b);
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    public void initialize() {
        branchSelector.getItems().addAll(
                "All Branches", "Nairobi HQ",
                "Mombasa Branch", "Kisumu Branch", "Nakuru Branch");
        branchSelector.setValue("All Branches");
    }

    private void setActiveBtn(Button active) {
        String inactive = "-fx-background-color: transparent; -fx-border-color: #E5E7EB; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #444444; -fx-font-size: 13; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 12;";
        String activeStyle = "-fx-background-color: #E04A2A; -fx-background-radius: 6; -fx-text-fill: white; -fx-font-size: 13; -fx-cursor: hand; -fx-border-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 12;";
        for (Button b : new Button[]{btnSales, btnPnL, btnOrders, btnStock, btnCustomer}) {
            b.setStyle(inactive);
        }
        active.setStyle(activeStyle);
    }

    @FXML private void selectSales()    { setActiveBtn(btnSales);    reportTitleLabel.setText("Sales Report"); }
    @FXML private void selectPnL()      { setActiveBtn(btnPnL);      reportTitleLabel.setText("Profit and Loss Report"); }
    @FXML private void selectOrders()   { setActiveBtn(btnOrders);   reportTitleLabel.setText("Order Report"); }
    @FXML private void selectStock()    { setActiveBtn(btnStock);    reportTitleLabel.setText("Stock Movement Report"); }
    @FXML private void selectCustomer() { setActiveBtn(btnCustomer); reportTitleLabel.setText("Customer History Report"); }

    @FXML
    private void handleGenerateReport() {
        System.out.println("Generating: " + reportTitleLabel.getText());
        System.out.println("Format: " + (pdfRadio.isSelected() ? "PDF" : "CSV"));
        System.out.println("Branch: " + branchSelector.getValue());
        // TODO: Query database and populate report table
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    @FXML
    private void handleExportPDF() {
        System.out.println("Exporting report to PDF...");
        // TODO: Use iText or Apache PDFBox to generate PDF
        if (dashboard != null) dashboard.resetSessionTimer();
    }
}
