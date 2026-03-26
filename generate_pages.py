import os

base_dir = "/var/home/xaimoh/IdeaProjects/KamnywesoLiqureStore/src/main"
fxml_dir = f"{base_dir}/resources/com/example/kamnywesoliqourstore/admin"
java_dir = f"{base_dir}/java/com/example/kamnywesoliqourstore/admin"

os.makedirs(fxml_dir, exist_ok=True)
os.makedirs(java_dir, exist_ok=True)

def write_file(path, content):
    with open(path, 'w') as f:
        f.write(content.strip())

# 1. ORDER MANAGEMENT
write_file(f"{fxml_dir}/order-management-view.fxml", """
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>
<VBox spacing="25" style="-fx-padding: 30; -fx-background-color: #F8FAFC;" stylesheets="@../style.css" xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.example.kamnywesoliqourstore.admin.OrderManagementController">
    <Label text="Order Management" style="-fx-font-size: 28px; -fx-font-weight: bold;" styleClass="text-navy"/>
    <VBox spacing="15" styleClass="card">
        <HBox spacing="15" alignment="BOTTOM_LEFT">
            <VBox spacing="5" HBox.hgrow="ALWAYS"><Label text="SEARCH" styleClass="text-muted" style="-fx-font-size: 10px; -fx-font-weight: bold;"/><TextField fx:id="searchField" promptText="Search by customer name..." styleClass="input-field"/></VBox>
            <VBox spacing="5" minWidth="150"><Label text="BRANCH" styleClass="text-muted" style="-fx-font-size: 10px; -fx-font-weight: bold;"/><ComboBox fx:id="branchCombo" maxWidth="Infinity" styleClass="input-field" promptText="All Branches"/></VBox>
            <VBox spacing="5" minWidth="150"><Label text="STATUS" styleClass="text-muted" style="-fx-font-size: 10px; -fx-font-weight: bold;"/><ComboBox fx:id="statusCombo" maxWidth="Infinity" styleClass="input-field" promptText="All Statuses"/></VBox>
            <VBox spacing="5" minWidth="150"><Label text="DATE RANGE" styleClass="text-muted" style="-fx-font-size: 10px; -fx-font-weight: bold;"/><DatePicker fx:id="datePicker" maxWidth="Infinity" styleClass="input-field" promptText="mm / dd / yyyy"/></VBox>
            <Button text="Apply" styleClass="button-primary" prefHeight="42" prefWidth="100"/>
        </HBox>
    </VBox>
    <VBox styleClass="card" VBox.vgrow="ALWAYS" spacing="0"><TableView fx:id="orderTable" VBox.vgrow="ALWAYS" prefHeight="400" styleClass="table-view"/></VBox>
</VBox>
""")

write_file(f"{java_dir}/OrderManagementController.java", """
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
""")

# 2. STOCK MANAGEMENT
write_file(f"{fxml_dir}/stock-management-view.fxml", """
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>
<VBox spacing="25" style="-fx-padding: 30; -fx-background-color: #F8FAFC;" stylesheets="@../style.css" xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.example.kamnywesoliqourstore.admin.StockManagementController">
    <Label text="Stock Management" style="-fx-font-size: 28px; -fx-font-weight: bold;" styleClass="text-navy"/>
    <HBox fx:id="statCardsContainer" spacing="20" alignment="CENTER_LEFT"/>
    <VBox styleClass="card" VBox.vgrow="ALWAYS" spacing="10">
        <Label text="Stock Inventory" style="-fx-font-weight: bold; -fx-font-size: 16;"/>
        <TableView fx:id="stockTable" VBox.vgrow="ALWAYS" prefHeight="300" styleClass="table-view"/>
    </VBox>
    <VBox styleClass="card" VBox.vgrow="ALWAYS" spacing="10">
        <Label text="Damaged Stock Returns" style="-fx-font-weight: bold; -fx-font-size: 16;"/>
        <TableView fx:id="returnsTable" VBox.vgrow="ALWAYS" prefHeight="200" styleClass="table-view"/>
    </VBox>
</VBox>
""")

write_file(f"{java_dir}/StockManagementController.java", """
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
""")

