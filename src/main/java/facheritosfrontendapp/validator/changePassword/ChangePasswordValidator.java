package facheritosfrontendapp.validator.changePassword;

import facheritosfrontendapp.controller.profile.ChangePasswordController;
import facheritosfrontendapp.util.Regex;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChangePasswordValidator {
    private String errorStyle;

    private String errorMessageStyle;

    private ChangePasswordController changePasswordController;


    public ChangePasswordValidator(ChangePasswordController changePasswordController) {
        errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        errorMessageStyle = String.format("-fx-text-fill: RED;");
        this.changePasswordController = changePasswordController;
    }

    public void setErrorStyles(TextField textField, Label label){
        textField.setStyle(getErrorStyle());
        label.setStyle(getErrorMessageStyle());
        new animatefx.animation.Shake(textField).play();
    }

    public Boolean currentPassword(TextField currentPasswordTextField, Label currentPasswordLabel, String currentPasswordLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.password(currentPasswordTextField.getText());
        ChangePasswordController changePasswordValidator;
        if(currentPasswordTextField.getText().isEmpty() || !changePasswordController.checkCurrentPassword()){
           currentPasswordLabel.setText(currentPasswordLabelContent);
        }else{
            correct = true;
        }
        return correct;
    }

    public Boolean newPassword(TextField newPasswordTextField, Label newPasswordLabel, String newPasswordLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.password(newPasswordTextField.getText());
        if(newPasswordTextField.getText().length() < 8 || !successRegex || !changePasswordController.checkNewPassword()){
            newPasswordLabel.setText(newPasswordLabelContent);
        }else{
            correct = true;
        }
        return correct;
    }

    public Boolean repeatNewPassword(TextField repeatNewPasswordTextField, Label repeatNewPasswordLabel, String repeatNewPasswordLabelContent){
        Boolean correct = false;
        Boolean successRegex = Regex.password(repeatNewPasswordTextField.getText());
        if(repeatNewPasswordTextField.getText().isEmpty() || !changePasswordController.checkRepeatNewPassword()){
            repeatNewPasswordLabel.setText(repeatNewPasswordLabelContent);
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
