package facheritosfrontendapp.objectRowView.inventoryRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;

public class PartRowView {

    private String name;

    private String price;

    private String headquarter;

    private Integer quantity;

    private Integer idPart;

    private VBox options;

    public PartRowView(String name, String price, String headquarter, Integer quantity, Integer idPart) {
        this.name = name;
        this.price = price;
        this.headquarter = headquarter;
        this.quantity = quantity;
        this.idPart = idPart;
        this.options = new VBox();
        this.setOptions();
       // this.getOptionsHBox().setCursor(Cursor.HAND); //Hand when mouse hover
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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

    public Integer getIdPart() {
        return idPart;
    }

    public void setIdPart(Integer idPart) {
        this.idPart = idPart;
    }

    public HBox getOptionsHBox(){
        return (HBox) this.getOptions().getChildren().get(0);
    }

    public VBox getOptions() {
        return options;
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
