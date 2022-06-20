package facheritosfrontendapp.controller.customer;
import backend.endpoints.customerEndpoint.CustomerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.customerRowView.CustomerRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CustomerController implements Initializable {

    private DashboardController dashboardController;

    private AddCustomerController addCustomerController;

    private CustomerSingleViewController customerSingleViewController;
    private CustomerEndpoint customerEndpoint;

    private ArrayList<CustomerRowView> customerRowsArray;

    //Customer tab components
    @FXML
    private TableView customerTableView;

    @FXML
    private TableColumn<CustomerRowView, String> colIdCustomer;

    @FXML
    private TableColumn<CustomerRowView, String> colFirstnameCustomer;

    @FXML
    private TableColumn<CustomerRowView, String> colLastnameCustomer;

    @FXML
    private TableColumn<CustomerRowView, String> colCellphoneCustomer;

    @FXML
    private TableColumn<CustomerRowView, VBox> colOptionsCustomer;


    public CustomerController(){
        customerEndpoint = new CustomerEndpoint();
        customerSingleViewController = new CustomerSingleViewController();
        customerRowsArray= new ArrayList<>();
    }

    @FXML
    protected void addCustomerAction() throws IOException {
        addCustomerController = (AddCustomerController) dashboardController.changeContent("customers/customersAdd");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    /**
     * showCustomers: void -> void
     * Purpose: shows the customers in the tableview
     */
    public void showCustomers(){

        new Thread(() -> {
            //Async call to the DB
            CompletableFuture<Map<Boolean, ResultSet>> customersCall = CompletableFuture.supplyAsync(() -> customerEndpoint.getCustomersForTableView());

            try {
                customersCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            setCustomersData(resultSet);
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
     * setCustomersData: ResultSet -> void
     * Purpose: showCustomers auxiliary, fill the customers table
     */
    public void setCustomersData(ResultSet resultSet) throws SQLException {
        //As long as there are records left to show
        while(resultSet.next()){
            CustomerRowView customerRow = new CustomerRowView(resultSet.getInt("id_person"), resultSet.getString("cc"), resultSet.getString("first_name"),
                    resultSet.getString("last_name"), resultSet.getString("cellphone"));
            customerRowsArray.add(customerRow);
        }

        //Set the handle events for the labels
        for(int i = 0; i < customerRowsArray.size(); i++){
            customerRowsArray.get(i).getOptionsLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        colIdCustomer.setCellValueFactory(new PropertyValueFactory<>("cc"));
        colFirstnameCustomer.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colLastnameCustomer.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colCellphoneCustomer.setCellValueFactory(new PropertyValueFactory<>("cellphone"));
        colOptionsCustomer.setCellValueFactory(new PropertyValueFactory<>("options"));

        customerTableView.setItems(FXCollections.observableArrayList(customerRowsArray));

    }

    /**
     * handleOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of both the view, edit and delete label.
     */
    private void handleOptionLabel(MouseEvent mouseEvent)  {
        for(int i = 0; i < customerRowsArray.size(); i++){
            if(mouseEvent.getSource() == customerRowsArray.get(i).getOptionsLabel()){
                //Here we will load the component to view the worker
                try {
                    customerSingleViewController = (CustomerSingleViewController) dashboardController.changeContent("customers/customersSingleView");
                    customerSingleViewController.showCustomer(customerRowsArray.get(i).getIdPerson());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}