package facheritosfrontendapp.validator.addInventory;

import facheritosfrontendapp.util.Regex;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddVehicleValidator {

    private String errorStyle;

    private String errorMessageStyle;

    public AddVehicleValidator() {
        errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        errorMessageStyle = String.format("-fx-text-fill: RED;");
    }

    public void setErrorStyles(TextField textField, Label label){
        textField.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(textField).play();
    }

    public void setErrorStyles(ComboBox comboBox, Label label){
        comboBox.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(comboBox).play();
    }

    public void setErrorStyles(ChoiceBox choiceBox, Label label){
        choiceBox.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(choiceBox).play();
    }


    public Boolean quantity(TextField quantityTextField, Label quantityLabel, String quantityLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.onlyDigits(quantityTextField.getText());
        if(quantityTextField.getText().isEmpty() || !successRegex){
            quantityLabel.setText(quantityLabelContent);
        } else{
            correct = true;
        }

        return correct;

    }


    public Boolean imageLink(TextField imageLinkTextField, Label imageLinkLabel, String imageLinkLabelContent){
        Boolean correct = false;
        if(imageLinkTextField.getText().isEmpty()){
            imageLinkLabel.setText(imageLinkLabelContent);
        } else{
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
