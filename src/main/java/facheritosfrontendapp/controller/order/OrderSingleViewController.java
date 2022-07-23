package facheritosfrontendapp.controller.order;

import backend.endpoints.orderEndpoint.OrderEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.inventoryRowView.PartRowView;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderSingleViewController implements Initializable {

    private final OrderEndpoint orderEndpoint;

    private DashboardController dashboardController;

    private OrderController orderController;

    private OrderEditController orderEditController;

    private final ArrayList<PartRowView> orderPartsRowViewList;

    private Integer idOrder;

    //Here are all the @FXML components

    //Order Summary table
    @FXML
    private TableView orderSummaryTableview;
    @FXML
    private TableColumn<VehicleRowView, String> colNamePartS;
    @FXML
    private TableColumn<VehicleRowView, Double> colPricePartS;
    @FXML
    private TableColumn<VehicleRowView, Integer> colQuantityPartS;

    @FXML
    private Label orderLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label ccLabel;

    @FXML
    private Label cellphoneLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    private Label headquarterLabel;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label statusLabel;

    public OrderSingleViewController() {
        orderEndpoint = new OrderEndpoint();
        orderPartsRowViewList = new ArrayList<>();
    }

    private void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    /*
      editAction: event -> void
      Purpose: shows the edit order view by pressing the 'Editar orden' button.
     */
    @FXML
    protected void editAction() throws IOException {
        orderEditController = (OrderEditController) dashboardController.changeContent("orders/ordersEdit", true);
        orderEditController.showForm(idOrder);
    }

    /**
     * backToOrdersClicked: void -> void
     * Purpose: when the backArrow is clicked  it returns to the orders view
     */
    @FXML
    protected void backToOrdersClicked() throws IOException {
        orderController = (OrderController) dashboardController.changeContent("orders/orders");
        orderController.showOrders();
    }

    /**
     * setOrderTotalPrice: Integer -> void
     * Purpose: sets the total price and quantity of parts of an order
     */
    public void setOrderTotalPrice(Integer id) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>>
                    userCall = CompletableFuture.supplyAsync(() -> orderEndpoint.getTotalPriceAndQuantity(id));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setTotalPriceAndQuantity(resultSet);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setTotalPriceAndQuantity(ResultSet resultSet) throws SQLException {
        totalPriceLabel.setText("" + resultSet.getDouble("total"));
        quantityLabel.setText(resultSet.getString("quantity"));
    }

    /**
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the form possible
     */
    public void showForm(Integer id) {
        setIdOrder(id);
        setOrderTotalPrice(id);

        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>>
                    userCall = CompletableFuture.supplyAsync(() -> orderEndpoint.getOrderById(id));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setForm(resultSet);
                                showOrderParts();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    public void setForm(ResultSet resultSet) throws SQLException {
        orderLabel.setText(resultSet.getString("id_job_order"));
        nameLabel.setText("   " + resultSet.getString("person_name"));
        ccLabel.setText("   " + resultSet.getString("cc"));
        cellphoneLabel.setText("   " + resultSet.getString("cellphone"));
        headquarterLabel.setText("   " + resultSet.getString("headquarter_name"));
        creationDateLabel.setText("   " + resultSet.getDate("created_at").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        dueDateLabel.setText("   " + resultSet.getDate("due_date").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        priceLabel.setText("   " + resultSet.getDouble("price"));
        statusLabel.setText("   " + resultSet.getString("status"));
    }

    /**
     * showParts: void -> void
     * Purpose: fills the order parts table.
     */
    public void showOrderParts() {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() ->
                    orderEndpoint.getOrderParts(idOrder));
            try {
                partsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setOrderPartsData(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setOrderPartsData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            PartRowView orderPartRow = new PartRowView(resultSet.getString("name"),
                    new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    "", resultSet.getInt("quantity"),
                    resultSet.getInt("id_part"));

            //Add the data of every vehicle to the array
            orderPartsRowViewList.add(orderPartRow);
        }

        colNamePartS.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPricePartS.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantityPartS.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderSummaryTableview.setItems(FXCollections.observableArrayList(orderPartsRowViewList));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
    }
}