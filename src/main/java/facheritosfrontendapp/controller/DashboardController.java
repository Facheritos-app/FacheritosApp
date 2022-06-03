package facheritosfrontendapp.controller;

import facheritosfrontendapp.dto.loginDTO.LoginDTO;
import facheritosfrontendapp.dto.otherDTO.ErrorDTO;
import facheritosfrontendapp.dto.personDTO.TypePersonDTO;
import facheritosfrontendapp.dto.personDTO.WorkerDTO;
import facheritosfrontendapp.endpoints.loginEndpoint.LoginEndpoint;

import facheritosfrontendapp.endpoints.typePersonEndpoint.TypePersonEndpoint;
import facheritosfrontendapp.views.FxmlLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane welcomePane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox navbar;
    @FXML
    private Label name;

    @FXML
    private Label rol;

    @FXML
    private HBox users;

    private LoginEndpoint loginEndpoint = new LoginEndpoint();



    private TypePersonEndpoint typePersonEndpoint = new TypePersonEndpoint();

    private WorkerDTO currentWorker;




    private Integer idTypePerson;


    public boolean setDashboard(LoginDTO loginDTO){

        try {
            Map<Integer, Object> responseLogin = loginEndpoint.sendCredentials(loginDTO);
            if(responseLogin.containsKey(200)){
                currentWorker = (WorkerDTO) responseLogin.get(200);
                setIdTypePerson(currentWorker.getId_type_person());
                TypePersonDTO responseTypePerson = typePersonEndpoint.getTypePerson(String.valueOf(idTypePerson));
                String trimName = currentWorker.getFirst_name();
                name.setText(trimName.contains(" ") ? trimName.split(" ")[0] : trimName);
                rol.setText(responseTypePerson.getRol_person());
                selectNavbar();
                return true;
            }else{
                ErrorDTO responseError = (ErrorDTO) responseLogin.values().stream().findFirst().get();
                return false;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void usersClicked() throws IOException {
        System.out.println(users.getChildren());
        borderPane.setRight( new FxmlLoader().getPage("users"));
    }

    protected void selectNavbar() throws IOException {
        switch(idTypePerson) {
            //Manager
            case 1:
                navbar.getChildren().add(new FxmlLoader().getPage("navbar/managerNavbar"));
                break;
            //Seller
            case 2:
                navbar.getChildren().add(new FxmlLoader().getPage("navbar/sellerNavbar"));

                break;
            //Mechanic
            case 3:
                navbar.getChildren().add(new FxmlLoader().getPage("navbar/mechanicNavbar"));
                break;
            //Customer
            case 4:
                break;
            default:
                break;
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //System.out.print("CC: "+ loginDTO.getCc());
    }

    public Integer getIdTypePerson() {
        return idTypePerson;
    }

    public void setIdTypePerson(Integer idTypePerson) {
        this.idTypePerson = idTypePerson;
    }
}

