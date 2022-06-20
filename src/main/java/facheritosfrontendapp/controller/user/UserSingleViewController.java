package facheritosfrontendapp.controller.user;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.YES;

public class UserSingleViewController implements Initializable {

    private WorkerEndpoint workerEndpoint;
    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private DashboardController dashboardController;

    private UserController userController;

    //Here are all the @FXML components
    @FXML
    private Label editUserLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private ComboBox roleCombo;

    @FXML
    private ComboBox headquarterCombo;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker birthdayPicker;

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

    public UserSingleViewController() {
        workerEndpoint = new WorkerEndpoint();
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
    }

    /**
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the form possible
     */
    public void showForm(Integer idPerson) {
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
        headquarterCombo.getSelectionModel().select(findHeadquarterById(resultSet.getInt("id_headquarter")).toString());
        phoneField.setText(resultSet.getString("cellphone"));
        emailField.setText(resultSet.getString("email"));
        birthdayPicker.setValue(resultSet.getDate("birthday").toLocalDate());
        idField.setText(resultSet.getString("cc"));
        statusCombo.getSelectionModel().select(resultSet.getBoolean("state") ? 0 : 1); //Ternary if to convert boolean into integer.
        salaryField.setText(String.valueOf(resultSet.getDouble("salary")));
        cityCombo.getSelectionModel().select(resultSet.getString("city_name"));
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