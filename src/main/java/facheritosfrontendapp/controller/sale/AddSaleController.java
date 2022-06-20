package facheritosfrontendapp.controller.sale;

import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.personEndpoint.PersonEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.views.FxmlLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddSaleController implements Initializable {
    private DashboardController dashboardController;
    private SaleController saleController;
    private FxmlLoader fxmlLoader;

    public static WorkerDTO currentWorker;

    private PersonEndpoint personEndpoint;

    @FXML
    private TextField ccSeller;

    @FXML
    private TextField nameSeller;

    @FXML
    private TextField emailSeller;

    @FXML
    private TextField numberSeller;

    @FXML
    private TextField ccClient;

    @FXML
    private TextField nameClient;

    @FXML
    private TextField emailClient;

    @FXML
    private TextField numberClient;

    @FXML
    private Button editClient;

    @FXML
    private Button searchClient;

    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    @FXML
    private ComboBox<HeadquarterView> headquarterCombobox;

    @FXML
    private ComboBox<String> typeCombobox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DashboardController.getCurrentWorker().getId_worker();
        dashboardController = MainController.getDashboardController();
        setCurrentWorker(DashboardController.getCurrentWorker());
    }

    public AddSaleController() {
        fxmlLoader = new FxmlLoader();
        headquarterComboboxList = new ArrayList<>();
        headquarterEndpoint = new HeadquarterEndpoint();
        personEndpoint = new PersonEndpoint();
    }

    public static synchronized WorkerDTO getCurrentWorker() {
        return currentWorker;
    }

    public void setCurrentWorker(WorkerDTO currentWorker) {
        this.currentWorker = currentWorker;
    }

    public void setSeller() throws SQLException, IOException {
        ccSeller.setEditable(false);
        nameSeller.setEditable(false);
        emailSeller.setEditable(false);
        numberSeller.setEditable(false);
        nameClient.setEditable(false);
        emailClient.setEditable(false);
        numberClient.setEditable(false);

        editClient.setDisable(true);
        editClient.setStyle("-fx-background-color: #C24E59; ");


        ccSeller.setText(currentWorker.getCc());
        nameSeller.setText(currentWorker.getFirst_name()+" "+currentWorker.getLast_name());
        emailSeller.setText(currentWorker.getEmail());
        numberSeller.setText(currentWorker.getCellphone());
    }


    public void setView() {
        typeCombobox.setItems(FXCollections.observableArrayList("Tarjeta de credito","Efectivo"));
        try {
            showHeadquarters();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

      public void showHeadquarters() throws ExecutionException, InterruptedException {

        new Thread(() -> {
            //Set the call to the DB.
            CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

            //Use the response from the BD to fill the combobox
            try {
                headquartersCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setHeadquarterCombobox(resultSet);
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

    public void setHeadquarterCombobox(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarterCombobox.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }
    @FXML
    protected void searchClientClicked(){
        showClient(ccClient.getText());
    }

    public void showClient(String ccClient){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> personEndpoint.getPersonById(ccClient));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setClientData(resultSet);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
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

    public void setClientData(ResultSet resultSet) throws SQLException, IOException {

        nameClient.setText(resultSet.getString("first_name")+" "+resultSet.getString("last_name"));
        numberClient.setText(resultSet.getString("cellphone"));
        emailClient.setText(resultSet.getString("email"));

        ccClient.setEditable(false);
        searchClient.setDisable(false);
        searchClient.setStyle("-fx-background-color: #C24E59; ");

        editClient.setDisable(false);
        editClient.setStyle("-fx-background-color: #C02130; ");
    }

    @FXML
    public void editClientClicked(){
        System.out.println("llegue");
        ccClient.setText("");
        nameClient.setText("");
        numberClient.setText("");
        emailClient.setText("");

        editClient.setDisable(true);
        editClient.setStyle("-fx-background-color: #C24E59; ");

        searchClient.setDisable(false);
        searchClient.setStyle("-fx-background-color: #C02130; ");
        ccClient.setEditable(true);

    }
}