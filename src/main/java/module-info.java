module com.example.kamnywesoliqourstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging; // <--- ADD THIS LINE FOR THE LOGGER

    // Main package
    opens com.example.kamnywesoliqourstore to javafx.fxml;
    exports com.example.kamnywesoliqourstore;

    opens com.example.kamnywesoliqourstore.auth to javafx.fxml;
    exports com.example.kamnywesoliqourstore.auth;

    opens com.example.kamnywesoliqourstore.admin to javafx.fxml;
    exports com.example.kamnywesoliqourstore.admin;

    // Temporarily comment these out until you add a Java file to the branch folder
    // opens com.example.kamnywesoliqourstore.branch to javafx.fxml;
    // exports com.example.kamnywesoliqourstore.branch;
}