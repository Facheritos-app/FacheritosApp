package facheritosfrontendapp.controller.order;
import backend.endpoints.orderEndpoint.OrderEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.orderRowView.OrderRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderController implements Initializable {

    private DashboardController dashboardController;

    private OrderEndpoint orderEndpoint;

    private ArrayList<OrderRowView> orderRowsArray;

    //Customer tab components
    @FXML
    private TableView orderTableView;

    @FXML
    private TableColumn<OrderRowView, String> colId;

    @FXML
    private TableColumn<OrderRowView, String> colSeat;

    @FXML
    private TableColumn<OrderRowView, String> colCustomerId;

    @FXML
    private TableColumn<OrderRowView, String> colDueDate;

    @FXML
    private TableColumn<OrderRowView, String> colStatus;

    @FXML
    private TableColumn<OrderRowView, VBox> colOptions;


    public OrderController(){
        orderEndpoint = new OrderEndpoint();
        orderRowsArray= new ArrayList<>();
    }

    @FXML
    protected void addOrderAction() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    /**
     * showOrders: void -> void
     * Purpose: shows the orders in the tableview.
     */
    public void showOrders(){

        new Thread(() -> {
            //Async call to the DB
            CompletableFuture<Map<Boolean, ResultSet>> customersCall = CompletableFuture.supplyAsync(() -> orderEndpoint.getOrdersForTableView());

            try {
                customersCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            setOrdersData(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true; //Returns true because we're using thenApply.
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * setOrdersData: ResultSet -> void
     * Purpose: showOrders auxiliary, fill the orders table.
     */
    public void setOrdersData(ResultSet resultSet) throws SQLException {
        //As long as there are records left to show
        while(resultSet.next()){
            OrderRowView orderRow = new OrderRowView(resultSet.getInt("id_job_order"), resultSet.getString("seat"), resultSet.getString("cc"),
                    resultSet.getDate("due_date"), resultSet.getString("status"));
            orderRowsArray.add(orderRow);
        }

        //Set the handle events for the labels
        for(int i = 0; i < orderRowsArray.size(); i++){
            orderRowsArray.get(i).getOptionsLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("seat"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("options"));

        orderTableView.setItems(FXCollections.observableArrayList(orderRowsArray));

    }

    /**
     * handleOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of both the view, edit and delete label.
     */
    private void handleOptionLabel(MouseEvent mouseEvent)  {
        for(int i = 0; i < orderRowsArray.size(); i++){
            if(mouseEvent.getSource() == orderRowsArray.get(i).getOptionsLabel()){
                //Here we will load the component to view the worker
            }
        }
    }


}
