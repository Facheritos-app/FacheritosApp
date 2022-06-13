package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.headquarter.HeadquarterController;
import facheritosfrontendapp.controller.inventory.InventoryController;
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
    private InventoryController inventoryController;

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
        headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
        headquarterController.showHeadquarters();
    }

    @FXML
    protected void inventoryClicked() throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        //MOSTRAR ELEMENTOS DEL INVENTARIO
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
