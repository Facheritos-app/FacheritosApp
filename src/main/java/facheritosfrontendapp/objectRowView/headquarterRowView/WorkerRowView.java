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

    private Integer idPerson;

    private String id;

    private String firstname;

    private String lastname;

    private String rol;

    private String headquarter;

    private VBox options;

    public WorkerRowView(Integer idPerson, String id, String firstname, String lastname, String rol, String headquarter){
        this.idPerson = idPerson;
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.rol = rol;
        this.headquarter = headquarter;
        this.options = new VBox();
        setOptions();
    }

    public Integer getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(Integer idPerson) {
        this.idPerson = idPerson;
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


    public HBox getOptionsHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public Label getOptionsLabel(){
        return (Label) getOptionsHBox().getChildren().get(1);
    }



    public void setOptions(VBox options) {
        this.options = options;
    }

    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxOptions = new HBox(new ImageView(editImage), new Label("  Mas opciones"));
        hboxOptions.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxOptions);
        options.setAlignment(Pos.CENTER);
    }
}
