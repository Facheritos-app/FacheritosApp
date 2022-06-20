package facheritosfrontendapp.controller;

import backend.dto.loginDTO.LoginDTO;
import facheritosfrontendapp.views.Main;
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
import java.util.concurrent.ExecutionException;

public class MainController implements Initializable {


    @FXML
    private Label error;

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
    private Button inicio;

    private static DashboardController dashboardController;


    @FXML
    protected void ccAction() {
        error.setVisible(false);
    }
    @FXML
    protected void passwordAction() {
        error.setVisible(false);
    }

    @FXML
    protected void boton(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCc(cc.getText());
        loginDTO.setPassword(password.getText());
        switchToDashBoard(event, loginDTO);
    } ;

    protected void switchToDashBoard(ActionEvent event, LoginDTO loginDTO) throws IOException, ExecutionException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcomeManager.fxml"));
        root = fxmlLoader.load();
        dashboardController = fxmlLoader.getController();

        if(dashboardController.setDashboard(loginDTO)) {
            inicio.setDisable(false); //Deshabilitar boton inciar sesion
            error.setVisible(false);
            inicio.setStyle("-fx-background-color: #006FC9; ");
            inicio.setText("Cargando");
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            error.setVisible(true);
            error.setStyle("-fx-text-fill: #C02130; ");
            error.setText("Error: Datos incorrectos, intente de nuevo");
        }

    }

    public static DashboardController getDashboardController() {
        return dashboardController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

    }

}