package com.example.facheritosfrontendapp.controladores;

import com.example.facheritosfrontendapp.dto.LoginDTO;
import com.example.facheritosfrontendapp.dto.WorkerDTO;
import com.example.facheritosfrontendapp.endpoints.LoginEndpoint;
import com.example.facheritosfrontendapp.vistas.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import com.google.gson.Gson;

public class MainController {

    @FXML
    private Label Label;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label welcomeText;

    @FXML
    private Button button;

    private LoginEndpoint loginEndpoint = new LoginEndpoint();


    @FXML
    protected void boton(ActionEvent event) throws IOException {
        switchToDashBoard(event);
        LoginDTO loginDTO = new LoginDTO();

        loginDTO.setCc("1234567");
        loginDTO.setPassword("Admin123+");

        Gson gson = new Gson();

        String jsonObject = gson.toJson(loginDTO);

        try {
            WorkerDTO responseWorker = loginEndpoint.sendCredentials(jsonObject);
            System.out.println(responseWorker.getFirst_name());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    } ;

    protected void switchToDashBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcomeManager.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

}