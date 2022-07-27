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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

public class OrderAddController implements Initializable {

    private final OrderEndpoint orderEndpoint;

    private final CustomerEndpoint customerEndpoint;

    private final OrderDTO newOrder;

    private OrderController orderController;

    private final AddUserValidator inputValidator;

    private final ArrayList<PartRowView> partRowViewList;

    private final ArrayList<PartRowView> orderPartsRowViewList;

    private DashboardController dashboardController;

    private Integer idHeadquarter;

    private String customerCc = "";

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
    private Label totalPriceLabel;

    @FXML
    private Label quantityLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;


    public OrderAddController() {
        orderEndpoint = new OrderEndpoint();
        customerEndpoint = new CustomerEndpoint();
        newOrder = new OrderDTO();
        inputValidator = new AddUserValidator();
        partRowViewList = new ArrayList<>();
        orderPartsRowViewList = new ArrayList<>();
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
     * Purpose: when the cancel button is clicked it returns to the orders view.
     */
    @FXML
    protected void cancelAction() throws IOException {
        /*Show dialogPane to confirm*/
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if (clickedButton.get() == YES) {
            orderController = (OrderController) dashboardController.changeContent("orders/orders");
            orderController.showOrders();
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
        if (customerCc.isBlank()) {
            searchButtonAction();
        }
        if (allValidations() && !customerCc.isBlank()) {
            ccField.setText(customerCc);
            OrderDTO orderDTO = createOrder();
            MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
            Optional<ButtonType> clickedButton = dialogPane.getClickedButton();

            if (clickedButton.get() == YES) {
                saveButton.setDisable(true);
                cancelButton.setDisable(true);

                try {
                    Alert wait = new Alert(Alert.AlertType.CONFIRMATION, "", OK);
                    wait.setHeaderText("Creando orden, por favor espere");
                    wait.show();

                    new Thread(() -> {

                        try {
                            Map<Boolean, ResultSet> response = orderEndpoint.createOrder(orderDTO,
                                    DashboardController.getCurrentWorker().getId_worker(), idHeadquarter);

                            if (response.containsKey(true)) {
                                ResultSet resultSet = response.get(true);
                                try {

                                    Integer idOrder = resultSet.getInt("id_job_order");

                                    //Update each part of the inventory
                                    for (PartRowView partRow : partRowViewList) {
                                        orderEndpoint.updatePart(partRow.getIdPart(), idHeadquarter, partRow.getQuantity());
                                    }

                                    //Add the parts to the order
                                    for (PartRowView orderPartRow : orderPartsRowViewList) {
                                        orderEndpoint.addPart(orderPartRow.getIdPart(), idOrder, orderPartRow.getQuantity());
                                    }

                                    Platform.runLater(() -> {
                                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Orden creada exitosamente", OK);
                                        success.show();
                                        try {
                                            orderController = (OrderController) dashboardController.changeContent("orders/orders");
                                            orderController.showOrders();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

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
        order.setId_customer(newOrder.getId_customer());
        order.setId_status(1); //Set status as assigned
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
                    colNamePartS.setCellValueFactory(new PropertyValueFactory<>("name"));
                    colPricePartS.setCellValueFactory(new PropertyValueFactory<>("price"));
                    colQuantityPartS.setCellValueFactory(new PropertyValueFactory<>("quantity"));
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

        totalPriceLabel.setText("" + new BigDecimal(totalPrice));
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
        orderPartLabel.setText("");
    }

    /**
     * showParts: Integer -> void
     * Purpose: fills the parts table
     */
    public void showParts(Integer idHeadquarter) {
        setIdHeadquarter(idHeadquarter);
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
        }

        colNamePart.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPricePart.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantityPart.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        partTableview.setItems(FXCollections.observableArrayList(partRowViewList));
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
    }
}