package com.example.facheritosfrontendapp.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Font.loadFont(getClass().getResourceAsStream("../fonts/Poppins-Regular.ttf"), 14);


        //Scene Welcome Manager
        Scene scene = new Scene(fxmlLoader.load());
        //stage.getIcons().add( new Image("https://www.purina-latam.com/sites/g/files/auxxlc391/files/styles/social_share_large/public/01_%C2%BFQu%C3%A9-puedo-hacer-si-mi-gato-est%C3%A1-triste-.png?itok=w67Nhubc"));
        //stage.getIcons().add(new Image(this.getClass().getResource("welcome-manager.png").toExternalForm()));
        String css = this.getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}