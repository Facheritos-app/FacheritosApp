package facheritosfrontendapp.controller.headquarters;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HeadquartersController implements Initializable {

    @FXML
    private Button addHeadquarter;

    private DashboardController dashboardController;

    @FXML
    protected void addHeadquarterClicked() throws IOException {
        dashboardController.navbarClicked("headquarters/headquartersAdd");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

}
