package facheritosfrontendapp.controller.customer;


import backend.dto.personDTO.PersonDTO;
import backend.endpoints.personEndpoint.PersonEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.validator.addUserValidator.AddUserValidator;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;


public class AddCustomerController implements Initializable {

    private DashboardController dashboardController;
    private AddUserValidator inputValidator;
    private PersonEndpoint personEndpoint;

    @FXML
    private TextField firstnameTextField;
    @FXML
    private Label firstnameLabel;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private Label lastnameLabel;
    @FXML
    private TextField celTextField;
    @FXML
    private Label celLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label emailLabel;
    @FXML
    private DatePicker birthdateDatePicker;
    @FXML
    private Label birthdateLabel;
    @FXML
    private TextField ccTextField;
    @FXML
    private Label ccLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }


    public AddCustomerController() {
        inputValidator = new AddUserValidator();
        personEndpoint = new PersonEndpoint();
    }


    @FXML
    public void saveButtonAddCustomerClicked(MouseEvent mouseEvent) {
        if (allValidations()) {
            PersonDTO customer = populateCustomerObject();
            try {
                MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                if (clickedButton.get() == ButtonType.YES) {
                    new Thread(() -> {
                        //DB call to save customer
                        Boolean result = createCustomer(customer);
                        Platform.runLater(() -> {
                            setFeedbackWindow(result);
                        });
                    }).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void cancelButtonAddCustomerClicked(MouseEvent mouseEvent) {
    }

    /**
     * createCustomer: PersonDTO -> Boolean
     * Purpose: This method calls the DB to create a customer
     */
    public Boolean createCustomer(PersonDTO customer) {
        try {
            return CompletableFuture.supplyAsync(() -> personEndpoint.createCustomer(customer)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }


    public void setFeedbackWindow(Boolean result) {
        if (result) {
            Platform.runLater(() -> {
                Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Cliente agregado exitosamente", OK);
                success.show();
                //Go to main user view
            });
        } else {
            Alert fail = new Alert(Alert.AlertType.ERROR, "Ha habido un problema, por favor intenta nuevamente", OK);
            fail.show();
        }
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the create-customer form
     */
    public Boolean allValidations() {
        cleanErrors();
        Boolean everythingCorrect = true;
        if (!inputValidator.name(firstnameTextField, firstnameLabel, "Escriba un nombre válido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(firstnameTextField, firstnameLabel);
        }
        if (!inputValidator.name(lastnameTextField, lastnameLabel, "Escriba un nombre valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(lastnameTextField, lastnameLabel);
        }
        if (!inputValidator.cellphone(celTextField, celLabel, "Escriba un numero valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(celTextField, celLabel);
        }
        if (!inputValidator.email(emailTextField, emailLabel, "Escriba un correo valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(emailTextField, emailLabel);
        }
        if (birthdateDatePicker.getValue() == null) {
            everythingCorrect = false;
            birthdateLabel.setText("Por favor indique una fecha");
            inputValidator.setErrorStyles(birthdateDatePicker, birthdateLabel);
        }
        if (!inputValidator.cc(ccTextField, ccLabel, "Ingrese una cedula valida")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(ccTextField, ccLabel);
        }
        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        firstnameTextField.setStyle("");
        firstnameLabel.setText("");
        lastnameTextField.setStyle("");
        lastnameLabel.setText("");
        celTextField.setStyle("");
        celLabel.setText("");
        emailTextField.setStyle("");
        emailLabel.setText("");
        birthdateDatePicker.setStyle("");
        birthdateLabel.setText("");
        ccTextField.setStyle("");
        ccLabel.setText("");
    }

    /**
     * populateCustomerObject: void -> PersonDTO
     * Purpose: This method creates a customer object from all the form's input
     */
    public PersonDTO populateCustomerObject() {
        PersonDTO customer = new PersonDTO();
        customer.setFirst_name(firstnameTextField.getText());
        customer.setLast_name(lastnameTextField.getText());
        customer.setCellphone(celTextField.getText());
        customer.setEmail(emailTextField.getText());
        customer.setBirthday(birthdateDatePicker.getValue());
        customer.setCc(ccTextField.getText());
        customer.setId_type_person(4);
        return customer;
    }

}
