package facheritosfrontendapp.controller.sale;

import backend.dto.personDTO.WorkerDTO;
import backend.dto.saleDTO.SaleDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.personEndpoint.PersonEndpoint;
import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.controller.quotation.QuotationController;
import facheritosfrontendapp.objectRowView.saleRowView.SaleCarRowView;
import facheritosfrontendapp.objectRowView.saleRowView.SaleSingleRowView;
import facheritosfrontendapp.validator.addSaleValidator.AddSaleValidator;
import facheritosfrontendapp.views.FxmlLoader;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import javax.naming.SizeLimitExceededException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

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
    private Label idSaleLabel;

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

    @FXML
    private Label deleteLabel;

    @FXML
    private Label addLabel;

    @FXML
    private Label noQuantity;

    private HeadquarterEndpoint headquarterEndpoint;

    private SaleEndpoint saleEndpoint;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    @FXML
    private ComboBox<HeadquarterView> headquarterCombobox;

    @FXML
    private ComboBox<String> typeCombobox;

    private ArrayList<SaleCarRowView> saleCarRowsArray;

    private ArrayList<SaleCarRowView> saleCarRowsArray2;

    private ObservableList<SaleCarRowView> saleCarObList;

    private ObservableList<SaleCarRowView> saleCarObList2;

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
    private TableColumn<SaleCarRowView, Integer> colQuantity1;

    @FXML
    private TableColumn<SaleCarRowView, String> colYear;

    @FXML
    private TableColumn<SaleCarRowView, String> colYear1;

    @FXML
    private TableView carTableView;

    @FXML
    private TableView carTableViewSell;

    public Integer contador;

    private SaleSingleViewController saleSingleViewController;

    @FXML
    private Label noFound;

    @FXML
    private Label noFoundTabla;

    private String clientCC;

    private AddSaleValidator inputValidator;

    private SaleDTO saleDTOcurrent;

    private ArrayList<SaleCarRowView> saleCarRowViewCurrent;

    private SaleDTO saleDTOnew;

    private ArrayList<SaleCarRowView> saleCarRowViewNew;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DashboardController.getCurrentWorker().getId_worker();
        dashboardController = MainController.getDashboardController();
        setCurrentWorker(DashboardController.getCurrentWorker());
        contador = 0;
        inputValidator = new AddSaleValidator();
        saleDTOcurrent = new SaleDTO();
        saleCarRowViewCurrent = new ArrayList<>();

        saleDTOnew = new SaleDTO();
        saleCarRowViewNew = new ArrayList<>();
        //setSellTableView();
    }

    public EditSaleController() {
        fxmlLoader = new FxmlLoader();
        headquarterComboboxList = new ArrayList<>();
        headquarterEndpoint = new HeadquarterEndpoint();
        saleEndpoint = new SaleEndpoint();
        saleCarRowsArray = new ArrayList<>();
        saleCarRowsArray2 = new ArrayList<>();
        personEndpoint = new PersonEndpoint();
        saleCarObList = FXCollections.observableArrayList();
        saleCarObList2 = FXCollections.observableArrayList();
        //saleSingleViewController = new SaleSingleViewController();
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
        idSaleLabel.setText(String.valueOf(idSale));
        //cantidad.setText(String.valueOf(contador));
        //priceLabel.setText(String.valueOf(contador));

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
        clientCC = resultSet.getString("cc_client");

        nameClient.setText(resultSet.getString("name_client"));
        numberClient.setText(resultSet.getString("cellphone_client"));
        emailClient.setText(resultSet.getString("email_client"));

       typeCombobox.setValue(resultSet.getString("name_method"));

    }
    @FXML
    protected void backArrowClicked() throws ExecutionException, InterruptedException, IOException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();

        if (clickedButton.get() == YES) {
            saleSingleViewController = (SaleSingleViewController) dashboardController.changeContent("sales/salesSingleView", true);
            saleSingleViewController.showSaleData(Integer.valueOf(idSaleLabel.getText()));
            saleSingleViewController.showQuantity(Integer.valueOf(idSaleLabel.getText()));
            saleSingleViewController.showSaleCars(Integer.valueOf(idSaleLabel.getText()));
        }
    }


    public void setView() {
        typeCombobox.setItems(FXCollections.observableArrayList("Tarjeta de credito","Efectivo"));
    }

    @FXML
    protected  void cancelClicked() throws IOException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();

        if (clickedButton.get() == YES) {
            saleSingleViewController = (SaleSingleViewController) dashboardController.changeContent("sales/salesSingleView", true);
            saleSingleViewController.showSaleData(Integer.valueOf(idSaleLabel.getText()));
            saleSingleViewController.showQuantity(Integer.valueOf(idSaleLabel.getText()));
            saleSingleViewController.showSaleCars(Integer.valueOf(idSaleLabel.getText()));
        }
    }

    @FXML
    protected  void saveClicked() throws IOException, ExecutionException, InterruptedException, SQLException {
        if(allValidations()) {
            MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
            Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
            if (clickedButton.get() == ButtonType.YES) {

                saleDTOnew.setCcSeller(ccSeller.getText());
                saleDTOnew.setCcClient(ccClient.getText());
                saleDTOnew.setPayment_method(typeCombobox.getValue());
                saleCarRowViewNew = saleCarRowsArray2;
                saleDTOnew.setId_sale(Integer.valueOf(idSaleLabel.getText()));
                createTableSale();

                if(compareQuotationsCars() && compareQuotationsData()){
                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "No hay cambios para hacer", OK);
                        success.show();
                }else{

                    try{

                        /*
                        saleEndpoint.updateVenta(saleDTOnew);
                        System.out.println(saleEndpoint.updateVenta(saleDTOnew));
                        //Boolean response = saleEndpoint.updateVenta(saleDTOnew);*/




                        new Thread(() -> {
                            CompletableFuture<Boolean> updateQuotationCall = CompletableFuture.supplyAsync(() -> saleEndpoint.updateVenta(saleDTOnew));
                            try {
                                if(updateQuotationCall.get()){
                                    borrarCarrosNew();

                                }

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();

                            try {
                                saleController = (SaleController) dashboardController.changeContent("sales/sales");
                                saleController.showSales();
                                Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Solicitud enviada al gerente", OK);
                                success.show();
                                /*
                                No hay buena concurrencia
                                saleSingleViewController = (SaleSingleViewController) dashboardController.changeContent("sales/salesSingleView", true);
                                saleSingleViewController.showSaleData(Integer.valueOf(idSaleLabel.getText()));
                                saleSingleViewController.showQuantity(Integer.valueOf(idSaleLabel.getText()));
                                saleSingleViewController.showSaleCars(Integer.valueOf(idSaleLabel.getText()));
                                saleSingleViewController.alter();*/
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                    }catch (Exception e){

                    }
                }







            } else {



            }
        }
    }

    //actualizar los carro de las ventas
    public void borrarCarrosNew(){
        for(int i =0;i < saleCarRowViewCurrent.size(); i++){
            if(buscaR(saleCarRowsArray2,saleCarRowViewCurrent.get(i).getIdCar())){

                if(buscaR2(saleCarRowsArray2,saleCarRowViewCurrent.get(i).getIdCar(),saleCarRowViewCurrent.get(i).getQuantity())){

                }else{

                    //cuidado

                    buscar3(saleCarRowsArray,saleCarRowViewCurrent.get(i).getIdCar());
                }
            }else{

                saleEndpoint.borrarCarros(saleDTOnew,saleCarRowViewCurrent.get(i).getIdCar());
                saleEndpoint.changeCarsQuantityReject(saleCarRowViewCurrent.get(i).getIdCar(),saleCarRowViewCurrent.get(i).getQuantity(),saleDTOcurrent.getId_headquarter());
            }
        }

        for(int i =0;i < saleCarRowsArray2.size(); i++){
            if(buscaR(saleCarRowViewCurrent,saleCarRowsArray2.get(i).getIdCar())){

                if(buscaR2(saleCarRowViewCurrent,saleCarRowsArray2.get(i).getIdCar(),saleCarRowsArray2.get(i).getQuantity())){

                }else{

                    //cuidado
                    saleEndpoint.updateSaleCar(saleCarRowsArray2.get(i).getQuantity(),saleCarRowsArray2.get(i).getIdCar(),Integer.valueOf(idSaleLabel.getText()));

                }

            }else{

                saleEndpoint.insertarCarros(saleCarRowsArray2.get(i).getIdCar(), Integer.valueOf(idSaleLabel.getText()),saleCarRowsArray2.get(i).getQuantity());
                saleEndpoint.changeCarsQuantityRestar(saleCarRowsArray2.get(i).getIdCar(), Double.valueOf(saleCarRowsArray2.get(i).getQuantity()),saleDTOcurrent.getId_headquarter());
            }
        }
    }

    private Boolean buscaR( ArrayList<SaleCarRowView> saleCarRowsView, Integer id){
        Boolean saber = false;
        for (Integer i =0;i <  saleCarRowsView.size() ;i++ ){
            if(id==saleCarRowsView.get(i).getIdCar()){
                return true;
            }
        }
        return saber;
    }

    private Boolean buscaR2( ArrayList<SaleCarRowView> saleCarRowsView, Integer id, Integer quantity){
        Boolean saber = false;
        for (Integer i =0;i <  saleCarRowsView.size() ;i++ ){
            if(id==saleCarRowsView.get(i).getIdCar()){
                if(saleCarRowsView.get(i).getQuantity()==quantity){
                    return true;
                }else {
                    return false;
                }

            }
        }
        return saber;
    }

    private void buscar3( ArrayList<SaleCarRowView> saleCarRowsView, Integer id){
        for (Integer i =0;i <  saleCarRowsView.size() ;i++ ){
            if(id==saleCarRowsView.get(i).getIdCar()){
                saleEndpoint.updateSaleHeadquarter(saleCarRowsArray.get(i).getQuantity(),saleCarRowsArray.get(i).getIdCar(),saleDTOcurrent.getId_headquarter());
            }
        }
    }
    private Boolean buscar( ArrayList<SaleCarRowView> saleCarRowsView, Integer id){
        Boolean saber = false;
        for (Integer i =0;i <  saleCarRowsView.size() ;i++ ){
            if(id==saleCarRowsArray2.get(i).getIdCar()){
                return true;
            }
        }
        return saber;
    }

    private Boolean buscar2( ArrayList<SaleCarRowView> saleCarRowsView, Integer id, Integer quantity){
        Boolean saber = false;
        for (Integer i =0;i <  saleCarRowsView.size() ;i++ ){
            if(id==saleCarRowsArray2.get(i).getIdCar()){
                if(saleCarRowsArray2.get(i).getQuantity()==quantity){
                    return true;
                }else {
                    return false;
                }

            }
        }
        return saber;
    }



    public Boolean compareQuotationsData(){


        if((saleDTOcurrent.getCcClient().equals(saleDTOnew.getCcClient())) &&
                (saleDTOcurrent.getCcSeller().equals(saleDTOnew.getCcSeller())) &&
                (saleDTOcurrent.getPayment_method().equals(saleDTOnew.getPayment_method()))){

            return true;
        }

        return false;

    }

    public Boolean compareQuotationsCars(){


        if(saleCarRowViewCurrent.size() != saleCarRowViewNew.size()){

            return false;

        }else{
            for (Integer i =0; i < saleCarRowViewCurrent.size() ;i++){
                if(buscar2(saleCarRowViewNew,saleCarRowViewCurrent.get(i).getIdCar(),saleCarRowViewCurrent.get(i).getQuantity())){

                }else{

                    return false;
                }
            }
        }
        return true;
    }



    public Boolean allValidations() {
        cleanErrors();
        Boolean everythingCorrect = true;

        if (nameClient.getText().isEmpty()) {
            everythingCorrect = false;
            noFound.setText("Ingrese un cliente, por favor");
            inputValidator.setErrorStyles(searchClient, noFound);
        }
        /*
        if (!inputValidator.client(searchClient, noFound, "Ingrese un cliente, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(searchClient, noFound);
        }*/

        if (saleCarRowsArray2.size()==0) {
            everythingCorrect = false;
            noFoundTabla.setText("Por favor ingrese al menos un carro");
            inputValidator.setErrorStyles(carTableViewSell, noFoundTabla);
        }


        return everythingCorrect;
    }


    public void getCurrentSale(SaleDTO saleDTO, ArrayList<SaleCarRowView> saleCarRowView){
        saleDTOcurrent = saleDTO;
        saleCarRowViewCurrent = saleCarRowView;
        saleDTOnew.setId_headquarter(saleDTOcurrent.getId_headquarter());
        saleDTOnew.setId_worker(saleDTOcurrent.getId_worker());
        saleDTOnew.setId_customer(saleDTOcurrent.getId_customer());

    }

    public void cleanErrors() {
        noFound.setText("");
        ccClient.setStyle("");


        noFoundTabla.setText("");
        carTableViewSell.setStyle("");
    }
    @FXML
    public void addCarSell(MouseEvent mouseEvent) throws FileNotFoundException {
        SaleCarRowView selectedCar = (SaleCarRowView) carTableView.getSelectionModel().getSelectedItem();
        if(selectedCar == null){
            //Crear una vista para esto
           /* Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para agregar", OK);
            fail.show();*/
            noQuantity.setText("");
            addLabel.setText("Selecciona un vehiculo para agregar");
            deleteLabel.setText("");
        }else{

            if(selectedCar.getQuantity()<=0){
                /*Alert fail = new Alert(Alert.AlertType.ERROR, "No hay cantidad suficiente", OK);
                fail.show();*/
                noQuantity.setText("Ya no hay carro de ese tipo");
                addLabel.setText("");
                deleteLabel.setText("");

            }else{
                noQuantity.setText("");
                addLabel.setText("");
                deleteLabel.setText("");
                //selectedCar.setQuantity(selectedCar.getQuantity()-1);
                Integer idSelect = selectedCar.getIdCar().intValue();
                for (int i=0 ; i< saleCarRowsArray.size(); i++){
                    if(idSelect==saleCarRowsArray.get(i).getIdCar()){
                        saleCarRowsArray.get(i).setQuantity( Integer.valueOf(saleCarRowsArray.get(i).getQuantity())-1);
                        carTableView.refresh();

                    }
                }

                if(buscar(saleCarRowsArray2,idSelect)){
                    for (int i=0 ; i< saleCarRowsArray2.size(); i++){
                        if(Arrays.asList(saleCarRowsArray2.get(i).getIdCar()).contains(idSelect)){
                            if(idSelect==saleCarRowsArray2.get(i).getIdCar()){

                                saleCarRowsArray2.get(i).setQuantity( Integer.valueOf(saleCarRowsArray2.get(i).getQuantity())+1);
                                carTableViewSell.refresh();
                            }
                        }
                    }

                }else {


                        SaleCarRowView car = new SaleCarRowView(selectedCar.getIdCar(), selectedCar.getModel(), selectedCar.getColor(),
                                selectedCar.getPrice(),
                                1,selectedCar.getDate());

                        saleCarRowsArray2.add(car);
                        carTableViewSell.getItems().add(car);

                        saleCarRowsArray2.get(saleCarRowsArray2.size()-1).setQuantity(1);
                        carTableViewSell.refresh();
                    }

               // carTableViewSell.getItems().add(selectedCar);
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
            /*Alert fail = new Alert(Alert.AlertType.ERROR, "Selecciona un vehículo para eliminar", OK);
            fail.show();*/
            noQuantity.setText("");
            addLabel.setText("");
            deleteLabel.setText("Selecciona un vehiculo para eliminar");
        }else{
            noQuantity.setText("");
            addLabel.setText("");
            deleteLabel.setText("");

            Integer idSelect = selectedCar.getIdCar().intValue();

            for (int i=0 ; i< saleCarRowsArray.size(); i++){
                if(idSelect==saleCarRowsArray.get(i).getIdCar()){
                    saleCarRowsArray.get(i).setQuantity( Integer.valueOf(saleCarRowsArray.get(i).getQuantity())+1);
                    carTableView.refresh();
                }

            }

            for (int i=0 ; i< saleCarRowsArray2.size(); i++){
                if(idSelect==saleCarRowsArray2.get(i).getIdCar()){
                    if(saleCarRowsArray2.get(i).getQuantity()-1==0){
                        carTableViewSell.getItems().remove(selectedCar);
                        saleCarRowsArray2.remove(i);

                    }else{
                        saleCarRowsArray2.get(i).setQuantity( Integer.valueOf(saleCarRowsArray2.get(i).getQuantity())-1);
                        carTableViewSell.refresh();
                    }

                }

            }

            //selectedCar.setQuantity(selectedCar.getQuantity()+1);
            //carTableViewSell.getItems().remove(selectedCar);
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


                            } catch (SQLException e) {
                                //Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del trabajador o no es una cédula válida, por favor intenta nuevamente", OK);
                                //fail.show();
                                showClient(clientCC);

                                throw new RuntimeException(e);

                            } catch (IOException e) {
                               // Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del trabajador o no es una cédula válida, por favor intenta nuevamente", OK);
                                //fail.show();
                                showClient(clientCC);

                                throw new RuntimeException(e);
                            }

                        }else{
                        Alert fail = new Alert(Alert.AlertType.ERROR, "No se ha encontrado la cedula del trabajador o no es una cédula válida, por favor intenta nuevamente", OK);
                        fail.show();

                        showClient(clientCC);

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

    public SaleDTO createTableSale() throws SQLException {


        Integer pay;
        if(typeCombobox.getValue()  == "Tarjeta de credito"){
            pay=1;
        }else{
            pay =2;
        }

        saleDTOnew.setId_payment_method(pay);

        saleDTOnew.setId_confirmation(1);

        saleDTOnew.setPrice(Double.valueOf(priceLabel.getText()));

        return saleDTOnew;
    }

    public void setClientData(ResultSet resultSet) throws SQLException, IOException {

        try {
            ccClient.setText(resultSet.getString("cc"));
            clientCC = resultSet.getString("cc");

            saleDTOnew.setId_customer(Integer.valueOf(resultSet.getString("id_person")));



            nameClient.setText(resultSet.getString("first_name")+" "+resultSet.getString("last_name"));

            numberClient.setText(resultSet.getString("cellphone"));
            emailClient.setText(resultSet.getString("email"));

            ccClient.setEditable(false);
            searchClient.setDisable(false);
            searchClient.setStyle("-fx-background-color: #C24E59; ");

            editClient.setDisable(false);
            editClient.setStyle("-fx-background-color: #C02130; ");
        }catch (Exception e){
            throw e;
        }

    }

    @FXML
    public void editClientClicked(){

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

    public void showSaleCarsSell(Integer idSale){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> saleEndpoint.getSalesCar(idSale));
            try {
                vehicleCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        Platform.runLater(() -> {
                            try {
                                setDataCarTableSell(resultSet);
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

    public void setDataCarTableSell(ResultSet resultSet) throws SQLException, FileNotFoundException {

       /* (Integer idSale, String nameSeller, String nameClient, Date dateSeller, String paymentMethod,String headquarter,
                Double priceSale)*/
        Double precio= 0.0;
        Integer cantidad2 =0;
        while(resultSet.next()){
            //Create the object that will contain all the data shown on the table
            SaleCarRowView car = new SaleCarRowView(resultSet.getInt("id_car"), resultSet.getString("description"), resultSet.getString("color"),
                    resultSet.getDouble("price"),
                    resultSet.getInt("quantity"),resultSet.getString("assemble_year"));
            saleCarRowsArray2.add(car);
            Integer i=1;
            while(resultSet.getInt("quantity")>=i){
                 //Add every element to the array.
                precio = precio + car.getPrice();
                    cantidad2 = cantidad2 +1;
                i++;
            }

        }
        //cantidad.setText(String.valueOf(saleCarRowsArray2.size()));
        cantidad.setText(String.valueOf(cantidad2));
        priceLabel.setText(String.valueOf(precio));



        //Add every element from our array to the observable list array that will show on the table
        for(Integer i = 0; i < saleCarRowsArray2.size(); i++){
            saleCarObList2.add(saleCarRowsArray2.get(i));
        }

        colId1.setCellValueFactory(new PropertyValueFactory("idCar"));
        colModel1.setCellValueFactory(new PropertyValueFactory("model"));
        colColor1.setCellValueFactory(new PropertyValueFactory("color"));
        colPrice1.setCellValueFactory(new PropertyValueFactory("price"));
        colQuantity1.setCellValueFactory(new PropertyValueFactory("quantity"));
        colYear1.setCellValueFactory(new PropertyValueFactory("date"));

        carTableViewSell.setItems(saleCarObList2);
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