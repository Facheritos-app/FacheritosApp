package facheritosfrontendapp.controller.dashboard;

import backend.endpoints.dashboardEndpoint.SellerDashboardEndpoint;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SellerDashboard implements Initializable {

    @FXML
    private BarChart<?, ?> salesChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    private SellerDashboardEndpoint sellerDashboardEndpoint;

    private ArrayList<String> months;

    private ArrayList<Integer> monthsTotal;

    private ArrayList<String> monthsNames;

    public SellerDashboard(){
        sellerDashboardEndpoint = new SellerDashboardEndpoint();
        months = new ArrayList<>();
        monthsTotal = new ArrayList<>();
        monthsNames = new ArrayList<String>(
                Arrays.asList("Enero","Febrero","Marzo","Abril","Mayo","Junio", "Julio","Agosto","Septiembre","Octubre","Noviembre", "Diciembre"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xAxis.setAnimated(false);
        this.getSalesPerMonth();
    }

    public void getSalesPerMonth(){
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> salesCall = CompletableFuture.supplyAsync(() -> sellerDashboardEndpoint.amountOfSalesPerMonthChart());

            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                salesCall.thenApply((response) -> {
                    if(response.containsKey(true)){
                        ResultSet resultSet = response.get(true);
                        //Set the tableview data visually
                        Platform.runLater(() -> {
                            try {
                                setSalesChart(resultSet);
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


    protected void setSalesChart(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            months.add(resultSet.getString("month"));
            monthsTotal.add(resultSet.getInt("total"));
        }

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Meses");

        for(Integer i=0; i<months.size();i++){
            series1.getData().add(new XYChart.Data(monthsNames.get(Integer.parseInt(months.get(i))-1), monthsTotal.get(i)));
        }

        salesChart.getData().addAll(series1);
    }
}
