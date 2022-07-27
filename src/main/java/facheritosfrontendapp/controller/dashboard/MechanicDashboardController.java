package facheritosfrontendapp.controller.dashboard;

import backend.endpoints.dashboardEndpoint.MechanicDashboardEnpoint;
import backend.endpoints.inventoryEndpoint.PartEndpoint;
import facheritosfrontendapp.ComboBoxView.ModelView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
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
    private ComboBox<?> partsCombobox;

    @FXML
    private ComboBox<String> selectionPartsCombobox;
    @FXML
    private ChoiceBox<String> yearPartChoicebox;
    private ArrayList<String> monthsNames;
    private ArrayList<String> years;
    private ArrayList<String> partComboboxList;
    private MechanicDashboardEnpoint mechanicDashboardEnpoint;
    private PartEndpoint partEndpoint;

    public MechanicDashboardController(){
        mechanicDashboardEnpoint = new MechanicDashboardEnpoint();
        partEndpoint = new PartEndpoint();
        years = new ArrayList<>(
                Arrays.asList("2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015"));

        partComboboxList = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void partsViewClicked(MouseEvent event) {

    }
    /**
     * showDashboard: void ->  void
     * Purpose: This method shows everything in the view from one single function
     */
    public void showDashboard() {
        setChoiceBox();
        preselectOptions();
        //salesSeeClicked();
        customersViewClicked();
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
                                // modelsViewClicked(); //Show the chart data
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
            Integer idModel = resultSet.getInt("id_part");
            String name = resultSet.getString("name");
            partComboboxList.add(new ModelView(idModel, name));
        }
        modelsCombobox.setItems(FXCollections.observableArrayList(modelComboboxList));
    }


}
