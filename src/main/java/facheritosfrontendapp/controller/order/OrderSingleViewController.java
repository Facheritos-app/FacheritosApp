package facheritosfrontendapp.controller.order;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import backend.endpoints.orderEndpoint.OrderEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.inventory.InventoryPartController;
import facheritosfrontendapp.objectRowView.inventoryRowView.PartRowView;
import facheritosfrontendapp.objectRowView.inventoryRowView.VehicleRowView;
import facheritosfrontendapp.validator.addUserValidator.AddUserValidator;
import facheritosfrontendapp.views.MyDialogPane;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class OrderSingleViewController implements Initializable {

    private OrderEndpoint orderEndpoint;
    private HeadquarterEndpoint headquarterEndpoint;
    private InventoryEndpoint inventoryEndpoint;

    private final AddUserValidator inputValidator;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private ArrayList<PartRowView> partRowViewList;

    private DashboardController dashboardController;

    private OrderController orderController;

    private InventoryPartController inventoryPartController;


    //Here are all the @FXML components
    //Parts table
    @FXML
    private TableView partTableview;
    @FXML
    private TableColumn<VehicleRowView, String> colNamePart;
    @FXML
    private TableColumn<VehicleRowView, Double> colPricePart;
    @FXML
    private TableColumn<VehicleRowView, Integer> colQuantityPart;

    //Order Summary table

    @FXML
    private Label orderLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField ccField;

    @FXML
    private Label ccLabel;

    @FXML
    private TextField creationDateField;

    @FXML
    private ComboBox headquarterCombo;

    @FXML
    private Label headquarterLabel;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Label dueDateLabel;

    @FXML
    private TextField priceField;

    @FXML
    private Label priceLabel;

    @FXML
    private TextField partField;

    @FXML
    private Label partLabel;

    @FXML
    private ComboBox statusCombo;

    @FXML
    private Label statusLabel;

    @FXML
    private Button deleteOrderButton;

    @FXML
    private Button editOrderButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button searchButton;


    public OrderSingleViewController() {
        orderEndpoint = new OrderEndpoint();
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
        inputValidator = new AddUserValidator();
        inventoryEndpoint = new InventoryEndpoint();
        partRowViewList = new ArrayList<>();
        inventoryPartController = new InventoryPartController();
    }


    @FXML
    /**
     * editAction: event -> void
     * Purpose: By pressing the 'Editar orden' button the text fields are enabled,
     * the title becomes visible and the 'Guardar' and 'Cancelar' buttons are enabled.
     */
    protected void editAction() {
        ccField.setDisable(false);
        headquarterCombo.setDisable(false);
        priceField.setDisable(false);
        dueDatePicker.setDisable(false);
        priceField.setDisable(false);
        partField.setDisable(false);
        statusCombo.setDisable(false);

        deleteOrderButton.setVisible(false);
        editOrderButton.setVisible(false);

        cancelButton.setVisible(true);
        saveButton.setVisible(true);
        searchButton.setVisible(true);
    }

    @FXML
    protected void cancelAction() throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            orderController = (OrderController) dashboardController.changeContent("orders/orders");
            orderController.showOrders();
        } else {
            System.out.println("No");
        }
    }

    @FXML
    protected void saveAction() throws IOException, NullPointerException {
        /*Show dialogPane to confirm*/
        if (allValidations()){
            MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
            Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
            if (clickedButton.get() == YES) {
                try{
                    //orderEndpoint
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Orden actualizada exitosamente", OK);
                    success.show();
                    try {
                        orderController = (OrderController) dashboardController.changeContent("orders/orders");
                        orderController.showOrders();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("No");
            }
        }
    }


    /**
     * allValidations: Void -> Boolean
     * Purpose: Group all the validations from the edit-user form
     */
    public Boolean allValidations() {
        cleanErrors();
        boolean everythingCorrect = true;

        if (!inputValidator.cc(ccField, ccLabel, "Ingrese una cédula válida")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(ccField, ccLabel);
        }

        if (headquarterCombo.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            headquarterLabel.setText("Seleccione una sede");
            inputValidator.setErrorStyles(headquarterCombo, headquarterLabel);
        }

        if (!inputValidator.price(priceField, priceLabel, "Ingrese un precio válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(priceField, priceLabel);
        }

        if (statusCombo.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            statusLabel.setText("Seleccione un estado");
            inputValidator.setErrorStyles(statusCombo, statusLabel);
        }

        if (partField.getText().isEmpty()) {
            everythingCorrect = false;
            partLabel.setText("Ingrese una parte");
            inputValidator.setErrorStyles(partField, partLabel);
        }

        if (dueDatePicker.getValue() == null || dueDatePicker.getValue().isBefore(LocalDate.now())) {
            everythingCorrect = false;
            dueDateLabel.setText("Indique una fecha válida");
            inputValidator.setErrorStyles(dueDatePicker, dueDateLabel);
        }


        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        ccField.setStyle("");
        ccLabel.setText("");
        headquarterCombo.setStyle("");
        headquarterLabel.setText("");
        dueDatePicker.setStyle("");
        dueDateLabel.setText("");
        priceField.setStyle(
                "-fx-background-color: #F4F4F4;\n " + "-fx-border-radius: 10;"
        );
        priceLabel.setText("");
        partField.setStyle("");
        partLabel.setText("");
        statusCombo.setStyle("");
        statusLabel.setText("");
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
            CompletableFuture<Map<Boolean, ResultSet>> userCall = CompletableFuture.supplyAsync(() -> orderEndpoint.getOrderById(id));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setHeadquarterCombo();
                            showParts();
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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

    /**
     * showParts: void -> void
     * Purpose: fills the parts table
     */
    public void showParts(){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getPartsForTableView());
            try {
                partsCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            setPartsData(resultSet);
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

    public void setPartsData(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            PartRowView partRow = new PartRowView(resultSet.getString("name"), new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    resultSet.getString("hq"), resultSet.getInt("quantity"),
                    resultSet.getInt("id_part"));
            //Add the data of every vehicle to the array
            partRowViewList.add(partRow);
        }

        colNamePart.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPricePart.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantityPart.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        partTableview.setItems(FXCollections.observableArrayList(partRowViewList));
    }

    /**
     * setHeadquarterCombo: void -> void
     * Purpose: fills the headquarter combo
     */
    public void setHeadquarterCombo() throws ExecutionException, InterruptedException {
        CompletableFuture<Map<Boolean, ResultSet>> headquarterCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

        headquarterCall.thenApply((response) -> {
            if(response.containsKey(true)){
                ResultSet resultSet = response.get(true);
                try {
                    fillHeadquarterCombo(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }).get();
    }

    public void fillHeadquarterCombo(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarterCombo.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    public void setForm(ResultSet resultSet) throws SQLException {
        orderLabel.setText(resultSet.getString("id_job_order"));
        nameField.setText(resultSet.getString("first_name")+" "+resultSet.getString("last_name"));
        ccField.setText(resultSet.getString("cc"));
        headquarterCombo.getSelectionModel().select(findHeadquarterById(resultSet.getInt("id_headquarter")).toString());
        creationDateField.setText(resultSet.getDate("createdOrder").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        dueDatePicker.setValue(resultSet.getDate("due_date").toLocalDate());
        priceField.setText(resultSet.getString("jobPrice"));
        partField.setText(resultSet.getString("partname"));
        statusCombo.getSelectionModel().select(resultSet.getInt("id_state")-1);
    }

    /**
     * findHeadquarterById: Integer -> HeadquarterView
     * Purpose: This method finds a headquarter by its id.
     * It is used to set the headquarter combobox given the id of the headquarter where the worker is settled.
     */
    public HeadquarterView findHeadquarterById(Integer id){
        for (HeadquarterView headquarterView : headquarterComboboxList) {
            if (id == headquarterView.getIdHeadquarter()) {
                return headquarterView;
            }
        }
        return new HeadquarterView(-100,"");
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
        statusCombo.setItems(FXCollections.observableArrayList("Asignado", "En proceso", "Realizado"));
    }
}