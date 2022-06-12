package facheritosfrontendapp.controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class UserSingleViewController implements Initializable {

    @FXML
    private Label editUserLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private ComboBox roleCombo;

    @FXML
    private ComboBox seatCombo;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField mailField;

    @FXML
    private DatePicker bornPicker;

    @FXML
    private TextField idField;

    @FXML
    private ComboBox statusCombo;

    @FXML
    private TextField salaryField;

    @FXML
    private ComboBox cityCombo;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button editUserButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    /*
    editAction: event -> void
    Purpose: By pressing the 'Editar usuario' button the text fields are enabled,
    the title becomes visible and the 'Guardar' and 'Cancelar' buttons are enabled.
    * */
    protected void editAction(ActionEvent event) {
        editUserLabel.setVisible(true);
        
        nameField.setDisable(false);
        lastNameField.setDisable(false);
        roleCombo.setDisable(false);
        seatCombo.setDisable(false);
        phoneField.setDisable(false);
        mailField.setDisable(false);
        bornPicker.setDisable(false);
        idField.setDisable(false);
        statusCombo.setDisable(false);
        salaryField.setDisable(false);
        cityCombo.setDisable(false);

        deleteUserButton.setVisible(false);
        editUserButton.setVisible(false);

        cancelButton.setVisible(true);
        saveButton.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
