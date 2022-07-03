package facheritosfrontendapp.controller.sale;

import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.personEndpoint.PersonEndpoint;
import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.objectRowView.saleRowView.SaleCarRowView;
import facheritosfrontendapp.views.FxmlLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;

public class EditSaleController implements Initializable {
    private DashboardController dashboardController;
    private SaleController saleController;
    private FxmlLoader fxmlLoader;

    public static WorkerDTO currentWorker;

    private PersonEndpoint personEndpoint;

    @FXML
    private Label priceLabel;

    @FXML
    private TextField cantidad;

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

    private SaleEndpoint saleEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    @FXML
    private ComboBox<HeadquarterView> headquarterCombobox;

    @FXML
    private ComboBox<String> typeCombobox;

    private ArrayList<SaleCarRowView> saleCarRowsArray;

    private ObservableList<SaleCarRowView> saleCarObList;

    @FXML
    private TableColumn<SaleCarRowView, Integer> colId;

    @FXML
    private TableColumn<SaleCarRowView, VBox> colOption;

    @FXML
    private TableColumn<SaleCarRowView, VBox> colId1;

    @FXML
    private TableColumn<SaleCarRowView, String> colModel;

    @FXML
    private TableColumn<SaleCarRowView, String> colModel1;

    @FXML
    private TableColumn<SaleCarRowView, String> colColor;

    @FXML
    private TableColumn<SaleCarRowView, String> colColor1;

    @FXML
    private TableColumn<SaleCarRowView, Double> colPrice;

    @FXML
    private TableColumn<SaleCarRowView, Double> colPrice1;

    @FXML
    private TableColumn<SaleCarRowView, Integer> colQuantity;

    @FXML
    private TableColumn<SaleCarRowView, String> colYear;

    @FXML
    private TableColumn<SaleCarRowView, String> colYear1;

    @FXML
    private TableView carTableView;

    @FXML
    private TableView carTableViewSell;

    public Integer contador;

