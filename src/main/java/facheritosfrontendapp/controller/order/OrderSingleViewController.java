package facheritosfrontendapp.controller.order;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.orderEndpoint.OrderEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.customer.CustomerController;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.YES;

public class OrderSingleViewController implements Initializable {

    private OrderEndpoint orderEndpoint;
    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private DashboardController dashboardController;

    private OrderController orderController;

    //Here are all the @FXML components
    @FXML
    private Label orderLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField ccField;

    @FXML
    private TextField creationDateField;

    @FXML
    private ComboBox headquarterCombo;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField priceField;

    @FXML
    private TextField partField;

    @FXML
    private ComboBox statusCombo;

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

    /**
     * backToOrdersClicked: void -> void
     * Purpose: when the backArrow is clicked  it returns to the orders view
     */
    @FXML
    protected void backToOrdersClicked() throws IOException {
        orderController = (OrderController) dashboardController.changeContent("orders/orders");
        orderController.showOrders();
    }

    public OrderSingleViewController() {
        orderEndpoint = new OrderEndpoint();
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
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
        for(Integer i = 0; i < headquarterComboboxList.size(); i++){
            if(id == headquarterComboboxList.get(i).getIdHeadquarter()){
                return headquarterComboboxList.get(i);
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