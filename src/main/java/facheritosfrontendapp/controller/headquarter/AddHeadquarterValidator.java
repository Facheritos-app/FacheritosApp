package facheritosfrontendapp.controller.headquarter;

import facheritosfrontendapp.util.Regex;
import javafx.scene.control.*;

public class AddHeadquarterValidator {
    private String errorStyle;

    private String errorMessageStyle;


    public AddHeadquarterValidator() {
        errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        errorMessageStyle = String.format("-fx-text-fill: RED;");
    }

    public void setErrorStyles(TextField textField, Label label){
        textField.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(textField).play();
    }

    public void setErrorStyles(ChoiceBox choiceBox, Label label){
        choiceBox.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(choiceBox).play();
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

    public Boolean cellphone(TextField cellphoneTextField, Label cellphoneLabel, String cellphoneLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.onlyDigits(cellphoneTextField.getText());
        if(cellphoneTextField.getText().isEmpty() || !successRegex){
            cellphoneLabel.setText(cellphoneLabelContent);
        } else{
            correct = true;
        }

        return correct;

    }

    public  Boolean addr(TextField addressTextField, Label addressLabel, String addressLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.address(addressTextField.getText());
        if(addressTextField.getText().isEmpty() || !successRegex){
            addressLabel.setText(addressLabelContent);
        } else{
            correct = true;
        }

        return correct;
    }

    public Boolean email(TextField emailTextField, Label emailLabel, String emailLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.emails(emailTextField.getText());
        if(emailTextField.getText().isEmpty() || !successRegex){
            emailLabel.setText(emailLabelContent);
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