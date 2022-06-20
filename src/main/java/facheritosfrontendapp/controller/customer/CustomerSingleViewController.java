package facheritosfrontendapp.controller.customer;

import backend.endpoints.customerEndpoint.CustomerEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.user.UserController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.awt.Desktop;

public class CustomerSingleViewController implements Initializable {

    private final CustomerEndpoint customerEndpoint;

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
    public ImageView backToCustomers;

    @FXML
    public ImageView backToUsers;

    private DashboardController dashboardController;

    private CustomerController customerController;

    private UserController userController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboardController = MainController.getDashboardController();
    }

    public CustomerSingleViewController() {
        customerEndpoint = new CustomerEndpoint();
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

}
