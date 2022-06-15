module  facheritosfrontendapp {
    requires java.naming;
    requires javafx.controls;
    requires javafx.fxml;
    requires annotations;
    requires lombok;
    requires java.dotenv;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires AnimateFX;


    //opens  facheritosfrontendapp to javafx.fxml;
    //exports  facheritosfrontendapp;

    exports  facheritosfrontendapp.views;
    opens  facheritosfrontendapp.views to javafx.fxml;
    exports  facheritosfrontendapp.controller;
    opens  facheritosfrontendapp.controller to javafx.fxml;
    exports  facheritosfrontendapp.controller.navbar;
    opens  facheritosfrontendapp.controller.navbar to javafx.fxml;
    exports  facheritosfrontendapp.controller.user;
    opens  facheritosfrontendapp.controller.user to javafx.fxml;
    exports  facheritosfrontendapp.controller.client;
    opens  facheritosfrontendapp.controller.client to javafx.fxml;
    exports  facheritosfrontendapp.controller.headquarter;
    opens  facheritosfrontendapp.controller.headquarter to javafx.fxml;
    exports  facheritosfrontendapp.controller.inventory;
    opens  facheritosfrontendapp.controller.inventory to javafx.fxml;
    opens facheritosfrontendapp.objectRowView.headquarterRowView to javafx.base;

}