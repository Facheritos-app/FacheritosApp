package facheritosfrontendapp.objectRowView.saleRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.net.URL;

public class SaleSingleRowView {
    private Integer idCar;

    private Integer model;

    private String color;

    private Double price;

    private Integer quantity;

    private VBox options;

    public HBox getEditHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public Label getEditLabel(){
        return (Label) getEditHBox().getChildren().get(1);
    }

    public SaleSingleRowView(Integer idCar, Integer model, String color, Double price, Integer quantity) throws FileNotFoundException {
        this.idCar = idCar;
        this.model= model;
        this.color = color;
        this.price = price;
        this.quantity= quantity;
        this.options = new VBox();
        this.setOptions();
    }

    public Integer getIdCar() {
        return idCar;
    }

    public void setIdCar(Integer idSale) {
        this.idCar = idCar;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public VBox getOptions() {
        return options;
    }
    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxEdit = new HBox(new ImageView(editImage), new Label("Opciones"));
        hboxEdit.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxEdit);
        options.setAlignment(Pos.CENTER);
    }
}