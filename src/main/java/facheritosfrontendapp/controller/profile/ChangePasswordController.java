package facheritosfrontendapp.controller.profile;

import backend.dto.inventoryDTO.VehicleDTO;
import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryAddVehicleController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.validator.changePassword.ChangePasswordValidator;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class ChangePasswordController implements Initializable {

    @FXML
    private PasswordField currentPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField repeatNewPassword;

    @FXML
    private Label currentPasswordLabel;

    @FXML
    private Label newPasswordLabel;

    @FXML
    private Label repeatNewPasswordLabel;

    private DashboardController dashboardController;

    private MyProfileViewController myProfileViewController;

    private WorkerDTO currentWorker;

    private String currentPage;

    private Boolean currentPageWithScrollpane;

    private ChangePasswordValidator inputValidator;

    private WorkerEndpoint workerEndpoint;

    public ChangePasswordController(){
        inputValidator = new ChangePasswordValidator(this);
        workerEndpoint = new WorkerEndpoint();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    @FXML
    void cancelClicked(MouseEvent event) throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        //Vehículo
        if(clickedButton.get() == ButtonType.YES){
            myProfileViewController = (MyProfileViewController) dashboardController.changeContent("profile/myProfileView");
            myProfileViewController.showData(currentWorker, currentPage, currentPageWithScrollpane);
        }
    }

    @FXML
    void saveClicked(MouseEvent event) {
        if (allValidations()) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                        if (clickedButton.get() == YES) {
                            setWorkerDTO();
                            //DB call to save worker
                            new Thread(() -> {
                                Boolean result = null;
                                try {
                                    result = CompletableFuture.supplyAsync(() -> workerEndpoint.changePassword(currentWorker)).get();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                                if (result) {
                                    Platform.runLater(() -> {
                                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Contraseña cambiada exitosamente", OK);
                                        success.show();
                                        //Go to main user view
                                        try {
                                            myProfileViewController = (MyProfileViewController) dashboardController.changeContent("profile/myProfileView");
                                            myProfileViewController.showData(currentWorker, currentPage, currentPageWithScrollpane);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                                } else {
                                    Alert fail = new Alert(Alert.AlertType.ERROR, "Ha habido un problema, por favor intenta nuevamente", OK);
                                    fail.show();
                                }
                            }).start();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        }

    }

    public void setWorkerDTO(){
        currentWorker.setPassword(newPassword.getText());
    }

    public void setWorkerData(WorkerDTO currentWorker, String currentPage, Boolean currentPageWithScrollpane){
        this.currentWorker = currentWorker;
        this.currentPage = currentPage;
        this.currentPageWithScrollpane = currentPageWithScrollpane;
    }
    /**
     * checkCurrentPassword: Void -> Boolean
     * Purpose: checks if the current password is equal to the one in the DB
     */
    public Boolean checkCurrentPassword() {
        return currentPassword.getText().equals(currentWorker.getPassword());
    }

    /**
     * checkNewPassword: Void -> Boolean
     * Purpose: checks if the new password is different from the current password
     */
    public Boolean checkNewPassword(){
        return !currentWorker.getPassword().equals(newPassword.getText());
    }
    /**
     * checkRepeatNewPassword: Void -> Boolean
     * Purpose: check if the repeated password is equal to the new password
     */
    public Boolean checkRepeatNewPassword(){
        return repeatNewPassword.getText().equals(newPassword.getText());
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the change password option
     */
    public Boolean allValidations() {
        cleanErrors();
        System.out.println("currentWorker.password");
        System.out.println(currentWorker.getPassword());
        System.out.println("currentPassword");
        System.out.println(currentPassword.getText());
        System.out.println("newPassword");
        System.out.println(newPassword.getText());
        System.out.println("repeatNewPassword");
        System.out.println(repeatNewPassword.getText());

        Boolean everythingCorrect = true;
        if (!inputValidator.currentPassword(currentPassword, currentPasswordLabel, "Contraseña actual no coincide")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(currentPassword, currentPasswordLabel);
        }
        if (!inputValidator.newPassword(newPassword, newPasswordLabel, "Debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(newPassword, newPasswordLabel);
        }
        if (!inputValidator.repeatNewPassword(repeatNewPassword, repeatNewPasswordLabel, "Contraseñas no coinciden")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(repeatNewPassword, repeatNewPasswordLabel);
        }
        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        currentPasswordLabel.setText("");
        currentPassword.setStyle("");
        newPasswordLabel.setText("");
        newPassword.setStyle("");
        repeatNewPassword.setStyle("");
        repeatNewPasswordLabel.setText("");
    }
}
