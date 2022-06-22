package facheritosfrontendapp.validator.addInventory;

import facheritosfrontendapp.util.Regex;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddPartValidator {

    private String errorStyle;

    private String errorMessageStyle;

    public AddPartValidator() {
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

    public Boolean name(TextField nameTextField, Label nameLabel, String nameLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.noSymbol(nameTextField.getText());
        if(nameTextField.getText().isEmpty() || !successRegex){
            nameLabel.setText(nameLabelContent);
        }else {
            correct = true;
        }

        return correct;

    }

    public Boolean price(TextField priceTextField, Label priceLabel, String priceLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.onlyDigits(priceTextField.getText());
        if(priceTextField.getText().isEmpty() || !successRegex){
            priceLabel.setText(priceLabelContent);
        } else{
            correct = true;
        }

        return correct;

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


    public String getErrorStyle() {
        return errorStyle;
    }

    public String getErrorMessageStyle() {
        return errorMessageStyle;
    }
}
