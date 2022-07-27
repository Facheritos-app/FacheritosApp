package facheritosfrontendapp.controller.report;


import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class ReportController {

    JasperConnection jasperConnection;

    @FXML
    public void sellersClicked(MouseEvent mouseEvent) {
        jasperConnection = new JasperConnection("FacheritosReports/sales/sellersReport.jrxml");
        jasperConnection.showReport();
    }
    @FXML
    public void salesClicked(MouseEvent mouseEvent) {
        jasperConnection = new JasperConnection("FacheritosReports/sales/salesMonth.jrxml");
        jasperConnection.addParameters("sales_year", (Integer) 2021);
        jasperConnection.showReport();
    }

    @FXML
    public void headquarterClicked(MouseEvent mouseEvent) {
        jasperConnection = new JasperConnection("FacheritosReports/headquarter/headquarterReport.jrxml");
        jasperConnection.showReport();
    }

    @FXML
    public void customersClicked(MouseEvent mouseEvent) {
        jasperConnection = new JasperConnection("FacheritosReports/customer/customerReport.jrxml");
        jasperConnection.showReport();
    }
    @FXML
    public void jobOrdersClicked(MouseEvent mouseEvent) {
        jasperConnection = new JasperConnection("FacheritosReports/jobOrders/jobOrdersReport.jrxml");
        jasperConnection.showReport();
    }

    @FXML
    public void carsClicked(MouseEvent mouseEvent) {
        jasperConnection = new JasperConnection("FacheritosReports/inventory/carReport.jrxml");
        jasperConnection.showReport();
    }
}
