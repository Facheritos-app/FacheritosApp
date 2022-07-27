package facheritosfrontendapp.controller.customer;
import backend.endpoints.customerEndpoint.CustomerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.user.UserController;
import facheritosfrontendapp.validator.addUserValidator.AddUserValidator;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class EditCustomerController implements Initializable {

    private final CustomerEndpoint customerEndpoint;

    private final AddUserValidator inputValidator;

    private DashboardController dashboardController;

    private UserController userController;

    private CustomerController customerController;

    private Integer idPerson;

    private String backTo;

    //Here are all the @FXML components
    @FXML
    private TextField nameField;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label lastNameLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private Label cellphoneLabel;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailLabel;

    @FXML
    private DatePicker birthdayPicker;

    @FXML
    private Label birthdayLabel;

    @FXML
    private TextField idField;

    @FXML
    private Label idLabel;


    public EditCustomerController() {
        customerEndpoint = new CustomerEndpoint();
        inputValidator = new AddUserValidator();
        backTo = "";
    }

    public void setBackTo(String view) {
        backTo = view;
    }

    /**
     * backToCustomersClicked: void -> void
     * Purpose: returns to the customers view
     */
    @FXML
    protected void backToCustomers() throws IOException {
        customerController = (CustomerController) dashboardController.changeContent("customers/customers");
        customerController.showCustomers();
    }

    /**
     * backToUsers: void -> void
     * Purpose: returns to the users view
     */
    @FXML
    protected void backToUsers() throws IOException {
        userController = (UserController) dashboardController.changeContent("users/users");
        userController.showWorkers();
        userController.showCustomers();
    }

    /**
     * whereToGoBack: String -> void
     * Purpose: decides which view should be displayed to return to an earlier point
     */
    public void whereToGoBack(String view) throws IOException {
        if (Objects.equals(view, "customers")){
            backToCustomers();
        }
        if (Objects.equals(view, "users")){
            backToUsers();
        }
    }

    @FXML
    protected void cancelAction() throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            whereToGoBack(backTo);
        }
    }


    @FXML
    protected void saveAction() throws IOException, NullPointerException {
        /*Show dialogPane to confirm*/
        if (allValidations()){
            MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
            Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
            if (clickedButton.get() == YES) {
                try{
                    customerEndpoint.updateCustomer(idPerson, idField.getText(), nameField.getText(), lastNameField.getText(),
                            phoneField.getText(), birthdayPicker.getValue(), emailField.getText());
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Cliente actualizado exitosamente", OK);
                    success.show();
                    try {
                        whereToGoBack(backTo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the edit-user form
     */
    public Boolean allValidations() {
        cleanErrors();
        boolean everythingCorrect = true;
        if (!inputValidator.name(nameField, nameLabel, "Ingrese un nombre válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(nameField, nameLabel);
        }
        if (!inputValidator.name(lastNameField, lastNameLabel, "Ingrese un apellido válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(lastNameField, lastNameLabel);
        }
        if (!inputValidator.cellphone(phoneField, cellphoneLabel, "Ingrese un celular válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(phoneField, cellphoneLabel);
        }
        if (!inputValidator.email(emailField, emailLabel, "Ingrese un correo válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(emailField, emailLabel);
        }
        if (birthdayPicker.getValue() == null || birthdayPicker.getValue().isAfter(LocalDate.now())) {
            everythingCorrect = false;
            birthdayLabel.setText("Indique una fecha válida");
            inputValidator.setErrorStyles(birthdayPicker, birthdayLabel);
        }
        if (!inputValidator.cc(idField, idLabel, "Ingrese una cédula válida")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(idField, idLabel);
        }

        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        nameLabel.setText("");
        nameField.setStyle("");
        lastNameLabel.setText("");
        lastNameField.setStyle("");
        idLabel.setText("");
        idField.setStyle("");
        emailLabel.setText("");
        emailField.setStyle("");
        cellphoneLabel.setText("");
        phoneField.setStyle("");
        birthdayLabel.setText("");
        birthdayPicker.setStyle("");
    }

    private void setIdPerson(Integer idPerson) {
        this.idPerson = idPerson;
    }

    /**
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the customer form possible
     */
    public void showCustomer(Integer idPerson) {
        setIdPerson(idPerson);
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> userCall = CompletableFuture.supplyAsync(() -> customerEndpoint.getCustomerById(idPerson));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setForm(resultSet);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    public void setForm(ResultSet resultSet) throws SQLException {
        nameField.setText(resultSet.getString("first_name"));
        lastNameField.setText(resultSet.getString("last_name"));
        phoneField.setText(resultSet.getString("cellphone"));
        emailField.setText(resultSet.getString("email"));
        birthdayPicker.setValue(resultSet.getDate("birthday").toLocalDate());
        idField.setText(resultSet.getString("cc"));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
    }
}
