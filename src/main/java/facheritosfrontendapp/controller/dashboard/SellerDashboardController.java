package facheritosfrontendapp.controller.dashboard;

import backend.endpoints.dashboardEndpoint.SellerDashboardEndpoint;
import backend.endpoints.inventoryEndpoint.ModelEndpoint;
import facheritosfrontendapp.ComboBoxView.ModelView;
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
    private BarChart<?, ?> customersChart;

    @FXML
    private BarChart modelsChart;

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

    //Top customers chart
    @FXML
    private ComboBox<String> selectionCustomersCombobox;
    @FXML
    private ChoiceBox<String> yearCustomersChoicebox;
    @FXML
    private ComboBox<String> idCombobox;

    @FXML
    private ComboBox<ModelView> modelsCombobox;

    @FXML
    private ComboBox<String> selectionModelsCombobox;

    @FXML
    private ChoiceBox<String> yearModelChoicebox;

    private SellerDashboardEndpoint sellerDashboardEndpoint;

    private ArrayList<String> categoryList;

    private ArrayList<Integer> categoryTotal;

    private ArrayList<String> monthsNames;

    private ArrayList<String> years;

    private ArrayList<String> customerChartList;

    private ArrayList<Integer> customerChartTotal;

    //Models combobox
    private ModelEndpoint modelEndpoint;

    private ArrayList<ModelView> modelComboboxList;

    //Models chart
    private ArrayList<String> modelChartList;

    private ArrayList<Integer> modelChartTotal;

    public SellerDashboardController() {
        sellerDashboardEndpoint = new SellerDashboardEndpoint();
        modelEndpoint = new ModelEndpoint();
        modelComboboxList = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryTotal = new ArrayList<>();
        monthsNames = new ArrayList<>(
                Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"));
        years = new ArrayList<>(
                Arrays.asList("2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015"));

        customerChartList = new ArrayList<>();
        customerChartTotal = new ArrayList<>();
        modelChartList = new ArrayList<>();
        modelChartTotal = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xAxis.setAnimated(false);
    }

    /**
     * setChoicebox: void -> void
     * Purpose: This method sets the elements of the choicebox to be shown
     */
    public void setChoiceBox() {
        //Sales chart
        daysMonthsYearsCombobox.setItems(FXCollections.observableArrayList("Meses", "Años"));
        yearChoicebox.setItems(FXCollections.observableArrayList(years));
        //Top customers chart
        selectionCustomersCombobox.setItems(FXCollections.observableArrayList("No. compras", "Valor de compra"));
        yearCustomersChoicebox.setItems(FXCollections.observableArrayList(years));
        idCombobox.setItems(FXCollections.observableArrayList("Cédula", "Nombre"));
        //Models chart
        selectionModelsCombobox.setItems(FXCollections.observableArrayList("No. de elementos vendidos", "Valor de elementos vendidos"));
        yearModelChoicebox.setItems(FXCollections.observableArrayList(years));
    }
    /**
     * preselectOptions: void -> void
     * Purpose: This method sets a selected item for the comboboxes and choiceboxes by default
     */
    protected void preselectOptions() {
        //Sales chart
        daysMonthsYearsCombobox.getSelectionModel().select(0); //Months
        yearChoicebox.getSelectionModel().select(0); //2022
        //Top customers chart
        selectionCustomersCombobox.getSelectionModel().select(0); //Number of sales
        yearCustomersChoicebox.getSelectionModel().select(0); //2022
        idCombobox.getSelectionModel().select(0); //id number
        //Models chart
        selectionModelsCombobox.getSelectionModel().select(0); //Number of sales
        yearModelChoicebox.getSelectionModel().select(0); //2022
    }
    /**
     * showDashboard: void ->  void
     * Purpose: This method shows everything in the view from one single function
     */
    public void showDashboard() {
        setChoiceBox();
        preselectOptions();
        salesSeeClicked();
        customersViewClicked();
        showModels();
    }
    /**
     * showModels: void -> void
     * Purpose: This method contains all the steps to show all the models in the combobox
     */
    public void showModels()   {

        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> modelsCall = CompletableFuture.supplyAsync(() -> modelEndpoint.getModels());

            //Use the response from the BD to fill the combobox
            try {
                modelsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setModelCombobox(resultSet);
                            Platform.runLater(() -> {
                                modelsCombobox.getSelectionModel().select(0); //any model
                                modelsViewClicked(); //Show the chart data
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
     * setModelCombobox: ResultSet -> void
     * Purpose: This method set the items of the models combobox according to the DB
     */
    public void setModelCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idModel = resultSet.getInt("id_model");
            String name = resultSet.getString("description");
            modelComboboxList.add(new ModelView(idModel, name));
        }
        modelsCombobox.setItems(FXCollections.observableArrayList(modelComboboxList));
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
                    sellerDashboardEndpoint.amountOfSalesChart(passToEnglish(category), year));
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
     * clearDataToUpdate: void -> void
     * Purpose: This method clears the data from the chart in order to change the parameters.
     * It must be executed before changing the chart parameters.
     */
    protected void clearDataToUpdate(Integer chart) throws Exception {
        switch(chart){
            case 0: //sales chart
                categoryList.clear();
                categoryTotal.clear();
                salesChart.getData().clear();
                salesChart.layout();
                break;
            case 1: //customers chart
                customerChartList.clear();
                customerChartTotal.clear();
                customersChart.getData().clear();
                customersChart.layout();
                break;
            case 2: //models chart
                modelChartList.clear();
                modelChartTotal.clear();
                modelsChart.getData().clear();
                modelsChart.layout();
                break;
            default:
                throw new Exception("ERROR: impossible to clear data from the chart");
        }

    }

    /**
     * setSalesChart: ResultSet, String -> void
     * Purpose: This method uses the data from the query and sets it in the chart
     */
    protected void setSalesChart(ResultSet resultSet, String category) throws Exception {
        clearDataToUpdate(0);
        while (resultSet.next()) {
            categoryList.add(resultSet.getString("cat"));
            categoryTotal.add(resultSet.getInt("total"));
        }
        XYChart.Series series1 = new XYChart.Series();
        switch (category) {
            case "month":
                for (Integer i = 0; i < categoryList.size(); i++) {
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

    /**
     * getCustomersData: String, Integer -> void
     * Purpose: This method connects to the DB and returns the necessary data to build the top customers chart
     */
    public void getCustomerData(Integer selectionType, Integer year, Integer id) {
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> customersCall = CompletableFuture.supplyAsync(() -> sellerDashboardEndpoint.customersChart(selectionType, year, id));
            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                customersCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setCustomersChart(resultSet, selectionType);
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

    @FXML
    protected void customersViewClicked(){
        getCustomerData(selectionCustomersCombobox.getSelectionModel().getSelectedIndex(),
                Integer.valueOf(yearCustomersChoicebox.getSelectionModel().getSelectedItem()), idCombobox.getSelectionModel().getSelectedIndex());
    }

    /**
     * setCustomersChart: ResultSet, Integer -> void
     * Purpose: This method uses the data from the query and sets it in the customers chart
     */
    protected void setCustomersChart(ResultSet resultSet, Integer selectionType) throws Exception {
        clearDataToUpdate(1);
        while (resultSet.next()) {
            customerChartList.add(resultSet.getString("data"));
            customerChartTotal.add(resultSet.getInt("total"));
        }
        XYChart.Series series1 = new XYChart.Series();
        for (Integer i = 0; i < customerChartList.size(); i++) {
            series1.getData().add(new XYChart.Data(customerChartList.get(i), customerChartTotal.get(i)));
        }
        series1.setName(selectionCustomersCombobox.getSelectionModel().getSelectedItem());
        customersChart.getData().addAll(series1);
    }

    @FXML
    protected void modelsViewClicked(){
        getModelsData(modelsCombobox.getSelectionModel().getSelectedItem().getIdModel(),
                selectionModelsCombobox.getSelectionModel().getSelectedIndex(), Integer.valueOf(yearModelChoicebox.getSelectionModel().getSelectedItem()));
    }

    /**
     * getModelsData: String, Integer -> void
     * Purpose: This method connects to the DB and returns the necessary data to build the models chart
     */
    public void getModelsData(Integer idModel, Integer selectionType, Integer year) {
        //This means that another thread different from the JavaFX app thread will update the required items with the data.
        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> modelsCall = CompletableFuture.supplyAsync(() -> sellerDashboardEndpoint.modelsChart(idModel, selectionType, year));
            //Concatenate the response of the previous call from the BD to actually populate the table with the data
            try {
                modelsCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setModelsChart(resultSet);
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
     * setModelsChart: ResultSet, Integer -> void
     * Purpose: This method uses the data from the query and sets it in the models chart
     */
    protected void setModelsChart(ResultSet resultSet) throws Exception {
        clearDataToUpdate(2);
        while (resultSet.next()) {
            modelChartList.add(resultSet.getString("data"));
            modelChartTotal.add(resultSet.getInt("total"));
        }
        XYChart.Series series1 = new XYChart.Series();
        for (Integer i = 0; i < modelChartList.size(); i++) {
            series1.getData().add(new XYChart.Data(monthsNames.get(Integer.parseInt(modelChartList.get(i)) - 1), modelChartTotal.get(i)));
        }
        series1.setName(selectionModelsCombobox.getSelectionModel().getSelectedItem());
        modelsChart.getData().addAll(series1);
    }
}
