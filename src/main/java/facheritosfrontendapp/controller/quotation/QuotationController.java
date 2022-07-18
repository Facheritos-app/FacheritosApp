package facheritosfrontendapp.controller.quotation;

import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.quotationRowView.QuotationRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class QuotationController implements Initializable {

    private ArrayList<QuotationRowView> quotationRowsArray;
    private QuotationEndpoint quotationEndpoint;
    private QuotationSingleViewController quotationSingleViewController;
    private QuotationAddController quotationAddController;

    private DashboardController dashboardController;


    @FXML
    private TableView quotationTableView;
    @FXML
    private TableColumn<QuotationRowView, Integer> colId;
    @FXML
    private TableColumn<QuotationRowView, String> colSeller;
    @FXML
    private TableColumn<QuotationRowView, String> colCustomer;
    @FXML
    private TableColumn<QuotationRowView, LocalDate> colSaleDate;
    @FXML
    private TableColumn<QuotationRowView, String> colHeadq;
    @FXML
    private TableColumn<QuotationRowView, VBox> colOptions;

    public QuotationController() throws SQLException {
        quotationRowsArray = new ArrayList<>();
        quotationEndpoint = new QuotationEndpoint();
    }

    /**
     * showQuotations: void -> void
     * Purpose: This method calls the quotation endpoint and gets the results, then it calls setQuotationsTable with these results.
     */
    public void showQuotations(){
        new Thread(() -> {
            //Async call to the DB
            CompletableFuture<Map<Boolean, ResultSet>> customersCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.getQuotationsForTableView());

            try {
                customersCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            setQuotationsTable(resultSet);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true; //Returns true because we're using thenApply.
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * setQuotationsTable: ResultSet -> void
     * Purpose: showQuotations auxiliary, fill the quotations table view
     */
    public void setQuotationsTable(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            QuotationRowView quotationRow = new QuotationRowView(resultSet.getInt("id_quotation"), resultSet.getString("seller_firstname") + " " + resultSet.getString("seller_lastname"),
                    resultSet.getString("customer_firstname") + " " +resultSet.getString("customer_lastname"),
                            resultSet.getDate("quotation_date").toLocalDate(), resultSet.getString("headquarter_name"));
            quotationRowsArray.add(quotationRow);
        }

        for(Integer i = 0; i < quotationRowsArray.size(); i++){
            quotationRowsArray.get(i).getOptionsLabel().setOnMouseClicked(this::handleOptionLabel);
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("idQuotation"));
        colSeller.setCellValueFactory(new PropertyValueFactory<>("seller"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colHeadq.setCellValueFactory(new PropertyValueFactory<>("headquarter"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("options"));

        quotationTableView.setItems(FXCollections.observableArrayList(quotationRowsArray));
    }

    public void handleOptionLabel(MouseEvent mouseEvent){
        for(Integer i = 0; i < quotationRowsArray.size(); i++){
            if(mouseEvent.getSource() == quotationRowsArray.get(i).getOptionsLabel()){
                try {
                    quotationSingleViewController = (QuotationSingleViewController) dashboardController.changeContent("quotations/quotationsDetails", true);
                    quotationSingleViewController.showQuotation(quotationRowsArray.get(i).getIdQuotation());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @FXML
    public void onAddQuotation(MouseEvent mouseEvent) throws IOException {
       quotationAddController = (QuotationAddController) dashboardController.changeContent("quotations/quotationsAdd",true);
       quotationAddController.showInventory();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }
}
