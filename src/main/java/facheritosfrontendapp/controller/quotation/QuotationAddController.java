package facheritosfrontendapp.controller.quotation;

import backend.dto.quotationDTO.QuotationDTO;
import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class QuotationAddController implements Initializable {

    private QuotationController quotationController;
    private DashboardController dashboardController;
    private QuotationEndpoint quotationEndpoint;
    private InventoryEndpoint inventoryEndpoint;
    private CustomerEndpoint customerEndpoint;
    private WorkerEndpoint workerEndpoint;
    private ArrayList<VehicleRowView> vehicleInventoryRowList;
    private QuotationDTO newQuotation;


    @FXML
    private TextField sellerCc;
    @FXML
    private TextField sellerName;
    @FXML
    private TextField sellerLastname;
    @FXML
    private TextField sellerHeadq;
    @FXML
    private TextField sellerEmail;

    @FXML
    private TextField customerCc;
    @FXML
    private TextField customerName;
    @FXML
    private TextField customerLastname;
    @FXML
    private TextField quotationQuantity;
    @FXML
    private ComboBox paymentMethod;
    @FXML
    private Label quotationPrice;

    @FXML
    private TableView quotationTableView;
    @FXML
    private TableColumn<VehicleRowView, String> colName;
    @FXML
    private TableColumn<VehicleRowView, Double> colPrice;
    @FXML
    private TableColumn<VehicleRowView, String> colHeadquarter;
    @FXML
    private TableView<VehicleRowView> inventoryTableView;
    @FXML
    private TableColumn<VehicleRowView, String> colNameInventory;
    @FXML
    private TableColumn<VehicleRowView, Double> colPriceInventory;
    @FXML
    private TableColumn<VehicleRowView, String> colHeadquarterInventory;
    @FXML
    private TableColumn<VehicleRowView, Integer> colQuantityInventory;

    public QuotationAddController(){
        quotationEndpoint = new QuotationEndpoint();
        inventoryEndpoint = new InventoryEndpoint();
        customerEndpoint = new CustomerEndpoint();
        workerEndpoint = new WorkerEndpoint();
        vehicleInventoryRowList = new ArrayList<>();
        newQuotation = new QuotationDTO();
    }

    public void showInventory() {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehiclesCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getVehiclesForTableView());

            try {
                vehiclesCall.thenApply(response -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setVehiclesData(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                }).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setVehiclesData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            VehicleRowView vehicleRow = new VehicleRowView(resultSet.getString("description"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    resultSet.getInt("id_headquarter"),resultSet.getString("name"), resultSet.getInt("quantity"),
                    resultSet.getInt("id_car"));
            vehicleInventoryRowList.add(vehicleRow);
        }

        colNameInventory.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPriceInventory.setCellValueFactory(new PropertyValueFactory<>("price"));
        colHeadquarterInventory.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        colQuantityInventory.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        inventoryTableView.setItems(FXCollections.observableArrayList(vehicleInventoryRowList));
    }


    public void getSellerByCc(String cc) {
        new Thread(() -> {
            sellerInfoLoading();
            CompletableFuture<Map<Boolean, ResultSet>> sellerCall = CompletableFuture.supplyAsync(() -> workerEndpoint.getWorkerByCc(cc));
            try {
                sellerCall.thenApply(response -> {
                    Platform.runLater(() -> {
                        if (response.containsKey(true)) {
                            ResultSet resultSet = response.get(true);
                            try {
                                setSellerInfo(resultSet, Arrays.asList("cc", "first_name", "last_name", "headquarter_name", "email", "id_worker", "id_headquarter"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            //Shows all the error feedbacks
                            Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del trabajador o no es una cédula válida, por favor intenta nuevamente", OK);
                            fail.show();
                            sellerCleanInfoLoading();
                            sellerCc.setStyle("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
                            new animatefx.animation.Shake(sellerCc).play();
                        }
                    });
                    return true;
                }).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void getCustomerByCc(String cc){
        new Thread(() -> {
            customerInfoLoading();
            CompletableFuture<Map<Boolean, ResultSet>> customerCall = CompletableFuture.supplyAsync(() -> customerEndpoint.getCustomerByCc(cc));
            try {
                customerCall.thenApply(response -> {
                    Platform.runLater(() -> {
                        if(response.containsKey(true)){
                            ResultSet resultSet = response.get(true);
                            try {
                                setCustomerInfo(resultSet, Arrays.asList("cc", "first_name", "last_name", "id_person"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            //Shows all the error feedbacks
                            Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del cliente o no es una cédula válida, por favor intenta nuevamente", OK);
                            fail.show();
                            customerCleanInfoLoading();
                            customerCc.setStyle("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;"); //Error style
                            new animatefx.animation.Shake(customerCc).play();
                        }
                    });
                    return true;
                }).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void createQuotation(){
        new Thread(() -> {
            CompletableFuture<Boolean> createQuotationCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.createQuotation(newQuotation));

            try {
                createQuotationCall.thenApply(state -> {
                    Platform.runLater(() -> {
                        if(state){
                            Alert success = new Alert(Alert.AlertType.CONFIRMATION, "La cotización ha sido creada", OK);
                            success.show();
                            try {
                                //Successful, then go back to the main quotation view
                                quotationController = (QuotationController) dashboardController.changeContent("quotations/quotations");
                                quotationController.showQuotations();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Alert fail = new Alert(Alert.AlertType.ERROR, "Hubo un problema con el servidor, vuelve a intentarlo por favor", OK);
                            fail.show();
                        }
                    });
                    return true;
                }).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public Integer getMethodPaymentId(String paymentMethod){
        switch (paymentMethod){
            case "Efectivo":
                return 2;
            case "Tarjeta de credito":
                return 1;
            default:
                return -3;
        }
    }

    public void setQuotationTableView() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colHeadquarter.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
    }

    /**
     * setCustomerInfo: ResultSet, List<String> -> Void
     * Purpose: This method sets all the customer's information and
     * starts to fill the quotation object that will be saved in the DB.
     */
    public void setCustomerInfo(ResultSet resultSet, List<String> columns) throws SQLException {
        customerCc.setText(resultSet.getString(columns.get(0)));
        customerName.setText(resultSet.getString(columns.get(1)));
        customerLastname.setText(resultSet.getString(columns.get(2)));

        newQuotation.setIdCustomer(resultSet.getInt(columns.get(3)));
    }

    public void customerInfoLoading(){
        customerName.setText("Cargando...");
        customerLastname.setText("Cargando...");
    }

    public void customerCleanInfoLoading(){
        customerName.setText("");
        customerLastname.setText("");
    }


    public void setSellerInfo(ResultSet resultSet, List<String> columns) throws SQLException {
        sellerCc.setText(resultSet.getString(columns.get(0)));
        sellerName.setText(resultSet.getString(columns.get(1)));
        sellerLastname.setText(resultSet.getString(columns.get(2)));
        sellerHeadq.setText(resultSet.getString(columns.get(3)));
        sellerEmail.setText(resultSet.getString(columns.get(4)));

        //Continue filling the quotation object with the seller's info
        newQuotation.setIdWorker(resultSet.getInt(columns.get(5)));
        newQuotation.setIdHeadquarter(resultSet.getInt(columns.get(6)));
        newQuotation.setQuotationDate(LocalDate.now());
        newQuotation.setIdConfirmation(1);
    }
    public void sellerInfoLoading() {
        sellerName.setText("Cargando...");
        sellerLastname.setText("Cargando...");
        sellerHeadq.setText("Cargando...");
        sellerEmail.setText("Cargando...");
    }

    /**
     * verifyQuotation: Void -> Boolean
     * Purpose: This method verifies that the quotation object that will be saved in the DB is completely filled
     * with the necessary fields for the DB.
     */
    public Boolean verifyQuotation(){
        if(newQuotation.getQuotationDate() != null && newQuotation.getIdPayment() != null &&
           newQuotation.getIdConfirmation() != null && newQuotation.getIdHeadquarter() != null &&
           newQuotation.getIdWorker() != null && newQuotation.getIdCustomer() != null &&
           newQuotation.getIdCar() != null){
            return true;
        }else{
            return false;
        }
    }

    public void sellerCleanInfoLoading(){
        sellerName.setText("");
        sellerLastname.setText("");
        sellerHeadq.setText("");
        sellerEmail.setText("");
    }

    @FXML
    public void onDeleteVehicle(MouseEvent mouseEvent) { //Used to delete a vehicle on the quotation table view
        VehicleRowView selectedVehicle = (VehicleRowView) quotationTableView.getSelectionModel().getSelectedItem();
        if(selectedVehicle == null){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para eliminar", OK);
            fail.show();
        }else{
            quotationTableView.getItems().remove(selectedVehicle);
            quotationPrice.setText("$ ");
            newQuotation.setIdCar(null);
            quotationQuantity.setText("0");
        }
    }
    @FXML
    public void onAddVehicle(MouseEvent mouseEvent) { //Used to add a vehicle from the inventory tableview to the quotation table view
        VehicleRowView selectedVehicle = inventoryTableView.getSelectionModel().getSelectedItem();
        if(selectedVehicle == null){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para agregar", OK);
            fail.show();
        }else if(quotationTableView.getItems().size() > 0){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Solo puede haber un vehículo en la cotización, por favor bora el existente para agregar uno nuevo", OK);
            fail.show();
        }else{
            quotationTableView.getItems().add(selectedVehicle);
            quotationPrice.setText("$ " + selectedVehicle.getPrice());
            newQuotation.setIdCar(selectedVehicle.getIdCar());
            quotationQuantity.setText("1");
        }
    }
    @FXML
    public void backArrowClicked(MouseEvent mouseEvent) throws IOException {
        cancelClick(mouseEvent);
    }
    @FXML
    public void clickedSearchSeller(MouseEvent mouseEvent) {
        sellerCc.setStyle("");
        getSellerByCc(sellerCc.getText());
    }
    @FXML
    public void clickedCustomerSearch(MouseEvent mouseEvent) {
        customerCc.setStyle("");
        getCustomerByCc(customerCc.getText());
    }

    @FXML
    public void onCreateQuotation(MouseEvent mouseEvent) {

        //Sets the id payment method, if the user has not clicked one then fills the object with a null value.
        if(paymentMethod.getSelectionModel().getSelectedItem() == null) {
            newQuotation.setIdPayment(null);
        }else{
            newQuotation.setIdPayment(getMethodPaymentId((String) paymentMethod.getSelectionModel().getSelectedItem()));
        }
        if(verifyQuotation()){
            createQuotation();
        }else{
            Alert fail = new Alert(Alert.AlertType.ERROR, "Falta información para crear la cotización", OK);
            fail.show();
        }
    }
    @FXML
    public void cancelClick(MouseEvent mouseEvent) throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            quotationController = (QuotationController) dashboardController.changeContent("quotations/quotations");
            quotationController.showQuotations();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setQuotationTableView();
        dashboardController = MainController.getDashboardController();
        paymentMethod.setItems(FXCollections.observableArrayList("Tarjeta de credito", "Efectivo"));
    }
}
