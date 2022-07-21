package facheritosfrontendapp.controller.user;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.validator.addUserValidator.AddUserValidator;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class UserSingleViewController implements Initializable {

    private WorkerEndpoint workerEndpoint;
    private HeadquarterEndpoint headquarterEndpoint;

    private AddUserValidator inputValidator;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private DashboardController dashboardController;

    private UserController userController;

    private Integer idPerson;

    //Here are all the @FXML components
    @FXML
    private Label editUserLabel;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label lastNameLabel;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Label roleLabel;

    @FXML
    private ComboBox<HeadquarterView> headquarterCombo;

    @FXML
    private Label headquarterLabel;

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

    @FXML
    private ComboBox statusCombo;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField salaryField;

    @FXML
    private Label salaryLabel;

    @FXML
    private TextField cityCombo;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button editUserButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    /**
     * editAction: event -> void
     * Purpose: By pressing the 'Editar usuario' button the text fields are enabled,
     * the title becomes visible and the 'Guardar' and 'Cancelar' buttons are enabled.
     */
    protected void editAction() {
        editUserLabel.setVisible(true);

        nameField.setDisable(false);
        lastNameField.setDisable(false);
        roleCombo.setDisable(false);
        headquarterCombo.setDisable(false);
        phoneField.setDisable(false);
        emailField.setDisable(false);
        birthdayPicker.setDisable(false);
        idField.setDisable(false);
        statusCombo.setDisable(false);
        salaryField.setDisable(false);
        cityCombo.setDisable(false);

        deleteUserButton.setVisible(false);
        editUserButton.setVisible(false);

        cancelButton.setVisible(true);
        saveButton.setVisible(true);
    }

    /**
     * getRolId: String -> Integer
     * Purpose: This method returns the id of the user's rol given its rol name
     */
    public Integer getRolId(String rol) {
        Integer rolId = 0;
        switch (rol) {
            case "Gerente":
                rolId = 1;
                break;
            case "Vendedor":
                rolId = 2;
                break;
            case "Jefe de taller":
                rolId = 3;
                break;
        }
        return rolId;
    }

    /**
     * getStatus: String -> Boolean
     * Purpose: Returns the boolean user's status given its status name
     */
    public Boolean getStatus(String status) {
        if (status == "Activo") {
            return true;
        }
        return false;
    }

    @FXML
    protected void cancelAction() throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            userController = (UserController) dashboardController.changeContent("users/users");
            //SHOW THE USERS IN TABLEVIEW
            userController.showWorkers();
            userController.showCustomers();
        } else {
            System.out.println("No");
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
                    workerEndpoint.updateWorker(idPerson, idField.getText(), nameField.getText(), lastNameField.getText(),
                            phoneField.getText(), birthdayPicker.getValue(), emailField.getText(), getRolId(roleCombo.getSelectionModel().getSelectedItem()),
                            headquarterCombo.getSelectionModel().getSelectedItem().getIdHeadquarter(), getStatus(statusCombo.getSelectionModel().getSelectedItem().toString()), Double.parseDouble(salaryField.getText()));
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Usuario actualizado exitosamente", OK);
                    success.show();

                    try {
                        userController = (UserController) dashboardController.changeContent("users/users");
                        //SHOW THE USERS IN TABLEVIEW
                        userController.showWorkers();
                        userController.showCustomers();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }catch (Exception e){

                }
            } else {
                System.out.println("No");
            }}
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the edit-user form
     */
    public Boolean allValidations() {
        cleanErrors();
        Boolean everythingCorrect = true;
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
        if (!inputValidator.salary(salaryField, salaryLabel, "Ingrese un salario válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(salaryField, salaryLabel);
        }
        if (birthdayPicker.getValue() == null || birthdayPicker.getValue().isAfter(LocalDate.now())) {
            everythingCorrect = false;
            birthdayLabel.setText("Indique una fecha válida");
            inputValidator.setErrorStyles(birthdayPicker, birthdayLabel);
        }

        if (roleCombo.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            roleLabel.setText("Seleccione un rol");
            inputValidator.setErrorStyles(roleCombo, roleLabel);
        }

        if (headquarterCombo.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            headquarterLabel.setText("Seleccione una sede");
            inputValidator.setErrorStyles(headquarterCombo, headquarterLabel);
        }

        if (statusCombo.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            statusLabel.setText("Seleccione un estado");
            inputValidator.setErrorStyles(statusCombo, statusLabel);
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
        salaryLabel.setText("");
        salaryField.setStyle("");
        cellphoneLabel.setText("");
        phoneField.setStyle("");
        birthdayLabel.setText("");
        birthdayPicker.setStyle("");
        roleLabel.setText("");
        roleCombo.setStyle("");
        headquarterLabel.setText("");
        headquarterCombo.setStyle("");
        statusLabel.setText("");
        statusCombo.setStyle("");
    }

    private void setIdPerson(Integer idPerson) {
        this.idPerson = idPerson;
    }

    public UserSingleViewController() {
        workerEndpoint = new WorkerEndpoint();
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
        inputValidator = new AddUserValidator();
    }

    /**
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the form possible
     */
    public void showForm(Integer idPerson) {
        setIdPerson(idPerson);
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> userCall = CompletableFuture.supplyAsync(() -> workerEndpoint.getWorkerById(idPerson));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setHeadquarterCombo();
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    public void setHeadquarterCombo() throws ExecutionException, InterruptedException {
        CompletableFuture<Map<Boolean, ResultSet>> headquarterCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

        headquarterCall.thenApply((response) -> {
            if(response.containsKey(true)){
                ResultSet resultSet = response.get(true);
                try {
                    fillHeadquarterCombo(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }).get();
    }

    public void fillHeadquarterCombo(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarterCombo.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    public void setForm(ResultSet resultSet) throws SQLException {
        nameField.setText(resultSet.getString("first_name"));
        lastNameField.setText(resultSet.getString("last_name"));
        roleCombo.getSelectionModel().select(resultSet.getInt("id_type_person") - 1);
        headquarterCombo.getSelectionModel().select(findHeadquarterById(resultSet.getInt("id_headquarter")));
        phoneField.setText(resultSet.getString("cellphone"));
        emailField.setText(resultSet.getString("email"));
        birthdayPicker.setValue(resultSet.getDate("birthday").toLocalDate());
        idField.setText(resultSet.getString("cc"));
        statusCombo.getSelectionModel().select(resultSet.getBoolean("state") ? 0 : 1); //Ternary if to convert boolean into integer.
        salaryField.setText(String.valueOf(resultSet.getDouble("salary")));
        cityCombo.setText((resultSet.getString("city_name")));
    }

    /**
     * findHeadquarterById: Integer -> HeadquarterView
     * Purpose: This method finds a headquarter by its id.
     * It is used to set the headquarter combobox given the id of the headquarter where the worker is settled.
     */
    public HeadquarterView findHeadquarterById(Integer id){
        for(Integer i = 0; i < headquarterComboboxList.size(); i++){
            if(id == headquarterComboboxList.get(i).getIdHeadquarter()){
                return headquarterComboboxList.get(i);
            }
        }
        return new HeadquarterView(-100,"");
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
        statusCombo.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        roleCombo.setItems(FXCollections.observableArrayList("Gerente", "Vendedor", "Jefe de taller"));
    }
}