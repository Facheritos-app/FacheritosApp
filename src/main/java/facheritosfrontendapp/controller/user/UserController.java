package facheritosfrontendapp.controller.user;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private TabPane usersTabpane;

    private DashboardController dashboardController;

    private AddUserController addUserController;

    @FXML
    protected void addUserClicked() throws IOException {
        addUserController = (AddUserController) dashboardController.changeContent("users/usersAdd", true);
        addUserController.setView();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        usersTabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

}
