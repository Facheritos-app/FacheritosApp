package facheritosfrontendapp.controller.dashboard;

import backend.endpoints.dashboardEndpoint.SellerDashboardEndpoint;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SellerDashboardController implements Initializable {

    @FXML
    private BarChart<?, ?> salesChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private ComboBox<String> daysMonthsYearsCombobox;

    @FXML
    private ChoiceBox<String> yearChoicebox;

    @FXML
    private Label yearChoiceboxLabel;

    private SellerDashboardEndpoint sellerDashboardEndpoint;

    private ArrayList<String> categoryList;

    private ArrayList<Integer> categoryTotal;

    private ArrayList<String> monthsNames;

    public SellerDashboardController() {
        sellerDashboardEndpoint = new SellerDashboardEndpoint();
        categoryList = new ArrayList<>();
        categoryTotal = new ArrayList<>();
        monthsNames = new ArrayList<>(
                Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xAxis.setAnimated(false);
        showDashboard();
    }

    /**
     * setChoicebox: void -> void
     * Purpose: This method sets the elements of the choicebox to be shown
     */
    public void setChoiceBox() {
        daysMonthsYearsCombobox.setItems(FXCollections.observableArrayList("Meses", "Años"));
        yearChoicebox.setItems(FXCollections.observableArrayList("2022", "2021", " 2020", "2019", "2018", "2017", "2016", "2015"));
    }

    /**
     * showDashboard: void ->  void
     * Purpose: This method shows everything in the view from one single function
     */
    public void showDashboard() {
        this.setChoiceBox();
        daysMonthsYearsCombobox.getSelectionModel().select(0); //Months
        yearChoicebox.getSelectionModel().select(0); //5 2022
        salesSeeClicked();
    }
    /**
     * getSalesPerMonth: String, Integer -> void
     * Purpose: This method connects to the DB and returns the necessary data to build the chart
     */
    public void getSalesPerMonth(String category, Integer year) {
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> salesCall = CompletableFuture.supplyAsync(() ->
                    sellerDashboardEndpoint.amountOfSalesPerMonthChart(passToEnglish(category), year));
            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                salesCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setSalesChart(resultSet, passToEnglish(category));
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
    /**
     * clearDataToUpdate: void -> void
     * Purpose: This method clears the data from the chart in order to change the parameters.
     * It must be executed before changing the chart parameters.
     */
    protected void clearDataToUpdate() {
        categoryList.clear();
        categoryTotal.clear();
        salesChart.getData().clear();
        salesChart.layout();
    }

    /**
     * setSalesChart: ResultSet, String -> void
     * Purpose: This method uses the data from the query and sets it in the chart
     */
    protected void setSalesChart(ResultSet resultSet, String category) throws SQLException {
        clearDataToUpdate();
        while (resultSet.next()) {
            categoryList.add(resultSet.getString("cat"));
            categoryTotal.add(resultSet.getInt("total"));
        }
        XYChart.Series series1 = new XYChart.Series();
        switch (category) {
            case "month":
                for (Integer i = 0; i < categoryList.size(); i++) {
                    System.out.println(categoryList.get(i));
                    series1.getData().add(new XYChart.Data(monthsNames.get(Integer.parseInt(categoryList.get(i)) - 1), categoryTotal.get(i)));
                }
                break;
            case "year":
                for (Integer i = 0; i < categoryList.size(); i++) {
                    series1.getData().add(new XYChart.Data(categoryList.get(i), categoryTotal.get(i)));
                }
                break;
            default:
                throw new RuntimeException("ERROR: No valid option for the chart");
        }
        series1.setName(category);
        salesChart.getData().addAll(series1);
    }

    @FXML
    protected void salesSeeClicked() {
        getSalesPerMonth(daysMonthsYearsCombobox.getSelectionModel().getSelectedItem(),
                Integer.valueOf(yearChoicebox.getSelectionModel().getSelectedItem()));
    }

    /**
     * passToEnglish: String -> String
     * Purpose: This method changes text from the GUI (spanish) to the query (english)
     */
    protected String passToEnglish(String text) {
        switch (text) {
            case "Meses":
                return "month";
            case "Años":
                return "year";
            default:
                return null;
        }
    }

    /**
     * salesSelectionChange: ActionEvent -> void
     * Purpose: This method is an event that activates when changing the selection item in the choicebox
     */
    public void salesSelectionChanged(javafx.event.ActionEvent actionEvent) {
        String selection = (String) ((ComboBox) actionEvent.getSource()).getSelectionModel().getSelectedItem();
        if(selection.equals("Años")){
            yearChoicebox.setVisible(false);
            yearChoiceboxLabel.setVisible(false);
        }
        else if(selection.equals("Meses")) {
            yearChoicebox.setVisible(true);
            yearChoiceboxLabel.setVisible(true);
        }
    }
}
