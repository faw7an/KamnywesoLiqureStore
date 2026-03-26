package com.example.kamnywesoliqourstore.admin;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
public class StockManagementController {
    @FXML private HBox statCardsContainer;
    @FXML private TableView<StockModel> stockTable;
    @FXML private TableView<ReturnModel> returnsTable;

    public static class StockModel {
        private String name, brand, branch, status, restocked; private int stock, min;
        public StockModel(String n, String br, String b, int s, int m, String st, String r) { name=n; brand=br; branch=b; stock=s; min=m; status=st; restocked=r; }
        public String getName() { return name; } public String getBrand() { return brand; } public String getBranch() { return branch; } public int getStock() { return stock; } public int getMin() { return min; } public String getStatus() { return status; } public String getRestocked() { return restocked; }
    }
    public static class ReturnModel {
        private String id, branch, item, reason, status, date; private int qty;
        public ReturnModel(String i, String b, String it, int q, String r, String s, String d) { id=i; branch=b; item=it; qty=q; reason=r; status=s; date=d; }
        public String getId() { return id; } public String getBranch() { return branch; } public String getItem() { return item; } public int getQty() { return qty; } public String getReason() { return reason; } public String getStatus() { return status; } public String getDate() { return date; }
    }
    
    @FXML public void initialize() {
        statCardsContainer.getChildren().setAll(
            createStatCard("Total SKUs", "24", "↑ 2 new items", "#2563EB"), createStatCard("Low Stock Items", "3", "↑ 1 vs yesterday", "#D97706"),
            createStatCard("Out of Stock", "1", "↑ Guinness Nakuru", "#DC2626"), createStatCard("Pending Returns", "1", "↑ Awaiting review", "#7C3AED")
        );
        setupTables();
    }
    
    private VBox createStatCard(String title, String val, String trend, String color) {
        VBox card = new VBox(8); card.setPrefWidth(240); card.setPadding(new Insets(20)); card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + color + "; -fx-border-width: 0 0 0 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
        Label t = new Label(title); t.setTextFill(Color.web("#64748B")); Label v = new Label(val); v.setStyle("-fx-font-size: 20; -fx-font-weight: bold;"); Label tr = new Label(trend); tr.setTextFill(Color.web(color)); tr.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        card.getChildren().addAll(t, v, tr); return card;
    }
    
    private void setupTables() {
        TableColumn<StockModel, String> cn = new TableColumn<>("ITEM NAME"); cn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<StockModel, String> cb = new TableColumn<>("BRAND"); cb.setCellValueFactory(new PropertyValueFactory<>("brand"));
        TableColumn<StockModel, String> cbr = new TableColumn<>("BRANCH"); cbr.setCellValueFactory(new PropertyValueFactory<>("branch"));
        TableColumn<StockModel, Integer> cs = new TableColumn<>("STOCK"); cs.setCellValueFactory(new PropertyValueFactory<>("stock"));
        TableColumn<StockModel, Integer> cm = new TableColumn<>("MIN"); cm.setCellValueFactory(new PropertyValueFactory<>("min"));
        TableColumn<StockModel, String> cst = new TableColumn<>("STATUS"); cst.setCellValueFactory(new PropertyValueFactory<>("status"));
        cst.setCellFactory(c -> new AdminDashboardController.StatusCell<>());
        stockTable.getColumns().setAll(cn, cb, cbr, cs, cm, cst);
        stockTable.setItems(FXCollections.observableArrayList(
            new StockModel("Tusker Lager", "EABL", "Nakuru", 48, 20, "OK", "22 Oct"),
            new StockModel("Guinness 500ml", "Diageo", "Nakuru", 0, 10, "Out of Stock", "18 Oct"),
            new StockModel("Coca Cola", "Coke", "Mombasa", 120, 30, "OK", "23 Oct")
        ));

        TableColumn<ReturnModel, String> rn = new TableColumn<>("RETURN ID"); rn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ReturnModel, String> rb = new TableColumn<>("BRANCH"); rb.setCellValueFactory(new PropertyValueFactory<>("branch"));
        TableColumn<ReturnModel, String> ri = new TableColumn<>("ITEM"); ri.setCellValueFactory(new PropertyValueFactory<>("item"));
        TableColumn<ReturnModel, String> rq = new TableColumn<>("QTY"); rq.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn<ReturnModel, String> rr = new TableColumn<>("REASON"); rr.setCellValueFactory(new PropertyValueFactory<>("reason"));
        TableColumn<ReturnModel, String> rst = new TableColumn<>("STATUS"); rst.setCellValueFactory(new PropertyValueFactory<>("status"));
        rst.setCellFactory(c -> new AdminDashboardController.StatusCell<>());
        returnsTable.getColumns().setAll(rn, rb, ri, rq, rr, rst);
        returnsTable.setItems(FXCollections.observableArrayList(
            new ReturnModel("RTN-001", "Nakuru", "Guinness 500ml", 4, "Damaged in transit", "Pending", "23 Oct"),
            new ReturnModel("RTN-002", "Mombasa", "Tusker Lager", 2, "Expired stock", "Received", "22 Oct")
        ));
    }
}