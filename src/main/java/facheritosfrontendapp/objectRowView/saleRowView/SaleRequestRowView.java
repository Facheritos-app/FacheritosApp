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

public class SaleRequestRowView {

    private String sellerName;

    private String customerName;

    private Integer idSale;

    private String saleDate;

    private String paymentMethod;

    private String headquarterName;

    private VBox options;

    public SaleRequestRowView(String sellerName, String customerName, Integer idSale, String saleDate, String paymentMethod, String headquarterName) {
        this.sellerName = sellerName;
        this.customerName = customerName;
        this.idSale = idSale;
        this.saleDate = saleDate;
        this.paymentMethod = paymentMethod;
        this.headquarterName = headquarterName;
        this.options = new VBox();
        this.setOptions();
        this.getOptionsHBox().setCursor(Cursor.HAND); //Hand when mouse hover
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getHeadquarterName() {
        return headquarterName;
    }

    public void setHeadquarterName(String headquarterName) {
        this.headquarterName = headquarterName;
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
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-view.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxEdit = new HBox(new ImageView(editImage), new Label("Ver más"));
        hboxEdit.setSpacing(5);
        hboxEdit.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxEdit);
        options.setAlignment(Pos.CENTER);
    }
}
