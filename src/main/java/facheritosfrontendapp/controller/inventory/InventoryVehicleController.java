package facheritosfrontendapp.controller.inventory;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;

public class InventoryVehicleController implements Initializable {

    @FXML
    private ScrollPane scrollpane;

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
    private Button editBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ImageView vehicleImage;

    private InventoryEndpoint inventoryEndpoint;

    private DashboardController dashboardController;

    private InventoryController inventoryController;

    private InventoryEditVehicleController inventoryEditVehicleController;

    private Integer idCar;

    private String imageLink;

    public InventoryVehicleController(){
        inventoryEndpoint = new InventoryEndpoint();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();

        scrollpane.setFitToWidth(true);
        scrollpane.setFitToHeight(true);

        //Only the manager can delete or edit elements of the inventory
        if(DashboardController.getCurrentWorker().getId_rol() != 1) {
            editBtn.setVisible(false);
            deleteBtn.setVisible(false);
        }

    }

    @FXML
    protected void backArrowClicked() throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        inventoryController.showInventory();
        inventoryController.selectionTabpane(0);
    }
    /**
     * showVehicleData: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the vehicle data possible
     */
    public void showVehicleData(Integer idCar, Integer idHeadquarter){
        this.idCar = idCar;
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getVehicleById(idCar, idHeadquarter));
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

        imageLink = resultSet.getString("image");
        vehicleImage.setImage(new Image(imageLink));


    }
    @FXML
    protected void editClicked() throws IOException, ExecutionException, InterruptedException {
        inventoryEditVehicleController = (InventoryEditVehicleController) dashboardController.changeContent("inventory/inventoryEditVehicle");
        inventoryEditVehicleController.setData(idCar, modelLabel.getText(), colorLabel.getText(), assemblyYearLabel.getText(),
                headquarterLabel.getText(), quantityLabel.getText(), imageLink);
        inventoryEditVehicleController.showForm();
    }

    @FXML
    protected void deleteClicked() throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationDelete");
        Optional<ButtonType> option = dialogPane.getClickedButton();
        if(option.get() == ButtonType.YES){
            deleteVehicle();
        }
    }

    /**
     * deleteVehicle: ResultSet -> void
     * Purpose: This method contains sets all the data from the specific vehicle
     */
    public void deleteVehicle(){
        new Thread(() -> {
            Boolean result = null;
            try {
                result = CompletableFuture.supplyAsync(() -> inventoryEndpoint.deleteVehicle(this.idCar)).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            if(result) {
            Platform.runLater(() -> {
                Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Vehículo eliminado exitosamente", OK);
                success.show();
                //Go to main user view
                try {
                    inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
                    //Show inventory
                    inventoryController.showInventory();
                    inventoryController.selectionTabpane(0); //cars tab
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
}
