package facheritosfrontendapp.controller.inventory;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    @FXML
    private TabPane inventoryTabpane;

    private DashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        //Tabs cannot be closed
        inventoryTabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }
}
