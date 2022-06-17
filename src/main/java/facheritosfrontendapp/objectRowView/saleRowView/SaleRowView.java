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
import java.util.Date;

public class SaleRowView {
    private Integer idSale;

    private String nameSeller;

    private String nameClient;

    private Date dateSeller;

    private String paymentMethod;

    private String headquarter;

    private Double priceSale;

    private VBox options;

    public HBox getEditHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public Label getEditLabel(){
        return (Label) getEditHBox().getChildren().get(1);
    }

    public SaleRowView(Integer idSale, String nameSeller, String nameClient, Date dateSeller, String paymentMethod,String headquarter,
                              Double priceSale) throws FileNotFoundException {
        this.idSale = idSale;
        this.nameSeller = nameSeller;
        this.nameClient = nameClient;
        this.dateSeller = dateSeller;
        this.paymentMethod = paymentMethod;
        this.headquarter = headquarter;
        this.priceSale = priceSale;
        this.options = new VBox();
        this.setOptions();
    }

    public Integer getIdSale() {
        return idSale;
    }

    public void setIdSale(Integer idSale) {
        this.idSale = idSale;
    }

    public String getNameSeller() {
        return nameSeller;
    }

    public void setNameSeller(String nameSeller) {
        this.nameSeller = nameSeller;
    }

    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    public Date getDateSeller() {
        return dateSeller;
    }

    public void setDateSeller(Date dateSeller) {
        this.dateSeller = dateSeller;
    }

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }

    public Double getPriceSale() {
        return priceSale;
    }

    public void setPriceSale(String model) {
        this.priceSale = priceSale;
    }

    public VBox getOptions() {
        return options;
    }

    public void setOptions(VBox options) {
        this.options = options;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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