package facheritosfrontendapp.controller.sale;


import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;

import facheritosfrontendapp.objectRowView.saleRowView.SaleRowView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;

public class SaleController implements Initializable {

    private DashboardController dashboardController;

    private SaleEndpoint saleEndpoint;

    @FXML
    private TableColumn<SaleRowView, Integer> colId;

    @FXML
    private TableColumn<SaleRowView, String> colSeller;

    @FXML
    private TableColumn<SaleRowView, String> colClient;

    @FXML
    private TableColumn<SaleRowView, Date> colDate;

    @FXML
    private TableColumn<SaleRowView, String> colPay;

    @FXML
    private TableColumn<SaleRowView, String> colHeadq;

    @FXML
    private TableColumn<SaleRowView, Double> colPriceSale;

    @FXML
    private TableColumn<SaleRowView, VBox> colOptions;

    private ObservableList<SaleRowView> saleObList;

    private AddSaleController addSaleController;
    @FXML
    private TableView saleTableView;

    private SaleSingleViewController saleSingleViewController;


    private ArrayList<SaleRowView> saleRowsArray;


    public SaleController()  {
        saleEndpoint = new SaleEndpoint();
        saleRowsArray = new ArrayList<>();
        saleSingleViewController = new SaleSingleViewController();
        addSaleController =  new AddSaleController();
        saleObList = FXCollections.observableArrayList();
    }

    public void alter(){;
        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Solicitud enviada al gerente", OK);
        success.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();

    }

    @FXML
    protected void addSaleClicked() throws IOException, SQLException {
        addSaleController = (AddSaleController) dashboardController.changeContent("sales/salesAdd", true);
        addSaleController.setSeller();
        addSaleController.setView();
        addSaleController.showSaleCars();
    }




    private void handleOptionLabel(MouseEvent mouseEvent) {
        for(Integer i = 0; i < saleRowsArray.size(); i++){
            if(mouseEvent.getSource() == saleRowsArray.get(i).getEditLabel()){
                try {
                    saleSingleViewController = (SaleSingleViewController) dashboardController.changeContent("sales/salesSingleView", true);
                    saleSingleViewController.showSaleData(saleRowsArray.get(i).getIdSale());
                    saleSingleViewController.showQuantity(saleRowsArray.get(i).getIdSale());
                    saleSingleViewController.showSaleCars(saleRowsArray.get(i).getIdSale());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public  void showSales() throws ExecutionException, InterruptedException {
        //Every update to the GUI from the DB must be encapsuled by Thread
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> salesCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSales());

            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                salesCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            setDataTable(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (FileNotFoundException e) {
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

    @FXML
    protected void backArrowClicked() throws IOException {
       dashboardController.changeContent("sales/chooseOption");
    }
    public void setDataTable(ResultSet resultSet) throws SQLException, FileNotFoundException {

       /* (Integer idSale, String nameSeller, String nameClient, Date dateSeller, String paymentMethod,String headquarter,
                Double priceSale)*/
        while(resultSet.next()){
            //Create the object that will contain all the data shown on the table
            SaleRowView sale = new SaleRowView(resultSet.getInt("id_sale"), resultSet.getString("name_seller"),
                    resultSet.getString("name_client"), resultSet.getDate("sale_date"),
                    resultSet.getString("payment_method"), resultSet.getString("name"),
                    new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString());
            saleRowsArray.add(sale); //Add every element to the array.
        }

        //Set the handle events for the labels
        for(Integer i = 0; i < saleRowsArray.size(); i++){
            saleRowsArray.get(i).getEditLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        //Add every element from our array to the observable list array that will show on the table
        for(Integer i = 0; i < saleRowsArray.size(); i++){
            saleObList.add(saleRowsArray.get(i));
        }

        colId.setCellValueFactory(new PropertyValueFactory("idSale"));
        colSeller.setCellValueFactory(new PropertyValueFactory("nameSeller"));
        colClient.setCellValueFactory(new PropertyValueFactory("nameClient"));
        colDate.setCellValueFactory(new PropertyValueFactory("dateSeller"));
        colPay.setCellValueFactory(new PropertyValueFactory("paymentMethod"));
        colHeadq.setCellValueFactory(new PropertyValueFactory("headquarter"));
        colPriceSale.setCellValueFactory(new PropertyValueFactory("priceSale"));
        colOptions.setCellValueFactory(new PropertyValueFactory("options"));

        saleTableView.setItems(saleObList);
    }
}