package facheritosfrontendapp.controller.inventory;

import backend.dto.inventoryDTO.VehicleDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.ColorEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.inventoryEndpoint.ModelEndpoint;
import facheritosfrontendapp.ComboBoxView.ColorView;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.ComboBoxView.ModelView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.validator.addInventory.AddVehicleValidator;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

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

    private InventoryEndpoint inventoryEndpoint;

    private Integer idCar;

    private String quantityText;

    private String headquarterText;

    private String modelText;

    private String colorText;

    private String yearText;

    private String imageLinkText;

    private Integer currentIdHeadquarter;

    private AddVehicleValidator inputValidator;

    public InventoryEditVehicleController() {
        headquarterEndpoint = new HeadquarterEndpoint();
        colorEndpoint = new ColorEndpoint();
        modelEndpoint = new ModelEndpoint();
        inventoryEndpoint = new InventoryEndpoint();
        headquarterComboboxList = new ArrayList<>();
        modelComboboxList = new ArrayList<>();
        colorComboboxList = new ArrayList<>();
        inputValidator = new AddVehicleValidator();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();

    }

    @FXML
    protected void backArrowClicked() throws IOException {
        cancelClicked();
    }

    @FXML
    protected void cancelClicked() throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            inventoryVehicleController = (InventoryVehicleController) dashboardController.changeContent("inventory/inventoryVehicle", true);
            inventoryVehicleController.showVehicleData(idCar, currentIdHeadquarter);
        }
    }

    @FXML
    protected void saveClicked(MouseEvent event) {
        if (allValidations()) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                        if (clickedButton.get() == YES) {
                            VehicleDTO vehicleDTO = createVehicleObject();
                            //DB call to save worker
                            new Thread(() -> {
                                Boolean result = null;
                                try {
                                    //If the headquarter is not being modified
                                    if(vehicleDTO.getIdHeadquarter() == currentIdHeadquarter) {

                                        result = CompletableFuture.supplyAsync(() -> inventoryEndpoint.changeOnlyVehicleQuantity(vehicleDTO)).get();
                                    }
                                    //If the headquarter is being modified
                                    else {
                                        result = CompletableFuture.supplyAsync(() -> {
                                            try {
                                                return inventoryEndpoint.completeEditVehicle(vehicleDTO, currentIdHeadquarter);
                                            } catch (Exception e) {
                                                Platform.runLater(() -> {
                                                    headquarterLabel.setText("Verifique si ya existe este vehículo en la misma sede");
                                                    inputValidator.setErrorStyles(headquarterCombobox, headquarterLabel);
                                                });
                                                throw new RuntimeException(e);
                                            }
                                        }).get();
                                    }
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                                if (result) {
                                    Platform.runLater(() -> {
                                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Vehículo modificado exitosamente", OK);
                                        success.show();
                                        //Go to main user view
                                        try {
                                            inventoryVehicleController = (InventoryVehicleController) dashboardController.changeContent("inventory/inventoryVehicle", true);
                                            inventoryVehicleController.showVehicleData(idCar, vehicleDTO.getIdHeadquarter());
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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        }

    }
    public VehicleDTO createVehicleObject(){
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setQuantity(Integer.parseInt(quantity.getText()));
        vehicle.setAssembleYear(yearChoicebox.getSelectionModel().getSelectedItem());
        vehicle.setIdColor(colorCombobox.getSelectionModel().getSelectedItem().getIdColor());
        vehicle.setIdHeadquarter(headquarterCombobox.getSelectionModel().getSelectedItem().getIdHeadquarter());
        vehicle.setIdModel(modelCombobox.getSelectionModel().getSelectedItem().getIdModel());
        vehicle.setImageLink(imageLink.getText());
        vehicle.setIdCar(idCar);
        return vehicle;
    }
    public boolean allValidations(){
        cleanErrors();
        Boolean everythingCorrect = true;
        if (colorCombobox.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            colorLabel.setText("Seleccione un color");
            inputValidator.setErrorStyles(colorCombobox, colorLabel);
        }
        if (modelCombobox.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            modelLabel.setText("Seleccione un modelo de vehículo");
            inputValidator.setErrorStyles(modelCombobox, modelLabel);
        }
        if (yearChoicebox.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            assembleYearLabel.setText("Seleccione un año de ensamblaje");
            inputValidator.setErrorStyles(yearChoicebox, assembleYearLabel);

        }
        if (!inputValidator.quantity(quantity, quantityLabel, "Escriba solamente números")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(quantity, quantityLabel);
        }
        if (headquarterCombobox.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            headquarterLabel.setText("Por favor, indique la sede del vehículo");
            inputValidator.setErrorStyles(headquarterCombobox, headquarterLabel);
        }
        if (!inputValidator.imageLink(imageLink, imageLinkLabel, "Escriba una dirección URL")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(imageLink, imageLinkLabel);
        }
        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        colorCombobox.setStyle("");
        colorLabel.setText("");
        modelCombobox.setStyle("");
        modelLabel.setText("");
        yearChoicebox.setStyle("");
        assembleYearLabel.setText("");
        quantity.setStyle("");
        quantityLabel.setText("");
        headquarterCombobox.setStyle("");
        headquarterLabel.setText("");
        imageLink.setStyle("");
        imageLinkLabel.setText("");
    }

    /**
     * setData: String x5 -> void
     * Purpose: this method receives the data already retrieved from the DB when viewing the vehicle.
     * The vehicle view passes the data so that the Edit Vehicle View can show it.
     */
    public void setData(Integer idCar, String model, String color, String assemblyYear, String headquarter, String quantity, String imageLink) throws ExecutionException, InterruptedException {

        this.idCar = idCar;
        this.modelText = model;
        this.colorText = color;
        this.yearText = assemblyYear;
        this.headquarterText = headquarter;
        this.quantityText = quantity;
        this.imageLinkText = imageLink;


    }

    public void selectItems() {
        this.quantity.setText(quantityText);
        this.imageLink.setText(imageLinkText);
        this.headquarterCombobox.getSelectionModel().select(findHeadquarterByName(headquarterText));
        this.modelCombobox.getSelectionModel().select(findModelByName(modelText));
        this.colorCombobox.getSelectionModel().select(findColorByName(colorText));
        this.yearChoicebox.getSelectionModel().select(yearText);

    }

    /**
     * findHeadquarterByName: String -> HeadquarterView
     * Purpose: This method finds a headquarter by its name.
     * .
     */
    public HeadquarterView findHeadquarterByName(String name) {
        for (Integer i = 0; i < headquarterComboboxList.size(); i++) {
            if (name.equals(headquarterComboboxList.get(i).getName())) {
                return headquarterComboboxList.get(i);
            }
        }
        return new HeadquarterView(-100, "");
    }

    /**
     * findModelByName: String -> ModelView
     * Purpose: This method finds a headquarter by its name.
     * .
     */
    public ModelView findModelByName(String model) {
        for (Integer i = 0; i < modelComboboxList.size(); i++) {
            if (model.equals(modelComboboxList.get(i).getName())) {
                return modelComboboxList.get(i);
            }
        }
        return new ModelView(-100, "");
    }

    /**
     * findCololrByName: String -> ColorView
     * Purpose: This method finds a headquarter by its name.
     * .
     */
    public ColorView findColorByName(String color) {
        for (Integer i = 0; i < colorComboboxList.size(); i++) {
            if (color.equals(colorComboboxList.get(i).getColor())) {
                return colorComboboxList.get(i);
            }
        }
        return new ColorView(-100, "");
    }

    /**
     * showForm: void -> void
     * Purpose: This method contains all the steps to show all the data in the comboboxes
     */
    public void showForm()  {
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());
            CompletableFuture<Map<Boolean, ResultSet>> modelsCall = CompletableFuture.supplyAsync(() -> modelEndpoint.getModels());
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
                Platform.runLater(()->{
                    setAssembleYearCombobox();
                    selectItems();
                    this.currentIdHeadquarter = findHeadquarterByName(headquarterText).getIdHeadquarter();
                });
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
        yearChoicebox.setItems(FXCollections.observableArrayList("2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014"));
    }
}
