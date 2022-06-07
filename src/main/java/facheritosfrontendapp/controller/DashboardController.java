package facheritosfrontendapp.controller;

import backend.dto.loginDTO.LoginDTO;
import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.loginEndpoint.LoginEndpoint;

import facheritosfrontendapp.views.FxmlLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DashboardController implements Initializable {

    @FXML
    private AnchorPane welcomePane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox navbar;
    @FXML
    private Label name;

    @FXML
    private Label rol;

    @FXML
    private HBox users;

    private LoginEndpoint loginEndpoint;



    private WorkerDTO currentWorker;

    public DashboardController() {
        loginEndpoint = new LoginEndpoint();
    }

    /**
     * This method contains all the requests to the API to set the Dashboard
     * First, we authenticate the user, if successful,
     * we proceed to set the Dashboard by making the request to the API to get their rol.
     */


    public boolean setDashboard(LoginDTO loginDTO) throws ExecutionException, InterruptedException {


        CompletableFuture<Map<Boolean, WorkerDTO>> loginCall = CompletableFuture.supplyAsync(() -> sendLogin(loginDTO));
        /**
         * Comment: on line 94, there's a ternary operator, which means that if there was any exception, return false,
         * which won't let the user log in. Otherwise, return true and let the user log in.
         */
        return loginCall.thenApply((response) -> {
            if(response.containsKey(true)){
                setCurrentWorker(response.get(true)); //Set the worker in the dashboard
                System.out.println(currentWorker.getFirst_name());
                setRol(currentWorker.getRol());
                try {
                    selectNavbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Error de login");
            }
            return true;
        }).handle((result, ex) -> null != ex ? false : true).get();

        //CompletableFuture<Map<Integer, Object>> loginCall = CompletableFuture.supplyAsync(() -> sendLogin(loginDTO));

        /**
        return loginCall.thenAccept((responseLogin) -> {
            if (responseLogin.containsKey(200)) {
                getRol(responseLogin);
            } else {
                ErrorDTO responseError = (ErrorDTO) responseLogin.values().stream().findFirst().get();
                throw new RuntimeException(responseError.getErrorMessage());
            }
        }).thenApply((bool) -> {
            setRol(typePerson.getRol_person());
            try {
                selectNavbar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }).handle((result, ex) -> null != ex ? false : result).get();
         **/
    }

    /**
     * This methods manages the event of clicking on any element of the Navbar
     */

    public void navbarClicked(String subpage) throws IOException {
        borderPane.setCenter(new FxmlLoader().getPage(subpage));
    }


    /**
     * This method sends the credentials to the API, it returns the response from the API modified by the sendCredentials method
     */
    protected Map<Boolean, WorkerDTO> sendLogin(LoginDTO loginDTO) {
        try {
            Map<Boolean, WorkerDTO> responseLogin = loginEndpoint.getCredentials(loginDTO);
            return responseLogin;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This function sets all the information about the user on the dashboard.
     */
    protected void setRol(String rolUser) {
        String trimName = currentWorker.getFirst_name();
        name.setText(trimName.contains(" ") ? trimName.split(" ")[0] : trimName);
        rol.setText(rolUser);
    }

    /**
     * This method decides which navbar show depending on user's rol.
     */
    protected void selectNavbar() throws IOException {
        switch (currentWorker.getId_rol()) {
            //Manager
            case 1:
                navbar.getChildren().add(new FxmlLoader().getPage("navbar/managerNavbar"));
                break;
            //Seller
            case 2:
                navbar.getChildren().add(new FxmlLoader().getPage("navbar/sellerNavbar"));

                break;
            //Mechanic
            case 3:
                navbar.getChildren().add(new FxmlLoader().getPage("navbar/mechanicNavbar"));
                break;
            default:
                /*THIS SHOULD THROW AN ERROR*/
                break;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //System.out.print("CC: "+ loginDTO.getCc());
    }
    public WorkerDTO getCurrentWorker() {
        return currentWorker;
    }

    public void setCurrentWorker(WorkerDTO currentWorker) {
        this.currentWorker = currentWorker;
    }
}

