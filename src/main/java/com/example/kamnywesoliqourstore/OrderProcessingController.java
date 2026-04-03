package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderProcessingController implements DashboardChild, BranchChild {

    @FXML private TextField customerNameField;
    @FXML private TextField customerPhoneField;
    @FXML private ComboBox<String> orderBranchCombo;
    @FXML private TextField loyaltyCardField;
    @FXML private TextField drinkSearchField;
    @FXML private VBox cartItemsBox;
    @FXML private Label emptyCartLabel;
    @FXML private Label cartItemCount;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Label orderErrorLabel;
    @FXML private Label loyaltyPointsLabel;
    @FXML private HBox loyaltyPointsBox;
    @FXML private HBox lowStockWarning;
    @FXML private Button walkInBtn;
    @FXML private Button registeredBtn;
    @FXML private VBox successPanel;
    @FXML private Label successOrderIdLabel;
    @FXML private Label successAmountLabel;

    private DashboardController hqDashboard;
    private BranchDashboardController branchDashboard;
    private String staffName, role, branch;
    private boolean isBranchMode = false;
    private static final double VAT_RATE = 0.16;

    private static class CartItem {
        String name; double price; int quantity; int stock;
        CartItem(String n, double p, int q, int s) { name=n; price=p; quantity=q; stock=s; }
        double lineTotal() { return price * quantity; }
    }
    private final List<CartItem> cartItems = new ArrayList<>();

    @Override
    public void setDashboardController(DashboardController d) {
        this.hqDashboard = d;
        this.isBranchMode = false;
    }

    @Override
    public void setBranchDashboardController(BranchDashboardController d) {
        this.branchDashboard = d;
        this.isBranchMode = true;
    }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;

        if (orderBranchCombo != null) {
            if (isBranchMode) {
                // Branch mode: auto-fill with current branch and disable editing
                orderBranchCombo.getItems().clear();
                orderBranchCombo.getItems().add(branch);
                orderBranchCombo.setValue(branch);
                orderBranchCombo.setDisable(true);
                orderBranchCombo.setStyle(
                        "-fx-background-color: #F0F0F0; -fx-border-color: #CCCCCC; " +
                                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-opacity: 0.85;");
            } else {
                // HQ mode: all branches selectable
                if (!orderBranchCombo.getItems().contains(branch)) {
                    orderBranchCombo.getItems().setAll(
                            "Nairobi HQ", "Mombasa Branch",
                            "Kisumu Branch", "Nakuru Branch");
                }
                orderBranchCombo.setValue(branch);
            }
        }
        resetTimer();
    }

    @FXML
    public void initialize() {
        orderBranchCombo.getItems().addAll(
                "Nairobi HQ", "Mombasa Branch",
                "Kisumu Branch", "Nakuru Branch");
        updateCartDisplay();
    }

    @FXML private void handleWalkIn() {
        walkInBtn.setStyle("-fx-background-color: #E04A2A; -fx-background-radius: 8; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand; -fx-border-color: transparent;");
        registeredBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #DDDDDD; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #444444; -fx-font-size: 12; -fx-cursor: hand;");
    }

    @FXML private void handleRegistered() {
        registeredBtn.setStyle("-fx-background-color: #E04A2A; -fx-background-radius: 8; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand; -fx-border-color: transparent;");
        walkInBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #DDDDDD; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #444444; -fx-font-size: 12; -fx-cursor: hand;");
    }

    @FXML private void handleAddAlvaro()   { addToCart("Alvaro 500ml",       120, 45); }
    @FXML private void handleAddFanta()    { addToCart("Fanta Orange 500ml", 80, 120); }
    @FXML private void handleAddCocaCola() { addToCart("Coca Cola 500ml",    80,  98); }
    @FXML private void handleAddSprite()   { addToCart("Sprite 500ml",       80,   8); }
    @FXML private void handleAddWater()    { addToCart("Dasani Water 500ml", 50, 200); }
    @FXML private void handleAddWine()     { /* Out of stock — button disabled */ }

    private void addToCart(String name, double price, int stock) {
        for (CartItem item : cartItems) {
            if (item.name.equals(name)) {
                if (item.quantity < item.stock) { item.quantity++; updateCartDisplay(); }
                else showError("No more stock available for " + name);
                return;
            }
        }
        cartItems.add(new CartItem(name, price, 1, stock));
        orderErrorLabel.setVisible(false);
        updateCartDisplay();
        resetTimer();
    }

    private void updateCartDisplay() {
        cartItemsBox.getChildren().clear();

        if (cartItems.isEmpty()) {
            emptyCartLabel.setVisible(true);
            cartItemsBox.getChildren().add(emptyCartLabel);
            cartItemCount.setText("0 items");
            subtotalLabel.setText("KES 0");
            taxLabel.setText("KES 0");
            totalLabel.setText("KES 0");
            loyaltyPointsBox.setVisible(false);
            lowStockWarning.setVisible(false);
            return;
        }

        boolean hasLowStock = false;
        for (CartItem item : cartItems) {
            cartItemsBox.getChildren().add(buildCartRow(item));
            if (item.stock <= 10) hasLowStock = true;
        }

        lowStockWarning.setVisible(hasLowStock);
        int totalQty    = cartItems.stream().mapToInt(i -> i.quantity).sum();
        cartItemCount.setText(totalQty + " item" + (totalQty != 1 ? "s" : ""));
        double subtotal = cartItems.stream().mapToDouble(CartItem::lineTotal).sum();
        double tax      = subtotal * VAT_RATE;
        double total    = subtotal + tax;
        subtotalLabel.setText(formatKes(subtotal));
        taxLabel.setText(formatKes(tax));
        totalLabel.setText(formatKes(total));
        int pts = (int)(subtotal / 10);
        if (pts > 0) { loyaltyPointsLabel.setText("+" + pts + " loyalty points will be earned"); loyaltyPointsBox.setVisible(true); }
        else loyaltyPointsBox.setVisible(false);
    }

    private HBox buildCartRow(CartItem item) {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: transparent transparent #F3F4F6 transparent; " +
                "-fx-border-width: 0 0 1 0; -fx-padding: 8 0 8 0;");

        // Name + price label
        VBox nameBox = new VBox(2);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label nameL  = new Label(item.name);
        nameL.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #0D1B2A;");
        Label priceL = new Label(formatKes(item.price) + " each");
        priceL.setStyle("-fx-font-size: 11; -fx-text-fill: #888888;");
        nameBox.getChildren().addAll(nameL, priceL);

        Button minus = new Button("\u2212"); // −

        minus.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-alignment: center; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 1; " +
                        "-fx-background-radius: 10; " +   // smooth rounded corners
                        "-fx-border-radius: 10; " +
                        "-fx-pref-width: 32; " +
                        "-fx-pref-height: 32;"
        );
        minus.setOnAction(e -> {
            if (item.quantity > 1) item.quantity--;
            else cartItems.remove(item);
            updateCartDisplay();
        });

        // Quantity label
        Label qtyL = new Label(String.valueOf(item.quantity));
        qtyL.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #0D1B2A; " +
                "-fx-min-width: 26; -fx-alignment: CENTER; -fx-pref-width: 26;");

        // ── PLUS BUTTON — same explicit styling ──
        Button plus = new Button("\u002B"); // +

        plus.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Arial'; " +
                        "-fx-alignment: center; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 1; " +
                        "-fx-background-radius: 10; " +   // same rounding
                        "-fx-border-radius: 10; " +
                        "-fx-pref-width: 32; " +
                        "-fx-pref-height: 32;"
        );
        plus.setOnAction(e -> {
            if (item.quantity < item.stock) { item.quantity++; updateCartDisplay(); }
        });

        // Line total
        Label lineL = new Label(formatKes(item.lineTotal()));
        lineL.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #0D1B2A; " +
                "-fx-min-width: 74; -fx-alignment: CENTER_RIGHT;");

        row.getChildren().addAll(nameBox, minus, qtyL, plus, lineL);
        return row;
    }

    @FXML
    private void handleSubmitOrder() {
        String name     = customerNameField.getText().trim();
        String phone    = customerPhoneField.getText().trim();
        String branchVal = orderBranchCombo.getValue();
        if (name.isEmpty())      { showError("Please enter the customer name.");  return; }
        if (phone.isEmpty())     { showError("Please enter the customer phone."); return; }
        if (branchVal == null)   { showError("Branch is not set.");               return; }
        if (cartItems.isEmpty()) { showError("Your cart is empty.");              return; }

        String orderId  = "#ORD-" + (8800 + new Random().nextInt(99));
        double subtotal = cartItems.stream().mapToDouble(CartItem::lineTotal).sum();
        double total    = subtotal * (1 + VAT_RATE);
        System.out.println("Order submitted: " + orderId + " at " + branchVal);
        successOrderIdLabel.setText("Order ID: " + orderId);
        successAmountLabel.setText("Total: " + formatKes(total) + "  (incl. 16% VAT)");
        successPanel.setVisible(true);
        successPanel.setManaged(true);
        orderErrorLabel.setVisible(false);
        cartItems.clear();
        updateCartDisplay();
        resetTimer();
    }

    @FXML private void handleClearCart() {
        cartItems.clear();
        updateCartDisplay();
        orderErrorLabel.setVisible(false);
    }

    @FXML private void handlePrintReceipt()         { System.out.println("Printing receipt..."); }
    @FXML private void handleNewOrderAfterSuccess() {
        successPanel.setVisible(false);
        successPanel.setManaged(false);
        customerNameField.clear();
        customerPhoneField.clear();
        loyaltyCardField.clear();
        orderErrorLabel.setVisible(false);
    }
    @FXML private void handleViewOrderHistory() { System.out.println("View order history."); }

    private void showError(String msg) { orderErrorLabel.setText(msg); orderErrorLabel.setVisible(true); }
    private String formatKes(double v) { return String.format("KES %,.0f", v); }
    private void resetTimer() {
        if (hqDashboard     != null) hqDashboard.resetSessionTimer();
        if (branchDashboard != null) branchDashboard.resetSessionTimer();
    }
}
