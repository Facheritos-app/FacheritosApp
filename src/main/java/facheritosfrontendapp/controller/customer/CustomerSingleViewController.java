package facheritosfrontendapp.controller.customer;

import backend.endpoints.customerEndpoint.CustomerEndpoint;
import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.sale.SaleSingleViewController;
import facheritosfrontendapp.controller.user.UserController;
import facheritosfrontendapp.objectRowView.saleRowView.SaleByIdCustomerRowView;
import facheritosfrontendapp.views.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.awt.Desktop;

public class CustomerSingleViewController implements Initializable {

    private final CustomerEndpoint customerEndpoint;

    private SaleEndpoint saleEndpoint;

    private ArrayList<SaleByIdCustomerRowView> saleRowsArray;

    private SaleSingleViewController saleSingleViewController;

    @FXML
    private Label nameLabel;

    @FXML
    private Label ccLabel;

    @FXML
    private Label cellphoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label birthdayLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    private TableView purchasesTable;

    @FXML
    private TableColumn<SaleByIdCustomerRowView, String> colType;

    @FXML
    private TableColumn<SaleByIdCustomerRowView, String> colNumber;

    @FXML
    private TableColumn<SaleByIdCustomerRowView, String> colDate;

    @FXML
    private TableColumn<SaleByIdCustomerRowView, String> colPaymentMethod;

    @FXML
    private TableColumn<SaleByIdCustomerRowView, String> colSeat;

    @FXML
    private TableColumn<SaleByIdCustomerRowView, VBox> colOptions;

    @FXML
    public ImageView backToCustomers;

    @FXML
    public ImageView backToUsers;

    private DashboardController dashboardController;

    private CustomerController customerController;

    private UserController userController;

    public CustomerSingleViewController() {
        customerEndpoint = new CustomerEndpoint();
        saleEndpoint = new SaleEndpoint();
        saleRowsArray = new ArrayList<>();
        saleSingleViewController = new SaleSingleViewController();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
    }


    /**
     * backToCustomersClicked: void -> void
     * Purpose: when the backArrow is clicked in customerSingleView from a
     * salesperson account it returns to the customers view
     */
    @FXML
    protected void backToCustomersClicked() throws IOException {
        customerController = (CustomerController) dashboardController.changeContent("customers/customers");
        customerController.showCustomers();
    }

    /**
     * backToUsersClicked: void -> void
     * Purpose: when the backArrow is clicked in customerSingleView from a
     * manager account it returns to the users view
     */
    @FXML
    protected void backToUsersClicked() throws IOException {
        userController = (UserController) dashboardController.changeContent("users/users");
        userController.showWorkers();
        userController.showCustomers();
    }

    /**
     * showForm: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the customer form possible
     */
    public void showCustomer(Integer idPerson) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> userCall = CompletableFuture.supplyAsync(() -> customerEndpoint.getCustomerById(idPerson));
            try {
                userCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setCustomerForm(resultSet);
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

    /**
     * setCustomerForm: ResultSet -> void
     * Purpose: sets the customer view elements.
     */
    public void setCustomerForm(ResultSet resultSet) throws SQLException {
        nameLabel.setText(resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
        ccLabel.setText(resultSet.getString("cc"));
        cellphoneLabel.setText(resultSet.getString("cellphone"));
        emailLabel.setText(resultSet.getString("email"));
        birthdayLabel.setText(resultSet.getDate("birthday").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        creationDateLabel.setText(resultSet.getDate("created_at").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }


    /**
     * emailClicked: void -> void
     * Purpose: when clicking on an email, the default email manager is opened to send an email.
     */
    @FXML
    protected void emailClicked() {
        openEmail(emailLabel.getText());
    }

    /**
     * openEmail: string -> void
     * Purpose: opens the default email manager to send an email.
     */
    public void openEmail (String email){
        Desktop link = Desktop.getDesktop();
        try {
            link.browse(new URI("mailto:"+email));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //Purchases Table
    public void showSales(Integer idPerson) {
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> saleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSaleByIdCustomer(idPerson));
            try {
                saleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setPurchasesTable(resultSet);
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

    public void setPurchasesTable(ResultSet resultSet) throws SQLException {
        //As long as there are records left to show
        while(resultSet.next()){
            SaleByIdCustomerRowView saleRow = new SaleByIdCustomerRowView(resultSet.getInt("id_sale"), resultSet.getDate("sale_date"),
                    resultSet.getString("payment_method"), resultSet.getString("name"));
            saleRowsArray.add(saleRow); //Add every element to the array.
        }

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("idSale"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        //colOptions.setCellValueFactory(new PropertyValueFactory<>("options"));

        purchasesTable.setItems(FXCollections.observableArrayList(saleRowsArray));
    }

}
