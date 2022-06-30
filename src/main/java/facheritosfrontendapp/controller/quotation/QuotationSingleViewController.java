package facheritosfrontendapp.controller.quotation;

import backend.dto.quotationDTO.QuotationDTO;
import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;

public class QuotationSingleViewController implements Initializable {


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
                                setQuotationDetails(resultSet, Arrays.asList("id_quotation", "price"));
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

    public void setVehiclesData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            VehicleRowView vehicleRow = new VehicleRowView(resultSet.getString("description"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    resultSet.getString("name"), resultSet.getInt("quantity"),
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
    }

    public void setCurrentQuotation(ResultSet resultSet, List<String> columns) throws SQLException {
        currentQuotation.setIdQuotation(resultSet.getInt(columns.get(0)));
        currentQuotation.setIdCar(resultSet.getInt(columns.get(1)));
        currentQuotation.setIdConfirmation(resultSet.getInt(columns.get(2)));
        currentQuotation.setIdHeadquarter(resultSet.getInt(columns.get(3)));
        currentQuotation.setQuotationDate(resultSet.getDate(columns.get(4)).toLocalDate());
        currentQuotation.setIdWorker(resultSet.getInt(columns.get(5)));
        currentQuotation.setIdCustomer(resultSet.getInt(columns.get(6)));
        newQuotation = currentQuotation;
    }

    public void setQuotationItems(ResultSet resultSet) throws SQLException {
        VehicleRowView vehicleRowView = new VehicleRowView(resultSet.getString("description"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                resultSet.getString("name"), 1, resultSet.getInt("id_car"));
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
    @FXML
    public void onDeleteVehicle(MouseEvent mouseEvent) {
        VehicleRowView selectedVehicle = (VehicleRowView) quotationTableView.getSelectionModel().getSelectedItem();
        if(selectedVehicle == null){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para eliminar", OK);
            fail.show();
        }else{
            quotationTableView.getItems().remove(selectedVehicle);
            System.out.println("Borro vehiculo");
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
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentMethod.setItems(FXCollections.observableArrayList("Tarjeta de credito", "Efectivo"));
    }



}
