package facheritosfrontendapp.controller.inventory;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InventoryVehicleController implements Initializable {

    @FXML
    private Label assemblyYearLabel;

    @FXML
    private Label batteryCapacityLabel;

    @FXML
    private Label colorLabel;

    @FXML
    private Label headquarterLabel;

    @FXML
    private Label launchYearLabel;

    @FXML
    private Label maxSpeedLabel;

    @FXML
    private Label modelLabel;

    @FXML
    private Label passengersLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label transmissionLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private ImageView vehicleImage;

    private InventoryEndpoint inventoryEndpoint;

    private DashboardController dashboardController;

    private InventoryController inventoryController;

    public InventoryVehicleController(){

        inventoryEndpoint = new InventoryEndpoint();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();

    }

    @FXML
    protected void backArrowClicked() throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        inventoryController.showVehicles();
    }
    /**
     * showVehicleData: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the vehicle data possible
     */
    public void showVehicleData(Integer idCar){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getVehicleById(idCar));
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

    /**
     * setData: ResultSet -> void
     * Purpose: This method contains sets all the data from the specific vehicle
     */
    public void setData(ResultSet resultSet) throws SQLException, IOException {

        modelLabel.setText(resultSet.getString("description"));
        assemblyYearLabel.setText(resultSet.getString("assemble_year"));
        colorLabel.setText(resultSet.getString("color"));
        headquarterLabel.setText(resultSet.getString("name"));
        quantityLabel.setText(resultSet.getString("quantity"));
        launchYearLabel.setText(resultSet.getString("year"));
        batteryCapacityLabel.setText(resultSet.getString("battery_capacity"));
        maxSpeedLabel.setText(resultSet.getString("max_speed"));
        passengersLabel.setText(resultSet.getString("passenger_capacity"));
        transmissionLabel.setText(resultSet.getString("transmision"));
        priceLabel.setText(resultSet.getString("price"));

        //Intento de agregar la imagen, NO CARGA
        new Thread(() -> {

            URLConnection connection = null;
            try {
                connection = new URL(resultSet.getString("image")).openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            connection.addRequestProperty("Use  r-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            Image image = null;
            try {
                image = new Image(connection.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            vehicleImage.setImage(image);
        }).start();


    }
}
