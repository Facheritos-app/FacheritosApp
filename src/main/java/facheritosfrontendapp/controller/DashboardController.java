package facheritosfrontendapp.controller;

import backend.dto.loginDTO.LoginDTO;
import backend.dto.personDTO.WorkerDTO;
import backend.endpoints.loginEndpoint.LoginEndpoint;

import facheritosfrontendapp.controller.dashboard.ManagerDashboardController;
import facheritosfrontendapp.controller.dashboard.MechanicDashboardController;
import facheritosfrontendapp.controller.dashboard.SellerDashboardController;
import facheritosfrontendapp.controller.profile.MyProfileViewController;
import facheritosfrontendapp.views.FxmlLoader;
import facheritosfrontendapp.views.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DashboardController implements Initializable {

    private String currentPage;

    private Boolean currentPageWithScrollpane;

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

    public static WorkerDTO currentWorker;

    public Stage stage;

    private MyProfileViewController myProfileViewController;

    private SellerDashboardController sellerDashboardController;

    private ManagerDashboardController managerDashboardController;

    private MechanicDashboardController mechanicDashboardController;

    public DashboardController() {
        loginEndpoint = new LoginEndpoint();
    }

    /**
     * logOutClicked: void -> void
     * Purpose: displays the login screen when pressing the logout button
     */
    @FXML
    public void logOutClicked() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        borderPane.setCenter(fxmlLoader.load());
        borderPane.setLeft(null);
        borderPane.setTop(null);
    }

    /**
     * setDashBoard: LoginDTO -> Boolean
     * Purpose: This method contains the call to the BD (line 59) and sets the currentWorker on the dashboard,
     * then it sets the rol and the right navbar for the user.
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
                setRol(currentWorker.getRol());
                try {
                    selectNavbar();
                    selectDashboard();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Error de login");
            }
            return true;
        }).handle((result, ex) -> null != ex ? false : true).get();
    }

    /**
     * changeContent: String -> Object.
     * Purpose: this method changes the content of the main page
     */

    public Object changeContent(String subpage) throws IOException {
        FxmlLoader loader = new FxmlLoader();
        borderPane.setCenter(loader.getPage(subpage));
        currentPage = subpage;
        currentPageWithScrollpane = false;
        return loader.getController();
    }
    /**
     * changeContent: String -> Object.
     * Purpose: this method changes the content of the main page but with a scrollpane
     */
    public Object changeContent(String subpage, boolean withScrollpane) throws IOException {
        FxmlLoader loader = new FxmlLoader();
        if(withScrollpane) {
            borderPane.setCenter(loader.getPage(subpage, true));
            currentPageWithScrollpane = true;
        } else {
            borderPane.setCenter(loader.getPage(subpage));
            currentPageWithScrollpane = false;
        }
        currentPage = subpage;
        return loader.getController();
    }


    /**
     * sendLogin: LoginDTO -> Map<Boolean, WorkerDTO>
     * Purpose: this method sends the credentials to the BD and returns the response
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
     * setRol: String -> void
     * Purpose: this method sets all the information about the user on the dashboard.
     */
    protected void setRol(String rolUser) {
        String trimName = currentWorker.getFirst_name();
        name.setText(trimName.contains(" ") ? trimName.split(" ")[0] : trimName);
        rol.setText(rolUser);
    }

    /**
     * selectNavbar: void -> void
     * Purpose: this method decides which navbar show depending on the user's rol.
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
                throw new RuntimeException("Wrong user type");
        }

    }

    /**
     * selectDashboard: void -> void
     * Purpose: this method decides which dashboard to show depending on the user's rol.
     */
    protected void selectDashboard() throws IOException {
        switch (currentWorker.getId_rol()) {
            //Manager
            case 1:
                managerDashboardController = (ManagerDashboardController) changeContent("dashboard/managerDashboard", true);
                managerDashboardController.showDashboard();
                break;
            //Seller
            case 2:
                sellerDashboardController = (SellerDashboardController) changeContent("dashboard/sellerDashboard", true);
                sellerDashboardController.showDashboard();
                break;
            //Mechanic
            case 3:
                mechanicDashboardController = (MechanicDashboardController) changeContent("dashboard/mechanicDashboard", true);
                mechanicDashboardController.showDashboard();
                break;
            default:
                throw new RuntimeException("Wrong user type");
        }

    }

    @FXML
    protected void myProfileClicked() throws IOException {
        myProfileViewController = (MyProfileViewController) changeContent("profile/myProfileView");
        myProfileViewController.showData(currentWorker, currentPage, currentPageWithScrollpane);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public static synchronized WorkerDTO getCurrentWorker() {
        return currentWorker;
    }

    public void setCurrentWorker(WorkerDTO currentWorker) {
        this.currentWorker = currentWorker;
    }
}

