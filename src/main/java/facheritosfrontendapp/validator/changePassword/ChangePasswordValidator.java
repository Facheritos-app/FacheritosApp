package facheritosfrontendapp.validator.changePassword;

import facheritosfrontendapp.util.Regex;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChangePasswordValidator {
    private String errorStyle;

    private String errorMessageStyle;


    public ChangePasswordValidator() {
        errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        errorMessageStyle = String.format("-fx-text-fill: RED;");
    }

    public void setErrorStyles(TextField textField, Label label){
        textField.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(textField).play();
    }

    public Boolean password(TextField currentPasswordTextField, Label currentPasswordLabel, String currentPasswordLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.password(currentPasswordTextField.getText());
        if(currentPasswordTextField.getText().isEmpty() || !successRegex){
           currentPasswordLabel.setText(currentPasswordLabelContent);
        }else{
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
