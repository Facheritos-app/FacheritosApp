package facheritosfrontendapp.controller.users;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    @FXML
    private TabPane usersTabpane;

    private DashboardController dashboardController;

    @FXML
    protected void addUserClicked() throws IOException {
        dashboardController.changeContent("users/usersAdd");

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        usersTabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

}
