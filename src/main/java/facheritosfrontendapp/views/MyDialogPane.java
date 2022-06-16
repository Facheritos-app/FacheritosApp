package facheritosfrontendapp.views;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.Optional;

public class MyDialogPane extends DialogPane {
    Optional<ButtonType> clickedButton;

    public MyDialogPane(String fileName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/facheritosfrontendapp/views/"+fileName+".fxml"));
        DialogPane dialogPane = fxmlLoader.load();
        //ConfirmationCancelController confirmationCancelController = fxmlLoader.getController();
        Button yesBtn = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(0));
        Button noBtn = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(1));
        yesBtn.getStyleClass().add("blue-button");
        noBtn.getStyleClass().add("red-button");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("¿Desea continuar?");


        clickedButton = dialog.showAndWait();
    }

    public Optional<ButtonType> getClickedButton(){
        return clickedButton;
    }


}
