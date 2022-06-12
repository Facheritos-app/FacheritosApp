package facheritosfrontendapp.controller.headquarter;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class AddHeadquarterController implements Initializable {


    private DashboardController dashboardController;

    private HeadquarterController headquarterController;

    @FXML
    private Button addHeadquarter;


    /*Add headquarter*/



    /*Add headquarters window functions*/
    @FXML
    protected void cancelButtonClicked() throws IOException, ExecutionException, InterruptedException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if(clickedButton.get() == ButtonType.YES){
            headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
            headquarterController.showHeadquarters();
        } else {
            System.out.println("No");
        }

    }

    @FXML
    protected void saveButtonClicked() throws IOException, ExecutionException, InterruptedException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if(clickedButton.get() == ButtonType.YES){
            //REALIZAR LOS CAMBIOS
            headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
            headquarterController.showHeadquarters();
        } else {
            System.out.println("No");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }
}
