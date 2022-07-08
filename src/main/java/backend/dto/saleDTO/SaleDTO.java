package backend.dto.saleDTO;

import facheritosfrontendapp.objectRowView.saleRowView.SaleCarRowView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class SaleDTO {

    public Object setId_customer;
    protected Integer id_sale;

    protected String ccSeller;

    protected String ccClient;

    protected String payment_method;

    protected Integer id_worker;

    protected Integer id_customer;

    protected Integer id_headquarter;
    protected Integer id_payment_method;

    protected Integer id_confirmation;

    protected Date sale_date;

    protected Double price;

    protected ArrayList<SaleCarRowView> saleCarRowsArray2;

    protected ArrayList<SaleCarRowView> headquarterCarRowsArray;

    public Integer getId_sale() {
        return id_sale;
    }

    public void setId_sale(Integer id_sale) {
        this.id_sale = id_sale;
    }

    public Integer getId_worker() {
        return id_worker;
    }

    public void setId_worker(Integer id_worker) {
        this.id_worker = id_worker;
    }

    public Integer getId_customer() {
        return id_customer;
    }

    public void setId_customer(Integer id_customer) {
        this.id_customer = id_customer;
    }

    public Integer getId_headquarter() {
        return id_headquarter;
    }

    public void setId_headquarter(Integer id_headquarter) {
        this.id_headquarter = id_headquarter;
    }

    public Integer getId_confirmation() {
        return id_confirmation;
    }

    public void setId_confirmation(Integer id_confirmation) {
        this.id_confirmation = id_confirmation;
    }

    public Date getSale_date() {
        return sale_date;
    }

    public void setSale_date(Date sale_date) {
        this.sale_date = sale_date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ArrayList<SaleCarRowView> getSaleCarRowsArray2() {
        return saleCarRowsArray2;
    }

    public void setSaleCarRowsArray2(ArrayList<SaleCarRowView> saleCarRowsArray2) {
        this.saleCarRowsArray2 = saleCarRowsArray2;
    }

    public ArrayList<SaleCarRowView> getHeadquarterCarRowsArray() {
        return headquarterCarRowsArray;
    }

    public void setHeadquarterCarRowsArray(ArrayList<SaleCarRowView> headquarterCarRowsArray) {
        this.headquarterCarRowsArray = headquarterCarRowsArray;
    }

    public Integer getId_payment_method() {
        return id_payment_method;
    }

    public void setId_payment_method(Integer id_payment_method) {
        this.id_payment_method = id_payment_method;
    }

    public String getCcSeller() {
        return ccSeller;
    }

    public void setCcSeller(String ccSeller) {
        this.ccSeller = ccSeller;
    }

    public String getCcClient() {
        return ccClient;
    }

    public void setCcClient(String ccClient) {
        this.ccClient = ccClient;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
}