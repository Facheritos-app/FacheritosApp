package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagerNavbar implements Initializable {
    @FXML
    private HBox users;

    private DashboardController dashboardController;

    @FXML
    protected void usersClicked() throws IOException {
        dashboardController.changeContent("users/users");
    }

    @FXML
    protected void headquartersClicked() throws IOException {
        dashboardController.changeContent("headquarters/headquarters");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
