package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderProcessingController implements DashboardChild {

    // ── Header fields ──────────────────────────────────────────
    @FXML private TextField customerNameField;
    @FXML private TextField customerPhoneField;
    @FXML private ComboBox<String> orderBranchCombo;
    @FXML private TextField loyaltyCardField;
    @FXML private TextField drinkSearchField;

    // ── Cart ───────────────────────────────────────────────────
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

    // ── Buttons ────────────────────────────────────────────────
    @FXML private Button walkInBtn;
    @FXML private Button registeredBtn;
    @FXML private Button submitOrderBtn;

    // ── Success panel ──────────────────────────────────────────
    @FXML private VBox successPanel;
    @FXML private Label successOrderIdLabel;
    @FXML private Label successAmountLabel;

    // ── Session ────────────────────────────────────────────────
    private DashboardController dashboard;
    private String staffName;
    private String role;
    private String branch;

    // ── Cart data ──────────────────────────────────────────────
    private static final double VAT_RATE = 0.16;

    private static class CartItem {
        String name;
        double price;
        int quantity;
        int stock;

        CartItem(String name, double price, int qty, int stock) {
            this.name     = name;
            this.price    = price;
            this.quantity = qty;
            this.stock    = stock;
        }

        double lineTotal() { return price * quantity; }
    }

    private List<CartItem> cartItems = new ArrayList<>();

    // ───────────────────────────────────────────────────────────
    // DashboardChild implementation
    // ───────────────────────────────────────────────────────────
    @Override
    public void setDashboardController(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void setSessionData(String staffName, String role, String branch) {
        this.staffName = staffName;
        this.role      = role;
        this.branch    = branch;
        orderBranchCombo.setValue(branch);
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    // ───────────────────────────────────────────────────────────
    // INITIALIZE
    // ───────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        orderBranchCombo.getItems().addAll(
                "Nairobi HQ", "Mombasa Branch",
                "Kisumu Branch", "Nakuru Branch");
        updateCartDisplay();
    }

    // ───────────────────────────────────────────────────────────
    // CUSTOMER TYPE TOGGLE
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleWalkIn() {
        walkInBtn.setStyle(
                "-fx-background-color: #E04A2A; -fx-background-radius: 8; " +
                        "-fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand; -fx-border-color: transparent;");
        registeredBtn.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #DDDDDD; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-text-fill: #444444; -fx-font-size: 12; -fx-cursor: hand;");
        customerNameField.setPromptText("John Doe");
        loyaltyCardField.setDisable(false);
    }

    @FXML
    private void handleRegistered() {
        registeredBtn.setStyle(
                "-fx-background-color: #E04A2A; -fx-background-radius: 8; " +
                        "-fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand; -fx-border-color: transparent;");
        walkInBtn.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #DDDDDD; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-text-fill: #444444; -fx-font-size: 12; -fx-cursor: hand;");
        customerNameField.setPromptText("Search by name or staff ID...");
    }

    // ───────────────────────────────────────────────────────────
    // ADD TO CART HANDLERS
    // ───────────────────────────────────────────────────────────
    @FXML private void handleAddAlvaro()   { addToCart("Alvaro 500ml",       120, 45); }
    @FXML private void handleAddFanta()    { addToCart("Fanta Orange 500ml", 80,  120); }
    @FXML private void handleAddCocaCola() { addToCart("Coca Cola 500ml",    80,  98); }
    @FXML private void handleAddSprite()   { addToCart("Sprite 500ml",       80,  8); }
    @FXML private void handleAddWater()    { addToCart("Dasani Water 500ml", 50,  200); }
    @FXML private void handleAddWine()     { /* Out of stock — button disabled */ }

    private void addToCart(String name, double price, int stock) {
        // Check if already in cart — increment quantity
        for (CartItem item : cartItems) {
            if (item.name.equals(name)) {
                if (item.quantity < item.stock) {
                    item.quantity++;
                    updateCartDisplay();
                    if (dashboard != null) dashboard.resetSessionTimer();
                } else {
                    showError("No more stock available for " + name);
                }
                return;
            }
        }
        cartItems.add(new CartItem(name, price, 1, stock));
        orderErrorLabel.setVisible(false);
        updateCartDisplay();
        if (dashboard != null) dashboard.resetSessionTimer();
    }

    // ───────────────────────────────────────────────────────────
    // CART DISPLAY
    // ───────────────────────────────────────────────────────────
    private void updateCartDisplay() {
        cartItemsBox.getChildren().clear();

        if (cartItems.isEmpty()) {
            cartItemsBox.getChildren().add(emptyCartLabel);
            emptyCartLabel.setVisible(true);
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
            HBox row = buildCartRow(item);
            cartItemsBox.getChildren().add(row);
            if (item.stock <= 10) hasLowStock = true;
        }

        lowStockWarning.setVisible(hasLowStock);

        int totalItems = cartItems.stream()
                .mapToInt(i -> i.quantity).sum();
        cartItemCount.setText(totalItems + " item" +
                (totalItems != 1 ? "s" : ""));

        double subtotal = cartItems.stream()
                .mapToDouble(CartItem::lineTotal).sum();
        double tax   = subtotal * VAT_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText(formatKes(subtotal));
        taxLabel.setText(formatKes(tax));
        totalLabel.setText(formatKes(total));

        // Loyalty points: 1 pt per KES 10 spent
        int points = (int)(subtotal / 10);
        if (points > 0) {
            loyaltyPointsLabel.setText("+" + points +
                    " loyalty points will be earned");
            loyaltyPointsBox.setVisible(true);
        } else {
            loyaltyPointsBox.setVisible(false);
        }
    }

    private HBox buildCartRow(CartItem item) {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: transparent transparent " +
                "#F3F4F6 transparent; -fx-border-width: 1; " +
                "-fx-padding: 6 0 6 0;");

        VBox nameBox = new VBox(2);
        nameBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameBox, javafx.scene.layout.Priority.ALWAYS);
        Label nameLabel = new Label(item.name);
        nameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; " +
                "-fx-text-fill: #0D1B2A;");
        Label priceLabel = new Label(formatKes(item.price) + " each");
        priceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #888888;");
        nameBox.getChildren().addAll(nameLabel, priceLabel);

        // Quantity controls
        Button minusBtn = new Button("-");
        minusBtn.setStyle(
                "-fx-background-color: #F3F4F6; -fx-border-color: #E5E7EB; " +
                        "-fx-border-radius: 4; -fx-background-radius: 4; " +
                        "-fx-font-size: 15; -fx-cursor: hand; -fx-min-width: 26; " +
                        "-fx-min-height: 26;");
        minusBtn.setOnAction(e -> {
            if (item.quantity > 1) {
                item.quantity--;
            } else {
                cartItems.remove(item);
            }
            updateCartDisplay();
        });

        Label qtyLabel = new Label(String.valueOf(item.quantity));
        qtyLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; " +
                "-fx-text-fill: #0D1B2A; -fx-min-width: 22; " +
                "-fx-alignment: center;");

        Button plusBtn = new Button("+");
        plusBtn.setStyle(
                "-fx-background-color: #F3F4F6; -fx-border-color: #E5E7EB; " +
                        "-fx-border-radius: 4; -fx-background-radius: 4; " +
                        "-fx-font-size: 15; -fx-cursor: hand; -fx-min-width: 26; " +
                        "-fx-min-height: 26;");
        plusBtn.setOnAction(e -> {
            if (item.quantity < item.stock) {
                item.quantity++;
                updateCartDisplay();
            }
        });

        Label lineTotalLabel = new Label(formatKes(item.lineTotal()));
        lineTotalLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; " +
                "-fx-text-fill: #0D1B2A; -fx-min-width: 70; " +
                "-fx-alignment: CENTER_RIGHT;");

        row.getChildren().addAll(nameBox, minusBtn, qtyLabel,
                plusBtn, lineTotalLabel);
        return row;
    }

    // ───────────────────────────────────────────────────────────
    // SUBMIT ORDER
    // ───────────────────────────────────────────────────────────
    @FXML
    private void handleSubmitOrder() {
        if (dashboard != null) dashboard.resetSessionTimer();

        String name   = customerNameField.getText().trim();
        String phone  = customerPhoneField.getText().trim();
        String branch = orderBranchCombo.getValue();

        if (name.isEmpty()) {
            showError("Please enter the customer name.");
            return;
        }
        if (phone.isEmpty()) {
            showError("Please enter the customer phone number.");
            return;
        }
        if (branch == null) {
            showError("Please select a branch.");
            return;
        }
        if (cartItems.isEmpty()) {
            showError("Your cart is empty. Please add drinks.");
            return;
        }

        // Generate order ID
        String orderId = "#ORD-" + (8800 + new Random().nextInt(99));
        double subtotal = cartItems.stream()
                .mapToDouble(CartItem::lineTotal).sum();
        double total = subtotal * (1 + VAT_RATE);

        // TODO: Save to database here
        System.out.println("Order submitted: " + orderId);
        System.out.println("Customer: " + name + " · " + phone);
        System.out.println("Branch: " + branch);
        System.out.println("Total: " + formatKes(total));
        cartItems.forEach(i -> System.out.println(
                "  " + i.name + " x" + i.quantity +
                        " = " + formatKes(i.lineTotal())));

        // Show success
        successOrderIdLabel.setText("Order ID: " + orderId);
        successAmountLabel.setText("Total: " + formatKes(total));
        successPanel.setVisible(true);
        successPanel.setManaged(true);

        orderErrorLabel.setVisible(false);
        cartItems.clear();
        updateCartDisplay();
    }

    @FXML
    private void handleClearCart() {
        cartItems.clear();
        updateCartDisplay();
        orderErrorLabel.setVisible(false);
    }

    @FXML
    private void handlePrintReceipt() {
        // TODO: Implement PDF receipt printing
        System.out.println("Printing receipt...");
    }

    @FXML
    private void handleNewOrderAfterSuccess() {
        successPanel.setVisible(false);
        successPanel.setManaged(false);
        customerNameField.clear();
        customerPhoneField.clear();
        loyaltyCardField.clear();
        orderErrorLabel.setVisible(false);
    }

    @FXML
    private void handleViewOrderHistory() {
        // TODO: Navigate to order history or show dialog
        System.out.println("View order history clicked.");
    }

    // ───────────────────────────────────────────────────────────
    // HELPERS
    // ───────────────────────────────────────────────────────────
    private void showError(String message) {
        orderErrorLabel.setText(message);
        orderErrorLabel.setVisible(true);
    }

    private String formatKes(double amount) {
        return String.format("KES %,.0f", amount);
    }
}