# 3. REPORTS
write_file(f"{fxml_dir}/reports-view.fxml", """
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>
<VBox spacing="25" style="-fx-padding: 30; -fx-background-color: #F8FAFC;" stylesheets="@../style.css" xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.example.kamnywesoliqourstore.admin.ReportsController">
    <Label text="Reports" style="-fx-font-size: 28px; -fx-font-weight: bold;" styleClass="text-navy"/>
    <HBox spacing="25" VBox.vgrow="ALWAYS">
        <VBox spacing="10" minWidth="220" styleClass="card" style="-fx-padding: 15;">
            <Button text="Sales Report" styleClass="button-primary" maxWidth="Infinity" style="-fx-alignment: CENTER_LEFT; -fx-padding: 12 20;"/>
            <Button text="Profit &amp; Loss" style="-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;" maxWidth="Infinity"/>
            <Button text="Order Report" style="-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;" maxWidth="Infinity"/>
            <Button text="Stock Report" style="-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 12 20;" maxWidth="Infinity"/>
        </VBox>
        <VBox spacing="25" HBox.hgrow="ALWAYS">
            <VBox styleClass="card" spacing="20">
                <Label text="Generate Sales Report" style="-fx-font-weight: bold; -fx-font-size: 16;"/>
                <HBox spacing="15">
                    <VBox spacing="5" HBox.hgrow="ALWAYS"><Label text="FROM DATE" styleClass="text-muted"/><DatePicker maxWidth="Infinity" styleClass="input-field"/></VBox>
                    <VBox spacing="5" HBox.hgrow="ALWAYS"><Label text="TO DATE" styleClass="text-muted"/><DatePicker maxWidth="Infinity" styleClass="input-field"/></VBox>
                </HBox>
                <VBox spacing="10">
                    <Label text="BRANCHES" styleClass="text-muted"/>
                    <HBox spacing="15"><CheckBox text="All" selected="true"/><CheckBox text="Nakuru"/><CheckBox text="Mombasa"/><CheckBox text="Kisumu"/><CheckBox text="HQ"/></HBox>
                </VBox>
                <Button text="Generate Report" styleClass="button-primary"/>
            </VBox>
            <VBox styleClass="card" VBox.vgrow="ALWAYS" spacing="10">
                <Label text="Preview: Sales Report" style="-fx-font-weight: bold; -fx-font-size: 16;"/>
                <TableView fx:id="reportTable" VBox.vgrow="ALWAYS" prefHeight="250"/>
            </VBox>
        </VBox>
    </HBox>
</VBox>
""")

write_file(f"{java_dir}/ReportsController.java", """
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
""")

# 4. USER MANAGEMENT
write_file(f"{fxml_dir}/user-management-view.fxml", """
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>
<VBox spacing="25" style="-fx-padding: 30; -fx-background-color: #F8FAFC;" stylesheets="@../style.css" xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.example.kamnywesoliqourstore.admin.UserManagementController">
    <Label text="User Management" style="-fx-font-size: 28px; -fx-font-weight: bold;" styleClass="text-navy"/>
    <VBox styleClass="card" VBox.vgrow="ALWAYS" spacing="10">
        <HBox spacing="10"><Button text="Staff Accounts" styleClass="button-primary"/><Button text="Pending Approvals" style="-fx-background-color: transparent;"/></HBox>
        <TableView fx:id="userTable" VBox.vgrow="ALWAYS" prefHeight="350"/>
    </VBox>
</VBox>
""")

write_file(f"{java_dir}/UserManagementController.java", """
package com.example.kamnywesoliqourstore.admin;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class UserManagementController {
    @FXML private TableView<UserModel> userTable;
    public static class UserModel {
        private String name, id, branch, role, status, lastLogin;
        public UserModel(String n, String i, String b, String r, String s, String l) { name=n; id=i; branch=b; role=r; status=s; lastLogin=l; }
        public String getName() { return name; } public String getId() { return id; } public String getBranch() { return branch; } public String getRole() { return role; } public String getStatus() { return status; } public String getLastLogin() { return lastLogin; }
    }
    @FXML public void initialize() {
        TableColumn<UserModel, String> cn = new TableColumn<>("NAME"); cn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<UserModel, String> ci = new TableColumn<>("STAFF ID"); ci.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UserModel, String> cb = new TableColumn<>("BRANCH"); cb.setCellValueFactory(new PropertyValueFactory<>("branch"));
        TableColumn<UserModel, String> cr = new TableColumn<>("ROLE"); cr.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<UserModel, String> cs = new TableColumn<>("STATUS"); cs.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<UserModel, String> cl = new TableColumn<>("LAST LOGIN"); cl.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));
        cs.setCellFactory(c -> new AdminDashboardController.StatusCell<>());
        userTable.getColumns().setAll(cn, ci, cb, cr, cs, cl);
        userTable.setItems(FXCollections.observableArrayList(
            new UserModel("Sarah Juma", "DS-4421", "Nakuru", "Branch Manager", "Received", "24 Oct, 08:00"),
            new UserModel("James Mwangi", "DS-4422", "Mombasa", "Cashier", "Received", "24 Oct, 07:45"),
            new UserModel("Peter Ochieng", "DS-4423", "Kisumu", "Cashier", "Critical", "20 Oct, 14:00")
        ));
    }
}
""")

print("Successfully generated files.")
