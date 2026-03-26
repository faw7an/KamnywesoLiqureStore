package com.example.kamnywesoliqourstore;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterController {

    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());

    @FXML private VBox stepContainer;
    @FXML private Region dot1, dot2, dot3;

    private int currentStep = 1;

    @FXML
    public void initialize() {
        showStep1();
    }

    private void showStep1() {
        currentStep = 1;
        updateDots();
        stepContainer.getChildren().clear();

        VBox form = new VBox(12);
        form.getChildren().addAll(
                createFieldGroup("FULL NAME", new TextField()),
                createFieldGroup("PHONE NUMBER", new TextField()),
                createFieldGroup("EMAIL", new TextField()),
                createComboGroup("BRANCH", "Nairobi HQ", "Nakuru", "Mombasa", "Kisumu"),
                createComboGroup("ROLE", "Cashier", "Branch Manager", "Administrator")
        );

        Button btn = new Button("Continue →");
        btn.getStyleClass().add("button-primary");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> showStep2());

        stepContainer.getChildren().addAll(form, btn);
    }

    private void showStep2() {
        currentStep = 2;
        updateDots();
        stepContainer.getChildren().clear();

        PasswordField pass = new PasswordField();
        PasswordField confirm = new PasswordField();

        VBox rulesBox = new VBox(5);
        Label r1 = new Label("○ At least 8 characters"); r1.getStyleClass().add("rule-text");
        Label r2 = new Label("○ One uppercase letter"); r2.getStyleClass().add("rule-text");

        // Real-time validation
        pass.textProperty().addListener((obs, oldVal, newVal) -> {
            r1.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("met"), newVal.length() >= 8);
            r2.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("met"), newVal.matches(".*[A-Z].*"));
        });

        HBox nav = new HBox(10);
        Button back = new Button("← Back"); back.getStyleClass().add("button-ghost"); back.setOnAction(e -> showStep1());
        Button cont = new Button("Continue →"); cont.getStyleClass().add("button-primary"); cont.setOnAction(e -> showStep3());
        HBox.setHgrow(back, Priority.ALWAYS); HBox.setHgrow(cont, Priority.ALWAYS);
        back.setMaxWidth(Double.MAX_VALUE); cont.setMaxWidth(Double.MAX_VALUE);
        nav.getChildren().addAll(back, cont);

        stepContainer.getChildren().addAll(
                createFieldGroup("PASSWORD", pass),
                createFieldGroup("CONFIRM PASSWORD", confirm),
                rulesBox, nav
        );
    }

    private void showStep3() {
        currentStep = 3;
        updateDots();
        stepContainer.getChildren().clear();

        Label info = new Label("OTP sent to +254 7XX XXX XXX");
        info.getStyleClass().add("text-muted");

        HBox otpBox = new HBox(5); // You can reuse your OTP logic here
        for(int i=0; i<6; i++) {
            TextField tf = new TextField(); tf.getStyleClass().add("otp-input");
            tf.setPrefWidth(45); otpBox.getChildren().add(tf);
        }

        Button complete = new Button("Complete Registration");
        complete.getStyleClass().add("button-primary");
        complete.setMaxWidth(Double.MAX_VALUE);
        complete.setOnAction(e -> navigateToLogin());

        stepContainer.getChildren().addAll(info, otpBox, complete);
    }

    private void updateDots() {
        dot1.getStyleClass().removeAll("dot-active", "dot-complete");
        dot2.getStyleClass().removeAll("dot-active", "dot-complete");
        dot3.getStyleClass().removeAll("dot-active", "dot-complete");

        if (currentStep >= 1) dot1.getStyleClass().add(currentStep == 1 ? "dot-active" : "dot-complete");
        if (currentStep >= 2) dot2.getStyleClass().add(currentStep == 2 ? "dot-active" : "dot-complete");
        if (currentStep >= 3) dot3.getStyleClass().add("dot-active");
    }

    @FXML
    private void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) stepContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Kamnyweso Liquor - Login");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate back to login-view.fxml", e);
        }
    }

    // Helper methods to generate UI groups programmatically
    private VBox createFieldGroup(String label, Control input) {
        Label l = new Label(label); l.getStyleClass().add("text-muted");
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        input.getStyleClass().add("input-field");
        return new VBox(5, l, input);
    }

    private VBox createComboGroup(String label, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.getSelectionModel().selectFirst();
        cb.setMaxWidth(Double.MAX_VALUE);
        return createFieldGroup(label, cb);
    }
}