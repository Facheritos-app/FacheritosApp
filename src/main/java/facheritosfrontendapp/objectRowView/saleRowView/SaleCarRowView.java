package facheritosfrontendapp.objectRowView.saleRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.net.URL;

public class SaleCarRowView {
    private Integer idCar;

    private String model;

    private Double price;

    private Integer quantity;

    private String date;

    private String color;

    private VBox options;

    private VBox options2;

    public SaleCarRowView(Integer idCar, String model,String color, Double price , Integer quantity,String date ) throws FileNotFoundException {
        this.idCar = idCar;
        this.model= model;
        this.color = color;
        this.price = price;
        this.quantity= quantity;
        this.date = date;
        this.options = new VBox();
        this.options2 = new VBox();
        this.setOptions();
        this.setOptions2();
    }

    public HBox getCheckHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public CheckBox getCheckLabel(){
        return (CheckBox) getCheckHBox().getChildren().get(1);
    }

    public HBox getQuantityHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public TextField getQuantityLabel(){
        return (TextField) getQuantityHBox().getChildren().get(1);
    }

    public Integer getIdCar() {
        return idCar;
    }

    public void setIdCar(Integer idCar) {
        this.idCar = idCar;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public VBox getOptions() {
        return options;
    }

    public void setOptions() {
        HBox hboxCheck = new HBox(new CheckBox());
        hboxCheck.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxCheck);
        options.setAlignment(Pos.CENTER);

    }

    public VBox getOptions2() {
        return options2;
    }
    public void setOptions2() {

        HBox hboxQuantity = new HBox(new TextField());
        hboxQuantity .setAlignment(Pos.CENTER);
        options2.getChildren().add(hboxQuantity);
        options2.setAlignment(Pos.CENTER);
    }


}