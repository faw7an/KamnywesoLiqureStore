package com.example.kamnywesoliqourstore.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDashboardController {

    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());

    @FXML private BorderPane mainBorderPane;
    @FXML private ScrollPane dashboardContent;
    @FXML private Button btnDashboard;
    @FXML private Button btnOrderTracking;

    @FXML private VBox sideNavVBox;
    @FXML private Button btnCollapse;
    @FXML private Label lblBrand;
    private boolean isCollapsed = false;

    @FXML private HBox statCardsContainer, revenueBarContainer, stockSummaryBox;
    @FXML private VBox stockAlertsContainer;
    @FXML private TableView<BranchProfit> profitTable;
    @FXML private TableView<Order> dispatchTable;

    @FXML
    public void initialize() {
        setupSummaryCards();
        setupRevenueBars();
        setupStockAlerts();
        setupTables();
        loadMockData();
    }

    @FXML
    private void handleNavDashboard(ActionEvent event) {
        // Update active styles
        btnDashboard.getStyleClass().setAll("button", "nav-button-active");
        btnOrderTracking.getStyleClass().setAll("button", "nav-button");

        // Swap back to dashboard
        if (mainBorderPane.getCenter() instanceof VBox) {
            VBox centerVBox = (VBox) mainBorderPane.getCenter();
            if (centerVBox.getChildren().size() > 1) {
                centerVBox.getChildren().set(1, dashboardContent);
            }
        }
    }

    @FXML
    private void handleNavOrderTracking(ActionEvent event) {
        // Update active styles
        btnOrderTracking.getStyleClass().setAll("button", "nav-button-active");
        btnDashboard.getStyleClass().setAll("button", "nav-button");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kamnywesoliqourstore/admin/order-tracking-view.fxml"));
            Parent orderTrackingNode = loader.load();
            
            // Swap out the center ScrollPane with the Order Tracking Node
            if (mainBorderPane.getCenter() instanceof VBox) {
                VBox centerVBox = (VBox) mainBorderPane.getCenter();
                if (centerVBox.getChildren().size() > 1) {
                    centerVBox.getChildren().set(1, orderTrackingNode);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load order-tracking-view", e);
        }
    }

    @FXML
    private void handleCollapseSidebar(ActionEvent event) {
        if (isCollapsed) {
            sideNavVBox.setPrefWidth(260);
            lblBrand.setText("Kamnyweso Liqour Store");
            btnCollapse.setText("Collapse");
            isCollapsed = false;
        } else {
            sideNavVBox.setPrefWidth(75);
            lblBrand.setText("KLS");
            btnCollapse.setText("Expand");
            isCollapsed = true;
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kamnywesoliqourstore/auth/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnCollapse.getScene().getWindow();
            stage.setTitle("Kamnyweso Liquor Store - Login");
            stage.setScene(new Scene(root, 1000, 750));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load login-view", e);
        }
    }

    private void setupTables() {
        // Profit Table Columns
        TableColumn<BranchProfit, String> colPBranch = new TableColumn<>("Branch");
        colPBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));

        TableColumn<BranchProfit, Double> colPRev = new TableColumn<>("Revenue");
        colPRev.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        TableColumn<BranchProfit, Double> colPCost = new TableColumn<>("Cost");
        colPCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableColumn<BranchProfit, Double> colPTotal = new TableColumn<>("Profit");
        colPTotal.setCellValueFactory(new PropertyValueFactory<>("profit"));

        TableColumn<BranchProfit, String> colPStatus = new TableColumn<>("Status");
        colPStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPStatus.setCellFactory(column -> new StatusCell<>());

        if (profitTable != null) {
            profitTable.getColumns().setAll(colPBranch, colPRev, colPCost, colPTotal, colPStatus);
        }

        // Dispatch Table Columns
        TableColumn<Order, String> colDId = new TableColumn<>("Order ID");
        colDId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> colDBranch = new TableColumn<>("To Branch");
        colDBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));

        TableColumn<Order, Integer> colDItems = new TableColumn<>("Items");
        colDItems.setCellValueFactory(new PropertyValueFactory<>("items"));

        TableColumn<Order, String> colDStatus = new TableColumn<>("Status");
        colDStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDStatus.setCellFactory(column -> new StatusCell<>());

        TableColumn<Order, String> colDTime = new TableColumn<>("Time");
        colDTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        if (dispatchTable != null) {
            dispatchTable.getColumns().setAll(colDId, colDBranch, colDItems, colDStatus, colDTime);
        }
    }

    private void loadMockData() {
        if (profitTable != null) {
            ObservableList<BranchProfit> profits = FXCollections.observableArrayList(
                new BranchProfit("Nakuru", 45000, 33000, 12000, "OK"),
                new BranchProfit("Mombasa", 75000, 53000, 22000, "OK"),
                new BranchProfit("Kisumu", 30000, 28000, 2000, "Low"),
                new BranchProfit("HQ", 90000, 95000, -5000, "Critical")
            );
            profitTable.setItems(profits);
        }

        if (dispatchTable != null) {
            ObservableList<Order> orders = FXCollections.observableArrayList(
                new Order("ORD-8829", "Nakuru", 24, "Received", "08:15"),
                new Order("ORD-8830", "Mombasa", 18, "In Transit", "09:30"),
                new Order("ORD-8831", "Kisumu", 12, "Dispatched", "10:45"),
                new Order("ORD-8832", "Nakuru", 6, "Pending", "11:00")
            );
            dispatchTable.setItems(orders);
        }
    }

    private void setupSummaryCards() {
        if (statCardsContainer != null) {
            statCardsContainer.getChildren().setAll(
                    createStatCard("Total Revenue Today", "KES 142,850", "↑ 12% vs yesterday", "#2563EB"),
                    createStatCard("Net Profit", "KES 48,200", "↑ 8% vs yesterday", "#059669"),
                    createStatCard("Orders Today", "184", "↑ 23 vs yesterday", "#D97706"),
                    createStatCard("Losses / Returns", "KES 4,200", "↑ 2 damaged returns", "#DC2626")
            );
        }
    }

    private void setupRevenueBars() {
        if (revenueBarContainer != null) {
            revenueBarContainer.getChildren().setAll(
                    createBar("45k", 0.45, "Nakuru", "#2563EB"),
                    createBar("75k", 0.75, "Mombasa", "#7C3AED"),
                    createBar("30k", 0.30, "Kisumu", "#10B981"),
                    createBar("90k", 0.90, "HQ", "#F59E0B")
            );
        }
    }

    private void setupStockAlerts() {
        if (stockSummaryBox != null) {
            stockSummaryBox.setStyle("-fx-background-color: #FEF2F2; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #FEE2E2;");
            Label summaryText = new Label("3 items require restocking");
            summaryText.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            stockSummaryBox.getChildren().setAll(summaryText);
        }

        if (stockAlertsContainer != null) {
            stockAlertsContainer.getChildren().setAll(
                    createAlertItem("Guinness 500ml", "Nakuru · 0 units", "Out of Stock", "#DC2626"),
                    createAlertItem("Tusker Lager", "Kisumu · 3 units", "Critical", "#EF4444"),
                    createAlertItem("White Cap", "Mombasa · 8 units", "Low", "#F59E0B")
            );
        }
    }

    private VBox createStatCard(String title, String val, String trend, String color) {
        VBox card = new VBox(8);
        card.setPrefWidth(240);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + color +
                "; -fx-border-width: 0 0 0 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

        Label t = new Label(title); t.setTextFill(Color.web("#64748B"));
        Label v = new Label(val); v.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        Label tr = new Label(trend); tr.setTextFill(Color.web(color)); tr.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");

        card.getChildren().addAll(t, v, tr);
        return card;
    }

    private VBox createBar(String label, double heightPercent, String branch, String color) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.BOTTOM_CENTER);
        Label val = new Label(label); val.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: #94A3B8;");
        Rectangle bar = new Rectangle(35, 160 * heightPercent);
        bar.setArcWidth(8); bar.setArcHeight(8); bar.setFill(Color.web(color));
        Label name = new Label(branch); name.setStyle("-fx-font-size: 11; -fx-text-fill: #64748B;");
        container.getChildren().addAll(val, bar, name);
        return container;
    }

    private HBox createAlertItem(String item, String detail, String status, String color) {
        HBox row = new HBox(); row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8;");
        VBox info = new VBox(2);
        Label title = new Label(item); title.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        Label sub = new Label(detail); sub.setStyle("-fx-font-size: 10; -fx-text-fill: #94A3B8;");
        info.getChildren().addAll(title, sub);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(status);
        badge.setStyle("-fx-background-color: " + color + "15; -fx-text-fill: " + color + "; -fx-padding: 3 8; -fx-background-radius: 4; -fx-font-weight: bold; -fx-font-size: 10;");
        row.getChildren().addAll(info, spacer, badge);
        return row;
    }

    public static class StatusCell<T> extends TableCell<T, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                Label badge = new Label(item);
                badge.getStyleClass().add("badge");
                if ("OK".equalsIgnoreCase(item) || "Delivered".equalsIgnoreCase(item) || "Received".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("badge-ok");
                } else if ("Low".equalsIgnoreCase(item) || "Pending".equalsIgnoreCase(item) || "Dispatched".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("badge-low");
                } else if ("Critical".equalsIgnoreCase(item) || "Delayed".equalsIgnoreCase(item) || "Out of Stock".equalsIgnoreCase(item) || "In Transit".equalsIgnoreCase(item)) {
                    // Treating In Transit as warning/low mostly depending on preference, but here let's map it clearly:
                    if ("In Transit".equalsIgnoreCase(item)) {
                        badge.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;"); // Blue tint
                    } else {
                        badge.getStyleClass().add("badge-critical");
                    }
                } else {
                    badge.getStyleClass().add("badge-low");
                }
                setGraphic(badge);
                setText(null);
            }
        }
    }
}