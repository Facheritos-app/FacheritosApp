package facheritosfrontendapp.controller.sale;


import backend.dto.saleDTO.SaleDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.saleEndpoint.SaleEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryController;
import facheritosfrontendapp.objectRowView.saleRowView.SaleCarRowView;
import facheritosfrontendapp.objectRowView.saleRowView.SaleRowView;
import facheritosfrontendapp.objectRowView.saleRowView.SaleSingleRowView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
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
    private Label head;

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
    private TableView carTableView;

    @FXML
    private TableColumn<SaleRowView, Integer> colId;

    @FXML
    private TableColumn<SaleRowView, String> colModel;

    @FXML
    private TableColumn<SaleRowView, String> colColor;

    @FXML
    private TableColumn<SaleRowView,Double> colPrice;
    @FXML
    private TableColumn<SaleRowView,Integer> colQuantity;
    @FXML
    private TableColumn<SaleRowView, VBox> colOptions;

    private ObservableList<SaleSingleRowView> saleSingleObList;

    private DashboardController dashboardController;

    private ArrayList<SaleSingleRowView> saleSingleRowsArray;

    private ArrayList<SaleCarRowView> saleCarRowsArray;

    private SaleEndpoint saleEndpoint;

    private SaleController saleController;

    private EditSaleController editSaleController;

    private SaleDTO saleDTOCurrent;

    public SaleSingleViewController() {
        saleEndpoint = new SaleEndpoint();
        saleSingleObList = FXCollections.observableArrayList();
        saleSingleRowsArray = new ArrayList<>();
        editSaleController =  new EditSaleController();
        saleDTOCurrent = new SaleDTO();
        saleCarRowsArray = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        head.setVisible(false);
    }

    @FXML
    protected void backArrowClicked() throws IOException, ExecutionException, InterruptedException {
        saleController = (SaleController) dashboardController.changeContent("sales/sales");
        saleController.showSales();
    }

    @FXML
    protected void editOnClicked() throws IOException, SQLException {
        editSaleController = (EditSaleController) dashboardController.changeContent("sales/salesEdit", true);
        editSaleController.setData(Integer.valueOf(idNumber.getText()));
        editSaleController.setView();
        editSaleController.showSaleCars(Integer.valueOf(head.getText()));
        editSaleController.showSaleCarsSell(Integer.valueOf(idNumber.getText()));
        editSaleController.getCurrentSale(saleDTOCurrent,saleCarRowsArray);

    }

    public void showSaleData(Integer idSale){
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
        head.setText(String.valueOf(resultSet.getInt("id_headquarter")));
        saleDTOCurrent.setId_headquarter(resultSet.getInt("id_headquarter"));

        labelCosto.setText(resultSet.getString("price"));
        idHeadq.setText(resultSet.getString("name_headq"));
        dateSale.setText(resultSet.getString("sale_date"));

        payMethod.setText(resultSet.getString("name_method"));
        saleDTOCurrent.setPayment_method(resultSet.getString("name_method"));

        confirmation.setText(resultSet.getString("confirmation_status"));

        ccSeller.setText(resultSet.getString("cc_seller"));
        saleDTOCurrent.setCcSeller(resultSet.getString("cc_seller"));
        saleDTOCurrent.setId_worker(resultSet.getInt("id_worker"));

        nameSeller.setText(resultSet.getString("name_seller"));
        emailSeller.setText(resultSet.getString("email_seller"));

        ccClient.setText(resultSet.getString("cc_client"));
        saleDTOCurrent.setCcClient(resultSet.getString("cc_client"));
        saleDTOCurrent.setId_customer(Integer.valueOf(resultSet.getString("idClient")));

        nameClient.setText(resultSet.getString("name_client"));
        cellphoneClient.setText(resultSet.getString("cellphone_client"));
        emailClient.setText(resultSet.getString("email_client"));
        cellphoneSeller.setText(resultSet.getString("cellphone_seller"));
    }

    public void showQuantity(Integer idSale){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getQunantityById(idSale));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setQuantity(resultSet);
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

    public void setQuantity(ResultSet resultSet) throws SQLException, IOException {

        labelCantidad.setText(String.valueOf(resultSet.getInt("sum")));

    }

    public void showSaleCars(Integer idSale){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSalesCar(idSale));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setDataCarTable(resultSet);
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

    private void handleOptionLabel(MouseEvent mouseEvent) {
        for(Integer i = 0; i < saleSingleRowsArray.size(); i++){
            if(mouseEvent.getSource() == saleSingleRowsArray.get(i).getEditLabel()){

            }
        }
    }

    public void setDataCarTable(ResultSet resultSet) throws SQLException, FileNotFoundException {

       /* (Integer idSale, String nameSeller, String nameClient, Date dateSeller, String paymentMethod,String headquarter,
                Double priceSale)*/
        while(resultSet.next()){
            //Create the object that will contain all the data shown on the table
            SaleSingleRowView car = new SaleSingleRowView(resultSet.getInt("id_car"), resultSet.getString("description"),
                    resultSet.getString("color"), resultSet.getDouble("price"),
                    resultSet.getInt("quantity"));
            saleSingleRowsArray.add(car); //Add every element to the array.

            SaleCarRowView car2 = new SaleCarRowView(resultSet.getInt("id_car"), resultSet.getString("description"), resultSet.getString("color"),
                    resultSet.getDouble("price"),
                    resultSet.getInt("quantity"),resultSet.getString("assemble_year"));
            saleCarRowsArray.add(car2);

        }
/*
        while(resultSet.next()){
            //Create the object that will contain all the data shown on the table
            SaleCarRowView car = new SaleCarRowView(resultSet.getInt("id_car"), resultSet.getString("description"), resultSet.getString("color"),
                    resultSet.getDouble("price"),
                    resultSet.getInt("quantity"),resultSet.getString("assemble_year"));
            saleCarRowsArray.add(car);
        }*/
        //Set the handle events for the labels
        for(Integer i = 0; i < saleSingleRowsArray.size(); i++){
            saleSingleRowsArray.get(i).getEditLabel().setOnMouseClicked(this::handleOptionLabel);
        }



        //Add every element from our array to the observable list array that will show on the table
        for(Integer i = 0; i < saleSingleRowsArray.size(); i++){
            saleSingleObList.add(saleSingleRowsArray.get(i));
        }

        colId.setCellValueFactory(new PropertyValueFactory("idCar"));
        colModel.setCellValueFactory(new PropertyValueFactory("model"));
        colColor.setCellValueFactory(new PropertyValueFactory("color"));
        colPrice.setCellValueFactory(new PropertyValueFactory("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory("quantity"));
        //colOptions.setCellValueFactory(new PropertyValueFactory("options"));

        carTableView.setItems(saleSingleObList);
    }


}