package facheritosfrontendapp.controller.user;

import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.validator.addUserValidator.AddUserValidator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddUserController implements Initializable {

    private HeadquarterEndpoint headquarterEndpoint;

    private WorkerEndpoint workerEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private AddUserValidator inputValidator;

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




    public AddUserController(){
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<HeadquarterView>();
        inputValidator = new AddUserValidator();
        workerEndpoint = new WorkerEndpoint();
    }
    @FXML
    public void cancelButtonAddUserClicked(MouseEvent mouseEvent) {
    }

    /**
     * saveButtonAddUserClicked: MouseEvent -> void
     * Purpose: This method contains the logic and the calls to the DB in order to create a worker
     */
    @FXML
    public void saveButtonAddUserClicked(MouseEvent mouseEvent) throws ExecutionException, InterruptedException {
        if(allValidations()){
            new Thread(() -> {
                WorkerDTO worker = populateWorkerObject();
                try {
                    Boolean createUser = CompletableFuture.supplyAsync(() -> workerEndpoint.createWorker(worker)).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    /**
     * setView: Void -> Void
     * Purpose: This method contains all the other methods that help set the view
     */
    public void setView(){
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
     * populateWorkerObject: void -> WorkerDTO
     * Purpose: This method populates a WorkerDTO object from all the information on the create-worker form
     * @return
     */
    public WorkerDTO populateWorkerObject(){
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

    public Integer getRolId(String rol){
        Integer rolId = 0;
        switch (rol){
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
                    if(response.containsKey(true)){
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
        while(resultSet.next()) {
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarterCombobox.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the create-user form
     */
    public Boolean allValidations(){
        Boolean everythingCorrect = true;
        if(!inputValidator.name(firstnameTextField, firstnameLabel, "Escriba un nombre valido, por favor")){
            everythingCorrect = false;
            inputValidator.setErrorStyles(firstnameTextField, firstnameLabel);
        }
        if(!inputValidator.name(lastnameTextField, lastnameLabel, "Escriba un nombre valido, por favor")){
            everythingCorrect = false;
            inputValidator.setErrorStyles(lastnameTextField, lastnameLabel);
        }
        if(!inputValidator.cellphone(celTextField, celLabel, "Escriba un numero valido, por favor")){
            everythingCorrect = false;
            inputValidator.setErrorStyles(celTextField, celLabel);
        }
        if(!inputValidator.email(emailTextField, emailLabel, "Escriba un correo valido, por favor")){
            everythingCorrect = false;
            inputValidator.setErrorStyles(emailTextField, emailLabel);
        }
        if(!inputValidator.salary(salaryTextField, salaryLabel, "Escriba un salario valido, por favor")){
            everythingCorrect = false;
            inputValidator.setErrorStyles(salaryTextField, salaryLabel);
        }
        if(typeCombobox.getSelectionModel().isEmpty()){
            everythingCorrect = false;
            typeLabel.setText("Por favor indique el rol del usuario");
            inputValidator.setErrorStyles(typeCombobox, typeLabel);
        }
        if(headquarterCombobox.getSelectionModel().isEmpty()){
            everythingCorrect = false;
            headquarterLabel.setText("Por favor indique una sede");
            inputValidator.setErrorStyles(headquarterCombobox, headquarterLabel);
        }
        if(birthdateDatePicker.getValue() == null){
            everythingCorrect = false;
            birthdateLabel.setText("Por favor indique una fecha");
            inputValidator.setErrorStyles(birthdateDatePicker, birthdateLabel);
        }
        if(!inputValidator.cc(ccTextField, ccLabel, "Ingrese una cedula valida")){
            everythingCorrect = false;
            inputValidator.setErrorStyles(ccTextField, ccLabel);
        }


        return everythingCorrect;
    }
}
