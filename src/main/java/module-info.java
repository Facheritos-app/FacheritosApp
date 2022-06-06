module  facheritosfrontendapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires annotations;
    requires okio;
    requires lombok;
    requires com.google.gson;
    requires java.dotenv;


    //opens  facheritosfrontendapp to javafx.fxml;
    //exports  facheritosfrontendapp;

    exports  facheritosfrontendapp.views;
    opens  facheritosfrontendapp.views to javafx.fxml;
    exports  facheritosfrontendapp.controller;
    opens  facheritosfrontendapp.controller to javafx.fxml;
    exports  facheritosfrontendapp.controller.navbar;
    opens  facheritosfrontendapp.controller.navbar to javafx.fxml;
    exports  facheritosfrontendapp.controller.users;
    opens  facheritosfrontendapp.controller.users to javafx.fxml;
    exports  facheritosfrontendapp.controller.headquarters;
    opens  facheritosfrontendapp.controller.headquarters to javafx.fxml;
    opens  facheritosfrontendapp.dto.loginDTO to com.google.gson;
    opens  facheritosfrontendapp.dto.personDTO to com.google.gson;
    opens  facheritosfrontendapp.dto.otherDTO to com.google.gson;
    //exports facheritosfrontendapp.controller.users;
    //opens facheritosfrontendapp.controller.users to javafx.fxml;

}