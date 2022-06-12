package facheritosfrontendapp.controller.inventory;

import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.user.UserController;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    @FXML
    private TabPane inventoryTabpane;

    private DashboardController dashboardController;

    @FXML
    protected void addClicked() throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("inventory/confirmationPart");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if(clickedButton.get() == ButtonType.APPLY){
            // CHANGE VIEW
            System.out.println("Apply");
        } else {
            //CHANGE VIEW
            System.out.println("Cancel");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        //Tabs cannot be closed
        inventoryTabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }
}
