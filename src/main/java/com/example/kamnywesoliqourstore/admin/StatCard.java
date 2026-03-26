package com.example.kamnywesoliqourstore.admin;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatCard extends VBox {

    public StatCard(String title, String value, String trend, String trendColor) {
        super(8);
        this.getStyleClass().add("stat-card");
        this.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("text-muted");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("text-navy");
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label trendLabel = new Label(trend);
        trendLabel.setStyle("-fx-text-fill: " + trendColor + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        this.getChildren().addAll(titleLabel, valueLabel, trendLabel);
    }
}
