package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.headquarters.HeadquartersController;
import facheritosfrontendapp.views.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ManagerNavbar implements Initializable {
    @FXML
    private HBox users;

    private DashboardController dashboardController;


    @FXML
    protected void homeClicked() throws IOException {
        dashboardController.changeContent("home");
    }
    @FXML
    protected void usersClicked() throws IOException {
        dashboardController.changeContent("users/users");
    }

    @FXML
    protected void headquartersClicked() throws IOException, ExecutionException, InterruptedException {
        dashboardController.changeContent("headquarters/headquarters");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
