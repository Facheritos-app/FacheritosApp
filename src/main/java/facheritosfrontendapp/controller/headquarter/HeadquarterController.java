package facheritosfrontendapp.controller.headquarter;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.sale.SaleSingleViewController;
import facheritosfrontendapp.objectRowView.headquarterRowView.HeadquarterRowView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HeadquarterController implements Initializable {

    private DashboardController dashboardController;

    private HeadquarterEndpoint headquarterEndpoint;

    private ObservableList<HeadquarterRowView> headquarterObList;

    @FXML
    private TableView headquarterTableView;

    @FXML
    private TableColumn<HeadquarterRowView, Integer> colId;

    @FXML
    private TableColumn<HeadquarterRowView, String> colName;

    @FXML
    private TableColumn<HeadquarterRowView, String> colCellphone;

    @FXML
    private TableColumn<HeadquarterRowView, String> colAddress;

    @FXML
    private TableColumn<HeadquarterRowView, String> colEmail;

    @FXML
    private TableColumn<HeadquarterRowView, String> colCity;

    @FXML
    private TableColumn<HeadquarterRowView, VBox> colOptions;

    private ArrayList<HeadquarterRowView> headquarterRowsArray;

    private EditHeadquarterController editHeadquarterController;

    @FXML
    protected void addHeadquarterClicked() throws IOException {
        dashboardController.changeContent("headquarters/headquartersAdd");
    }

    public HeadquarterController(){
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterRowsArray = new ArrayList<>();
        headquarterObList = FXCollections.observableArrayList();
        editHeadquarterController = new EditHeadquarterController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        /**
        try {
            this.showHeadquarters();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
         **/
    }

    /**
     * handleOptionLabel: mouseEvent -> void
     * Purpose: Listens to the events of both the edit and delete label.
     */
    private void handleOptionLabel(MouseEvent mouseEvent) {
        for(Integer i = 0; i < headquarterRowsArray.size(); i++){
            if(mouseEvent.getSource() == headquarterRowsArray.get(i).getEditLabel()){
                try {
                    editHeadquarterController = (EditHeadquarterController) dashboardController.changeContent("headquarters/headquartersEdit", true);
                    editHeadquarterController.showHeadquarterData(headquarterRowsArray.get(i).getIdHeadquarter());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(mouseEvent.getSource() == headquarterRowsArray.get(i).getDeleteLabel()){
                //Here we will load the component to delete a headquarter
            }
        }
    }

    /**
     * showHeaders: void -> void
     * Purpose: This method contains all the steps to show all the headquarters on the table.
     */

    public  void showHeadquarters() throws ExecutionException, InterruptedException {
        //Every update to the GUI from the DB must be encapsuled by Thread
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                headquartersCall.thenApply((response) -> {
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

    public void setDataTable(ResultSet resultSet) throws SQLException, FileNotFoundException {

        //As long as there are records left to show
        while(resultSet.next()){
            //Create the object that will contain all the data shown on the table
            HeadquarterRowView headquarter = new HeadquarterRowView(resultSet.getInt("id_headquarter"), resultSet.getInt("id_city"),
                                                                    resultSet.getString("name"), resultSet.getString("cellphone"),
                                                                    resultSet.getString("email"), resultSet.getString("address"),
                                                                     resultSet.getString("city_name"));
            headquarterRowsArray.add(headquarter); //Add every element to the array.
        }

        //Set the handle events for the labels
        for(Integer i = 0; i < headquarterRowsArray.size(); i++){
            headquarterRowsArray.get(i).getEditLabel().setOnMouseClicked(this::handleOptionLabel);
            headquarterRowsArray.get(i).getDeleteLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        //Add every element from our array to the observable list array that will show on the table
        for(Integer i = 0; i < headquarterRowsArray.size(); i++){
            headquarterObList.add(headquarterRowsArray.get(i));
        }

        /**
         * Setting up the columns of the table,
         * Comment: the value passed on the 'PropertyValueFactory' MUST match with the attributes of the HeadquarterRowView object
         */
        colId.setCellValueFactory(new PropertyValueFactory("idHeadquarter"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        colCellphone.setCellValueFactory(new PropertyValueFactory("cellphone"));
        colAddress.setCellValueFactory(new PropertyValueFactory("address"));
        colEmail.setCellValueFactory(new PropertyValueFactory("email"));
        colCity.setCellValueFactory(new PropertyValueFactory("city"));
        colOptions.setCellValueFactory(new PropertyValueFactory("options"));

        headquarterTableView.setItems(headquarterObList);
    }

}
