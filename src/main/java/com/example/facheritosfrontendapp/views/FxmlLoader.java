package com.example.facheritosfrontendapp.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class FxmlLoader {
    private Pane view;
    private FXMLLoader fxmlLoader;

    public Pane getPage(String fileName) throws IOException {
        URL fileURL = Main.class.getResource(fileName+".fxml");
        if(fileURL == null){
            throw new FileNotFoundException("FXML not found");
        }
        view = new FXMLLoader(fileURL).load();
        return view;
    }
}
