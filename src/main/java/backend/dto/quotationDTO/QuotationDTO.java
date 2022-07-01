package backend.dto.quotationDTO;

import java.time.LocalDate;

public class QuotationDTO implements Cloneable {

    private Integer idQuotation;

    private Integer idWorker;

    private Integer idHeadquarter;

    private Integer idCar;

    private Integer idCustomer;

    private Integer idConfirmation;

    private LocalDate quotationDate;

    private Integer idPayment;


    public Object clone() throws CloneNotSupportedException {
        QuotationDTO quotationDTO = (QuotationDTO) super.clone();
        return quotationDTO;
    }

    @Override
    public String toString(){
        return "id_quotation: " + idQuotation + "\n" + "id_worker: " + idWorker + "\n" + "id_headquarter: " + idHeadquarter +
                "\n" + "id_car: " + idCar + "\n" + "id_customer: " + idCustomer + "\n" + "id_confirmation: " + idConfirmation +
                "\n" + "quotation_date: " + quotationDate + "\n" + "id_payment: " + idPayment;

    }


    public Integer getIdQuotation() {
        return idQuotation;
    }

    public void setIdQuotation(Integer idQuotation) {
        this.idQuotation = idQuotation;
    }

    public Integer getIdWorker() {
        return idWorker;
    }

    public void setIdWorker(Integer idWorker) {
        this.idWorker = idWorker;
    }

    public Integer getIdHeadquarter() {
        return idHeadquarter;
    }

    public void setIdHeadquarter(Integer idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }

    public Integer getIdCar() {
        return idCar;
    }

    public void setIdCar(Integer idCar) {
        this.idCar = idCar;
    }

    public Integer getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Integer idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Integer getIdConfirmation() {
        return idConfirmation;
    }

    public void setIdConfirmation(Integer idConfirmation) {
        this.idConfirmation = idConfirmation;
    }

    public LocalDate getQuotationDate() {
        return quotationDate;
    }

    public void setQuotationDate(LocalDate quotationDate) {
        this.quotationDate = quotationDate;
    }

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }
}
