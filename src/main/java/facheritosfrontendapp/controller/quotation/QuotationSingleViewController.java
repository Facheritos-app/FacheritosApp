package facheritosfrontendapp.controller.quotation;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class QuotationSingleViewController implements Initializable {


    private QuotationEndpoint quotationEndpoint;
    private InventoryEndpoint inventoryEndpoint;
    private ArrayList<VehicleRowView> vehicleQuotationRowList;
    private ArrayList<VehicleRowView> vehicleInventoryRowList;

    @FXML
    private TableView quotationTableView;
    @FXML
    private TableColumn<VehicleRowView, String> colName;
    @FXML
    private TableColumn<VehicleRowView, Double> colPrice;
    @FXML
    private TableColumn<VehicleRowView, String> colHeadquarter;
    @FXML
    private TableView inventoryTableView;
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
    private Button cancel;

    public QuotationSingleViewController(){
        quotationEndpoint = new QuotationEndpoint();
        inventoryEndpoint = new InventoryEndpoint();
        vehicleQuotationRowList = new ArrayList<>();
        vehicleInventoryRowList = new ArrayList<>();
    }

    public void showQuotation(Integer idQuotation){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> quotationCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.getQuotation(idQuotation));

            try {
                quotationCall.thenApply(response -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                            Platform.runLater(() -> {
                                try {
                                    setQuotationItems(resultSet);
                                    setSellerInfo(resultSet);
                                    setCustomerInfo(resultSet);
                                    setQuotationDetails(resultSet);
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

    public void showInventory(){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehiclesCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getVehiclesForTableView());

            try {
                vehiclesCall.thenApply(response -> {
                    if(response.containsKey(true)){
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
        while(resultSet.next()){
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

    @FXML
    public void editQuotation(MouseEvent mouseEvent){
        showInventory();
        sellerCc.setDisable(false);
        customerCc.setDisable(false);
        paymentMethod.setDisable(false);
    }

    public void setSellerInfo(ResultSet resultSet) throws SQLException {
        sellerCc.setText(resultSet.getString("seller_cc"));
        sellerName.setText(resultSet.getString("seller_firstname"));
        sellerLastname.setText(resultSet.getString("seller_lastname"));
        sellerHeadq.setText(resultSet.getString("headquarter_name"));
        sellerEmail.setText(resultSet.getString("seller_email"));
    }

    public void setCustomerInfo(ResultSet resultSet) throws SQLException {
        customerCc.setText(resultSet.getString("customer_cc"));
        customerName.setText(resultSet.getString("customer_firstname"));
        customerLastname.setText(resultSet.getString("customer_lastname"));
    }

    public void setQuotationDetails(ResultSet resultSet) throws SQLException {
        paymentMethod.setItems(FXCollections.observableArrayList("Tarjeta de credito", "Efectivo"));
        quotationQuantity.setText("1");
        quotationId.setText(resultSet.getString("id_quotation"));
        quotationPrice.setText("$ " + resultSet.getString("price"));
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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
