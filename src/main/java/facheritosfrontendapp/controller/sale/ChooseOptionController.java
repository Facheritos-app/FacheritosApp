package facheritosfrontendapp.controller.sale;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.quotation.QuotationController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ChooseOptionController implements Initializable {



    private DashboardController dashboardController;

    private SaleController saleController;

    private QuotationController quotationController;

    public ChooseOptionController(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    @FXML
    protected void salesClicked() throws IOException, ExecutionException, InterruptedException {
        saleController = (SaleController) dashboardController.changeContent("sales/sales");
        saleController.showSales();
    }

    @FXML
    protected void quotationsClicked() throws IOException {
        quotationController = (QuotationController) dashboardController.changeContent("quotations/quotations");
        quotationController.showQuotations();
    }

}
