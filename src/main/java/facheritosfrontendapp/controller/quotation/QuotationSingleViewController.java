package facheritosfrontendapp.controller.quotation;

import backend.dto.quotationDTO.QuotationDTO;
import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class QuotationSingleViewController implements Initializable {


    private DashboardController dashboardController;
    private QuotationController quotationController;
    private QuotationEndpoint quotationEndpoint;
    private InventoryEndpoint inventoryEndpoint;
    private WorkerEndpoint workerEndpoint;
    private CustomerEndpoint customerEndpoint;
    private QuotationDTO currentQuotation;
    private QuotationDTO newQuotation;
    private ArrayList<VehicleRowView> vehicleQuotationRowList;
    private ArrayList<VehicleRowView> vehicleInventoryRowList;

    private String workerCc;
    private String clientCc;

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
    private Label quotationId;
    @FXML
    private Button searchSeller;
    @FXML
    private Button searchCustomer;
    @FXML
    private Button addVehicleButton;
    @FXML
    private Button deleteVehicleButton;
    @FXML
    private Button editQuotationButton;
    @FXML
    private Button updateQuotation;

    public QuotationSingleViewController() {
        quotationEndpoint = new QuotationEndpoint();
        inventoryEndpoint = new InventoryEndpoint();
        customerEndpoint = new CustomerEndpoint();
        workerEndpoint = new WorkerEndpoint();
        vehicleQuotationRowList = new ArrayList<>();
        vehicleInventoryRowList = new ArrayList<>();
        currentQuotation = new QuotationDTO();
        newQuotation = new QuotationDTO();
    }

    /**
     * showQuotation: Integer -> Void
     * Purpose: This method calls the  quotation endpoint to get all the information required of the quotation,
     * then, it calls all the methods to set the seller, worker and quotation info.
     */
    public void showQuotation(Integer idQuotation) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> quotationCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.getQuotation(idQuotation));

            try {
                quotationCall.thenApply(response -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setQuotationItems(resultSet);
                                setSellerInfo(resultSet, Arrays.asList("seller_cc", "seller_firstname", "seller_lastname", "headquarter_name", "seller_email"));
                                workerCc = resultSet.getString("seller_cc");
                                setCustomerInfo(resultSet, Arrays.asList("customer_cc", "customer_firstname", "customer_lastname"));
                                clientCc = resultSet.getString("customer_cc");
                                setQuotationDetails(resultSet, Arrays.asList("id_quotation", "price", "id_payment"));
                                setCurrentQuotation(resultSet, Arrays.asList("id_quotation", "id_car", "id_confirmation", "id_headquarter", "quotation_date", "id_worker", "id_person_customer"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
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

    /**
     * showInventory: Void -> Void
     * Purpose: This method calls the inventory endpoint in order to show the inventory available
     */
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

    /**
     * changeSeller: String -> Void
     * Purpose: This method calls the worker endpoint to get a worker by cc in order to change the quotation's seller if necessary.
     * Comments: In this method we start to change the newQuotation object which refers to the quotation that's currently being modified.
     */
    public void changeSeller(String cc) {
        new Thread(() -> {
            sellerInfoLoading();
            CompletableFuture<Map<Boolean, ResultSet>> sellerCall = CompletableFuture.supplyAsync(() -> workerEndpoint.getWorkerByCc(cc));
            try {
                sellerCall.thenApply(response -> {
                    Platform.runLater(() -> {
                        if (response.containsKey(true)) {
                            ResultSet resultSet = response.get(true);
                            try {
                                setSellerInfo(resultSet, Arrays.asList("cc", "first_name", "last_name", "headquarter_name", "email"));
                                newQuotation.setIdWorker(resultSet.getInt("id_worker")); //Set the new seller for the quotation.
                                newQuotation.setIdHeadquarter(resultSet.getInt("id_headquarter")); //Set the new location for the quotation
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del trabajador o no es una cédula válida, por favor intenta nuevamente", OK);
                            fail.show();
                            changeSeller(workerCc); //Error, so go back to the original seller
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
    /**
     * changeCustomer: String -> Void
     * Purpose: This method calls the person endpoint to get a customer by cc in order to change the quotation's customer if necessary.
     * Comments: In this method we continue changing the newQuotation object which refers to the quotation that's currently being modified.
     */
    public void changeCustomer(String cc){
        new Thread(() -> {
            customerInfoLoading();
            CompletableFuture<Map<Boolean, ResultSet>> customerCall = CompletableFuture.supplyAsync(() -> customerEndpoint.getCustomerByCc(cc));
            try {
                customerCall.thenApply(response -> {
                    Platform.runLater(() -> {
                        if(response.containsKey(true)){
                            ResultSet resultSet = response.get(true);
                            try {
                                setCustomerInfo(resultSet, Arrays.asList("cc", "first_name", "last_name"));
                                newQuotation.setIdCustomer(resultSet.getInt("id_person")); //Set the new customer for the quotation
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del cliente o no es una cédula válida, por favor intenta nuevamente", OK);
                            fail.show();
                            changeCustomer(clientCc);
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

    /**
     * updateQuotation: Void -> Void
     * Purpose: This method calls the quotation endpoint to update a quotation.
     */
    public void updateQuotation(){
        new Thread(() -> {
            CompletableFuture<Boolean> updateQuotationCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.updateQuotation(newQuotation));
            try {
                updateQuotationCall.thenApply(status -> {
                    Platform.runLater(() -> {
                        if(status){
                            Alert success = new Alert(Alert.AlertType.CONFIRMATION, "La cotización ha sido actualizada correctamente", OK);
                            success.show();
                        }else{
                            Alert fail = new Alert(Alert.AlertType.ERROR, "Hubo un error con el servidor, intentalo otra vez por favor", OK);
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


    @FXML
    public void editQuotation(MouseEvent mouseEvent) {
        sellerCc.setDisable(false);
        customerCc.setDisable(false);
        paymentMethod.setDisable(false);
        searchCustomer.setDisable(false);
        searchCustomer.setVisible(true);
        searchSeller.setDisable(false);
        searchSeller.setVisible(true);
        addVehicleButton.setDisable(false);
        addVehicleButton.setVisible(true);
        deleteVehicleButton.setDisable(false);
        deleteVehicleButton.setVisible(true);
        editQuotationButton.setDisable(true);
        editQuotationButton.setVisible(false);
        updateQuotation.setDisable(false);
        updateQuotation.setVisible(true);
        showInventory();
    }

    public void setSellerInfo(ResultSet resultSet, List<String> columns) throws SQLException {
        sellerCc.setText(resultSet.getString(columns.get(0)));
        sellerName.setText(resultSet.getString(columns.get(1)));
        sellerLastname.setText(resultSet.getString(columns.get(2)));
        sellerHeadq.setText(resultSet.getString(columns.get(3)));
        sellerEmail.setText(resultSet.getString(columns.get(4)));
    }
    public void sellerInfoLoading() {
        sellerName.setText("Cargando...");
        sellerLastname.setText("Cargando...");
        sellerHeadq.setText("Cargando...");
        sellerEmail.setText("Cargando...");
    }

    public void setCustomerInfo(ResultSet resultSet, List<String> columns) throws SQLException {
        customerCc.setText(resultSet.getString(columns.get(0)));
        customerName.setText(resultSet.getString(columns.get(1)));
        customerLastname.setText(resultSet.getString(columns.get(2)));
    }

    public void customerInfoLoading(){
        customerName.setText("Cargando...");
        customerLastname.setText("Cargando...");
    }

    public void setQuotationDetails(ResultSet resultSet, List<String> columns) throws SQLException {
        quotationQuantity.setText("1");
        quotationId.setText(String.valueOf(resultSet.getInt(columns.get(0))));
        quotationPrice.setText("$ " + resultSet.getString(columns.get(1)));
        paymentMethod.getSelectionModel().select(resultSet.getInt(columns.get(2)) - 1);
    }

    /**
     * setCurrentQuotation: ResultSet, List<String> -> Void
     * Purpose: This method sets the current quotation object and creates a new one for updates
     */
    public void setCurrentQuotation(ResultSet resultSet, List<String> columns) throws SQLException {
        currentQuotation.setIdQuotation(resultSet.getInt(columns.get(0)));
        currentQuotation.setIdCar(resultSet.getInt(columns.get(1)));
        currentQuotation.setIdConfirmation(resultSet.getInt(columns.get(2)));
        currentQuotation.setIdHeadquarter(resultSet.getInt(columns.get(3)));
        currentQuotation.setQuotationDate(resultSet.getDate(columns.get(4)).toLocalDate());
        currentQuotation.setIdWorker(resultSet.getInt(columns.get(5)));
        currentQuotation.setIdCustomer(resultSet.getInt(columns.get(6)));
        currentQuotation.setIdPayment(getMethodPaymentId((String) paymentMethod.getSelectionModel().getSelectedItem()));
        try {
            newQuotation = (QuotationDTO) currentQuotation.clone(); //Clone the current quotation into the one that will be most likely modified
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setQuotationItems(ResultSet resultSet) throws SQLException {
        VehicleRowView vehicleRowView = new VehicleRowView(resultSet.getString("description"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                resultSet.getInt("id_headquarter"),resultSet.getString("name"), 1, resultSet.getInt("id_car"));
        vehicleQuotationRowList.add(vehicleRowView);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colHeadquarter.setCellValueFactory(new PropertyValueFactory<>("headquarter"));

        quotationTableView.setItems(FXCollections.observableArrayList(vehicleQuotationRowList));
    }

    @FXML
    public void clickedSearchSeller(MouseEvent mouseEvent) {
        changeSeller(sellerCc.getText());
    }
    @FXML
    public void clickedCustomerSearch(MouseEvent mouseEvent) {
      changeCustomer(customerCc.getText());
    }

    /**
     * onDeleteVehicle: MouseEvent -> Void
     * Purpose: This method is called when the user wants to delete the vehicle from the quotation
     */
    @FXML
    public void onDeleteVehicle(MouseEvent mouseEvent) {
        VehicleRowView selectedVehicle = (VehicleRowView) quotationTableView.getSelectionModel().getSelectedItem();
        if(selectedVehicle == null){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para eliminar", OK);
            fail.show();
        }else{
            quotationTableView.getItems().remove(selectedVehicle);
            newQuotation.setIdCar(null);
            quotationPrice.setText("$ ");
        }

    }
    @FXML
    public void onAddVehicle(MouseEvent mouseEvent) {
        VehicleRowView selectedVehicle = inventoryTableView.getSelectionModel().getSelectedItem();
        if(selectedVehicle == null){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para agregar", OK);
            fail.show();
        }else if(quotationTableView.getItems().size() > 0){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Solo puede haber un vehículo en la cotización, por favor bora el existente para agregar uno nuevo", OK);
            fail.show();
        }else{
            quotationTableView.getItems().add(selectedVehicle);
            newQuotation.setIdCar(selectedVehicle.getIdCar());
            quotationPrice.setText("$ " + selectedVehicle.getPrice());
        }
    }
    @FXML
    public void backArrowClicked(MouseEvent mouseEvent) throws IOException {
        cancelClick(mouseEvent);
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

    @FXML
    public void deleteQuotationClick(MouseEvent mouseEvent) throws IOException, ExecutionException, InterruptedException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationDelete");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if(clickedButton.get() == YES) {
            CompletableFuture<Boolean> deleteQuotationCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.deleteQuotation(currentQuotation.getIdQuotation()));
            deleteQuotationCall.thenApply(response -> {
                Platform.runLater(() -> {
                    if(response){
                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Esta cotización ha sido eliminada");
                        success.showAndWait();
                        try {
                            quotationController = (QuotationController) dashboardController.changeContent("quotations/quotations");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        quotationController.showQuotations();
                    } else {
                        Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha podido eliminar esta cotización, vuelvalo a intentar");
                        fail.show();
                    }
                });
                return response;
            }).get();
        }
    }

    /**
     * onUpdateQuotation: MouseEvent -> Void
     * Purpose: This method is called when the user wants to update the current quotation,
     * it verifies that everything is correct in order to make the update.
     */
    @FXML
    public void onUpdateQuotation(MouseEvent mouseEvent) {
        newQuotation.setIdPayment(getMethodPaymentId((String) paymentMethod.getSelectionModel().getSelectedItem()));
        if(compareQuotations()){
            Alert info = new Alert(Alert.AlertType.INFORMATION, "No hay nada que actualizar", OK);
            info.show();
        }else {
            if(newQuotation.getIdCar() == null){
                Alert fail = new Alert(Alert.AlertType.ERROR, "La cotización debe tener un carro", OK);
                fail.show();
            } else{
                updateQuotation();
                try {
                    currentQuotation = (QuotationDTO) newQuotation.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * compareQuotations: Void -> Boolean
     * Purpose: This method compares the new quotation and the current quotation, so the controller does not
     * make an update request when it is not necessary.
     */
    public Boolean compareQuotations(){
        return currentQuotation.getIdQuotation() == newQuotation.getIdQuotation() &&
           currentQuotation.getIdWorker() == newQuotation.getIdWorker() &&
           currentQuotation.getIdHeadquarter() == newQuotation.getIdHeadquarter() &&
           currentQuotation.getIdCar() == newQuotation.getIdCar() &&
           currentQuotation.getIdCustomer() == newQuotation.getIdCustomer() &&
           currentQuotation.getIdConfirmation() == newQuotation.getIdConfirmation() &&
           currentQuotation.getQuotationDate() == newQuotation.getQuotationDate() &&
           currentQuotation.getIdPayment() == newQuotation.getIdPayment();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        paymentMethod.setItems(FXCollections.observableArrayList("Tarjeta de credito", "Efectivo"));
    }
}
