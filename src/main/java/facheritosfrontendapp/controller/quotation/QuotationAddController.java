package facheritosfrontendapp.controller.quotation;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class QuotationAddController implements Initializable {

    private InventoryEndpoint inventoryEndpoint;

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
        inventoryEndpoint = new InventoryEndpoint();
        vehicleInventoryRowList = new ArrayList<>();
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

    @FXML
    public void onDeleteVehicle(MouseEvent mouseEvent) {
    }
    @FXML
    public void onAddVehicle(MouseEvent mouseEvent) {
    }
    @FXML
    public void backArrowClicked(MouseEvent mouseEvent) {
    }
    @FXML
    public void clickedSearchSeller(MouseEvent mouseEvent) {
    }
    @FXML
    public void clickedCustomerSearch(MouseEvent mouseEvent) {
    }
    @FXML
    public void onUpdateQuotation(MouseEvent mouseEvent) {
    }
    @FXML
    public void cancelClick(MouseEvent mouseEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }
}
