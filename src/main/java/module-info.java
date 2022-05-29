module com.example.facheritosfrontendapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.facheritosfrontendapp to javafx.fxml;
    exports com.example.facheritosfrontendapp;
}