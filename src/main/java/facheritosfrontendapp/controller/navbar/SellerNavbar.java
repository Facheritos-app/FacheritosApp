package facheritosfrontendapp.controller.navbar;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
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

    @FXML
    public void customersClicked() throws IOException {
        dashboardController.changeContent("customers/customersAdd");
    }

    @FXML
    public void salesClicked() throws IOException, ExecutionException, InterruptedException {
        saleController = (SaleController) dashboardController.changeContent("sales/sales");
        //saleController = (SaleController) dashboardController.changeContent("sales/sales");
        //saleController.showSales();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(DashboardController.getCurrentWorker().getId_worker());
        dashboardController = MainController.getDashboardController();
    }
}
