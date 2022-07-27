package facheritosfrontendapp.controller.dashboard;

import backend.endpoints.dashboardEndpoint.MechanicDashboardEnpoint;
import backend.endpoints.inventoryEndpoint.PartEndpoint;
import facheritosfrontendapp.ComboBoxView.ModelView;
import facheritosfrontendapp.ComboBoxView.PartView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MechanicDashboardController implements Initializable {
    @FXML
    private BarChart partsChart;

    @FXML
    private ComboBox<PartView> partsCombobox;

    @FXML
    private ComboBox<String> selectionPartsCombobox;
    @FXML
    private ChoiceBox<String> yearPartChoicebox;
    private ArrayList<Integer> partsChartList;
    private ArrayList<Integer> partsChartListTotal;
    private ArrayList<String> years;
    private ArrayList<PartView> partComboboxList;
    private MechanicDashboardEnpoint mechanicDashboardEnpoint;
    private PartEndpoint partEndpoint;
    private ArrayList<String> monthsNames;

    public MechanicDashboardController(){
        mechanicDashboardEnpoint = new MechanicDashboardEnpoint();
        partEndpoint = new PartEndpoint();
        years = new ArrayList<>(
                Arrays.asList("2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015"));
        monthsNames = new ArrayList<>(
                Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"));

        partComboboxList = new ArrayList<>();
        partsChartList = new ArrayList<>();
        partsChartListTotal = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    /**
     * clearDataToUpdate: void -> void
     * Purpose: This method clears the data from the chart in order to change the parameters.
     * It must be executed before changing the chart parameters.
     */
    protected void clearDataToUpdate(Integer chart) throws Exception {
        switch (chart) {
            case 0: //sales chart
                partsChartList.clear();
                partsChartListTotal.clear();
                partsChart.getData().clear();
                partsChart.layout();
                break;
            default:
                throw new Exception("ERROR: impossible to clear data from the chart");
        }
    }
    @FXML
    void partsViewClicked() {
        getPartsData(partsCombobox.getSelectionModel().getSelectedItem().getIdPart(),
                selectionPartsCombobox.getSelectionModel().getSelectedIndex(), Integer.valueOf(yearPartChoicebox.getSelectionModel().getSelectedItem()));
    }
    /**
     * showDashboard: void ->  void
     * Purpose: This method shows everything in the view from one single function
     */
    public void showDashboard() {
        setChoiceBox();
        preselectOptions();
        showParts();
    }
    /**
     * setChoicebox: void -> void
     * Purpose: This method sets the elements of the choicebox to be shown
     */
    public void setChoiceBox(){
        //parts chart
        selectionPartsCombobox.setItems(FXCollections.observableArrayList("Cantidad de repuestos", "Valor de repuestos"));
        yearPartChoicebox.setItems(FXCollections.observableArrayList(years));
    }
    /**
     * preselectOptions: void -> void
     * Purpose: This method sets a selected item for the comboboxes and choiceboxes by default
     */
    protected void preselectOptions() {
        //parts chart
        selectionPartsCombobox.getSelectionModel().select(0); //Amount
        yearPartChoicebox.getSelectionModel().select(0); //2022
    }
    /**
     * showParts: void -> void
     * Purpose: This method contains all the steps to show all the parts in the combobox
     */
    public void showParts()   {
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() -> partEndpoint.getParts());
            //Use the response from the BD to fill the combobox
            try {
                partsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setPartCombobox(resultSet);
                            Platform.runLater(() -> {
                                partsCombobox.getSelectionModel().select(0); //any model
                                partsViewClicked(); //Show the chart data
                            });
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
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
     * setPartCombobox: ResultSet -> void
     * Purpose: This method set the items of the models combobox according to the DB
     */
    public void setPartCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idPart = resultSet.getInt("id_part");
            String name = resultSet.getString("name");
            partComboboxList.add(new PartView(idPart, name));
        }
        partsCombobox.setItems(FXCollections.observableArrayList(partComboboxList));
    }
    /**
     * getPartsData: String, Integer -> void
     * Purpose: This method connects to the DB and returns the necessary data to build the parts chart
     */
    public void getPartsData(Integer idPart, Integer selectionType, Integer year) {
        //This means that another thread different from the JavaFX app thread will update the required items with the data.+
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> partsCall = CompletableFuture.supplyAsync(() -> mechanicDashboardEnpoint.partsChart(idPart, selectionType, year));
            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                partsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setPartsChart(resultSet, selectionType);
                            } catch (Exception e) {
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
     * setPartsChart: ResultSet, Integer -> void
     * Purpose: This method uses the data from the query and sets it in the customers chart
     */
    protected void setPartsChart(ResultSet resultSet, Integer selectionType) throws Exception {
        clearDataToUpdate(0);
        while (resultSet.next()) {
            partsChartList.add(resultSet.getInt("data"));
            partsChartListTotal.add(resultSet.getInt("total"));
        }
        XYChart.Series series1 = new XYChart.Series();
        for (Integer i = 0; i < partsChartList.size(); i++) {
            series1.getData().add(new XYChart.Data(monthsNames.get(partsChartList.get(i) - 1), partsChartListTotal.get(i)));
        }
        series1.setName(selectionPartsCombobox.getSelectionModel().getSelectedItem());
        partsChart.getData().addAll(series1);
    }


}
