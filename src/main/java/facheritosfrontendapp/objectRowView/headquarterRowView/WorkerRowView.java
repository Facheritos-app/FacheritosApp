package facheritosfrontendapp.objectRowView.headquarterRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;

public class WorkerRowView {

    private String id;

    private String firstname;

    private String lastname;

    private String rol;

    private String headquarter;

    private VBox options;

    public WorkerRowView(String id, String firstname, String lastname, String rol, String headquarter){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rol = rol;
        this.headquarter = headquarter;
        this.options = new VBox();
        setOptions();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }

    public VBox getOptions() {
        return options;
    }

    public HBox getViewHBox() {return (HBox) options.getChildren().get(0);}

    public Label getViewLabel(){return (Label) getViewHBox().getChildren().get(1);}

    public HBox getEditHBox(){
        return (HBox) options.getChildren().get(1);
    }

    public Label getEditLabel(){
        return (Label) getEditHBox().getChildren().get(1);
    }

    public HBox getDeleteHBox(){
        return (HBox) options.getChildren().get(2);
    }

    public Label getDeleteLabel(){
        return (Label) getDeleteHBox().getChildren().get(1);
    }


    public void setOptions(VBox options) {
        this.options = options;
    }

    public void setOptions() {
        URL iconViewURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-view.png");
        URL iconDeleteURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-delete.png");
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image deleteImage = new Image(String.valueOf(iconDeleteURL));
        Image editImage = new Image(String.valueOf(iconEditURL));
        Image viewImage = new Image(String.valueOf(iconViewURL));
        HBox hBoxView = new HBox(new ImageView(viewImage), new Label("Ver"));
        HBox hboxEdit = new HBox(new ImageView(editImage), new Label("Editar"));
        HBox hboxDelete = new HBox(new ImageView(deleteImage), new Label("Eliminar"));
        hBoxView.setAlignment(Pos.CENTER);
        hboxEdit.setAlignment(Pos.CENTER);
        hboxDelete.setAlignment(Pos.CENTER);
        options.getChildren().add(hBoxView);
        options.getChildren().add(hboxEdit);
        options.getChildren().add(hboxDelete);
        options.setAlignment(Pos.CENTER);
    }
}
