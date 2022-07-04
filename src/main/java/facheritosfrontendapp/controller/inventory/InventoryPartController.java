package facheritosfrontendapp.controller.inventory;

import backend.dto.inventoryDTO.PartDTO;
import backend.endpoints.headquarterEndpoint.HeadquarterEndpoint;
import backend.endpoints.inventoryEndpoint.InventoryEndpoint;
import facheritosfrontendapp.ComboBoxView.HeadquarterView;
import facheritosfrontendapp.controller.DashboardController;
import facheritosfrontendapp.controller.MainController;
import facheritosfrontendapp.views.Main;
import facheritosfrontendapp.views.MyDialogPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;

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
    private ComboBox<HeadquarterView> headquarter;

    @FXML
    private TextArea description;

    private InventoryEndpoint inventoryEndpoint;

    private InventoryController inventoryController;

    private InventoryPartController inventoryPartController;
    private DashboardController dashboardController;

    private ArrayList<HeadquarterView> headquarterComboboxList;

    private HeadquarterEndpoint headquarterEndpoint;

    private Integer idPart;

    public InventoryPartController(){
        inventoryEndpoint = new InventoryEndpoint();
        headquarterEndpoint = new HeadquarterEndpoint();
        headquarterComboboxList = new ArrayList<>();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardController = MainController.getDashboardController();

        //Only the manager can delete or edit elements of the inventory
        if(DashboardController.getCurrentWorker().getId_rol() != 1) {
            editBtn.setVisible(false);
            deleteBtn.setVisible(false);
        }
    }


    public void setHeadquarterCombo() throws ExecutionException, InterruptedException {
        CompletableFuture<Map<Boolean, ResultSet>> headquarterCall = CompletableFuture.supplyAsync(() -> headquarterEndpoint.getHeadquarters());

        headquarterCall.thenApply((response) -> {
            if(response.containsKey(true)){
                ResultSet resultSet = response.get(true);
                try {
                    fillHeadquarter(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }).get();
    }

    public void fillHeadquarter(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            Integer idHeadquarter = resultSet.getInt("id_headquarter");
            String name = resultSet.getString("name");
            headquarterComboboxList.add(new HeadquarterView(idHeadquarter, name));
        }
        headquarter.setItems(FXCollections.observableArrayList(headquarterComboboxList));
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
        headquarter.setDisable(false);
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
        this.idPart = idPart;
        new Thread(() -> {
            CompletableFuture<Map<Boolean, ResultSet>> partCall = CompletableFuture.supplyAsync(() -> inventoryEndpoint.getPartById(idPart));
            try {
                partCall.thenApply((response) -> {
                    if (response.containsKey(true)) {
                        ResultSet resultSet = response.get(true);
                        try {
                            setHeadquarterCombo();
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Platform.runLater(() -> {
                            try {
                                setData(resultSet);
                            } catch (SQLException | IOException e) {
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
        headquarter.setStyle(resultSet.getString("hq"));
        headquarter.getSelectionModel().select(findHeadquarterById(resultSet.getInt("id_headquarter")));
        price.setText(resultSet.getString("price"));
        quantity.setText(resultSet.getString("quantity"));
        description.setText(resultSet.getString("description"));


    }

    /**
     * findHeadquarterById: Integer -> HeadquarterView
     * Purpose: This method finds a headquarter by its id.
     * It is used to set the headquarter combobox given the id of the headquarter where the worker is settled.
     */
    public HeadquarterView findHeadquarterById(Integer id){
        for(Integer i = 0; i < headquarterComboboxList.size(); i++){
            if(id == headquarterComboboxList.get(i).getIdHeadquarter()){
                return headquarterComboboxList.get(i);
            }
        }
        return new HeadquarterView(-100,"");
    }
    /**
     * findHeadquarterByName: String -> HeadquarterView
     * Purpose: This method finds a headquarter by its name.
     *.
     */
    public HeadquarterView findHeadquarterByName(String name){
        for(Integer i = 0; i < headquarterComboboxList.size(); i++){
            if(name == headquarterComboboxList.get(i).getName()){
                return headquarterComboboxList.get(i);
            }
        }
        return new HeadquarterView(-100,"");
    }


    @FXML
    void cancelClicked(MouseEvent event) {
        name.setEditable(false);
        headquarter.setEditable(false);
        headquarter.setDisable(true);
        price.setEditable(false);
        quantity.setEditable(false);
        description.setEditable(false);
        header.setText("Repuesto");
        deleteBtn.setVisible(true);
        editBtn.setVisible(true);
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
    }

    @FXML
    void deleteClicked(MouseEvent event) {

    }


    @FXML
    void saveClicked(MouseEvent event) {
        if (allValidations()) {
            new Thread(() -> {
                PartDTO partDTO = createPartObject();
                Platform.runLater(() -> {
                    try {
                        MyDialogPane dialogPane = new MyDialogPane("confirmationSave");
                        Optional<ButtonType> clickedButton = dialogPane.getClickedButton();
                        if (clickedButton.get() == YES) {
                            //DB call to save worker
                            new Thread(() -> {
                                Boolean result = null;
                                try {
                                    result = CompletableFuture.supplyAsync(() -> inventoryEndpoint.completeEditPart(partDTO)).get();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                    throw new RuntimeException(e);
                                }
                                if (result) {
                                    Platform.runLater(() -> {
                                        Alert success = new Alert(Alert.AlertType.CONFIRMATION, "Repuesto agregado exitosamente", OK);
                                        success.show();
                                        //Go to main user view
                                        try {
                                            inventoryController = (InventoryController) dashboardController.changeContent("inventory/inventory");
                                            //Show inventory
                                            inventoryController.showInventory();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                                } else {
                                    Alert fail = new Alert(Alert.AlertType.ERROR, "Ha habido un problema, por favor intenta nuevamente", OK);
                                    fail.show();
                                }
                            }).start();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        }
    }

    private PartDTO createPartObject() {
        PartDTO part = new PartDTO();
        part.setIdPart(idPart);
        part.setName(name.getText());
        part.setDescription(description.getText());
        part.setQuantity(Integer.parseInt(quantity.getText()));
        part.setPrice(Double.parseDouble(price.getText()));
        part.setId_headquarter(findHeadquarterByName(String.valueOf(headquarter.getSelectionModel().getSelectedItem())).getIdHeadquarter());
        return part;
    }

    private boolean allValidations() {
        return true;
    }


}
