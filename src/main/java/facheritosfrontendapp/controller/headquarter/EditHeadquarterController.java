package facheritosfrontendapp.controller.headquarter;

import backend.connectionBD.ConnectionBD;
import backend.dto.city.CityDTO;
import backend.dto.headquarterDTO.HeadquarterDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.saleEndpoint.SaleEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;

public class EditHeadquarterController implements Initializable {


    private DashboardController dashboardController;

    private HeadquarterController headquarterController;

    private AddHeadquarterValidator inputValidator;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nam;

    @FXML
    private TextField addr;

    @FXML
    private TextField cellp;

    @FXML
    private TextField eml;

    @FXML
    private ChoiceBox city;

    @FXML
    private Label nameLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label cellphoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label cityLabel;
    private HeadquarterEndpoint headquarterEndpoint;


    /*Add headquarter*/

    public EditHeadquarterController() {
        headquarterEndpoint = new HeadquarterEndpoint();

    }


    /*Add headquarters window functions*/
    @FXML
    protected void cancelButtonClicked() throws IOException, ExecutionException, InterruptedException {
        MyDialogPane dialogPane = new MyDialogPane("confirmationCancel");
        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
        if(clickedButton.get() == ButtonType.YES){
            headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
            headquarterController.showHeadquarters();
        } else {
            System.out.println("No");
        }

    }

    public void createHeadquarter(){
     HeadquarterDTO headquarterDTO = new HeadquarterDTO();
     CityDTO cityDTO = new CityDTO();
     cityDTO.setCity_name((String) city.getValue());
     headquarterDTO.setId_city(idCity((String) city.getValue(),cityDTO));
     //headquarterDTO.setId_city(1);
     headquarterDTO.setName(nam.getText());
     headquarterDTO.setCellphone(cellp.getText());
     headquarterDTO.setEmail(eml.getText());
     headquarterDTO.setAddress(addr.getText());
     headquarterEndpoint.createHeadquarter(headquarterDTO);
    }

    public Integer idCity(String city, CityDTO cityDTO){
        System.out.println(city);
        System.out.println(cityDTO.getCity_name());
        try {
            return headquarterEndpoint.getCity(city,cityDTO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML

    protected void saveButtonClicked() throws IOException, ExecutionException, InterruptedException {
        if(allValidations()) {
            MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
            Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
            if (clickedButton.get() == ButtonType.YES) {

                try{
                    createHeadquarter();
                   // headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
                    //headquarterController.showHeadquarters();
                    Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Sede agregada exitosamente", OK);
                    success.show();
                    //Go to main user view
                    try {
                        headquarterController = (HeadquarterController) dashboardController.changeContent("headquarters/headquarters");
                        //Show users in table
                        headquarterController.showHeadquarters();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }catch (Exception e){

                }




            } else {
                System.out.println("No");


            }
        }
    }

    public void llenadocombobox() {
        ObservableList<String> listacombo= FXCollections.observableArrayList();
        String query = "select city_name from city";
        PreparedStatement ps=null;
        ResultSet rs=null;
        try(Connection conn = ConnectionBD.connectDB().getConnection()){
            ps=conn.prepareStatement(query);
            rs=ps.executeQuery();

            while(rs.next()) {

                listacombo.add(rs.getString("city_name"));


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        city.getItems().addAll(listacombo);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
        inputValidator = new AddHeadquarterValidator();

        llenadocombobox();
    }

    public void showHeadquarterData(Integer idHeadquarter){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> vehicleCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarterById(idHeadquarter));
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

        nam.setText(resultSet.getString("name"));
        addr.setText(resultSet.getString("address"));
        cellp.setText(resultSet.getString("cellphone"));
        eml.setText(resultSet.getString("email"));
        city.setValue(resultSet.getString("city_name"));

    }

    public Boolean allValidations() {
        cleanErrors();
        Boolean everythingCorrect = true;
        if (!inputValidator.name(nam, nameLabel, "Escriba un nombre valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(nam, nameLabel);
        }

        if (!inputValidator.email(eml, emailLabel, "Escriba un correo valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(eml, emailLabel);
        }

        if (!inputValidator.cellphone(cellp, cellphoneLabel, "Escriba un numero valido, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(cellp, cellphoneLabel);
        }

        if (!inputValidator.addr(addr, addressLabel, "Escriba una direccion valida, por favor")) {
            everythingCorrect = false;
            inputValidator.setErrorStyles(addr, addressLabel);
        }

        if (city.getSelectionModel().isEmpty()) {
            everythingCorrect = false;
            cityLabel.setText("Por favor indique una ciudad");
            inputValidator.setErrorStyles(city, cityLabel);
        }

        return everythingCorrect;
    }

    public void cleanErrors() {
        nameLabel.setText("");
        nam.setStyle("");
        emailLabel.setText("");
        eml.setStyle("");
        cellphoneLabel.setText("");
        cellp.setStyle("");
        addressLabel.setText("");
        addr.setStyle("");
        cityLabel.setText("");
        city.setStyle("");

    }
}
