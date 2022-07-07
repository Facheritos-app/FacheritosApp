package facheritosfrontendapp.validator.addUserValidator;

import facheritosfrontendapp.util.Regex;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddUserValidator {

    private String errorStyle;

    private String errorMessageStyle;


    public AddUserValidator() {
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

    public void setErrorStyles(DatePicker datePicker, Label label){
        datePicker.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(datePicker).play();
    }

    public Boolean name(TextField nameTextField, Label nameLabel, String nameLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.onlyLetters(nameTextField.getText());
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
        String cellphone = cellphoneTextField.getText();
        if(cellphone.isEmpty() || !successRegex || cellphone.length() != 10 ){
            cellphoneLabel.setText(cellphoneLabelContent);
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

    public Boolean salary(TextField salaryTextField, Label salaryLabel, String salaryLabelContent){
        Boolean correct = false;
        Double content = 0.0;
        Boolean successRegex = Regex.onlyDigits(salaryTextField.getText());
        try {
            content = Double.parseDouble(salaryTextField.getText());
        }catch(NumberFormatException e){
            content = 0.0;
        }

        if(salaryTextField.getText().isEmpty() || !successRegex || content < 900000){
            salaryLabel.setText(salaryLabelContent);
        }else{
            correct = true;
        }
        return correct;
    }

    public Boolean cc(TextField ccTextField, Label ccLabel, String ccLabelContent){
        Boolean correct = false;
        String cc = ccTextField.getText();
        Boolean sucessRegex = Regex.onlyDigits(cc);

        if(cc.isEmpty() || !sucessRegex || cc.length() < 5 ){
            ccLabel.setText(ccLabelContent);
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
