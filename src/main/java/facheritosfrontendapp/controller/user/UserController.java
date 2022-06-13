package facheritosfrontendapp.controller.user;

import backend.endpoints.workerEndpoint.WorkerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.headquarterRowView.WorkerRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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

    private WorkerEndpoint workerEndpoint;
    private ArrayList<WorkerRowView> workerRowsArray;

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


    public UserController(){
        workerEndpoint = new WorkerEndpoint();
        workerRowsArray = new ArrayList<>();
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


    /**
     * handleOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of both the view, edit and delete label.
     */
    private void handleOptionLabel(MouseEvent mouseEvent) {
        for(Integer i = 0; i < workerRowsArray.size(); i++){
            if(mouseEvent.getSource() == workerRowsArray.get(i).getViewLabel()){
                //Here we will load the component to view the worker
            }
            if(mouseEvent.getSource() == workerRowsArray.get(i).getEditLabel()){
                //Here we will load the component to edit a worker
            }
            if(mouseEvent.getSource() == workerRowsArray.get(i).getDeleteLabel()){
                //Here we will load the component to delete a worker
            }

        }
    }

    public void setData(ResultSet resultSet) throws SQLException {
        //As long as there are records left to show
        while(resultSet.next()){
            WorkerRowView workerRow = new WorkerRowView(resultSet.getString("cc"), resultSet.getString("first_name"),
                                    resultSet.getString("last_name"), resultSet.getString("rol_person"),
                                    resultSet.getString("name"));
            workerRowsArray.add(workerRow);
            }

        //Set the handle events for the labels
        for(Integer i = 0; i < workerRowsArray.size(); i++){
                workerRowsArray.get(i).getViewLabel().setOnMouseClicked(this::handleOptionLabel);
                workerRowsArray.get(i).getEditLabel().setOnMouseClicked(this::handleOptionLabel);
                workerRowsArray.get(i).getDeleteLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colHeadquarter.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("options"));

        userTableView.setItems(FXCollections.observableArrayList(workerRowsArray));

    }

}
