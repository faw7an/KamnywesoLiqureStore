module com.example.kamnywesoliqourstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.kamnywesoliqourstore to javafx.fxml;
    exports com.example.kamnywesoliqourstore;
}