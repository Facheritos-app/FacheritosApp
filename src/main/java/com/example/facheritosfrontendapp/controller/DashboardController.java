package com.example.facheritosfrontendapp.controller;

import com.example.facheritosfrontendapp.dto.loginDTO.LoginDTO;
import com.example.facheritosfrontendapp.dto.otherDTO.ErrorDTO;
import com.example.facheritosfrontendapp.dto.personDTO.TypePersonDTO;
import com.example.facheritosfrontendapp.dto.personDTO.WorkerDTO;
import com.example.facheritosfrontendapp.endpoints.loginEndpoint.LoginEndpoint;

import com.example.facheritosfrontendapp.endpoints.typePersonEndpoint.TypePersonEndpoint;
import com.example.facheritosfrontendapp.views.FxmlLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

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
    private Label name;

    @FXML
    private Label rol;

    @FXML
    private HBox users;

    private LoginEndpoint loginEndpoint = new LoginEndpoint();

    private TypePersonEndpoint typePersonEndpoint = new TypePersonEndpoint();

    private WorkerDTO currentWorker;


    public boolean setDashboard(LoginDTO loginDTO){

        try {
            Map<Integer, Object> responseLogin = loginEndpoint.sendCredentials(loginDTO);
            if(responseLogin.containsKey(200)){
                currentWorker = (WorkerDTO) responseLogin.get(200);
                TypePersonDTO responseTypePerson = typePersonEndpoint.getTypePerson(String.valueOf(currentWorker.getId_type_person()));
                String trimName = currentWorker.getFirst_name();
                name.setText(trimName.contains(" ") ? trimName.split(" ")[0] : trimName);
                rol.setText(responseTypePerson.getRol_person());

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
        name.setText("Hola");

        borderPane.setRight( new FxmlLoader().getPage("users"));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}

