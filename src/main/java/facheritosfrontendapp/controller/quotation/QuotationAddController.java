package facheritosfrontendapp.controller.quotation;

import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class QuotationAddController implements Initializable {

    private QuotationController quotationController;
    private DashboardController dashboardController;
    private InventoryEndpoint inventoryEndpoint;
    private CustomerEndpoint customerEndpoint;
    private WorkerEndpoint workerEndpoint;
    private ArrayList<VehicleRowView> vehicleInventoryRowList;


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
        inventoryEndpoint = new InventoryEndpoint();
        customerEndpoint = new CustomerEndpoint();
        workerEndpoint = new WorkerEndpoint();
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
                                setSellerInfo(resultSet, Arrays.asList("cc", "first_name", "last_name", "headquarter_name", "email"));
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
                                setCustomerInfo(resultSet, Arrays.asList("cc", "first_name", "last_name"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            //Shows all the error feedbacks
                            Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del cliente o no es una cédula válida, por favor intenta nuevamente", OK);
                            fail.show();
                            customerCleanInfoLoading();
                            customerCc.setStyle("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
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

    public void setQuotationItems() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colHeadquarter.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
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
    }
    public void sellerInfoLoading() {
        sellerName.setText("Cargando...");
        sellerLastname.setText("Cargando...");
        sellerHeadq.setText("Cargando...");
        sellerEmail.setText("Cargando...");
    }

    public void sellerCleanInfoLoading(){
        sellerName.setText("");
        sellerLastname.setText("");
        sellerHeadq.setText("");
        sellerEmail.setText("");
    }

    @FXML
    public void onDeleteVehicle(MouseEvent mouseEvent) {
        VehicleRowView selectedVehicle = (VehicleRowView) quotationTableView.getSelectionModel().getSelectedItem();
        if(selectedVehicle == null){
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para eliminar", OK);
            fail.show();
        }else{
            quotationTableView.getItems().remove(selectedVehicle);
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
            quotationPrice.setText("$ " + selectedVehicle.getPrice());
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
    public void onUpdateQuotation(MouseEvent mouseEvent) {
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
        setQuotationItems();
        dashboardController = MainController.getDashboardController();
    }
}
