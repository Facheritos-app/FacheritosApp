package facheritosfrontendapp.validator.addSaleValidator;

import facheritosfrontendapp.util.Regex;
import javafx.scene.control.*;

public class AddSaleValidator {
    private String errorStyle;

    private String errorMessageStyle;


    public AddSaleValidator() {
        errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        errorMessageStyle = String.format("-fx-text-fill: RED;");
    }

    public void setErrorStyles(TextField textField, Label label) {
        textField.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(textField).play();
    }

    public void setErrorStyles(ComboBox choiceBox, Label label) {
        choiceBox.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(choiceBox).play();
    }

    public void setErrorStyles(TableView tabla, Label label) {
        tabla.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(tabla).play();
    }

    public Boolean client(TextField nameTextField, Label nameLabel, String nameLabelContent){
        Boolean correct = false;
        if(nameTextField.getText().isEmpty() ){
            nameLabel.setText(nameLabelContent);
        }else {
            correct = true;
        }

        return correct;

    }


    public String getErrorStyle() {
        return errorStyle;
    }

    public String getErrorMessageStyle() {
        return errorMessageStyle;
    }
}


