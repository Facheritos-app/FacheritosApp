package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.customer.CustomerController;
import facheritosfrontendapp.controller.dashboard.SellerDashboardController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.controller.quotation.QuotationController;
import facheritosfrontendapp.controller.sale.SaleController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class SellerNavbar implements Initializable {

    private DashboardController dashboardController;
    private SaleController saleController;

    private CustomerController customerController;

    private QuotationController quotationController;

    private SellerDashboardController sellerDashboardController;

    private InventoryController inventoryController;

    @FXML
    public void homeClicked() throws IOException {
        sellerDashboardController = (SellerDashboardController) dashboardController.changeContent("dashboard/sellerDashboard", true);
        sellerDashboardController.showDashboard();
    }

    @FXML
    public void customersClicked() throws IOException {
        customerController = (CustomerController) dashboardController.changeContent("customers/customers");
        customerController.showCustomers();
    }

    @FXML
    public void salesClicked() throws IOException, ExecutionException, InterruptedException {
        saleController = (SaleController) dashboardController.changeContent("sales/sales");
        saleController.showSales();
    }

    @FXML
    public void quotations() throws IOException {
        quotationController = (QuotationController) dashboardController.changeContent("quotations/quotations");
        quotationController.showQuotations();
    }

    @FXML
    public void inventoryClicked() throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        inventoryController.showInventory();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
