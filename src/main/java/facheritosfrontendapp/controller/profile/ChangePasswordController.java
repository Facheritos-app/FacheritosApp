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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private TextField currentPassword;

    @FXML
    private TextField newPassword;

    @FXML
    private TextField repeatNewPassword;

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
        inputValidator = new ChangePasswordValidator();
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
                setWorkerDTO();
                Platform.runLater(() -> {
                    try {
                        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                        if (clickedButton.get() == YES) {
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
     * Purpose: check if the current password is correct
     */
    protected Boolean checkCurrentPassword() {
        return currentPassword.getText().equals(currentWorker.getPassword());
    }

    /**
     * checkNewPassword: Void -> Boolean
     * Purpose: check if the new password is different from the current password
     */
    protected Boolean checkNewPassword(){
        return !currentWorker.getPassword().equals(newPassword.getText());
    }
    /**
     * checkRepeatNewPassword: Void -> Boolean
     * Purpose: check if the repeated password is equal to the new password
     */
    protected Boolean checkRepeatNewPassword(){
        return repeatNewPassword.getText().equals(newPassword.getText());
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the change password option
     */
    public Boolean allValidations() {
        cleanErrors();
        Boolean everythingCorrect = true;
        if (!inputValidator.password(currentPassword, currentPasswordLabel, "Contraseña actual no válida")
        && checkCurrentPassword()) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(currentPassword, currentPasswordLabel);
        }
        if (!inputValidator.password(newPassword, newPasswordLabel, "Contraseña nueva no válida")
        && checkNewPassword()) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(newPassword, newPasswordLabel);
        }
        if (!checkRepeatNewPassword()) {
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
