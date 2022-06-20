package facheritosfrontendapp.controller.inventory;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.inventoryRowView.PartRowView;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InventoryController implements Initializable {
    @FXML
    private TabPane inventoryTabpane;

    @FXML
    private TableView vehicleTableview;

    @FXML
    private TableColumn<VehicleRowView, String> colHeadquarter;

    @FXML
    private TableColumn<VehicleRowView, String> colName;

    @FXML
    private TableColumn<VehicleRowView, VBox> colOptions;

    @FXML
    private TableColumn<VehicleRowView, Double> colPrice;

    @FXML
    private TableColumn<VehicleRowView, Integer> colQuantity;

    @FXML
    private TableView partTableview;

    @FXML
    private TableColumn<VehicleRowView, String> colHeadquarterPart;

    @FXML
    private TableColumn<VehicleRowView, String> colNamePart;

    @FXML
    private TableColumn<VehicleRowView, VBox> colOptionsPart;

    @FXML
    private TableColumn<VehicleRowView, Double> colPricePart;

    @FXML
    private TableColumn<VehicleRowView, Integer> colQuantityPart;

    private InventoryEndpoint inventoryEndpoint;

    private ArrayList<VehicleRowView> vehicleRowViewList;

    private ArrayList<PartRowView> partRowViewList;

    private DashboardController dashboardController;

    private InventoryVehicleController inventoryVehicleController;

    private InventoryPartController inventoryPartController;

    public InventoryController(){
        inventoryEndpoint = new InventoryEndpoint();
        vehicleRowViewList = new ArrayList<>();
        partRowViewList = new ArrayList<>();
        inventoryVehicleController = new InventoryVehicleController();
        inventoryPartController = new InventoryPartController();
    }

    @FXML
    protected void addClicked() throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("inventory/confirmationPart");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if(clickedButton.get() == ButtonType.APPLY){
            // CHANGE VIEW
            System.out.println("Apply");
        } else {
            //CHANGE VIEW
            System.out.println("Cancel");
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        //Tabs cannot be closed
        inventoryTabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }
    /**
     * showVehicles: void -> void
     * Purpose: shows the vehicles data on the tableview
     */
    public void showVehicles(){
        //Every update to the GUI from the DB must be encapsuled by Thread
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> vehiclesCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getVehiclesForTableView());

            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                vehiclesCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            //Set the tableview data visually
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
     * showParts: void -> void
     * Purpose: shows the parts data on the tableview
     */
    public void showParts(){
        //Every update to the GUI from the DB must be encapsuled by Thread
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getPartsForTableView());

            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                partsCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            //Set the tableview data visually
                            setPartsData(resultSet);
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
     * handleVehicleOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of the View More label
     */
    private void handleVehicleOptionHbox(MouseEvent mouseEvent)  {
        for(Integer i = 0; i < vehicleRowViewList.size(); i++){
            if(mouseEvent.getSource() == vehicleRowViewList.get(i).getOptionsHBox()){
                //Here we will load the component to view, edit and delete the vehicle
                try {
                    inventoryVehicleController = (InventoryVehicleController) dashboardController.changeContent("inventory/inventoryVehicle", true);
                    inventoryVehicleController.showVehicleData(vehicleRowViewList.get(i).getIdCar());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    /**
     * handlePartOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of the View More label
     */
    private void handlePartOptionHbox(MouseEvent mouseEvent)  {
        for(Integer i = 0; i < partRowViewList.size(); i++){
            if(mouseEvent.getSource() == partRowViewList.get(i).getOptionsHBox()){
                //Here we will load the component to view, edit and delete the vehicle
                try {
                    inventoryPartController = (InventoryPartController) dashboardController.changeContent("inventory/inventoryPart");
                    //SHOW PART DATA
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void setPartsData(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            PartRowView partRow = new PartRowView(resultSet.getString("description"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    resultSet.getString("name"), resultSet.getInt("quantity"),
                    resultSet.getInt("id_part"));
            //Add the data of every vehicle to the array
            partRowViewList.add(partRow);
        }

        //Set the handle events for the boxes
        for(Integer i = 0; i < partRowViewList.size(); i++){
            partRowViewList.get(i).getOptionsHBox().setOnMouseClicked(this::handlePartOptionHbox);
        }
        /**
         * Setting up the columns of the table,
         * Comment: the value passed on the 'PropertyValueFactory' MUST match with the attributes of the VehicleRowView object
         * */
        colNamePart.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPricePart.setCellValueFactory(new PropertyValueFactory<>("price"));
        colHeadquarterPart.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        colQuantityPart.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colOptionsPart.setCellValueFactory(new PropertyValueFactory<>("options"));

        partTableview.setItems(FXCollections.observableArrayList(partRowViewList));

    }
    public void setVehiclesData(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            VehicleRowView vehicleRow = new VehicleRowView(resultSet.getString("description"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                                                            resultSet.getString("name"), resultSet.getInt("quantity"),
                                                            resultSet.getInt("id_car"));
            //Add the data of every vehicle to the array
            vehicleRowViewList.add(vehicleRow);
            System.out.println(resultSet.getDouble("price"));
        }

        //Set the handle events for the boxes
        for(Integer i = 0; i < vehicleRowViewList.size(); i++){
            vehicleRowViewList.get(i).getOptionsHBox().setOnMouseClicked(this::handleVehicleOptionHbox);
        }
        /**
         * Setting up the columns of the table,
         * Comment: the value passed on the 'PropertyValueFactory' MUST match with the attributes of the VehicleRowView object
         * */
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colHeadquarter.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("options"));

        vehicleTableview.setItems(FXCollections.observableArrayList(vehicleRowViewList));

    }
    /**
     * showInventory: void -> void
     * Purpose: shows the vehicles and parts of the inventory
     */
    public void showInventory(){
        showVehicles();
        showParts();
    }

}
