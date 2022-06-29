package facheritosfrontendapp.controller.sale;

import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.saleRowView.SaleRequestSingleRowView;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class SaleRequestSingleViewController implements Initializable {
    @FXML
    private Button approveBtn;

    @FXML
    private Label customerLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label headquarterLabel;

    @FXML
    private Label paymentMethodLabel;

    @FXML
    private Button rejectBtn;

    @FXML
    private Label saleNumberLabel;

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private Label sellerLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private TableView vehicleQuantityTableview;

    private Double totalSale;

    @FXML
    private TableColumn<SaleRequestSingleRowView, Double> priceCol;

    @FXML
    private TableColumn<SaleRequestSingleRowView, Double> unitPriceCol;

    @FXML
    private TableColumn<SaleRequestSingleRowView, Integer> quantityCol;

    @FXML
    private TableColumn<SaleRequestSingleRowView, String> vehicleCol;

    private DashboardController dashboardController;

    private SaleRequestController saleRequestController;

    private SaleEndpoint saleEndpoint;

    private Integer idSale;


    private ArrayList<SaleRequestSingleRowView> saleRequestSingleRowViewList;

    public SaleRequestSingleViewController(){
        saleEndpoint = new SaleEndpoint();
        totalSale = 0.0;
    }

    @FXML
    protected void approveBtnClicked(MouseEvent event) throws IOException {
        approveRequest();
    }

    @FXML
    protected void rejectBtnClicked(MouseEvent event) throws IOException {
        rejectRequest();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        saleRequestSingleRowViewList = new ArrayList<>();
    }

    @FXML
    void backArrowClicked(MouseEvent event) throws IOException {
        saleRequestController = (SaleRequestController) dashboardController.changeContent("sales/salesRequests");
        saleRequestController.showSaleRequests();
    }
    /**
     * showSaleData: mouseEvent -> void
     * Purpose: Shows all the data of the sale in order to see if it should be approved
     */

    public void showSaleData(Integer idSale){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> tableSaleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getTableInSaleRequestById(idSale));
            CompletableFuture<Map<Boolean, ResultSet>> saleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSaleRequestById(idSale));

            try {
                tableSaleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setTableData(resultSet);
                                setTotalLabel();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return true;
                }).get();
                saleCall.thenApply((response) -> {
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

    protected void setTotalLabel(){
        totalLabel.setText(new BigDecimal(String.valueOf(totalSale)).toPlainString());
    }

    /**
     * setData: ResultSet -> void
     * Purpose: This method contains sets all the data from the specific sale
     */
    public void setData(ResultSet resultSet) throws SQLException, IOException {
        idSale = resultSet.getInt("id_sale");
        saleNumberLabel.setText(String.valueOf(idSale));
        customerLabel.setText(resultSet.getString("customer_firstname")+" "+resultSet.getString("customer_lastname"));
        dateLabel.setText(resultSet.getString("sale_date"));
        headquarterLabel.setText(resultSet.getString("name"));
        paymentMethodLabel.setText(resultSet.getString("payment_method"));
        sellerLabel.setText(resultSet.getString("first_name")+" "+resultSet.getString("last_name"));
    }
    /**
     * setTableData: ResultSet -> void
     * Purpose: This method contains sets all the data that should appear in the tableview
     */
    public void setTableData(ResultSet resultSet) throws SQLException {

        while(resultSet.next()){
            Integer quantity = resultSet.getInt("quantity");
            Double price = resultSet.getDouble("price");
            Double multipliedPrice = quantity * price;
            SaleRequestSingleRowView saleRow = new SaleRequestSingleRowView(resultSet.getInt("id_car"), resultSet.getInt("id_model"),
                    resultSet.getString("description"), quantity, new BigDecimal(String.valueOf(price)).toPlainString(), new BigDecimal(String.valueOf(multipliedPrice)).toPlainString());
            //Add the data of every vehicle to the array
            saleRequestSingleRowViewList.add(saleRow);
            totalSale+=multipliedPrice;
        }

        //Set the handle events for the boxes
        for(Integer i = 0; i < saleRequestSingleRowViewList.size(); i++){
            //saleRequestSingleRowViewList.get(i).get.setOnMouseClicked(this::handleVehicleOptionHbox);
        }
        /**
         * Setting up the columns of the table,
         * Comment: the value passed on the 'PropertyValueFactory' MUST match with the attributes of the VehicleRowView object
         * */

        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("multipliedPrice"));

        vehicleQuantityTableview.setItems(FXCollections.observableArrayList(saleRequestSingleRowViewList));

    }
    /**
     * approveRequest: void -> void
     * Purpose: changes the state of the sale to active
     */
    public void approveRequest() throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("sales/confirmationSale");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            //DB call to save worker
            new Thread(() -> {
                Boolean result = null;
                try {
                    result = CompletableFuture.supplyAsync(() -> saleEndpoint.changeStateConfirmation(Integer.valueOf(saleNumberLabel.getText()))).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if (result) {
                    Platform.runLater(() -> {
                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Venta aprobada exitosamente", OK);
                        success.show();
                        //Go to main user view
                        try {
                            saleRequestController = (SaleRequestController) dashboardController.changeContent("sales/salesRequests");
                            //Show sale requests
                            saleRequestController.showSaleRequests();
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

    /**
     * rejectRequest: void -> void
     * Purpose: changes the state of the sale to inactive
     */
    protected void rejectRequest() throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("sales/rejectSale");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            new Thread(() -> {
                Boolean result = null;
                AtomicReference<Boolean> result2 = null;
                try {
                    result = CompletableFuture.supplyAsync(() -> saleEndpoint.changeStateReject(idSale)).get();
                    result2 = changeCarsQuantity();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if (result && result2.get()) {
                    Platform.runLater(() -> {
                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Venta rechazada exitosamente", OK);
                        success.show();
                        //Go to main user view
                        try {
                            saleRequestController = (SaleRequestController) dashboardController.changeContent("sales/salesRequests");
                            //Show sale requests
                            saleRequestController.showSaleRequests();
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

    /**
     * rejectRequest: void -> AtomicReference<Boolean>
     * Purpose: changes the quantity of cars in the car_headquarter table.
     * If the sale has been rejected, the quantity of cars that were going to be sold must be recovered in the inventory
     */
    public AtomicReference<Boolean> changeCarsQuantity() {
        /////ava
        AtomicReference<Boolean> accumResult = new AtomicReference<>(true);
        for (Integer elem = 0; elem < saleRequestSingleRowViewList.size(); elem++) {
            //DB call to save worker
            Integer finalElem = elem;
            new Thread(() -> {
                Boolean result = null;
                try {
                    System.out.println("IdCar");
                    System.out.println(saleRequestSingleRowViewList.get(finalElem).getIdCar());
                    result = CompletableFuture.supplyAsync(() -> saleEndpoint.changeCarsQuantityReject(saleRequestSingleRowViewList.get(finalElem).getIdCar(),
                            saleRequestSingleRowViewList.get(finalElem).getQuantity())).get();
                    accumResult.set(accumResult.get() && result);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        }
        return accumResult;
    }
}
