package com.example.facheritosfrontendapp.controller;

import com.example.facheritosfrontendapp.dto.loginDTO.LoginDTO;
import com.example.facheritosfrontendapp.dto.otherDTO.ErrorDTO;
import com.example.facheritosfrontendapp.dto.personDTO.TypePersonDTO;
import com.example.facheritosfrontendapp.dto.personDTO.WorkerDTO;
import com.example.facheritosfrontendapp.endpoints.loginEndpoint.LoginEndpoint;

import com.example.facheritosfrontendapp.endpoints.typePersonEndpoint.TypePersonEndpoint;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {


    @FXML
    private Label welcomeLabel;

    @FXML
    private Label name;

    @FXML
    private Label rol;

    private LoginEndpoint loginEndpoint = new LoginEndpoint();

    private TypePersonEndpoint typePersonEndpoint = new TypePersonEndpoint();



    public boolean setDashboard(LoginDTO loginDTO){

        try {
            Map<Integer, Object> responseLogin = loginEndpoint.sendCredentials(loginDTO);
            if(responseLogin.containsKey(200)){
                WorkerDTO responseWorker = (WorkerDTO) responseLogin.get(200);
                TypePersonDTO responseTypePerson = typePersonEndpoint.getTypePerson(String.valueOf(responseWorker.getId_type_person()));
                name.setText(responseWorker.getFirst_name() + responseWorker.getLast_name());
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
