package facheritosfrontendapp.views;

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
        stage.getIcons().add(new Image("file:src/main/resources/facheritosfrontendapp/icons/welcome-manager.png"));
        String css = this.getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setMinHeight(600.0); //to avoid the stage being resized less than the size of the scenes.
        stage.setMinWidth(850.0);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}