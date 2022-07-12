package facheritosfrontendapp.controller.report;


import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class ReportController {

    JasperConnection jasperConnection;

    public ReportController(){
        jasperConnection = new JasperConnection("FacheritosReports/sales/sellersReport.jrxml");
    }

    @FXML
    public void sellersClicked(MouseEvent mouseEvent) {
        jasperConnection.showReport();
    }
}
