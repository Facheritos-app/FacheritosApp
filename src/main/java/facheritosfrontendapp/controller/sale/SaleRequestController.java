package facheritosfrontendapp.controller.sale;

import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryVehicleController;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import facheritosfrontendapp.objectRowView.saleRowView.SaleRequestRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SaleRequestController implements Initializable {

    @FXML
    private TableView<SaleRequestRowView> saleRequestTableview;

    @FXML
    private TableColumn<SaleRequestRowView, String> customerCol;

    @FXML
    private TableColumn<SaleRequestRowView, String> headquarterCol;

    @FXML
    private TableColumn<SaleRequestRowView, VBox> optionsCol;

    @FXML
    private TableColumn<SaleRequestRowView, String> paymentMethodCol;

    @FXML
    private TableColumn<SaleRequestRowView, String> saleDateCol;

    @FXML
    private TableColumn<SaleRequestRowView, Integer> saleNumberCol;

    @FXML
    private TableColumn<SaleRequestRowView, String> sellerCol;


    private SaleEndpoint saleEndpoint;

    private ArrayList<SaleRequestRowView> saleRequestRowViewList;

    private DashboardController dashboardController;

    public SaleRequestController(){
        saleEndpoint = new SaleEndpoint();
        saleRequestRowViewList = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }


    @FXML
    protected void backArrowClicked() throws IOException {
        dashboardController.changeContent("sales/chooseOption");
    }
    /**
     * showSaleRequests: void -> void
     * Purpose: shows the sales requests on the tableview
     */
    public void showSaleRequests(){
        //Every update to the GUI from the DB must be encapsuled by Thread
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> vehiclesCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSaleRequests());

            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                vehiclesCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            //Set the tableview data visually
                            setSaleRequestsData(resultSet);
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

    public void setSaleRequestsData(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            SaleRequestRowView saleRequestRow = new SaleRequestRowView
                    (resultSet.getString("first_name")+ " "+ resultSet.getString("last_name"),
                            resultSet.getString("customer_firstname") + " " + resultSet.getString("customer_lastname"),
                            resultSet.getInt("id_sale"), resultSet.getString("sale_date"),
                            resultSet.getString("payment_method"), resultSet.getString("name"));
            //Add the data of every vehicle to the array
            saleRequestRowViewList.add(saleRequestRow);
        }

        //Set the handle events for the boxes
        for(Integer i = 0; i < saleRequestRowViewList.size(); i++){
            saleRequestRowViewList.get(i).getOptionsHBox().setOnMouseClicked(this::handleSaleRequestOptionHbox);
        }
        /**
         * Setting up the columns of the table,
         * Comment: the value passed on the 'PropertyValueFactory' MUST match with the attributes of the VehicleRowView object
         * */
        saleNumberCol.setCellValueFactory(new PropertyValueFactory<>("idSale"));
        sellerCol.setCellValueFactory(new PropertyValueFactory<>("sellerName"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        saleDateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        headquarterCol.setCellValueFactory(new PropertyValueFactory<>("headquarterName"));
        optionsCol.setCellValueFactory(new PropertyValueFactory<>("options"));

        saleRequestTableview.setItems(FXCollections.observableArrayList(saleRequestRowViewList));

    }
    /**
     * handleSaleRequestOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of the View More label
     */
    private void handleSaleRequestOptionHbox(MouseEvent mouseEvent)  {
        for(Integer i = 0; i < saleRequestRowViewList.size(); i++){
            if(mouseEvent.getSource() == saleRequestRowViewList.get(i).getOptionsHBox()){
                //Here we will load the component to view, edit and delete the vehicle
                /*
                inventoryVehicleController = (InventoryVehicleController) dashboardController.changeContent("inventory/inventoryVehicle", true);
                inventoryVehicleController.showVehicleData(vehicleRowViewList.get(i).getIdCar());*/
            }
        }
    }

}
