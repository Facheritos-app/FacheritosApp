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
    requires java.desktop;
    requires jasperreports;


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
    exports  facheritosfrontendapp.controller.headquarter;
    opens  facheritosfrontendapp.controller.headquarter to javafx.fxml;
    exports  facheritosfrontendapp.controller.inventory;
    opens  facheritosfrontendapp.controller.inventory to javafx.fxml;
    opens facheritosfrontendapp.objectRowView.headquarterRowView to javafx.base;
    opens facheritosfrontendapp.controller.customer to javafx.fxml;
    opens facheritosfrontendapp.objectRowView.customerRowView to javafx.base;
    exports facheritosfrontendapp.controller.sale to javafx.fxml;
    opens facheritosfrontendapp.controller.sale to javafx.fxml;
    opens facheritosfrontendapp.objectRowView.saleRowView to javafx.base;
    exports facheritosfrontendapp.controller.customer;
    opens facheritosfrontendapp.objectRowView.inventoryRowView to javafx.base;
    exports facheritosfrontendapp.controller.order;
    opens facheritosfrontendapp.controller.order to javafx.fxml;
    opens facheritosfrontendapp.objectRowView.orderRowView to javafx.base;
    exports facheritosfrontendapp.controller.quotation to javafx.fxml;
    opens  facheritosfrontendapp.controller.quotation to javafx.fxml;
    exports facheritosfrontendapp.controller.report to javafx.fxml;
    opens facheritosfrontendapp.controller.report to javafx.fxml;
    opens facheritosfrontendapp.objectRowView.quotationRowView to javafx.base;
    exports facheritosfrontendapp.controller.profile;
    opens facheritosfrontendapp.controller.profile to javafx.fxml;
    exports facheritosfrontendapp.controller.dashboard;
    opens facheritosfrontendapp.controller.dashboard to javafx.fxml;

}