package facheritosfrontendapp.controller;

import backend.dto.personDTO.WorkerDTO;
import facheritosfrontendapp.views.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MyProfileViewController implements Initializable {

    @FXML
    private TextField cellphone;

    @FXML
    private DatePicker date;

    @FXML
    private TextField email;

    @FXML
    private TextField firstname;

    @FXML
    private TextField headquarter;

    @FXML
    private TextField idNumber;

    @FXML
    private TextField lastname;

    @FXML
    private TextField typeWorker;

    private DashboardController dashboardController;

    private String lastPage;

    private Boolean lastPageWithScrollpane;

    private WorkerDTO currentWorker;

    public MyProfileViewController(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }
/*
    @FXML
    protected void backArrowClicked(MouseEvent event) throws IOException {
        if(lastPageWithScrollpane){
           dashboardController.changeContent(lastPage, true);
           //execute func
        } else {
            dashboardController.changeContent(lastPage);
            //execute func
        }
    }
    */

    @FXML
    protected void changePasswordClicked(MouseEvent event) {

    }
    /**
     * showData: WorkerDTO, String, Boolean -> Void
     * Purpose: This method shows the worker personal data of the profile
     */
    public void showData(WorkerDTO currentWorker, String lastPage, Boolean lastPageWithScrollpane) {
        this.lastPage = lastPage;
        this.lastPageWithScrollpane = lastPageWithScrollpane;
        this.currentWorker = currentWorker;

        firstname.setText(currentWorker.getFirst_name());
        lastname.setText(currentWorker.getLast_name());
        typeWorker.setText(currentWorker.getRol());
        cellphone.setText(currentWorker.getCellphone());
        email.setText(currentWorker.getEmail());
        idNumber.setText(currentWorker.getCc());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(String.valueOf(currentWorker.getBirthday()), formatter);
        date.setValue(localDate);
        headquarter.setText(currentWorker.getHeadquarter_name());

    }
}
