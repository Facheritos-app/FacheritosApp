package facheritosfrontendapp.controller.order;

import backend.dto.orderDTO.OrderDTO;
import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.orderEndpoint.OrderEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.inventoryRowView.PartRowView;
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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class OrderEditController implements Initializable {

    private final OrderEndpoint orderEndpoint;

    private final CustomerEndpoint customerEndpoint;

    private final OrderDTO newOrder;

    private final AddUserValidator inputValidator;

    private final ArrayList<PartRowView> partRowViewList;

    private final ArrayList<PartRowView> orderPartsRowViewList;

    private final ArrayList<PartRowView> initialOrderParts;

    private DashboardController dashboardController;

    private OrderSingleViewController orderSingleViewController;

    private Integer idOrder;

    private Integer idHeadquarter;

    private String customerCc;

    //Here are all the @FXML components
    //Parts table
    @FXML
    private TableView partTableview;
    @FXML
    private TableColumn<PartRowView, String> colNamePart;
    @FXML
    private TableColumn<PartRowView, Double> colPricePart;
    @FXML
    private TableColumn<PartRowView, Integer> colQuantityPart;

    //Order Summary table
    @FXML
    private TableView orderSummaryTableview;
    @FXML
    private TableColumn<PartRowView, String> colNamePartS;
    @FXML
    private TableColumn<PartRowView, Double> colPricePartS;
    @FXML
    private TableColumn<PartRowView, Integer> colQuantityPartS;

    @FXML
    private Label orderLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label customerLabel;

    @FXML
    private TextField ccField;

    @FXML
    private Label ccLabel;

    @FXML
    private Label cellphoneLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    private Label headquarterLabel;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label partLabel;

    @FXML
    private Label orderPartLabel;

    @FXML
    private TextField priceField;

    @FXML
    private Label priceLabel;

    @FXML
    private ComboBox statusCombo;

    @FXML
    private Label statusLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;


    public OrderEditController() {
        orderEndpoint = new OrderEndpoint();
        customerEndpoint = new CustomerEndpoint();
        newOrder = new OrderDTO();
        inputValidator = new AddUserValidator();
        partRowViewList = new ArrayList<>();
        orderPartsRowViewList = new ArrayList<>();
        initialOrderParts = new ArrayList<>();
    }

    private void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    private void setIdHeadquarter(Integer idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }

    /**
     * backToOrdersClicked: void -> void
     * Purpose: when the backArrow is clicked  it returns to the orders view.
     */
    @FXML
    protected void backToOrdersClicked() throws IOException {
        cancelAction();
    }

    /**
     * cancelAction: void -> void
     * Purpose: when the cancel button is clicked it returns to the order view.
     */
    @FXML
    protected void cancelAction() throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            orderSingleViewController = (OrderSingleViewController) dashboardController.changeContent("" +
                    "orders/ordersSingleView", true);
            orderSingleViewController.showForm(idOrder);
        }
    }

    /**
     * cancelAction: void -> void
     * Purpose: when the search button is clicked it changes the customer
     */
    @FXML
    protected void searchButtonAction() {
        //Restores styles
        ccField.setStyle("-fx-background-color: #F4F4F4;\n " + "-fx-border-radius: 10;");
        ccLabel.setText("");
        customerLabel.setText("");

        //Checks the cc
        if (inputValidator.cc(ccField, ccLabel, "Ingrese una cédula válida")) {
            customerInfoLoading();
            changeCustomer(ccField.getText());
        } else {
            inputValidator.setErrorStyles(ccField, ccLabel);
        }
    }

    public void customerInfoLoading() {
        nameLabel.setText("   " + "Cargando...");
        cellphoneLabel.setText("   " + "Cargando...");
    }

    /**
     * saveAction: void -> void
     * Purpose: when the save button is clicked it updates the order and returns to the order view.
     */
    @FXML
    protected void saveAction() throws IOException, NullPointerException {
        /*Show dialogPane to confirm*/
        if (allValidations()) {
            ccField.setText(customerCc);
            OrderDTO orderDTO = createOrder();
            MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
            Optional<ButtonType> clickedButton = dialogPane.getClickedButton();

            if (clickedButton.get() == YES) {
                saveButton.setDisable(true);
                cancelButton.setDisable(true);

                try {
                    boolean changes = false;

                    //Check if there were any changes in the order parts
                    for (int i = 0; i < partRowViewList.size(); i++) {
                        if (partRowViewList.get(i).getQuantity() != initialOrderParts.get(i).getQuantity()) {
                            changes = true;
                            break;
                        }
                    }

                    if (changes) {
                        Alert wait = new Alert(Alert.AlertType.CONFIRMATION, "", OK);
                        wait.setHeaderText("Se está actualizando la orden, por favor espere");
                        wait.show();

                        new Thread(() -> {
                            boolean enough = true;

                            //Check in the inventory for parts availability
                            for (PartRowView orderPartRow : orderPartsRowViewList) {
                                Integer checkQuantity = null;
                                try {
                                    checkQuantity = orderEndpoint.getPartQuantity(orderPartRow.getIdPart(), idHeadquarter);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                if (orderPartRow.getQuantity() > checkQuantity) {
                                    enough = false;
                                    break;
                                }
                            }

                            if (enough) {

                                //Update oder data
                                orderEndpoint.updateOrder(orderDTO);

                                //Remove initial parts
                                orderEndpoint.removeParts(idOrder);

                                //Update each part of the inventory
                                for (PartRowView partRow : partRowViewList) {
                                    orderEndpoint.updatePart(partRow.getIdPart(), idHeadquarter, partRow.getQuantity());
                                }

                                //Add the parts to the order
                                for (PartRowView orderPartRow : orderPartsRowViewList) {
                                    orderEndpoint.addPart(orderPartRow.getIdPart(), idOrder, orderPartRow.getQuantity());
                                }
                                Platform.runLater(() -> {
                                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Orden actualizada exitosamente", OK);
                                    success.show();
                                    try {
                                        orderSingleViewController = (OrderSingleViewController) dashboardController.changeContent("" +
                                                "orders/ordersSingleView", true);
                                        orderSingleViewController.showForm(idOrder);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                            } else {
                                Platform.runLater(() -> {
                                    Alert fail = new Alert(Alert.AlertType.ERROR, "Ha habido un problema, por favor intente nuevamente", OK);
                                    fail.show();
                                    showParts(idHeadquarter);
                                });
                            }
                        }).start();


                    } else {
                        orderEndpoint.updateOrder(orderDTO);
                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Orden actualizada exitosamente", OK);
                        success.show();
                        try {
                            orderSingleViewController = (OrderSingleViewController) dashboardController.changeContent("" +
                                    "orders/ordersSingleView", true);
                            orderSingleViewController.showForm(idOrder);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } catch (Exception e) {
                    Alert fail = new Alert(Alert.AlertType.ERROR, "Ha habido un problema, por favor intente nuevamente", OK);
                    fail.show();
                    throw new RuntimeException(e);

                }
            }
        }
    }

    private OrderDTO createOrder() {
        OrderDTO order = new OrderDTO();
        order.setId_order(newOrder.getId_order());
        order.setId_customer(newOrder.getId_customer());
        order.setId_status(statusCombo.getSelectionModel().getSelectedIndex() + 1);
        order.setDue_date(Date.valueOf(dueDatePicker.getValue()));
        order.setPrice(Double.parseDouble(priceField.getText()));
        return order;
    }

    /**
     * addClicked: void -> void
     * Purpose: updates the order summary when a part of the inventory is added to the order.
     */
    @FXML
    protected void addClicked() {
        partLabel.setText("");
        orderPartLabel.setText("");

        if (!partTableview.getSelectionModel().isEmpty()) {

            PartRowView selectedRow = partRowViewList.get(partTableview.getSelectionModel().getSelectedIndex());
            int idPart = selectedRow.getIdPart();
            boolean isInTheOrderSummary = false;

            if (selectedRow.getQuantity() > 0) {

                for (PartRowView orderPartRow : orderPartsRowViewList) {
                    if (orderPartRow.getIdPart().equals(idPart)) {
                        selectedRow.setQuantity(selectedRow.getQuantity() - 1);
                        orderPartRow.setQuantity(orderPartRow.getQuantity() + 1);
                        isInTheOrderSummary = true;
                        break;
                    }
                }

                if (!isInTheOrderSummary) {
                    selectedRow.setQuantity(selectedRow.getQuantity() - 1);
                    PartRowView addNewPartRow = new PartRowView(selectedRow.getName(), selectedRow.getPrice(),
                            "", 1, selectedRow.getIdPart());
                    orderPartsRowViewList.add(addNewPartRow);
                    orderSummaryTableview.setItems(FXCollections.observableArrayList(orderPartsRowViewList));
                }

                updateTotalPriceAndQuantity();
                partTableview.refresh();
                orderSummaryTableview.refresh();

            } else {
                partLabel.setText("No hay unidades suficientes");
            }
        } else {
            partLabel.setText("Seleccione una parte");
        }
    }

    /**
     * removeClicked: void -> void
     * Purpose: updates the order summary when a part is removed from the order.
     */
    @FXML
    protected void removeClicked() {
        partLabel.setText("");
        orderPartLabel.setText("");

        if (!orderSummaryTableview.getSelectionModel().isEmpty()) {

            PartRowView selectedRow =
                    orderPartsRowViewList.get(orderSummaryTableview.getSelectionModel().getSelectedIndex());
            int idPart = selectedRow.getIdPart();

            if (selectedRow.getQuantity() > 0) {

                for (PartRowView partRow : partRowViewList) {
                    if (partRow.getIdPart().equals(idPart)) {
                        selectedRow.setQuantity(selectedRow.getQuantity() - 1);
                        partRow.setQuantity(partRow.getQuantity() + 1);
                        break;
                    }
                }

                if (selectedRow.getQuantity().equals(0)) {
                    orderPartsRowViewList.remove(selectedRow);
                    orderSummaryTableview.setItems(FXCollections.observableArrayList(orderPartsRowViewList));
                }

                updateTotalPriceAndQuantity();
                partTableview.refresh();
                orderSummaryTableview.refresh();

            } else {
                orderPartLabel.setText("No hay unidades suficientes");
            }
        } else {
            orderPartLabel.setText("Seleccione una parte");
        }
    }

    /**
     * updateTotalPriceAndQuantity: void -> void
     * Purpose: updates the total price and the quantity of parts of the order.
     */
    private void updateTotalPriceAndQuantity() {
        double totalPrice = 0.0;
        int quantity = 0;
        for (int i = 0; i < orderSummaryTableview.getItems().size(); i++) {
            PartRowView order = orderPartsRowViewList.get(i);
            double price = Double.parseDouble(order.getPrice());
            totalPrice += price * order.getQuantity();
            quantity += order.getQuantity();
        }

        if (servicePriceIsCorrect()) {
            totalPrice += Double.parseDouble(priceField.getText());
        }

        totalPriceLabel.setText("" + totalPrice);
        quantityLabel.setText("" + quantity);
    }

    /**
     * priceAction: void -> void
     * Purpose: when the service price changes, the total price is updated.
     */
    @FXML
    protected void priceAction() {
        updateTotalPriceAndQuantity();
    }

    /**
     * servicePriceIsCorrect: void -> void
     * Purpose: checks if the service price is correct.
     */
    @FXML
    private Boolean servicePriceIsCorrect() {
        if (!inputValidator.price(priceField, priceLabel, "Ingrese un precio válido")) {
            inputValidator.setErrorStyles(priceField, priceLabel);
            return false;
        }
        priceField.setStyle("-fx-background-color: #F4F4F4;\n " + "-fx-border-radius: 10;");
        priceLabel.setText("");
        return true;
    }

    /**
     * allValidations: Void -> Boolean
     * Purpose: group all the validations.
     */
    public Boolean allValidations() {
        cleanErrors();
        boolean everythingCorrect = true;


        if (!inputValidator.price(priceField, priceLabel, "Ingrese un precio válido")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(priceField, priceLabel);
        }

        if (statusCombo.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            statusLabel.setText("Seleccione un estado");
            inputValidator.setErrorStyles(statusCombo, statusLabel);
        }

        if (dueDatePicker.getValue() == null || dueDatePicker.getValue().isBefore(LocalDate.now())) {
            everythingCorrect = false;
            dueDateLabel.setText("Indique una fecha válida");
            inputValidator.setErrorStyles(dueDatePicker, dueDateLabel);
        }

        if (orderPartsRowViewList.isEmpty()) {
            everythingCorrect = false;
            orderPartLabel.setText("Añada alguna parte");
        }

        return everythingCorrect;
    }

    /**
     * cleanErrors: void -> void
     * Purpose: This method cleans all the error messages presented to the user
     */
    public void cleanErrors() {
        ccField.setStyle("-fx-background-color: #F4F4F4;\n " + "-fx-border-radius: 10;");
        ccLabel.setText("");
        customerLabel.setText("");
        dueDatePicker.setStyle("");
        dueDateLabel.setText("");
        priceField.setStyle("-fx-background-color: #F4F4F4;\n " + "-fx-border-radius: 10;");
        priceLabel.setText("");
        statusCombo.setStyle("-fx-background-color: #F4F4F4;\n " + "-fx-border-radius: 10;");
        statusLabel.setText("");
        orderPartLabel.setText("");
    }

    /**
     * setOrderTotalPrice: Integer -> void
     * Purpose: sets the total price and quantity of parts of an order
     */
    public void setOrderTotalPrice(Integer id) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>>
                    userCall = CompletableFuture.supplyAsync(() -> orderEndpoint.getTotalPriceAndQuantity(id));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setTotalPriceAndQuantity(resultSet);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setTotalPriceAndQuantity(ResultSet resultSet) throws SQLException {
        totalPriceLabel.setText("" + resultSet.getDouble("total"));
        quantityLabel.setText(resultSet.getString("quantity"));
    }

    /**
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the form possible
     */
    public void showForm(Integer id) {
        setIdOrder(id);
        newOrder.setId_order(id);

        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> userCall = CompletableFuture.supplyAsync(() ->
                    orderEndpoint.getOrderById(id));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        showOrderParts();
                        Platform.runLater(() -> {
                            try {
                                setIdHeadquarter(resultSet.getInt("id_headquarter"));
                                showParts(idHeadquarter);
                                setForm(resultSet);
                                setOrderTotalPrice(id);
                                customerCc = resultSet.getString("cc");
                                newOrder.setId_customer(resultSet.getInt("id_person"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    public void setForm(ResultSet resultSet) throws SQLException {
        orderLabel.setText(resultSet.getString("id_job_order"));
        nameLabel.setText("   " + resultSet.getString("person_name"));
        ccField.setText(resultSet.getString("cc"));
        cellphoneLabel.setText("   " + resultSet.getString("cellphone"));
        headquarterLabel.setText("   " + resultSet.getString("headquarter_name"));
        creationDateLabel.setText("   " + resultSet.getDate("created_at").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        dueDatePicker.setValue(resultSet.getDate("due_date").toLocalDate());
        priceField.setText(resultSet.getString("price"));
        statusCombo.getSelectionModel().select(resultSet.getInt("id_state") - 1);
    }

    /**
     * showParts: Integer -> void
     * Purpose: fills the parts table
     */
    public void showParts(Integer idHeadquarter) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() ->
                    orderEndpoint.getPartsFromInventory(idHeadquarter));
            try {
                partsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setPartsData(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setPartsData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            PartRowView partRow = new PartRowView(resultSet.getString("name"),
                    new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    resultSet.getString("hq"), resultSet.getInt("quantity"),
                    resultSet.getInt("id_part"));
            partRowViewList.add(partRow);

            PartRowView initialPartRow = new PartRowView(resultSet.getString("name"),
                    new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    resultSet.getString("hq"), resultSet.getInt("quantity"),
                    resultSet.getInt("id_part"));
            initialOrderParts.add(initialPartRow);
        }

        colNamePart.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPricePart.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantityPart.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        partTableview.setItems(FXCollections.observableArrayList(partRowViewList));
    }

    /**
     * showOrderParts: void -> void
     * Purpose: fills the order parts table.
     */
    public void showOrderParts() {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() ->
                    orderEndpoint.getOrderParts(idOrder));
            try {
                partsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setOrderPartsData(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setOrderPartsData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            PartRowView orderPartRow = new PartRowView(resultSet.getString("name"),
                    new BigDecimal(String.valueOf(resultSet.getDouble("price"))).toPlainString(),
                    "", resultSet.getInt("quantity"),
                    resultSet.getInt("id_part"));
            //Add the data of every vehicle to the array
            orderPartsRowViewList.add(orderPartRow);
        }

        colNamePartS.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPricePartS.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantityPartS.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderSummaryTableview.setItems(FXCollections.observableArrayList(orderPartsRowViewList));
        orderSummaryTableview.refresh();
    }

    /**
     * changeCustomer: String -> void
     * Purpose: changes a customer's data according to their cc
     */
    public void changeCustomer(String cc) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> customerCall = CompletableFuture.supplyAsync(() ->
                    customerEndpoint.getCustomerByCc(cc));
            try {
                customerCall.thenApply(response -> {
                    Platform.runLater(() -> {
                        if (response.containsKey(true)) {
                            ResultSet resultSet = response.get(true);
                            try {
                                setCustomerInfo(resultSet);
                                customerCc = cc;
                                newOrder.setId_customer(resultSet.getInt("id_person"));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            customerLabel.setText("Cliente no encontrado");
                            inputValidator.setErrorStyles(ccField, customerLabel);
                            changeCustomer(customerCc);
                        }
                    });
                    return true;
                }).get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setCustomerInfo(ResultSet resultSet) throws SQLException {
        nameLabel.setText("   " + resultSet.getString("first_name") + " " +
                resultSet.getString("last_name"));
        cellphoneLabel.setText("   " + resultSet.getString("cellphone"));
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
        statusCombo.setItems(FXCollections.observableArrayList("Asignado", "En proceso", "Realizado"));
    }
}