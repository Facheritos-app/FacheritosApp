package facheritosfrontendapp.controller.quotation;

import backend.endpoints.quotationEndpoint.QuotationEndpoint;
import facheritosfrontendapp.objectRowView.quotationRowView.QuotationRowView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class QuotationController {

    private ArrayList<QuotationRowView> quotationRowsArray;
    private QuotationEndpoint quotationEndpoint;


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

    public void showQuotations(){
        new Thread(() -> {
            //Async call to the DB
            CompletableFuture<Map<Boolean, ResultSet>> customersCall = CompletableFuture.supplyAsync(() -> quotationEndpoint.getQuotationsForTableView());

            try {
                customersCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        try {
                            System.out.println("Entro a showQuotations");
                            setTable(resultSet);
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

    public void setTable(ResultSet resultSet) throws SQLException {
        System.out.println("Entro a setTable");
        while(resultSet.next()){
            QuotationRowView quotationRow = new QuotationRowView(resultSet.getInt("id_quotation"), resultSet.getString("seller_name"),
                    resultSet.getString("customer_name"), resultSet.getDate("quotation_date").toLocalDate(), resultSet.getString("headquarter_name"));
            System.out.println("Id quotation: " + quotationRow.getIdQuotation());
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

        }
    }


    @FXML
    public void addQuotation(javafx.scene.input.MouseEvent mouseEvent) {
    }
}
