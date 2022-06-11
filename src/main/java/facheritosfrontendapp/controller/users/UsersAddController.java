package facheritosfrontendapp.controller.users;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import facheritosfrontendapp.validator.createUserValidator.CreateUserValidator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

public class UsersAddController implements Initializable {

    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<String> headquarterComboboxList;

    private CreateUserValidator inputValidator;

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
    private ComboBox<String> headquarterCombobox;
    @FXML
    private Label headquarterLabel;




    public UsersAddController(){
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<String>();
        inputValidator = new CreateUserValidator();
    }
    @FXML
    public void cancelButtonAddUserClicked(MouseEvent mouseEvent) {
    }
    @FXML
    public void saveButtonAddUserClicked(MouseEvent mouseEvent) {
        allValidations();
    }

    /**
     * showHeaders: void -> void
     * Purpose: This method contains all the steps to show all the headquarters in the combobox
     */
    public void showHeadquarters() throws ExecutionException, InterruptedException {
        //Set the call to the DB.
        CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

        //Use the response from the BD to fill the combobox
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
    }
    /**
     * showHeaders: ResultSet -> void
     * Purpose: This method set the items of the headquarters combobox according to the DB
     */
    public void setHeadquarterCombobox(ResultSet resultSet) throws SQLException {
        while(resultSet.next()) {
            headquarterComboboxList.add(resultSet.getString("name"));
        }
        headquarterCombobox.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set items for Rol combobox
        typeCombobox.setItems(FXCollections.observableArrayList("Gerente", "Vendedor", "Jefe de taller"));
        try {
            this.showHeadquarters();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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


        return everythingCorrect;
    }
}
