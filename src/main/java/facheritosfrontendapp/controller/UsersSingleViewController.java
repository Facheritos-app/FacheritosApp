package facheritosfrontendapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersSingleViewController implements Initializable {

    @FXML
    private Label editUserLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField seatField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField mailField;

    @FXML
    private TextField bornField;

    @FXML
    private TextField idField;

    @FXML
    private TextField statusField;

    @FXML
    private TextField salaryField;

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
    editButton: event -> void
    Purpose: By pressing the 'Editar usuario' button the text fields are enabled,
    the title becomes visible and the 'Guardar' and 'Cancelar' buttons are enabled.
    * */
    protected void editAction(ActionEvent event) {
        editUserLabel.setVisible(true);
        
        nameField.setDisable(false);
        lastNameField.setDisable(false);
        roleField.setDisable(false);
        seatField.setDisable(false);
        phoneField.setDisable(false);
        mailField.setDisable(false);
        bornField.setDisable(false);
        idField.setDisable(false);
        statusField.setDisable(false);
        salaryField.setDisable(false);

        deleteUserButton.setVisible(false);
        editUserButton.setVisible(false);

        cancelButton.setVisible(true);
        saveButton.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
