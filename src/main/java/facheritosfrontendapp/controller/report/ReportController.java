package facheritosfrontendapp.controller.report;


import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class ReportController {

    JasperConnection jasperConnection;

    public ReportController(){
        jasperConnection = new JasperConnection("FacheritosReports/sellersReport.jrxml");
    }

    @FXML
    public void sellersClicked(MouseEvent mouseEvent) {
        System.out.println("");
        jasperConnection.showReport();
    }
}
