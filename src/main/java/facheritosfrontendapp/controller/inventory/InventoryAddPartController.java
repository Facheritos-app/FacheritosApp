package facheritosfrontendapp.controller.inventory;

import backend.dto.inventoryDTO.PartDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.inventoryRowView.PartRowView;
import facheritosfrontendapp.validator.addInventory.AddPartValidator;
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

public class InventoryAddPartController implements Initializable {

    @FXML
    private Button cancelBtn;


    @FXML
    private TextArea description;


    @FXML
    private Label nameLabel;

    @FXML
    private Label header;

    @FXML
    private ComboBox<HeadquarterView> headquarter;

    @FXML
    private Label headquarterLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private TextField name;

    @FXML
    private TextField price;

    @FXML
    private TextField quantity;

    @FXML
    private Label salaryLabel;

    @FXML
    private Button saveBtn;

    @FXML
    private Label priceLabel;

    @FXML
    private Label descriptionLabel;

    private DashboardController dashboardController;

    private InventoryController inventoryController;

    private InventoryEndpoint inventoryEndpoint;

    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private AddPartValidator inputValidator;


    public InventoryAddPartController() {
        inventoryEndpoint = new InventoryEndpoint();
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
        inputValidator = new AddPartValidator();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }
    @FXML
    void backArrowClicked(MouseEvent event) {

    }

    @FXML
    void cancelClick(MouseEvent event) throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
            inventoryController.showInventory();
        }
    }


    @FXML
    void saveClicked(MouseEvent event) {
        if (allValidations()) {
            new Thread(() -> {
                PartDTO partDTO = createPartObject();
                Platform.runLater(() -> {
                    try {
                        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                        if (clickedButton.get() == YES) {
                            //DB call to save worker
                            new Thread(() -> {
                                Boolean result = null;
                                try {
                                    result = CompletableFuture.supplyAsync(() -> inventoryEndpoint.completeCreatePart(partDTO)).get();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                                if (result) {
                                    Platform.runLater(() -> {
                                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Repuesto agregado exitosamente", OK);
                                        success.show();
                                        //Go to main user view
                                        try {
                                            inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
                                            //Show inventory
                                            inventoryController.showInventory();
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

    public PartDTO createPartObject(){
        PartDTO part = new PartDTO();
        part.setName(name.getText());
        part.setDescription(description.getText());
        part.setQuantity(Integer.parseInt(quantity.getText()));
        part.setPrice(Double.parseDouble(price.getText()));
        part.setId_headquarter(headquarter.getSelectionModel().getSelectedItem().getIdHeadquarter());
        return part;
    }
    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the create-carPart form
     */
    public Boolean allValidations() {
        cleanErrors();
        Boolean everythingCorrect = true;
        if (!inputValidator.name(name, nameLabel, "No se permiten símbolos")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(name, nameLabel);
        }
        if (!inputValidator.price(price, priceLabel, "Escriba solamente números")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(price, priceLabel);
        }
        if (!inputValidator.quantity(quantity, quantityLabel, "Escriba solamente números")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(quantity, quantityLabel);
        }
        if (headquarter.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            headquarterLabel.setText("Por favor, indique la sede del repuesto");
            inputValidator.setErrorStyles(headquarter, headquarterLabel);
        }
        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        name.setStyle("");
        nameLabel.setText("");
        headquarterLabel.setText("");
        headquarter.setStyle("");
        quantity.setStyle("");
        quantityLabel.setText("");
        price.setStyle("");
        priceLabel.setText("");
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
        headquarter.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }


}
