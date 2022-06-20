package facheritosfrontendapp.controller.inventory;

import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.views.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InventoryPartController implements Initializable {

    @FXML
    private Label header;

    @FXML
    private Button editBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField name;

    @FXML
    private TextField price;

    @FXML
    private TextField quantity;

    @FXML
    private TextField headquarter;

    @FXML
    private TextArea description;

    private InventoryEndpoint inventoryEndpoint;

    private InventoryController inventoryController;

    private DashboardController dashboardController;

    public InventoryPartController(){
        inventoryEndpoint = new InventoryEndpoint();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();
    }

    @FXML
    void backArrowClicked(MouseEvent event) throws IOException {
        inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
        inventoryController.showInventory();
        inventoryController.selectionTabpane(1); //parts tab
    }

    @FXML
    void editClicked(MouseEvent event) {
        name.setEditable(true);
        headquarter.setEditable(true);
        price.setEditable(true);
        quantity.setEditable(true);
        description.setEditable(true);
        header.setText("Editar repuesto");
        deleteBtn.setVisible(false);
        editBtn.setVisible(false);
        cancelBtn.setVisible(true);
        saveBtn.setVisible(true);
    }
    /**
     * showPartData: Integer -> void
     * Purpose: This method contains all the other methods that together make showing the part data possible
     */
    public void showPartData(Integer idPart){
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> partCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getPartById(idPart));
            try {
                partCall.thenApply((response) -> {
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
    /**
     * setData: ResultSet -> void
     * Purpose: This method contains sets all the data from the specific vehicle
     */
    public void setData(ResultSet resultSet) throws SQLException, IOException {
        name.setText(resultSet.getString("name"));
        headquarter.setText(resultSet.getString("hq"));
        price.setText(resultSet.getString("price"));
        quantity.setText(resultSet.getString("quantity"));
        description.setText(resultSet.getString("description"));

    }


}
