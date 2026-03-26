package com.example.kamnywesoliqourstore.admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class OrderManagementController {
    @FXML private ComboBox<String> branchCombo, statusCombo;
    @FXML private TableView<OrderModel> orderTable;
    public static class OrderModel {
        private String id, customer, branch, items, total, status, date;
        public OrderModel(String i, String c, String b, String it, String t, String s, String d) { id=i; customer=c; branch=b; items=it; total=t; status=s; date=d; }
        public String getId() { return id; } public String getCustomer() { return customer; } public String getBranch() { return branch; } public String getItems() { return items; } public String getTotal() { return total; } public String getStatus() { return status; } public String getDate() { return date; }
    }
    @FXML public void initialize() {
        branchCombo.getItems().addAll("All Branches", "Nakuru", "Mombasa", "Kisumu", "HQ");
        statusCombo.getItems().addAll("All Statuses", "Pending", "Processing", "Dispatched", "In Transit", "Confirmed", "Received", "Cancelled");
        TableColumn<OrderModel, String> colId = new TableColumn<>("ORDER ID"); colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<OrderModel, String> colCust = new TableColumn<>("CUSTOMER"); colCust.setCellValueFactory(new PropertyValueFactory<>("customer"));
        TableColumn<OrderModel, String> colBr = new TableColumn<>("BRANCH"); colBr.setCellValueFactory(new PropertyValueFactory<>("branch"));
        TableColumn<OrderModel, String> colIt = new TableColumn<>("ITEMS"); colIt.setCellValueFactory(new PropertyValueFactory<>("items"));
        TableColumn<OrderModel, String> colTot = new TableColumn<>("TOTAL"); colTot.setCellValueFactory(new PropertyValueFactory<>("total"));
        TableColumn<OrderModel, String> colStat = new TableColumn<>("STATUS"); colStat.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<OrderModel, String> colDate = new TableColumn<>("DATE/TIME"); colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStat.setCellFactory(c -> new AdminDashboardController.StatusCell<>());
        orderTable.getColumns().setAll(colId, colCust, colBr, colIt, colTot, colStat, colDate);
        orderTable.setItems(FXCollections.observableArrayList(
            new OrderModel("ORD-8829", "Harrison Mbugua", "Nakuru", "Tusker x12, Guinness x4", "KES 4,800", "Received", "24 Oct 2023, 08:15"),
            new OrderModel("ORD-8830", "Grace Wanjiku", "Mombasa", "Coke x24, Water x12", "KES 3,200", "In Transit", "24 Oct 2023, 09:30"),
            new OrderModel("ORD-8831", "David Ochieng", "Kisumu", "White Cap x6", "KES 1,680", "Processing", "24 Oct 2023, 10:45"),
            new OrderModel("ORD-8832", "Amina Hassan", "Mombasa", "Fanta x12, Stoney x6", "KES 1,800", "Pending", "24 Oct 2023, 11:00"),
            new OrderModel("ORD-8833", "Peter Kamau", "Nakuru", "Tusker x24", "KES 6,000", "Dispatched", "24 Oct 2023, 11:30")
        ));
    }
}