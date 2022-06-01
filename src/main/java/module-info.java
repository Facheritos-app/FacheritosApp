module com.example.facheritosfrontendapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires annotations;
    requires okio;
    requires lombok;
    requires com.google.gson;


    //opens com.example.facheritosfrontendapp to javafx.fxml;
    //exports com.example.facheritosfrontendapp;

    exports com.example.facheritosfrontendapp.vistas;
    opens com.example.facheritosfrontendapp.vistas to javafx.fxml;
    exports com.example.facheritosfrontendapp.controladores;
    opens com.example.facheritosfrontendapp.controladores to javafx.fxml;
    opens com.example.facheritosfrontendapp.dto to com.google.gson;

}