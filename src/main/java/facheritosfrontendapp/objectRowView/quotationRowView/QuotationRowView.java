package facheritosfrontendapp.objectRowView.quotationRowView;

import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;

public class QuotationRowView {

    private Integer idQuotation;

    private String seller;

    private String customer;

    private LocalDate saleDate;

    private String headquarter;

    private VBox options;

    public QuotationRowView(Integer idQuotation, String seller, String customer, LocalDate saleDate, String headquarter){
        this.idQuotation = idQuotation;
        this.seller = seller;
        this.customer = customer;
        this.saleDate = saleDate;
        this.headquarter = headquarter;
        options = new VBox();
        setOptions();
    }

    public Integer getIdQuotation() {
        return idQuotation;
    }

    public void setIdQuotation(Integer idQuotation) {
        this.idQuotation = idQuotation;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public String getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(String headquarter) {
        this.headquarter = headquarter;
    }

    public VBox getOptions(){
        return options;
    }

    public HBox getOptionsHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public Label getOptionsLabel(){
        return (Label) getOptionsHBox().getChildren().get(1);
    }

    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-view.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxOptions = new HBox(new ImageView(editImage), new Label("Ver más"));
        hboxOptions.setSpacing(5);
        hboxOptions.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxOptions);
        options.setAlignment(Pos.CENTER);
    }
}
