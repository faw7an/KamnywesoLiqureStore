package com.example.kamnywesoliqourstore.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class OrderTrackingController {

    @FXML private TextField searchField;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colStatus;

    private ObservableList<Order> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadData();

        // Reuse the StatusCell logic from Admin Dashboard for consistency
        colStatus.setCellFactory(column -> new AdminDashboardController.StatusCell<>());

        FilteredList<Order> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(order -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return order.getBranch().toLowerCase().contains(lowerCaseFilter) ||
                       order.getId().toLowerCase().contains(lowerCaseFilter) ||
                       order.getStatus().toLowerCase().contains(lowerCaseFilter);
            });
        });

        orderTable.setItems(filteredData);
    }

    private void loadData() {
        masterData.addAll(
            new Order("ORD-1045", "Nairobi HQ", 150, "Delivered", "10:30 AM"),
            new Order("ORD-1046", "Mombasa", 85, "Delivered", "11:15 AM"),
            new Order("ORD-1047", "Nakuru", 42, "Pending", "11:45 AM"),
            new Order("ORD-1048", "Kisumu", 120, "Delayed", "12:30 PM"),
            new Order("ORD-1049", "Nairobi HQ", 200, "Pending", "01:00 PM"),
            new Order("ORD-1050", "Mombasa", 300, "Delivered", "02:00 PM")
        );
    }
}
