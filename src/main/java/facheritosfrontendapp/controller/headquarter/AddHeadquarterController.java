package facheritosfrontendapp.controller.headquarter;

import backend.connectionBD.ConnectionBD;
import backend.dto.city.CityDTO;
import backend.dto.headquarterDTO.HeadquarterDTO;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.loginEndpoint.LoginEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
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
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class AddHeadquarterController implements Initializable {


    private DashboardController dashboardController;

    private HeadquarterController headquarterController;

    @FXML
    private Button addHeadquarter;

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

    private HeadquarterEndpoint headquarterEndpoint;

    /*Add headquarter*/
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,"?", ButtonType.YES, ButtonType.NO);

    /*Add headquarters window functions*/
    @FXML
    protected void cancelButtonClicked() throws IOException, ExecutionException, InterruptedException {
        confirmation.setContentText("¿Está seguro que desea cancelar?");
        confirmation.setTitle("Confirme su respuesta");
        confirmation.setHeaderText("Cancelar");
        confirmation.showAndWait();
        if(confirmation.getResult() == ButtonType.YES){
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
    protected void saveButtonClicked(){
        confirmation.setContentText("¿Está seguro que desea guardar?");
        confirmation.setTitle("Confirme su respuesta");
        confirmation.setHeaderText("Guardar");
        confirmation.showAndWait();

        if(confirmation.getResult() == ButtonType.YES){

            System.out.println("Sí");
            createHeadquarter();
        } else {
            System.out.println("No");


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
        llenadocombobox();
    }
}