    @FXML
    private Label noFound;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DashboardController.getCurrentWorker().getId_worker();
        dashboardController = MainController.getDashboardController();
        setCurrentWorker(DashboardController.getCurrentWorker());
        contador = 0;
        setSellTableView();
    }

    public EditSaleController() {
        fxmlLoader = new FxmlLoader();
        headquarterComboboxList = new ArrayList<>();
        headquarterEndpoint = new HeadquarterEndpoint();
        saleEndpoint = new SaleEndpoint();
        saleCarRowsArray = new ArrayList<>();
        personEndpoint = new PersonEndpoint();
        saleCarObList = FXCollections.observableArrayList();

    }

    public static synchronized WorkerDTO getCurrentWorker() {
        return currentWorker;
    }

    public void setCurrentWorker(WorkerDTO currentWorker) {
        this.currentWorker = currentWorker;
    }

    public void setData(Integer idSale) throws SQLException, IOException {
        ccSeller.setEditable(false);
        nameSeller.setEditable(false);
        emailSeller.setEditable(false);
        numberSeller.setEditable(false);
        nameClient.setEditable(false);
        emailClient.setEditable(false);
        numberClient.setEditable(false);
        ccClient.setEditable(false);

        searchClient.setDisable(true);
        searchClient.setStyle("-fx-background-color: #C24E59; ");
        cantidad.setDisable(true);
        cantidad.setText(String.valueOf(contador));
        priceLabel.setText(String.valueOf(contador));

        showSaleData(idSale);


    }

    public void showSaleData(Integer idSale){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSaleById(idSale));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setData(resultSet);
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



    public void setData(ResultSet resultSet) throws SQLException, IOException {


        ccSeller.setText(resultSet.getString("cc_seller"));
        nameSeller.setText(resultSet.getString("name_seller"));
        emailSeller.setText(resultSet.getString("email_seller"));
        numberSeller.setText(resultSet.getString("cellphone_seller"));

        ccClient.setText(resultSet.getString("cc_client"));
        nameClient.setText(resultSet.getString("name_client"));
        numberClient.setText(resultSet.getString("cellphone_client"));
        emailClient.setText(resultSet.getString("email_client"));

       typeCombobox.setValue(resultSet.getString("name_method"));

    }


    public void setView() {
        typeCombobox.setItems(FXCollections.observableArrayList("Tarjeta de credito","Efectivo"));
    }

    public void setSellTableView() {
        colId1.setCellValueFactory(new PropertyValueFactory<>("idCar"));
        colModel1.setCellValueFactory(new PropertyValueFactory<>("model"));
        colColor1.setCellValueFactory(new PropertyValueFactory<>("color"));
        colPrice1.setCellValueFactory(new PropertyValueFactory<>("price"));
        colYear1.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    @FXML
    public void addCarSell(MouseEvent mouseEvent) {
        SaleCarRowView selectedCar = (SaleCarRowView) carTableView.getSelectionModel().getSelectedItem();
        if(selectedCar == null){
            //Crear una vista para esto
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para agregar", OK);
            fail.show();
        }else{

            if(selectedCar.getQuantity()<=0){
                Alert fail = new Alert(Alert.AlertType.ERROR, "No hay cantidad suficiente", OK);
                fail.show();
            }else{
                selectedCar.setQuantity(selectedCar.getQuantity()-1);
                carTableViewSell.getItems().add(selectedCar);
                cantidad.setText(String.valueOf(Integer.valueOf(cantidad.getText())+1));
                priceLabel.setText(String.valueOf(Double.valueOf(priceLabel.getText())+selectedCar.getPrice()));
            }
        }
    }

    @FXML
    protected void searchClientClicked(){
        showClient(ccClient.getText());
    }

    @FXML
    protected void deleteCarSeller(){
        SaleCarRowView selectedCar = (SaleCarRowView) carTableViewSell.getSelectionModel().getSelectedItem();
        if(selectedCar == null){
            //Crear una vista para esto
            Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para eliminar", OK);
            fail.show();
        }else{
            selectedCar.setQuantity(selectedCar.getQuantity()+1);
            carTableViewSell.getItems().remove(selectedCar);
            cantidad.setText(String.valueOf(Integer.valueOf(cantidad.getText())-1));
            priceLabel.setText(String.valueOf(Double.valueOf(priceLabel.getText())-selectedCar.getPrice()));


        }
    }

    public void showClient(String ccClient){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> personEndpoint.getPersonById(ccClient));
            try {
                vehicleCall.thenApply((response) -> {
                    Platform.runLater(() -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);

                            try {
                                setClientData(resultSet);
                                noFound.setText("");
                            } catch (SQLException e) {
                                noFound.setText("Cliente no encontrado");
                                throw new RuntimeException(e);

                            } catch (IOException e) {
                                noFound.setText("Cliente no encontrado");
                                throw new RuntimeException(e);
                            }

                        }else{
                        noFound.setText("Cliente no encontrado");
                    }
                    });
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

    public void showSaleCars(Integer idHeadquarter){
        System.out.println(idHeadquarter);
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getCar(idHeadquarter));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                             setDataCarTable(resultSet);
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

    private void handleOptionLabel(MouseEvent mouseEvent) {
        for(Integer i = 0; i < saleCarRowsArray.size(); i++){
            if(mouseEvent.getSource() == saleCarRowsArray.get(i).getCheckLabel()){

            }
            if(mouseEvent.getSource() == saleCarRowsArray.get(i).getQuantityLabel()){

            }
        }
    }

    public void setDataCarTable(ResultSet resultSet) throws SQLException, FileNotFoundException {

       /* (Integer idSale, String nameSeller, String nameClient, Date dateSeller, String paymentMethod,String headquarter,
                Double priceSale)*/
        while(resultSet.next()){
            //Create the object that will contain all the data shown on the table
            SaleCarRowView car = new SaleCarRowView(resultSet.getInt("id_car"), resultSet.getString("description"), resultSet.getString("color"),
                    resultSet.getDouble("price"),
                    resultSet.getInt("quantity"),resultSet.getString("assemble_year"));
            saleCarRowsArray.add(car); //Add every element to the array.
        }

        //Set the handle events for the labels
        for(Integer i = 0; i < saleCarRowsArray.size(); i++){
            saleCarRowsArray.get(i).getCheckHBox().setOnMouseClicked(this::handleOptionLabel);
            saleCarRowsArray.get(i).getQuantityHBox().setOnMouseClicked(this::handleOptionLabel);
        }

        //Add every element from our array to the observable list array that will show on the table
        for(Integer i = 0; i < saleCarRowsArray.size(); i++){
            saleCarObList.add(saleCarRowsArray.get(i));
        }

        colId.setCellValueFactory(new PropertyValueFactory("idCar"));
        colModel.setCellValueFactory(new PropertyValueFactory("model"));
        colColor.setCellValueFactory(new PropertyValueFactory("color"));
        colPrice.setCellValueFactory(new PropertyValueFactory("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory("quantity"));
        colYear.setCellValueFactory(new PropertyValueFactory("date"));

        carTableView.setItems(saleCarObList);
    }

}