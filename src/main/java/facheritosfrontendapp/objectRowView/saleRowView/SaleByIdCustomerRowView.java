package facheritosfrontendapp.objectRowView.saleRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.sql.Date;

public class SaleByIdCustomerRowView {

    private String type = "Venta";
    private Integer idSale;

    private String saleDate;

    private String paymentMethod;

    private String headquarter;

    private VBox options;

    public SaleByIdCustomerRowView(Integer idSale, Date saleDate, String paymentMethod, String headquarter) {
        this.idSale = idSale;
        this.saleDate = saleDate.toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        this.paymentMethod = paymentMethod;
        this.headquarter = headquarter;
        this.options = new VBox();
        setOptions();
        getOptionsLabel().setCursor(Cursor.HAND);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIdSale() {
        return idSale;
    }

    public void setIdSale(Integer idSale) {
        this.idSale = idSale;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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
    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxOptions = new HBox(new ImageView(editImage), new Label("Ver más"));
        hboxOptions.setSpacing(5);
        hboxOptions.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxOptions);
        options.setAlignment(Pos.CENTER);
    }
}
