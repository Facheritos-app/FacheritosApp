module com.example.facheritosfrontendapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires annotations;
    requires okio;
    requires lombok;
    requires com.google.gson;
    requires java.dotenv;


    //opens com.example.facheritosfrontendapp to javafx.fxml;
    //exports com.example.facheritosfrontendapp;

    exports com.example.facheritosfrontendapp.views;
    opens com.example.facheritosfrontendapp.views to javafx.fxml;
    exports com.example.facheritosfrontendapp.controller;
    opens com.example.facheritosfrontendapp.controller to javafx.fxml;
    opens com.example.facheritosfrontendapp.dto.loginDTO to com.google.gson;
    opens com.example.facheritosfrontendapp.dto.personDTO to com.google.gson;
    opens com.example.facheritosfrontendapp.dto.otherDTO to com.google.gson;

}