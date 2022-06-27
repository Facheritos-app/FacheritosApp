package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.controller.order.OrderController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MechanicNavbar implements Initializable {
    private DashboardController dashboardController;
    private OrderController orderController;

    private InventoryController inventoryController;

    @FXML
    public void homeClicked() throws IOException {
        dashboardController.changeContent("home");
    }

    @FXML
    public void ordersClicked() throws IOException {
        orderController = (OrderController) dashboardController.changeContent("orders/orders");
        orderController.showOrders();
    }

    @FXML
    protected void inventoryClicked() throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        inventoryController.showInventory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
