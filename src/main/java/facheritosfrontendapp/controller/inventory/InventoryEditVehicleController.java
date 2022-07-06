package facheritosfrontendapp.controller.inventory;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.ColorEndpoint;
import backend.endpoints.inventoryEndpoint.ModelEndpoint;
import facheritosfrontendapp.ComboBoxView.ColorView;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.ComboBoxView.ModelView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InventoryEditVehicleController implements Initializable {

    @FXML
    private Label assembleYearLabel;

    @FXML
    private Button cancelBtn;

    @FXML
    private ComboBox<ColorView> colorCombobox;

    @FXML
    private Label colorLabel;

    @FXML
    private Label header;

    @FXML
    private ComboBox<HeadquarterView> headquarterCombobox;

    @FXML
    private Label headquarterLabel;

    @FXML
    private TextField imageLink;

    @FXML
    private Label imageLinkLabel;

    @FXML
    private ComboBox<ModelView> modelCombobox;

    @FXML
    private Label modelLabel;

    @FXML
    private TextField quantity;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label salaryLabel;

    @FXML
    private Button saveBtn;

    @FXML
    private ChoiceBox<String> yearChoicebox;

    private DashboardController dashboardController;

    private InventoryVehicleController inventoryVehicleController;

    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private ArrayList<ModelView> modelComboboxList;
    
    private ArrayList<ColorView> colorComboboxList;

    private ModelEndpoint modelEndpoint;

    private ColorEndpoint colorEndpoint;

    private Integer idCar;

    public InventoryEditVehicleController(){
        headquarterEndpoint = new HeadquarterEndpoint();
        colorEndpoint = new ColorEndpoint();
        modelEndpoint = new ModelEndpoint();
        headquarterComboboxList = new ArrayList<>();
        modelComboboxList = new ArrayList<>();
        colorComboboxList = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();

    }

    @FXML
    protected void backArrowClicked(MouseEvent event) throws IOException {
        inventoryVehicleController = (InventoryVehicleController) dashboardController.changeContent("inventory/inventoryVehicle");
        inventoryVehicleController.showVehicleData(this.idCar);
    }

    @FXML
    protected void cancelClicked(MouseEvent event) {

    }

    @FXML
    protected void saveClicked(MouseEvent event) {

    }
    /**
    * setData: String x5 -> void
     * Purpose: this method receives the data already retrieved from the DB when viewing the vehicle.
     * The vehicle view passes the data so that the Edit Vehicle View can show it.
    *
    * */
    public void setData(Integer idCar, String model, String color, String assemblyYear, String headquarter, String quantity){
        this.quantity.setText(quantity);
        this.idCar = idCar;
    }

    /**
     * showForm: void -> void
     * Purpose: This method contains all the steps to show all the data in the comboboxes
     */
    public void showForm() throws ExecutionException, InterruptedException {
        showHeadquarters();
        showModels();
        showColors();
        setAssembleYearCombobox();
    }

    /**
     * showHeadquarters: void -> void
     * Purpose: This method contains all the steps to show all the headquarters in the combobox
     */
    public void showHeadquarters() throws ExecutionException, InterruptedException {

        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

            //Use the response from the BD to fill the combobox
            try {
                headquartersCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setHeadquarterCombobox(resultSet);
                        } catch (SQLException e) {
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

    /**
     * setHeadquarterCombobox: ResultSet -> void
     * Purpose: This method set the items of the headquarters combobox according to the DB
     */
    public void setHeadquarterCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarterCombobox.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    /**
     * showModels: void -> void
     * Purpose: This method contains all the steps to show all the models in the combobox
     */
    public void showModels() throws ExecutionException, InterruptedException {

        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> modelsCall = CompletableFuture.supplyAsync(() -> modelEndpoint.getModels());

            //Use the response from the BD to fill the combobox
            try {
                modelsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setModelCombobox(resultSet);
                        } catch (SQLException e) {
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
    /**
     * setModelCombobox: ResultSet -> void
     * Purpose: This method set the items of the models combobox according to the DB
     */
    public void setModelCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idModel = resultSet.getInt("id_model");
            String name = resultSet.getString("description");
            modelComboboxList.add(new ModelView(idModel, name));
        }
        modelCombobox.setItems(FXCollections.observableArrayList(modelComboboxList));
    }

    /**
     * showColors: void -> void
     * Purpose: This method contains all the steps to show all the colors in the combobox
     */
    public void showColors() throws ExecutionException, InterruptedException {

        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> colorsCall = CompletableFuture.supplyAsync(() -> colorEndpoint.getColors());

            //Use the response from the BD to fill the combobox
            try {
                colorsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setColorCombobox(resultSet);
                        } catch (SQLException e) {
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
    /**
     * setColorCombobox: ResultSet -> void
     * Purpose: This method set the items of the colors combobox according to the DB
     */
    public void setColorCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idColor = resultSet.getInt("id_color");
            String color = resultSet.getString("color");
            colorComboboxList.add(new ColorView(idColor, color));
        }
        colorCombobox.setItems(FXCollections.observableArrayList(colorComboboxList));
    }

    /**
     * setAssembleYearCombobox: ResultSet -> void
     * Purpose: This method set the items of the year combobox according to the DB
     */
    public void setAssembleYearCombobox() {
        yearChoicebox.setItems(FXCollections.observableArrayList("2022",  "2021",  "2020", "2019", "2018", "2017", "2016", "2015", "2014"));
    }
}
