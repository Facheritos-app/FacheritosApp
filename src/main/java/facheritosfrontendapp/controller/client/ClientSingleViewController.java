package facheritosfrontendapp.controller.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientSingleViewController implements Initializable {

    @FXML
    private Label nameLabel;

    @FXML
    private Label ccLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label birthdayLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    private TableView purchasesTable;

    public void initialize(URL url, ResourceBundle rb) {

    }
}
