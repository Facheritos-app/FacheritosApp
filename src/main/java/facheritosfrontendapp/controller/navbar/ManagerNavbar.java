package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.headquarters.HeadquarterController;
import javafx.fxml.FXML;
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

    private HeadquarterController headquarterController;


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
        System.out.println("Thread en headquartersClicked: "+ Thread.currentThread().getName());
        headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
        headquarterController.showHeadquarters();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
