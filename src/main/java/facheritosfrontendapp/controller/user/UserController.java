package facheritosfrontendapp.controller.user;

import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.customer.CustomerSingleViewController;
import facheritosfrontendapp.objectRowView.customerRowView.CustomerRowView;
import facheritosfrontendapp.objectRowView.headquarterRowView.WorkerRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
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

public class UserController implements Initializable {

    private DashboardController dashboardController;

    private AddUserController addUserController;

    private UserSingleViewController userSingleViewController;

    private CustomerSingleViewController customerSingleViewController;

    private WorkerEndpoint workerEndpoint;

    private CustomerEndpoint customerEndpoint;

    private ArrayList<WorkerRowView> workerRowsArray;

    private ArrayList<CustomerRowView> customerRowsArray;

    //All the FXML imported components are here
    @FXML
    private TabPane usersTabpane;
    @FXML
    private TableView userTableView;
    @FXML
    private TableColumn<WorkerRowView, String> colId;
    @FXML
    private TableColumn<WorkerRowView, String> colFirstname;
    @FXML
    private TableColumn<WorkerRowView, String> colLastname;
    @FXML
    private TableColumn<WorkerRowView, String> colRol;
    @FXML
    private TableColumn<WorkerRowView, String> colHeadquarter;
    @FXML
    private TableColumn<WorkerRowView, VBox> colOptions;

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


    public UserController(){
        workerEndpoint = new WorkerEndpoint();
        customerEndpoint = new CustomerEndpoint();
        userSingleViewController = new UserSingleViewController();
        workerRowsArray = new ArrayList<>();
        customerRowsArray= new ArrayList<>();
    }

    @FXML
    protected void addUserClicked() throws IOException {
        addUserController = (AddUserController) dashboardController.changeContent("users/usersAdd", true);
        addUserController.setView();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        usersTabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    //For workers

    public void showWorkers(){

        new Thread(() -> {
            //Async call to the DB
            CompletableFuture<Map<Boolean, ResultSet>> workersCall = CompletableFuture.supplyAsync(() -> workerEndpoint.getWorkersForTableView());

            try {
                workersCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            setData(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true; //Returns true because we're using thenApply.
                }).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setData(ResultSet resultSet) throws SQLException {
        //As long as there are records left to show
        while(resultSet.next()){
            WorkerRowView workerRow = new WorkerRowView(resultSet.getInt("id_person"), resultSet.getString("cc"), resultSet.getString("first_name"),
                                    resultSet.getString("last_name"), resultSet.getString("rol_person"),
                                    resultSet.getString("name"));
            workerRowsArray.add(workerRow);
            }

        //Set the handle events for the labels
        for(int i = 0; i < workerRowsArray.size(); i++){
                workerRowsArray.get(i).getOptionsLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colHeadquarter.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("options"));

        userTableView.setItems(FXCollections.observableArrayList(workerRowsArray));

    }

    //For customers
    /**
     * showCustomers: void -> void
     * Purpose: shows the customers in the customers tab
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
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
        for(int i = 0; i < workerRowsArray.size(); i++){
            if(mouseEvent.getSource() == workerRowsArray.get(i).getOptionsLabel()){
                //Here we will load the component to view, edit and delete the worker
                try {
                    userSingleViewController = (UserSingleViewController) dashboardController.changeContent("users/usersSingleView");
                    userSingleViewController.showForm(workerRowsArray.get(i).getIdPerson());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

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
