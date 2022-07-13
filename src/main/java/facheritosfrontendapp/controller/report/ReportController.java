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
}
