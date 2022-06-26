package facheritosfrontendapp.controller.quotation;

import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class QuotationSingleViewController implements Initializable {


    private QuotationEndpoint quotationEndpoint;

    @FXML
    private TextField sellerCc;
    @FXML
    private TextField sellerName;
    @FXML
    private TextField sellerLastname;
    @FXML
    private TextField sellerHeadq;
    @FXML
    private TextField sellerEmail;

    @FXML
    private TextField customerCc;
    @FXML
    private TextField customerName;
    @FXML
    private TextField customerLastname;
    @FXML
    private TextField quotationQuantity;
    @FXML
    private ComboBox paymentMethod;
    @FXML
    private Label quotationPrice;

    public QuotationSingleViewController(){
        quotationEndpoint = new QuotationEndpoint();
    }

    public void showQuotation(Integer idQuotation){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> quotationCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.getQuotation(idQuotation));

            try {
                quotationCall.thenApply(response -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                            Platform.runLater(() -> {
                                try {
                                    setSellerInfo(resultSet);
                                    setCustomerInfo(resultSet);
                                    setQuotationDetails(resultSet);
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

    public void setSellerInfo(ResultSet resultSet) throws SQLException {
        sellerCc.setText(resultSet.getString("seller_cc"));
        sellerName.setText(resultSet.getString("seller_firstname"));
        sellerLastname.setText(resultSet.getString("seller_lastname"));
        sellerHeadq.setText(resultSet.getString("headquarter_name"));
        sellerEmail.setText(resultSet.getString("seller_email"));
    }

    public void setCustomerInfo(ResultSet resultSet) throws SQLException {
        customerCc.setText(resultSet.getString("customer_cc"));
        customerName.setText(resultSet.getString("customer_firstname"));
        customerLastname.setText(resultSet.getString("customer_lastname"));
    }

    public void setQuotationDetails(ResultSet resultSet) throws SQLException {
        paymentMethod.setItems(FXCollections.observableArrayList("Tarjeta de credito", "Efectivo"));
        quotationQuantity.setText("1");
        quotationPrice.setText("$ " + resultSet.getString("price"));
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
