package com.example.facheritosfrontendapp.controladores;

import com.example.facheritosfrontendapp.vistas.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Label Label;
    private Stage stage;
    private Scene scene;
    @FXML
    private Label welcomeText;

    @FXML
    private Button button;

    @FXML
    protected void boton(ActionEvent event) throws IOException {
        Label.setText("hola");
        switchToDashBoard(event);
    } ;

    protected void switchToDashBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcomeManager.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

}