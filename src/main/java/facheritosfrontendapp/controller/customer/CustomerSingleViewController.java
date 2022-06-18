package facheritosfrontendapp.controller.customer;

import backend.endpoints.customerEndpoint.CustomerEndpoint;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public CustomerSingleViewController() {
        customerEndpoint = new CustomerEndpoint();
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

    public void initialize(URL url, ResourceBundle rb) {

    }
}
