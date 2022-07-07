package facheritosfrontendapp.objectRowView.customerRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;

public class CustomerRowView {

    private Integer idPerson;

    private String cc;

    private String firstname;

    private String lastname;

    private String cellphone;

    private VBox options;

    public CustomerRowView(Integer idPerson, String cc, String firstname, String lastname, String cellphone){
        this.idPerson = idPerson;
        this.cc = cc;
        this.firstname = firstname;
        this.lastname = lastname;
        this.cellphone = cellphone;
        this.options = new VBox();
        setOptions();
        getOptionsLabel().setCursor(Cursor.HAND); //Cursor hand when mouse hover
        getEditLabel().setCursor(Cursor.HAND);
    }

    public Integer getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(Integer idPerson) {
        this.idPerson = idPerson;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
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

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
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

    public HBox getEditHBox(){
        return (HBox) options.getChildren().get(1);
    }

    public Label getEditLabel(){
        return (Label) getEditHBox().getChildren().get(1);
    }

    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxOptions = new HBox(new ImageView(editImage), new Label("Ver más"));
        HBox hboxEdit = new HBox(new ImageView(editImage), new Label("Editar"));
        hboxOptions.setSpacing(5);
        hboxOptions.setAlignment(Pos.CENTER);
        hboxOptions.setSpacing(5);
        hboxEdit.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxOptions);
        options.getChildren().add(hboxEdit);
        options.setAlignment(Pos.CENTER);
    }
}
