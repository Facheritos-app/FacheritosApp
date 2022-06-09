package facheritosfrontendapp.objectRowView.headquarterRowView;

import facheritosfrontendapp.views.Main;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.net.URL;

public class HeadquarterRowView {

    private Integer idHeadquarter;

    private Integer idCity;

    private String name;

    private String cellphone;

    private String email;

    private String address;

    private String city;

    private VBox options;

    public HeadquarterRowView(Integer idHeadquarter, Integer idCity, String name, String cellphone, String email,
                              String address, String city) throws FileNotFoundException {
        this.idHeadquarter = idHeadquarter;
        this.idCity = idCity;
        this.name = name;
        this.cellphone = cellphone;
        this.email = email;
        this.address = address;
        this.city = city;
        this.options = new VBox();
        this.setOptions();
    }

    public Integer getIdHeadquarter() {
        return idHeadquarter;
    }

    public void setIdHeadquarter(Integer idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }

    public Integer getIdCity() {
        return idCity;
    }

    public void setIdCity(Integer idCity) {
        this.idCity = idCity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public VBox getOptions() {
        return options;
    }

    public HBox getEditHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public Label getEditLabel(){
        return (Label) getEditHBox().getChildren().get(1);
    }

    public HBox getDeleteHBox(){
        return (HBox) options.getChildren().get(1);
    }

    public Label getDeleteLabel(){
        return (Label) getDeleteHBox().getChildren().get(1);
    }

    public void setOptions() {
        URL iconDeleteURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-delete.png");
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image deleteImage = new Image(String.valueOf(iconDeleteURL));
        Image editImage = new Image(String.valueOf(iconEditURL));
        options.getChildren().add(new HBox(new ImageView(editImage), new Label("Editar")));
        options.getChildren().add(new HBox(new ImageView(deleteImage), new Label("Eliminar")));
    }


}
