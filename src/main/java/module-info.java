module com.example.facheritosfrontendapp {
    requires javafx.controls;
    requires javafx.fxml;


    //opens com.example.facheritosfrontendapp to javafx.fxml;
    //exports com.example.facheritosfrontendapp;

    exports com.example.facheritosfrontendapp.vistas;
    opens com.example.facheritosfrontendapp.vistas to javafx.fxml;
    exports com.example.facheritosfrontendapp.controladores;
    opens com.example.facheritosfrontendapp.controladores to javafx.fxml;

}