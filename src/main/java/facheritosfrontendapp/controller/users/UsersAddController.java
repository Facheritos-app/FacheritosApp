package facheritosfrontendapp.controller.users;

import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import facheritosfrontendapp.controller.headquarters.HeadquartersController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UsersAddController implements Initializable {

    @FXML
    private ComboBox<String> typeCombobox;
    @FXML
    private ComboBox<String> headquarterCombobox;
    private HeadquarterEndpoint headquarterEndpoint;

    private ArrayList<String> headquarterComboboxList;

    public UsersAddController(){
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<String>();
    }
    @FXML
    public void cancelButtonAddUserClicked(MouseEvent mouseEvent) {
    }
    @FXML
    public void saveButtonAddUserClicked(MouseEvent mouseEvent) {
    }

    /**
     * showHeaders: void -> void
     * Purpose: This method contains all the steps to show all the headquarters in the combobox
     */
    public void showHeadquarters() throws ExecutionException, InterruptedException {
        //Set the call to the DB.
        CompletableFuture<Map<Boolean, ResultSet>> headquartersCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

        //Use the response from the BD to fill the combobox
        headquartersCall.thenApply((response) -> {
            if(response.containsKey(true)){
                ResultSet resultSet = response.get(true);
                try {
                    setHeadquarterCombobox(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }).get();
    }
    /**
     * showHeaders: ResultSet -> void
     * Purpose: This method set the items of the headquarters combobox according to the DB
     */
    public void setHeadquarterCombobox(ResultSet resultSet) throws SQLException {
        while(resultSet.next()) {
            headquarterComboboxList.add(resultSet.getString("name"));
        }
        headquarterCombobox.setItems(FXCollections.observableArrayList(headquarterComboboxList));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set items for Rol combobox
        typeCombobox.setItems(FXCollections.observableArrayList("Gerente", "Vendedor", "Jefe de taller"));
        try {
            this.showHeadquarters();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
