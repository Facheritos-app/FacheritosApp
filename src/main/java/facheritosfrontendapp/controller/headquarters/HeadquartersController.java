package facheritosfrontendapp.controller.headquarters;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HeadquartersController implements Initializable {

    @FXML
    private Button addHeadquarter;

    private DashboardController dashboardController;

    /*Add headquarter*/
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,"?", ButtonType.YES, ButtonType.NO);


    @FXML
    protected void addHeadquarterClicked() throws IOException {
        dashboardController.changeContent("headquarters/headquartersAdd");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }


    /*Add headquarters window functions*/
    @FXML
    protected void cancelButtonClicked() throws IOException {
        confirmation.setContentText("¿Está seguro que desea cancelar?");
        confirmation.setTitle("Confirme su respuesta");
        confirmation.setHeaderText("Cancelar");
        confirmation.showAndWait();
        if(confirmation.getResult() == ButtonType.YES){
            dashboardController.changeContent("headquarters/headquarters");
        } else {
            System.out.println("No");
        }

    }

    @FXML
    protected void saveButtonClicked(){
        confirmation.setContentText("¿Está seguro que desea guardar?");
        confirmation.setTitle("Confirme su respuesta");
        confirmation.setHeaderText("Guardar");
        confirmation.showAndWait();
        if(confirmation.getResult() == ButtonType.YES){
            System.out.println("Sí");
        } else {
            System.out.println("No");
        }
    }
}
