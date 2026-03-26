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