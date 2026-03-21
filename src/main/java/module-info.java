module com.example.kamnywesoliqourstore {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.kamnywesoliqourstore to javafx.fxml;
    exports com.example.kamnywesoliqourstore;
}