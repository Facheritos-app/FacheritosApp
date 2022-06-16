package facheritosfrontendapp.controller.inventory;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.user.UserController;
import facheritosfrontendapp.controller.user.UserSingleViewController;
import facheritosfrontendapp.objectRowView.headquarterRowView.VehicleRowView;
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

import java.io.FileNotFoundException;
import java.io.IOException;
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

    private InventoryEndpoint inventoryEndpoint;

    private ArrayList<VehicleRowView> vehicleRowViewList;

    private DashboardController dashboardController;

    private InventoryVehicleController inventoryVehicleController;

    public InventoryController(){
        inventoryEndpoint = new InventoryEndpoint();
        vehicleRowViewList = new ArrayList<>();
        inventoryVehicleController = new InventoryVehicleController();
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
                            setData(resultSet);
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
     * handleOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of the View More label
     */
    private void handleOptionHbox(MouseEvent mouseEvent)  {
        for(Integer i = 0; i < vehicleRowViewList.size(); i++){
            if(mouseEvent.getSource() == vehicleRowViewList.get(i).getOptionsHBox()){
                System.out.println("Id del carro");
                System.out.println(vehicleRowViewList.get(i).getIdCar());
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
    //String name, Double price, String headquarter, Integer quantity, Integer idModel
    public void setData(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            VehicleRowView vehicleRow = new VehicleRowView(resultSet.getString("description"), resultSet.getDouble("price"),
                                                            resultSet.getString("name"), resultSet.getInt("quantity"),
                                                            resultSet.getInt("id_car"));
            //Add the data of every vehicle to the array
            vehicleRowViewList.add(vehicleRow);
        }

        //Set the handle events for the boxes
        for(Integer i = 0; i < vehicleRowViewList.size(); i++){
            vehicleRowViewList.get(i).getOptionsHBox().setOnMouseClicked(this::handleOptionHbox);
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

}
