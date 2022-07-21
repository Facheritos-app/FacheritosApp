package facheritosfrontendapp.controller.order;

import backend.endpoints.orderEndpoint.OrderEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderSingleViewController implements Initializable {

    private OrderEndpoint orderEndpoint;

    private DashboardController dashboardController;

    private OrderController orderController;

    //Here are all the @FXML components

    //Order Summary table
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
    private Label statusLabel;

    @FXML
    private TextField partField;


    public OrderSingleViewController() {
        orderEndpoint = new OrderEndpoint();
    }


    @FXML
    /**
     * editAction: event -> void
     * Purpose: shows the edit order view by pressing the 'Editar orden' button.
     */
    protected void editAction() {

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
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the form possible
     */
    public void showForm(Integer id) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>>
                    userCall = CompletableFuture.supplyAsync(() -> orderEndpoint.getOrderByIdForView(id));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setForm(resultSet);
                            } catch (SQLException e) {
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


    public void setForm(ResultSet resultSet) throws SQLException {
        orderLabel.setText(resultSet.getString("id_job_order"));
        nameLabel.setText("   "+resultSet.getString("person_name"));
        ccLabel.setText("   "+resultSet.getString("cc"));
        cellphoneLabel.setText("   "+resultSet.getString("cellphone"));
        headquarterLabel.setText("   "+resultSet.getString("headquarter_name"));
        creationDateLabel.setText("   "+resultSet.getDate("created_at").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        dueDateLabel.setText("   "+resultSet.getDate("due_date").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        priceLabel.setText("   "+resultSet.getString("price"));
        statusLabel.setText("   "+resultSet.getString("status"));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
    }
}