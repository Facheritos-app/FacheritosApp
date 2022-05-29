package com.example.facheritosfrontendapp.vistas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(Main.class.getResource("welcomeManager.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcomeManager.fxml"));

        //Scene Welcome Manager
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Welcome");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}