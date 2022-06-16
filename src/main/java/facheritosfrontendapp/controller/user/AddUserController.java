package facheritosfrontendapp.controller.user;

import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.headquarter.HeadquarterController;
import facheritosfrontendapp.validator.addUserValidator.AddUserValidator;
import facheritosfrontendapp.views.FxmlLoader;
import javafx.application.Platform;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.*;

public class AddUserController implements Initializable {

    private DashboardController dashboardController;

    private UserController userController;

    private HeadquarterEndpoint headquarterEndpoint;

    private WorkerEndpoint workerEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private AddUserValidator inputValidator;

    private FxmlLoader fxmlLoader;

    //Here are all the @FXML components
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
    private TextField salaryTextField;
    @FXML
    private Label salaryLabel;
    @FXML
    private ComboBox<String> typeCombobox;
    @FXML
    private Label typeLabel;
    @FXML
    private ComboBox<HeadquarterView> headquarterCombobox;
    @FXML
    private Label headquarterLabel;
    @FXML
    private DatePicker birthdateDatePicker;
    @FXML
    private Label birthdateLabel;
    @FXML
    private TextField ccTextField;
    @FXML
    private Label ccLabel;


    public AddUserController() {
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
        inputValidator = new AddUserValidator();
        workerEndpoint = new WorkerEndpoint();
        fxmlLoader = new FxmlLoader();
    }

    @FXML
    public void cancelButtonAddUserClicked(MouseEvent mouseEvent) throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            userController = (UserController) dashboardController.changeContent("users/users");
            //SHOW THE USERS IN TABLEVIEW
            userController.showWorkers();
        } else {
            System.out.println("No");
        }
    }

    /**
     * saveButtonAddUserClicked: MouseEvent -> void
     * Purpose: This method contains the logic and the calls to the DB in order to create a worker
     */
    @FXML
    public void saveButtonAddUserClicked(MouseEvent mouseEvent) throws ExecutionException, InterruptedException {

        if (allValidations()) {
            new Thread(() -> {
                WorkerDTO worker = createWorkerObject();
                Platform.runLater(() -> {
                    try {
                        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                        if (clickedButton.get() == YES) {
                            //DB call to save worker
                            new Thread(() -> {
                                Boolean result = null;
                                try {
                                    result = CompletableFuture.supplyAsync(() -> workerEndpoint.createWorker(worker)).get();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                                if (result) {
                                    Platform.runLater(() -> {
                                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Usuario agregado exitosamente", OK);
                                        success.show();
                                        //Go to main user view
                                        try {
                                            userController = (UserController) dashboardController.changeContent("users/users");
                                            //Show users in table
                                            userController.showWorkers();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                                } else {
                                    Alert fail = new Alert(Alert.AlertType.ERROR, "Ha habido un problema, por favor intenta nuevamente", OK);
                                    fail.show();
                                }
                            }).start();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        }
    }

    /**
     * setView: Void -> Void
     * Purpose: This method contains all the other methods that help set the view
     */
    public void setView() {
        //Set items for Rol combobox
        typeCombobox.setItems(FXCollections.observableArrayList("Gerente", "Vendedor", "Jefe de taller"));
        try {
            showHeadquarters();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * createWorkerObject: void -> WorkerDTO
     * Purpose: This method populates a WorkerDTO object from all the information on the create-worker form
     *
     * @return
     */
    public WorkerDTO createWorkerObject() {
        WorkerDTO worker = new WorkerDTO();
        worker.setFirst_name(firstnameTextField.getText());
        worker.setLast_name(lastnameTextField.getText());
        worker.setCellphone(celTextField.getText());
        worker.setEmail(emailTextField.getText());
        worker.setSalary(Double.parseDouble(salaryTextField.getText()));
        worker.setId_type_person(getRolId(typeCombobox.getSelectionModel().getSelectedItem()));
        worker.setRol(typeCombobox.getSelectionModel().getSelectedItem());
        worker.setId_headquarter(headquarterCombobox.getSelectionModel().getSelectedItem().getIdHeadquarter());
        worker.setBirthday(birthdateDatePicker.getValue());
        worker.setCc(ccTextField.getText());
        worker.setState(true);
        return worker;
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
     * showHeaders: void -> void
     * Purpose: This method contains all the steps to show all the headquarters in the combobox
     */
    public void showHeadquarters() throws ExecutionException, InterruptedException {

        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

            //Use the response from the BD to fill the combobox
            try {
                headquartersCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setHeadquarterCombobox(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
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

    /**
     * setHeadquarterCombobox: ResultSet -> void
     * Purpose: This method set the items of the headquarters combobox according to the DB
     */
    public void setHeadquarterCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarterCombobox.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the create-user form
     */
    public Boolean allValidations() {
        Boolean everythingCorrect = true;
        if (!inputValidator.name(firstnameTextField, firstnameLabel, "Escriba un nombre valido, por favor")) {
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
        if (!inputValidator.salary(salaryTextField, salaryLabel, "Escriba un salario valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(salaryTextField, salaryLabel);
        }
        if (typeCombobox.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            typeLabel.setText("Por favor indique el rol del usuario");
            inputValidator.setErrorStyles(typeCombobox, typeLabel);
        }
        if (headquarterCombobox.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            headquarterLabel.setText("Por favor indique una sede");
            inputValidator.setErrorStyles(headquarterCombobox, headquarterLabel);
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
}
