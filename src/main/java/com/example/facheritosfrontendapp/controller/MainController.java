package com.example.facheritosfrontendapp.controller;

import com.example.facheritosfrontendapp.dto.loginDTO.LoginDTO;
import com.example.facheritosfrontendapp.dto.personDTO.WorkerDTO;
import com.example.facheritosfrontendapp.endpoints.loginEndpoint.LoginEndpoint;
import com.example.facheritosfrontendapp.views.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label Label;

    @FXML
    private Label welcomeText;

    @FXML
    private Button button;

    @FXML
    private TextField cc;

    @FXML
    private PasswordField password;

    private Stage stage;
    private Scene scene;
    private Parent root;



    @FXML
    protected void boton(ActionEvent event) throws IOException {
        LoginDTO loginDTO = new LoginDTO();

        loginDTO.setCc(cc.getText());
        loginDTO.setPassword(password.getText());

        switchToDashBoard(event, loginDTO);
    } ;

    protected void switchToDashBoard(ActionEvent event, LoginDTO loginDTO) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcomeManager.fxml"));
        root = fxmlLoader.load();

        DashboardController dashboardController = fxmlLoader.getController();

        if(dashboardController.setDashboard(loginDTO)){
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

    }

}