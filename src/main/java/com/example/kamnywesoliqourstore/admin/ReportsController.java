package com.example.kamnywesoliqourstore.admin;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class ReportsController {
    @FXML private TableView<ReportModel> reportTable;
    public static class ReportModel {
        private String date, branch, revenue, avg; private int items;
        public ReportModel(String d, String b, int i, String r, String a) { date=d; branch=b; items=i; revenue=r; avg=a; }
        public String getDate() { return date; } public String getBranch() { return branch; } public int getItems() { return items; } public String getRevenue() { return revenue; } public String getAvg() { return avg; }
    }
    @FXML public void initialize() {
        TableColumn<ReportModel, String> cd = new TableColumn<>("DATE"); cd.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<ReportModel, String> cb = new TableColumn<>("BRANCH"); cb.setCellValueFactory(new PropertyValueFactory<>("branch"));
        TableColumn<ReportModel, Integer> ci = new TableColumn<>("ITEMS SOLD"); ci.setCellValueFactory(new PropertyValueFactory<>("items"));
        TableColumn<ReportModel, String> cr = new TableColumn<>("REVENUE"); cr.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        TableColumn<ReportModel, String> ca = new TableColumn<>("AVG ORDER"); ca.setCellValueFactory(new PropertyValueFactory<>("avg"));
        reportTable.getColumns().setAll(cd, cb, ci, cr, ca);
        reportTable.setItems(FXCollections.observableArrayList(
            new ReportModel("24 Oct", "Nakuru", 156, "KES 45,000", "KES 288"),
            new ReportModel("24 Oct", "Mombasa", 220, "KES 75,000", "KES 341"),
            new ReportModel("24 Oct", "Kisumu", 98, "KES 30,000", "KES 306"),
            new ReportModel("24 Oct", "HQ", 310, "KES 90,000", "KES 290")
        ));
    }
}