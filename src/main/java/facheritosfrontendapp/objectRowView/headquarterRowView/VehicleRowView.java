package facheritosfrontendapp.objectRowView.headquarterRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;

public class VehicleRowView {

    private String name;

    private Double price;

    private String headquarter;

    private Integer quantity;

    private Integer idModel;


    private VBox options;

    public VehicleRowView(String name, Double price, String headquarter, Integer quantity, Integer idModel) {
        this.name = name;
        this.price = price;
        this.headquarter = headquarter;
        this.quantity = quantity;
        this.idModel = idModel;
        this.options = new VBox();
        this.setOptions();
        this.getOptionsHBox().setCursor(Cursor.HAND); //Hand when mouse hover
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getIdModel() {
        return idModel;
    }

    public void setIdModel(Integer idModel) {
        this.idModel = idModel;
    }

    public VBox getOptions() {
        return options;
    }

    public HBox getOptionsHBox(){
        return (HBox) this.getOptions().getChildren().get(0);
    }

    public Label getOptionsLabel(){
        return (Label) getOptionsHBox().getChildren().get(1);
    }


    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxOptions = new HBox(new ImageView(editImage), new Label("Ver más"));
        hboxOptions.setAlignment(Pos.CENTER);
        hboxOptions.setSpacing(5); //Espaciado entre el ícono y el texto
        options.getChildren().add(hboxOptions);
        options.setAlignment(Pos.CENTER);
    }
}
