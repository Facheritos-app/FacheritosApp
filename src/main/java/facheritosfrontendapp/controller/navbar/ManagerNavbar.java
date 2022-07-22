package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.dashboard.ManagerDashboardController;
import facheritosfrontendapp.controller.headquarter.HeadquarterController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.controller.order.OrderController;
import facheritosfrontendapp.controller.sale.ChooseOptionController;
import facheritosfrontendapp.controller.sale.SaleController;
import facheritosfrontendapp.controller.user.UserController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
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

    private UserController userController;

    private ChooseOptionController chooseOptionController;

    private OrderController orderController;

    private ManagerDashboardController managerDashboardController;

    @FXML
    protected void homeClicked() throws IOException {
        managerDashboardController = (ManagerDashboardController) dashboardController.changeContent("dashboard/managerDashboard");
        managerDashboardController.showDashboard();
    }

    @FXML
    public void reportsClicked(MouseEvent mouseEvent) throws IOException {
        dashboardController.changeContent("reports/chooseReportModule");
    }
    @FXML
    protected void usersClicked() throws IOException {
        userController = (UserController) dashboardController.changeContent("users/users");
        userController.showWorkers();
        userController.showCustomers();
    }

    @FXML
    protected void headquartersClicked() throws IOException, ExecutionException, InterruptedException {
        headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
        headquarterController.showHeadquarters();
    }

    @FXML
    protected void inventoryClicked() throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        inventoryController.showInventory();
    }

    @FXML
    protected void salesClicked() throws IOException, ExecutionException, InterruptedException {
        chooseOptionController = (ChooseOptionController) dashboardController.changeContent("sales/chooseOption");
    }

    @FXML
    protected void ordersClicked() throws IOException {
        orderController = (OrderController) dashboardController.changeContent("orders/orders");
        orderController.showOrders();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
