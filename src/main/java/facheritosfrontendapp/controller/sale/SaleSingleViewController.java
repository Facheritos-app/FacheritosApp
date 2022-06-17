package facheritosfrontendapp.controller.sale;


import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.saleEndpoint.SaleEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.objectRowView.saleRowView.SaleRowView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SaleSingleViewController implements Initializable{

    @FXML
    private Label idNumber;

    @FXML
    private Label labelCosto;

    @FXML
    private Label labelCantidad;

    @FXML
    private Label idHeadq;

    @FXML
    private Label dateSale;

    @FXML
    private Label payMethod;

    @FXML
    private Label confirmation;

    @FXML
    private Label ccSeller;

    @FXML
    private Label ccClient;

    @FXML
    private Label nameSeller;

    @FXML
    private Label nameClient;

    @FXML
    private Label cellphoneSeller;

    @FXML
    private Label cellphoneClient;

    @FXML
    private Label emailSeller;

    @FXML
    private Label emailClient;

    @FXML
    private TableColumn<SaleRowView, Integer> colId;

    @FXML
    private TableColumn<SaleRowView, String> colModel;

    @FXML
    private TableColumn<SaleRowView, String> colColor;

    @FXML
    private TableColumn<SaleRowView,Double> colPrice;

    @FXML
    private TableColumn<SaleRowView, VBox> colOptions;

   // private ObservableList<SaleSingleRowView> saleSingleObList;

    private DashboardController dashboardController;

    private SaleEndpoint saleEndpoint;

    private SaleController saleController;

    public SaleSingleViewController() {
        saleEndpoint = new SaleEndpoint();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    @FXML
    protected void backArrowClicked() throws IOException, ExecutionException, InterruptedException {
        saleController = (SaleController) dashboardController.changeContent("sales/sales");
        saleController.showSales();
    }

    public void showVehicleData(Integer idSale){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSaleById(idSale));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setData(resultSet);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
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

    public void setData(ResultSet resultSet) throws SQLException, IOException {

        idNumber.setText(String.valueOf(resultSet.getInt("id_sale")));
        labelCosto.setText(resultSet.getString("price"));
        idHeadq.setText(resultSet.getString("name_headq"));
        dateSale.setText(resultSet.getString("sale_date"));
        payMethod.setText(resultSet.getString("name_method"));
        confirmation.setText(resultSet.getString("confirmation_status"));
        ccSeller.setText(resultSet.getString("cc_seller"));
        nameSeller.setText(resultSet.getString("name_seller"));
        emailSeller.setText(resultSet.getString("email_seller"));
        ccClient.setText(resultSet.getString("cc_client"));
        nameClient.setText(resultSet.getString("name_client"));
        cellphoneClient.setText(resultSet.getString("cellphone_client"));
        emailClient.setText(resultSet.getString("email_client"));
        cellphoneSeller.setText(resultSet.getString("cellphone_seller"));
    }
}